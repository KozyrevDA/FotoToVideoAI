import data.network.DEFAULT_IP
import kotlinx.coroutines.suspendCancellableCoroutine
import okio.ByteString.Companion.toByteString
import platform.AuthenticationServices.ASAuthorization
import platform.AuthenticationServices.ASAuthorizationAppleIDCredential
import platform.AuthenticationServices.ASAuthorizationAppleIDProvider
import platform.AuthenticationServices.ASAuthorizationController
import platform.AuthenticationServices.ASAuthorizationControllerDelegateProtocol
import platform.AuthenticationServices.ASAuthorizationControllerPresentationContextProvidingProtocol
import platform.AuthenticationServices.ASAuthorizationScopeEmail
import platform.AuthenticationServices.ASAuthorizationScopeFullName
import platform.AuthenticationServices.ASPresentationAnchor
import platform.AuthenticationServices.ASUserDetectionStatus.ASUserDetectionStatusLikelyReal
import platform.Foundation.NSError
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIApplication
import platform.UIKit.UIViewController
import platform.darwin.NSObject
import kotlin.coroutines.resume

class IOSProviderAuthPage : ProviderAuthPage {
    override fun launchAuthGoogle() {
        val nsUrl = NSURL.URLWithString(DEFAULT_IP.plus("users/signin/google")) ?: return
        val viewController = getRootViewController() ?: return
        val safariViewController = SFSafariViewController(uRL = nsUrl)

        viewController.presentViewController(
            viewControllerToPresent = safariViewController,
            animated = true,
            completion = null
        )
    }

    override suspend fun launchAuthApple(): SignInCredential {
        val appleIDProvider = ASAuthorizationAppleIDProvider()
        val request = appleIDProvider.createRequest().apply {
            requestedScopes = listOf(ASAuthorizationScopeFullName, ASAuthorizationScopeEmail)
        }
        val authorizationController = ASAuthorizationController(listOf(request))
        return authorizationController.performSignIn()
    }

    /**
     * Получение корневого UIViewController
     */
    private fun getRootViewController(): UIViewController? {
        return UIApplication.sharedApplication.keyWindow?.rootViewController
    }

    private suspend fun ASAuthorizationController.performSignIn() =
        suspendCancellableCoroutine { cont ->
            val delegate = AuthorizationDelegate { cont.resume(it) }

            cont.invokeOnCancellation {
                delegate.onComplete = {}
                cancel()
            }

            presentationContextProvider = PresentationProtocol()
            this.delegate = delegate

            performRequests()
        }

    private class AuthorizationDelegate(
        var onComplete: (SignInCredential) -> Unit,
    ) : NSObject(), ASAuthorizationControllerDelegateProtocol {
        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithAuthorization: ASAuthorization,
        ) {
            when (val credential = didCompleteWithAuthorization.credential) {
                is ASAuthorizationAppleIDCredential -> {
                    SignInCredential.Apple(
                        id = credential.user,
                        token = credential.identityToken
                            ?.toByteString()
                            ?.utf8()
                            ?: return onComplete(SignInCredential.Error(desc = "identityToken")),
                        authCode = credential.authorizationCode
                            ?.toByteString()
                            ?.utf8()
                            ?: return onComplete(SignInCredential.Error(desc = "authCode")),
                        fullName = credential.fullName?.givenName ?: credential.fullName?.nickname,
                        email = credential.email,
                        likelyRealPerson = credential.realUserStatus == ASUserDetectionStatusLikelyReal
                    )
                }

                else -> {
                    SignInCredential.Error(desc = "Incorrect type credential")
                }
            }.let {
                onComplete(it)
            }
        }

        override fun authorizationController(
            controller: ASAuthorizationController,
            didCompleteWithError: NSError,
        ) {
            onComplete(
                SignInCredential.Error(
                    desc = "Error with code: ${didCompleteWithError.code}"
                )
            )
        }
    }

    private class PresentationProtocol : NSObject(),
        ASAuthorizationControllerPresentationContextProvidingProtocol {

        override fun presentationAnchorForAuthorizationController(
            controller: ASAuthorizationController,
        ): ASPresentationAnchor = UIApplication.sharedApplication.keyWindow
    }
}

actual fun getProviderAuthPage(): ProviderAuthPage = IOSProviderAuthPage()