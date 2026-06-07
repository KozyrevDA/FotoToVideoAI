import co.touchlab.crashkios.crashlytics.enableCrashlytics
import co.touchlab.crashkios.crashlytics.setCrashlyticsUnhandledExceptionHook
import org.nla.phototovideoai.composeapp.nativeInterop.cinterop.firebase.FirebaseKMPWrapper
import kotlinx.cinterop.ExperimentalForeignApi

class IOSFirebaseKMP : FirebaseKMP {
    override fun initialize() {
        enableCrashlytics() //TODO зарегать в Firebase. google.json заменить
        setCrashlyticsUnhandledExceptionHook()
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun reportEvent(eventName: String) {
        runCatching {
            FirebaseKMPWrapper.reportEventWithName(eventName = eventName)
        }
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun reportEvent(eventName: String, parameters: Map<String, Any>) {
        runCatching {
            FirebaseKMPWrapper.reportEventWithName(
                eventName = eventName,
                parameters = parameters.mapKeys { it.key as? Any }
            )
        }
    }

    override fun recordException(throwable: Throwable) = Unit
}

actual fun getFirebaseKMP(): FirebaseKMP = IOSFirebaseKMP()