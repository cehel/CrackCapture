package view.navigation

enum class ScreenKeys {
    HOME,
    CRACKLOGDETAIL,
    CRACKLOGCREATION,
    CRACKITEM,
    UNKNOWN;

    companion object {
        fun fromString(key: String): ScreenKeys {
            return entries.firstOrNull { it.toString() == key } ?: UNKNOWN
        }
    }
}


