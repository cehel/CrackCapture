package data

import io.realm.kotlin.Realm
import io.realm.kotlin.ext.copyFromRealm
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.DeletedList
import io.realm.kotlin.notifications.DeletedObject
import io.realm.kotlin.notifications.InitialList
import io.realm.kotlin.notifications.InitialObject
import io.realm.kotlin.notifications.PendingObject
import io.realm.kotlin.notifications.ResultsChange
import io.realm.kotlin.notifications.UpdatedList
import io.realm.kotlin.notifications.UpdatedObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import kotlinx.datetime.LocalDateTime
import org.mongodb.kbson.ObjectId

class PhotoItemRepository(val realm: Realm) {

    fun crackItemForId(crackLogId: String, crackItemId: Long) =
        findCrackItemFor(crackItemId, crackLogId)
            .first()
            .asFlow().map { crackQuery ->
                when (crackQuery) {
                    is DeletedObject -> null
                    is InitialObject -> crackQuery.obj
                    is UpdatedObject -> crackQuery.obj
                    is PendingObject -> crackQuery.obj
                }
            }

    private fun findCrackItemFor(
        crackItemId: Long,
        crackLogId: String
    ) = realm.query<CrackItem>("id == $0 AND parentCrackLogId == $1", crackItemId, crackLogId)

    suspend fun updateCrackItem(
        crackId: Long, crackLogId: String,
        orientation: String? = null,
        description: String? = null,
        length: String? = null,
        width: String? = null
    ) = withContext(Dispatchers.IO){
        realm.write {
            val parentObject =
                query<CrackLogItem>("_id == $0", ObjectId.invoke(crackLogId)).find().first()
            val cracks = parentObject.cracks
            val crackIndex = cracks.indexOfFirst { it.id == crackId }
            orientation?.let { parentObject.cracks[crackIndex].orientation = it }
            description?.let { parentObject.cracks[crackIndex].description = it }
            length?.let { parentObject.cracks[crackIndex].length = it }
            width?.let { parentObject.cracks[crackIndex].width = it }
        }
    }


    suspend fun savePhotoItem(
        photoItem: PhotoItem,
        crackLogItemId: ObjectId,
        crackItemId: Long
    ) = withContext(Dispatchers.IO) {
        realm.write {
            val parentObject = query<CrackLogItem>("_id == $0", crackLogItemId).find().first()
            val cracks = parentObject.cracks
            val crackIndex = cracks.indexOfFirst { it.id == crackItemId }
            parentObject.cracks[crackIndex].photos.add(photoItem)
        }
    }

    suspend fun saveCrackLog(logname: String, addr: String = ""): CrackLogItem = withContext(
        Dispatchers.IO
    ) {
        realm.write {
            copyToRealm(CrackLogItem().apply {
                name = logname
                address = addr
                cracks.add(
                    CrackItem(
                        id = ((maxCrackId() ?: 0L) + 1L),
                        description = "",
                        parentCrackLogId = this._id.toHexString()
                    )
                )
            })
        }.copyFromRealm()
    }

    suspend fun createNewCrackItem(crackLogId: ObjectId): CrackItem? = withContext(Dispatchers.IO) {

        val crackLogItem = realm.query<CrackLogItem>("_id == $0", crackLogId).find().first()
        val crackItem = CrackItem(
            id = ((maxCrackId() ?: 0L) + 1L),
            description = "",
            parentCrackLogId = crackLogId.toHexString()
        )
        realm.write {
            findLatest(crackLogItem)?.let { crackLog ->
                crackLog.cracks.add(crackItem)
                return@write crackItem
            }
            null
        }
    }

    private fun maxCrackId() =
        realm.query<CrackItem>("id >= $0 SORT(id DESC)", 0L).first().find()?.id

    fun crackLogItemFlow(crackLogId: ObjectId) =
        realm.query<CrackLogItem>("_id == $0", crackLogId).first().asFlow()

    fun photoItemsForCrackLogAndItemId(
        crackLogId: String,
        crackItemId: Long
    ): Flow<List<PhotoItem>> {
        val query = realm.query<CrackLogItem>("_id == $0", ObjectId.invoke(crackLogId)).first()

        // Observing changes in the CrackLogItem
        return query.find()?.cracks?.asFlow()?.map { changes ->
            when (changes) {
                is InitialList -> {
                    val crackItem = changes.list.firstOrNull { it.id == crackItemId }
                    crackItem?.photos ?: emptyList<PhotoItem>()
                }

                is UpdatedList -> {
                    val crackItem = changes.list.firstOrNull { it.id == crackItemId }
                    crackItem?.photos ?: emptyList<PhotoItem>()
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

    fun deleteCrackLog(id: ObjectId) {
        realm.writeBlocking {
            val photoToDelete = query<CrackLogItem>("_id == $0", id).find()
            delete(photoToDelete.first())
        }
    }

    fun deletePhotoItem(dateTime: LocalDateTime) {
        realm.writeBlocking {
            val photoToDelete = query<PhotoItem>("datetime == $0", dateTime.toString()).find()
            delete(photoToDelete.first())
        }
    }

    fun deleteCrackItem(crackLogId: ObjectId, crackId: Long) {
        println("Delete crackLogId: ${crackLogId.toHexString()} and itemid: $crackId")
        realm.writeBlocking {
            val crackToDelete = query<CrackItem>(
                "parentCrackLogId == $0 AND id == $1",
                crackLogId.toHexString(),
                crackId
            ).find()
            delete(crackToDelete.first())
        }
    }
}