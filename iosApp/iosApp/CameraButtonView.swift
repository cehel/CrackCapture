//
//  CameraButtonView.swift
//  iosApp
//
//  Created by Celine Heldner on 14/12/23.
//  Copyright © 2023 orgName. All rights reserved.
//
import SwiftUI

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
                            // viewModel.saveImageToCrackLog(image)
                        }
                    }
            }
        }
    }
}
