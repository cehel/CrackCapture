import SwiftUI

struct CameraView: UIViewControllerRepresentable {
    @Environment(\.presentationMode) private var presentationMode
    @Binding var capturedImage: UIImage?

    class Coordinator: NSObject, UINavigationControllerDelegate, UIImagePickerControllerDelegate {
        var parent: CameraView

        init(_ parent: CameraView) {
            self.parent = parent
        }

        func imagePickerController(_ picker: UIImagePickerController, didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey: Any]) {
            DispatchQueue.main.async {
                picker.dismiss(animated: false, completion: nil)

            }
            if let image = info[.originalImage] as? UIImage {
                parent.capturedImage = image
                let imageSaver = ImageSaver()
                imageSaver.writeToPhotoAlbum(image: image)
            }
            parent.presentationMode.wrappedValue.dismiss()
        }

        func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
            parent.presentationMode.wrappedValue.dismiss()
            DispatchQueue.main.async {
                picker.dismiss(animated: false, completion: nil)
            }
        }
    }

    func makeCoordinator() -> Coordinator {
        Coordinator(self)
    }

    func makeUIViewController(context: Context) -> UIImagePickerController {
        let picker = UIImagePickerController()
        picker.sourceType = .camera
        picker.delegate = context.coordinator
        return picker
    }

    func updateUIViewController(_ uiViewController: UIImagePickerController, context: Context) {

    }
}
