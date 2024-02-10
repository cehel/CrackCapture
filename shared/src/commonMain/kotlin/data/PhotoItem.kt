package data

import decodeBase64ToImage
import io.realm.kotlin.types.EmbeddedRealmObject
import kotlinx.datetime.LocalDateTime
import model.PhotoInfo

class PhotoItem() : EmbeddedRealmObject {
    var description: String = ""
    var datetime: String = ""
    var imageBase64: String = ""

    constructor(descr: String = "") : this() {
        description = descr
    }

    fun toPhotoInfo(): PhotoInfo {
        val dateTime = if (datetime.isNotEmpty()) LocalDateTime.parse(datetime) else null
        return PhotoInfo(
            image = decodeBase64ToImage(this.imageBase64),
            dateTime = dateTime,
            descriptionBig = dateTime?.date?.toString() ?: "",
            descriptionSmall = dateTime?.time?.let { "${it.hour}:${it.minute}:${it.second}" } ?: ""
        )
    }
}