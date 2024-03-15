import app.cash.turbine.test
import data.PhotoItem
import data.PhotoItemRepository
import domain.realmConfigWithName
import io.realm.kotlin.Realm
import kotlinx.coroutines.runBlocking
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals


class PhotoRepositoryTest {
    val testRealmConfig = realmConfigWithName("CrackLogTestDB")
    lateinit var testRealm: Realm

    lateinit var testSubject: PhotoItemRepository

    @BeforeTest
    fun init() {
        Realm.deleteRealm(testRealmConfig)
        testRealm = Realm.open(testRealmConfig)
        testSubject = PhotoItemRepository(testRealm)
    }

    @AfterTest
    fun tearDown() {
        testRealm.close()
        Realm.deleteRealm(testRealmConfig)
    }

    @Test
    fun testSaveCrackLogWithFlow() {

        runBlocking {

            testSubject.crackLogItems.test {
                val initialResult = awaitItem()
                testSubject.saveCrackLog("TestCrack")
                assertEquals(0, initialResult.list.size)
                val afterInsertResult = awaitItem()
                assertEquals(1, afterInsertResult.list.size)
                assertEquals("TestCrack", afterInsertResult.list.first().name)

            }
        }
    }

    @Test
    fun testSaveCrackLogWithQuery() {

        runBlocking {
            assertEquals(0, testSubject.findAllCrackLogItems().size)
            testSubject.saveCrackLog("TestCrack")
            assertEquals(1, testSubject.findAllCrackLogItems().size)
        }
    }

    @Test
    fun testAddPhotoItem() {

        runBlocking {
            assertEquals(0, testSubject.findAllCrackLogItems().size)
            val crackLogItem = testSubject.saveCrackLog("TestCrack")
            assertEquals(1, testSubject.findAllCrackLogItems().size)
            val crackItem = crackLogItem.cracks.first()
            assertEquals(0, crackItem.photos.size)
            testSubject.savePhotoItem(
                photoItem = PhotoItem(descr = "TestPhoto"),
                crackLogItemId = crackLogItem._id,
                crackItemId = crackItem.id
            )
            val savedCrackLog = testSubject.findAllCrackLogItems().first()
            assertEquals(1, savedCrackLog.cracks.first().photos.size)
        }
    }

    @Test
    fun testDetectPhotoItemInsertions() {

        runBlocking {
            val crackLogItem = testSubject.saveCrackLog("TestCrack")
            assertEquals(1, testSubject.findAllCrackLogItems().size)
            testSubject.photoItemsForCrackLogAndItemId(
                crackLogId = crackLogItem._id.toHexString(),
                crackItemId = crackLogItem.cracks.first().id
            ).test {
                val photoList = awaitItem()
                assertEquals(0, photoList.size)
                testSubject.savePhotoItem(
                    photoItem = PhotoItem(descr = "TestPhoto"),
                    crackLogItemId = crackLogItem._id,
                    crackItemId = crackLogItem.cracks.first().id
                )
                val photoList2 = awaitItem()
                assertEquals(1, photoList2.size)
            }
        }
    }
}