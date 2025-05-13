package ru.hukm.pokercards.events

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer


class PlayerInventoryClickEvent: Listener {

    @EventHandler
    fun onPlayerInventoryClickEvent(event: InventoryClickEvent) {
        val player = event.whoClicked as Player

        if (event.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) DeckOfCardsEvent.init(event.view, event.rawSlot, event, true)
        else DeckOfCardsEvent.init(event.view, event.rawSlot, event, false)

        val item = event.currentItem ?: return

        if(DeckOfCardsContainer.getInventoryItems(item) != null && DeckCardsItem.Menu.isMenu(player.openInventory.topInventory)) {
            event.isCancelled = true
        }
    }

}