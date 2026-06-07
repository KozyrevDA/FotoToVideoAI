import SwiftUI

@main
struct iOSApp: App {

    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate

    var body: some Scene {
        WindowGroup {
            ContentView()
                .onOpenURL { url in
                    if let scheme = url.scheme,
                       scheme == "com.nla.phototovideoai" {
                        handleRedirect(url: url)
                    }
                }
        }
    }

    private func handleRedirect(url: URL) {
        let parameters = getQueryParameters(url: url)
        if let accessToken = parameters["accessToken"],
           let refreshToken = parameters["refreshToken"] {
            saveTokens(accessToken: accessToken, refreshToken: refreshToken)
        } else {
            print("Token is missing or invalid")
        }
    }

    private func saveTokens(accessToken: String, refreshToken: String) {
        UserDefaults.standard.set(accessToken, forKey: "access_token")
        UserDefaults.standard.set(refreshToken, forKey: "refresh_token")
    }

    private func getQueryParameters(url: URL) -> [String: String] {
        guard let components = URLComponents(url: url, resolvingAgainstBaseURL: false),
              let queryItems = components.queryItems else { return [:] }
        var parameters = [String: String]()
        for item in queryItems {
            parameters[item.name] = item.value
        }
        return parameters
    }
}
