package ru.hukm.pokercards.events

import org.bukkit.Bukkit
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.entity.DeckCardsEntity
import ru.hukm.pokercards.entity.DeckCardsEntity.Companion.setCountCardsInName
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString

class PlayerClickEntityEvent: Listener {

    @EventHandler
    fun onPlayerClickEvent(event: PlayerInteractEntityEvent) {
        val player = event.player
        val clickedEntity = event.rightClicked

        if(clickedEntity is Interaction && DeckOfCardsContainer.isDeckCards(clickedEntity)) {
            val contents = DeckOfCardsContainer.getInventoryItems(clickedEntity)

            if(Api.isFullInventory(player.inventory)) {
                player.sendMessage(getString("deckCardsEntity.takeCard.isFullInventory"))
            }else {
                val item = deacreaseLastAndSetItems(contents, clickedEntity)
                if(item != null) Api.giveItem(item, player)
            }

            setCountCardsInName(clickedEntity)

            if(DeckOfCardsContainer.getInventoryItems(clickedEntity).size != 0) {
                DeckCardsEntity.startTakeCardAnimation(player, clickedEntity)
                player.swingMainHand()
            }
        }

        if (clickedEntity is ItemFrame && ItemsManager.getType(clickedEntity.item) == "card") {
            Bukkit.getScheduler().runTaskLater(PokerCards.instance, Runnable {
                clickedEntity.remove()
            }, 1)
        }
    }

    private fun deacreaseLastAndSetItems(array: ArrayList<ItemStack?>, interaction: Interaction): ItemStack? {
        val reversedArr = array.reversed().toMutableList()
        for(itemIndex in reversedArr.indices) {
            val item = reversedArr[itemIndex]
            if(item != null) {
                if(item.amount == 1) reversedArr[itemIndex] = null
                else item.amount -= 1

                DeckOfCardsContainer.setInventoryItems(interaction, getArrayList(reversedArr.reversed()))
                return item
            }
        }

        return null
    }

    private fun getArrayList(collection: Collection<ItemStack?>): ArrayList<ItemStack?> {
        val array = arrayListOf<ItemStack?>()

        for (item in collection) {
            array.add(item)
        }

        return array
    }
}