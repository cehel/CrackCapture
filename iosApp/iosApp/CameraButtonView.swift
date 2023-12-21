//
//  CameraButtonView.swift
//  iosApp
//
//  Created by Celine Heldner on 14/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//
import SwiftUI
import shared

struct CameraButtonView: View {
    @State private var isShowingCamera = false
    @State private var capturedImage: UIImage?

    var body: some View {
        VStack {
            // Your existing Button and .sheet modifier
            Button(action: {
                isShowingCamera = true
            }) {
                HStack {
                    Image(systemName: "camera")
                    Text("Take Photo")
                }
            }
            .sheet(isPresented: $isShowingCamera) {
                CameraView(capturedImage: $capturedImage)
                    .onDisappear {
                        if let image = capturedImage {

                           if let jpegData = capturedImage?.jpegData(compressionQuality: 1.0) {
                               let byteArray = [UInt8](jpegData)
                               let kotlinByteArray = KotlinByteArray(size: Int32(byteArray.count))
                                   for (index, byte) in byteArray.enumerated() {
                                       kotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
                                   }

                                Main_iosKt.passInByteArray(byteArray: kotlinByteArray)
                           }
                            //let imageFilePath = saveImage(image)
                        }
                    }
            }
        }
    }

}


func saveImage(image: UIImage, fileName: String) -> String? {
    // var imageUrl: String
    
    //for crackLog in crackLogs {

        do {
            let imageUrl = URL(fileURLWithPath: NSTemporaryDirectory()).appendingPathComponent("\(fileName).jpg")
            let compressedImageData = image.jpegData(compressionQuality: 0.2) // adjust compression quality as needed
            try compressedImageData?.write(to: imageUrl)
            return imageUrl.absoluteString
        } catch {
            print("Failed to save image data: \(error.localizedDescription)")
        }
    // }
    
    return nil
}
