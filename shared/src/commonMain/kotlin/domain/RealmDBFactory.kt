package domain

import data.CrackItem
import data.CrackLogItem
import data.PhotoItem
import data.RoomItem
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

fun realmConfigWithName(dbName: String): RealmConfiguration {
    val config = RealmConfiguration.Builder(schema = setOf(
        PhotoItem::class,
        RoomItem::class,
        CrackItem::class,
        CrackLogItem::class)

    ).name(dbName).build()

    return config
}