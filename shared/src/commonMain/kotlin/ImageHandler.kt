interface ImageHandler {

    fun onImageBytesCaptured(byteArray: ByteArray?)
    fun onCancelled()
}
