package ru.hukm.pokercards.events

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.entity.DeckCardsEntity
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager

class PlayerHurtEntityEvent: Listener {

    @EventHandler
    fun onPlayerHurtEntityEvent(event: EntityDamageByEntityEvent) {
        val hurtedEntity = event.entity
        val damager = event.damager
        if(damager is Player) {
            if(hurtedEntity is Interaction && damager.isSneaking && DeckOfCardsContainer.getInventoryItems(hurtedEntity) != null) {
                DeckOfCardsContainer.getItemDisplayesUUID(hurtedEntity)?.forEach {
                    Bukkit.getEntity(it)?.remove()
                }

                hurtedEntity.world.dropItemNaturally(hurtedEntity.location, DeckCardsItem.get(DeckOfCardsContainer.getInventoryItems(hurtedEntity)))
                hurtedEntity.remove()
            }

            if(hurtedEntity is ItemFrame && ItemsManager.getType(hurtedEntity.item) == "card") {
                DeckCardsEntity.startTakeCardAnimation(damager, hurtedEntity)
                Api.giveItem(hurtedEntity.item, damager)
                hurtedEntity.setItem(ItemStack(Material.AIR))
                hurtedEntity.remove()
            }
        }
    }
}