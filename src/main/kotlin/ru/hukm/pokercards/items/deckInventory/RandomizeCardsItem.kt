package ru.hukm.pokercards.items.deckInventory

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import ru.hukm.pokercards.utils.ItemsManager
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString

class RandomizeCardsItem {
    companion object {
        fun get(): ItemStack {
            val item = ItemStack(Material.DRAGON_EGG)
            val meta = item.itemMeta

            meta.setDisplayName(getString("deckCards.inventory.randomizeCards.title"))

            item.setItemMeta(meta)

            ItemsManager.setType(item, "randomizeCards")

            return item
        }
    }
}