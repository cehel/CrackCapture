package data

import androidx.compose.ui.graphics.ImageBitmap
import encodeImageToBase64
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.ResultsChange
import kotlinx.coroutines.flow.Flow
import kotlinx.datetime.LocalDateTime

class PhotoItemRepository {
    val config = RealmConfiguration.create(schema = setOf(PhotoItem::class))
    val realm: Realm = Realm.open(config)

    fun savePhotoItem(image: ImageBitmap, localDateTime: LocalDateTime) {
        realm.writeBlocking {
            copyToRealm(PhotoItem().apply {
                datetime = localDateTime.toString()
                imageBase64 = encodeImageToBase64(image) ?: ""
            })
        }
    }

    // all items in the realm
    val photoItems: Flow<ResultsChange<PhotoItem>> = realm.query<PhotoItem>().find().asFlow()

    fun deletePhotoItem(dateTime: LocalDateTime) {
        realm.writeBlocking {
            val photoToDelete = query<PhotoItem>("datetime == $0", dateTime.toString()).find()
            delete(photoToDelete.first())
        }
    }
}