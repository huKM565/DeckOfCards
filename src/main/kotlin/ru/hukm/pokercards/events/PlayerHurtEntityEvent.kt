package ru.hukm.pokercards.events

import org.bukkit.Bukkit
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager

class PlayerHurtEntityEvent: Listener {

    @EventHandler
    fun onPlayerHurtEntityEvent(event: EntityDamageByEntityEvent) {
        val hurtedEntity = event.entity
        val damager = event.damager

        if(damager is Player) {
            if(hurtedEntity is Interaction && damager.isSneaking) {
                DeckOfCardsContainer.getItemDisplayesUUID(hurtedEntity)?.forEach {
                    Bukkit.getEntity(it)?.remove()
                }

                hurtedEntity.world.dropItemNaturally(hurtedEntity.location, DeckCardsItem.get(DeckOfCardsContainer.getInventoryItems(hurtedEntity)))
                hurtedEntity.remove()
            }
        }
    }
}