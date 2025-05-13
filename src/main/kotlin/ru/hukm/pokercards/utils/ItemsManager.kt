package ru.hukm.pokercards.utils

import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards

class ItemsManager {
    companion object {
        private val key = NamespacedKey(PokerCards.instance, "typeItem")

        fun setType(item: ItemStack, type: String) = Api.setContainerValue(item, key, PersistentDataType.STRING, type)

        fun getType(item: ItemStack?): String? {
            if(item == null) return null
            return Api.getContainerValue(item, key, PersistentDataType.STRING)
        }
    }
}