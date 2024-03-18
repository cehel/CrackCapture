package data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.EmbeddedRealmObject
import io.realm.kotlin.types.RealmList

class CrackItem() : EmbeddedRealmObject {
    var id: Long = 0L
    var parentCrackLogId: String = ""
    var title: String = ""
    var description: String = ""
    var width: String = ""
    var length: String = ""
    var orientation: String = ""
    var location: String? = null
    var photos: RealmList<PhotoItem> = realmListOf()

    constructor(id: Long, title: String, parentCrackLogId: String) : this() {
        this.title = title
        this.parentCrackLogId = parentCrackLogId
        this.id = id
    }

}
