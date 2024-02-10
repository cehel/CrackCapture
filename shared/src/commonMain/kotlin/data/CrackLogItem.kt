package data

import io.realm.kotlin.ext.realmListOf
import io.realm.kotlin.types.RealmList
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

/**
 * A complete Crack Log project f.ex. of one building or apartment
 */
class CrackLogItem() : RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var name: String = ""
    var address: String? = null
    var cracks: RealmList<CrackItem> = realmListOf()

    constructor(name: String) : this() {
        this.name = name
    }

}
