import UIKit
import SwiftUI
import shared

struct ComposeViewControllerRepresentable: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        Main_iosKt.MainViewController(createCameraView: { () -> UIView in
            SwiftUIInUIView(
                content: CameraIOSView()
            )
        })
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {
    }
}

private class SwiftUIInUIView<Content: View>: UIView {

    init(content: Content) {
        super.init(frame: CGRect())
        let hostingController = UIHostingController(rootView: content)
        hostingController.view.translatesAutoresizingMaskIntoConstraints = false
        addSubview(hostingController.view)
        NSLayoutConstraint.activate([
            hostingController.view.topAnchor.constraint(equalTo: topAnchor),
            hostingController.view.leadingAnchor.constraint(equalTo: leadingAnchor),
            hostingController.view.trailingAnchor.constraint(equalTo: trailingAnchor),
            hostingController.view.bottomAnchor.constraint(equalTo: bottomAnchor)
        ])
    }

    required init?(coder: NSCoder) {
        fatalError("init(coder:) has not been implemented")
    }
}

struct ContentView: View {
    var body: some View {
        ComposeViewControllerRepresentable()
                    .ignoresSafeArea(.all)
    }
}
