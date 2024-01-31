package model

import androidx.compose.ui.graphics.ImageBitmap
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import org.mongodb.kbson.ObjectId

data class PhotoInfo(
    val objectId: ObjectId,
    val image: ImageBitmap,
    val dateTime: LocalDateTime?,
    val descriptionBig: String,
    val descriptionSmall: String
)
