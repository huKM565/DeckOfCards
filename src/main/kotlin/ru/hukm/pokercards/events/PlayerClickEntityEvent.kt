package ru.hukm.pokercards.events

import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemFrame
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.entity.DeckCardsEntity
import ru.hukm.pokercards.entity.DeckCardsEntity.Companion.setCountCardsInName
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString
import ru.hukm.pokercards.utils.configuration.MainConfig

class PlayerClickEntityEvent: Listener {

    private val maxDeckCards = DeckCardsItem.Menu.slotsForItems.size * 2

    private fun addCardToDeck(items: ArrayList<ItemStack?>, card: ItemStack): Boolean {
        val free = items.indexOfFirst { it == null }
        if (free != -1) {
            items[free] = card
            return true
        }
        if (items.size < maxDeckCards) {
            items.add(card)
            return true
        }
        return false
    }

    @EventHandler
    fun onPlayerClickEvent(event: PlayerInteractEntityEvent) {
        val player = event.player
        val clickedEntity = event.rightClicked

        if(clickedEntity is Interaction && DeckOfCardsContainer.isDeckCards(clickedEntity)) {
            if (player.isSneaking) {
                val r = MainConfig.getDeckCollectCardsRadius()
                val cards = clickedEntity.getNearbyEntities(r, r, r)
                    .filterIsInstance<ItemFrame>()
                    .filter { ItemsManager.getType(it.item) == "card" }

                val items = DeckOfCardsContainer.getInventoryItems(clickedEntity)

                var collected = false
                for (frame in cards) {
                    if (!addCardToDeck(items, frame.item)) break
                    DeckCardsEntity.startTakeCardAnimation(clickedEntity, frame)
                    frame.setItem(null)
                    frame.remove()
                    collected = true
                }

                if (collected) {
                    DeckOfCardsContainer.setInventoryItems(clickedEntity, items)
                    setCountCardsInName(clickedEntity)
                    player.swingMainHand()
                }
                return
            }

            val contents = DeckOfCardsContainer.getInventoryItems(clickedEntity)

            if(Api.isFullInventory(player.inventory)) {
                player.sendMessage(getString("deckCardsEntity.takeCard.isFullInventory"))
            }else {
                val item = deacreaseLastAndSetItems(contents, clickedEntity)
                if(item != null) Api.giveItem(item, player)

                setCountCardsInName(clickedEntity)

                if(DeckOfCardsContainer.getInventoryItems(clickedEntity).count {it != null} != 0) {
                    DeckCardsEntity.startTakeCardAnimation(player, clickedEntity)
                    player.swingMainHand()
                }
            }
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