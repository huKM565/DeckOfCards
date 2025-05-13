package ru.hukm.pokercards.items

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.MapMeta
import org.bukkit.persistence.PersistentDataType
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.PokerCards.Companion.instance
import ru.hukm.pokercards.items.DeckCardsItem.Companion.ranks
import ru.hukm.pokercards.items.DeckCardsItem.Companion.suits
import ru.hukm.pokercards.utils.CustomMapRender.Companion.mapViews
import ru.hukm.pokercards.utils.ItemsManager
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString


class CardItem {
    companion object {
        fun getAllCards(): ArrayList<ItemStack?> {
            val items = arrayListOf<ItemStack?>()

            var i = 0

            for(suit in suits) {
                for(rank in ranks) {
                    if(rank == "joker" && suit != "hearts" && suit != "clubs") continue

                    val map = ItemStack(Material.FILLED_MAP)
                    val meta = map.itemMeta as MapMeta

                    meta.mapView = mapViews[i]
                    meta.setDisplayName(getString("card.$suit.suffix") + getString("card.$rank"))

                    map.setItemMeta(meta)

                    items.add(ItemsManager.setType(map, "card"))

                    i += 1
                }
            }

            return items
        }
    }
}