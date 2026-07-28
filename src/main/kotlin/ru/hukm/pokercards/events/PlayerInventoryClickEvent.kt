package ru.hukm.pokercards.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager


class PlayerInventoryClickEvent: Listener {

    @EventHandler
    fun onPlayerInventoryClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) DeckOfCardsEvent.init(event.view, event.rawSlot, event, true)
        else DeckOfCardsEvent.init(event.view, event.rawSlot, event, false)

        if (DeckCardsItem.Menu.isMenu(event.view.topInventory)
            && event.action.name.startsWith("DROP")
            && event.rawSlot < event.view.topInventory.size) {
            val dropped = if (event.action.name.endsWith("CURSOR")) event.cursor else event.currentItem
            if (dropped != null && ItemsManager.getType(dropped) == "card") {
                DeckOfCardsEvent.handleDrop(event.view, player)
            }
        }

        val item = event.currentItem ?: return

        if(DeckOfCardsContainer.getInventoryItems(item) != null && DeckCardsItem.Menu.isMenu(player.openInventory.topInventory)) {
            event.isCancelled = true
        }
    }

}