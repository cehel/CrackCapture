//
//  CameraIOSView.swift
//  iosApp
//
//  Created by Celine Heldner on 26/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//

import SwiftUI	
import shared

struct CameraIOSView : View {
    @State private var isShowingCamera = true
    @State private var capturedImage: UIImage?
    
    var body: some View {
        Text("Camera/Photo processing...").fullScreenCover(isPresented: $isShowingCamera) {
            CameraView(capturedImage: $capturedImage)
                .onDisappear {
                    isShowingCamera = false
                    if let image = capturedImage {

                        if let jpegData = capturedImage?.jpegData(compressionQuality: 0.2) {
                           let byteArray = [UInt8](jpegData)
                           let kotlinByteArray = KotlinByteArray(size: Int32(byteArray.count))
                               for (index, byte) in byteArray.enumerated() {
                                   kotlinByteArray.set(index: Int32(index), value: Int8(bitPattern: byte))
                               }

                            Main_iosKt.passInByteArray(byteArray: kotlinByteArray)
                           
                       }
                    }
                    Main_iosKt.onCameraCancelled()
                    
                }
        }
    }
    
    
}
