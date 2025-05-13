package ru.hukm.pokercards.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryDragEvent

class PlayerDragInventoryEvent: Listener {

    @EventHandler
    fun onPlayerDragInventoryEvent(event: InventoryDragEvent) {
        DeckOfCardsEvent.init(event.view, event.rawSlots.first(), event, false)
    }
}