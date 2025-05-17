package ru.hukm.pokercards.utils

import org.bukkit.NamespacedKey
import org.bukkit.entity.Interaction
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString
import java.util.*

class DeckOfCardsContainer {
    companion object{
        private val KEY_INVENTORY = NamespacedKey(PokerCards.instance, "inventory")
        private val KEY_ITEM_DISPLAYS_UUID = NamespacedKey(PokerCards.instance, "item_displays_uuid")

        fun getInventoryItems(interaction: Interaction) = Api.compactBase64GetContainerValue(interaction, KEY_INVENTORY, arrayListOf<ItemStack?>()::class.java)
        fun setInventoryItems(interaction: Interaction, inventoryItems: ArrayList<ItemStack?>) = Api.compactBase64SetContainerValue(interaction, KEY_INVENTORY, inventoryItems)
        fun isDeckCards(interaction: Interaction) = getInventoryItems(interaction) != null
        fun getItemDisplayesUUID(interaction: Interaction) = Api.compactBase64GetContainerValue(interaction, KEY_ITEM_DISPLAYS_UUID, arrayListOf<UUID>()::class.java)
        fun setItemDisplayesUUID(interaction: Interaction, uuids: ArrayList<UUID>) = Api.compactBase64SetContainerValue(interaction, KEY_ITEM_DISPLAYS_UUID, uuids)

        fun getInventoryItems(itemStack: ItemStack) = Api.compactBase64GetContainerValue(itemStack, KEY_INVENTORY, arrayListOf<ItemStack?>()::class.java)
        fun setInventoryItemsAndSetCountInLore(itemStack: ItemStack, inventoryItems: ArrayList<ItemStack?>) {
            val meta = itemStack.itemMeta

            meta.lore = arrayListOf(
                getString("deckCards.description") + inventoryItems.count { it != null }
            )

            itemStack.itemMeta = meta

            Api.compactBase64SetContainerValue(itemStack, KEY_INVENTORY, inventoryItems)
        }
        fun isDeckCards(itemStack: ItemStack) = getInventoryItems(itemStack) != null
    }
}