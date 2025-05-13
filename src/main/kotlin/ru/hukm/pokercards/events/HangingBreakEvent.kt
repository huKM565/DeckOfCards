package ru.hukm.pokercards.events

import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.hanging.HangingBreakEvent
import ru.hukm.pokercards.utils.ItemsManager

class HangingBreakEvent: Listener {

    @EventHandler
    fun onHangingBreakEvent(event: HangingBreakEvent) {
        val entity = event.entity
        if(entity is ItemFrame && ItemsManager.getType(entity.item).equals("card")) {
            entity.world.dropItemNaturally(entity.location, entity.item)
            entity.remove()
            event.isCancelled = true
        }
    }

}