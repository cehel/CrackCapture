package data

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.DeletedList
import io.realm.kotlin.notifications.InitialList
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedList
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.datetime.LocalDateTime
import org.mongodb.kbson.ObjectId

class PhotoItemRepository(val realm: Realm) {

    suspend fun savePhotoItem(
        photoItem: PhotoItem,
        crackLogItemId: ObjectId,
        crackItemId: Long
    ) {
        realm.write {
            val parentObject = query<CrackLogItem>("_id == $0", crackLogItemId).find().first()
            val cracks = parentObject.cracks
            val crackIndex = cracks.indexOfFirst { it.id == crackItemId }
            parentObject.cracks[crackIndex].photos.add(photoItem)
        }
    }

    suspend fun saveCrackLog(logname: String, addr: String = ""): CrackLogItem {
        return realm.write {
            copyToRealm(CrackLogItem().apply {
                name = logname
                address = addr
                //TODO
                cracks.add(CrackItem(id = ((maxCrackId()?:0L)+1L), description = ""))
            })
        }.copyFromRealm()
    }

    suspend fun createNewCrackItem(crackLogId: ObjectId): CrackItem? {
        val crackLogItem = realm.query<CrackLogItem>("_id == $0", crackLogId).find().first()
        val crackItem = CrackItem(id = ((maxCrackId()?:0L)+1L), description = "")
        return realm.write {
            findLatest(crackLogItem)?.let { crackLog ->
                crackLog.cracks.add(crackItem)
                return@write crackItem
            }
            null
        }
    }

    fun maxCrackId() = realm.query<CrackItem>("id >= $0 SORT(id DESC)",0L ).first().find()?.id

    fun crackLogItemFlow( crackLogId: ObjectId) = realm.query<CrackLogItem>("_id == $0", crackLogId).first().asFlow()

    fun photoItemsForCrackLogAndItemId(crackLogId: ObjectId, crackItemId: Long): Flow<List<PhotoItem>> {
        val query = realm.query<CrackLogItem>("_id == $0", crackLogId).first()

        // Observing changes in the CrackLogItem
        return query.find()?.cracks?.asFlow()?.map { changes ->
            when (changes) {
                is InitialList -> {
                    val crackItem = changes.list.firstOrNull { it.id == crackItemId }
                    crackItem?.photos?: emptyList<PhotoItem>()
                }
                is UpdatedList -> {
                    val crackItem = changes.list.firstOrNull { it.id == crackItemId }
                    crackItem?.photos?: emptyList<PhotoItem>()
                }
                is DeletedList -> {
                    emptyList<PhotoItem>()
                }

            }
        } ?: flowOf(emptyList())
    }

    val crackLogItems: Flow<ResultsChange<CrackLogItem>> =
        realm.query<CrackLogItem>().find().asFlow()

    fun findAllCrackLogItems() = realm.query<CrackLogItem>().find()


    // all items in the realm
    val photoItems: Flow<ResultsChange<PhotoItem>> = realm.query<PhotoItem>().find().asFlow()

    fun deletePhotoItem(dateTime: LocalDateTime) {
        realm.writeBlocking {
            val photoToDelete = query<PhotoItem>("datetime == $0", dateTime.toString()).find()
            delete(photoToDelete.first())
        }
    }
}