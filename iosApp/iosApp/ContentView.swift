import UIKit
import SwiftUI
import ComposeApp

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    var body: some View {
        ZStack {
            Color(
                UIColor(
                    red:   9.0/255.0,
                    green: 8.0/255.0,
                    blue:  15.0/255.0,
                    alpha: 1.0
                )
            )
            .edgesIgnoringSafeArea(.all)
            
            ComposeView()
                .ignoresSafeArea(.keyboard) // Compose has own keyboard handler
        }
    }
}



