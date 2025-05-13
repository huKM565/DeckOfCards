package ru.hukm.pokercards.items.deckInventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.hukm.pokercards.utils.ItemsManager
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString

class PageScrollingItems {
    companion object {
        private fun get(title: String): ItemStack {
            val item = ItemStack(Material.REDSTONE)
            val meta = item.itemMeta

            meta.setDisplayName(title)

            item.setItemMeta(meta)

            return item
        }

        fun getNextPageItem(): ItemStack {
            val item = get(getString("deckCards.inventory.nextPage.title"))
            ItemsManager.setType(item, "nextPage")
            return item
        }

        fun getPreviousPageItem(): ItemStack {
            val item = get(getString("deckCards.inventory.previousPage.title"))
            ItemsManager.setType(item, "previousPage")
            return item
        }
    }
}