package ru.hukm.pokercards.utils

import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.persistence.PersistentDataType
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards.Companion.instance

class WorldContainer {
    companion object {
        private val keyMapsIds = NamespacedKey(instance, "mapsIds")
        private val overworld = Bukkit.getWorlds()[0]

        fun getMapsIds(): ArrayList<Int> = Api.base64GetContainerValue(overworld, keyMapsIds, ArrayList<Int>()::class.java)
        fun setMapsIds(ids: ArrayList<Int>) = Api.base64SetContainerValue(overworld, keyMapsIds, ids)
        fun hasMapsIds(): Boolean = Api.hasContainerValue(overworld, keyMapsIds, PersistentDataType.STRING)
    }
}