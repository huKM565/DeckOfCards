package ru.hukm.pokercards.events

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.Cancellable
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.ItemsManager

class DeckOfCardsEvent {

    companion object {
        fun init(inventoryView: InventoryView, rawSlot: Int, event: Cancellable, isMove: Boolean) {
            if(rawSlot == -999) return

            val inventory = inventoryView.topInventory
            val inventoryHolder = inventory.holder
            val item = inventoryView.getItem(rawSlot)
            val player = inventoryView.player as Player

            if(inventoryHolder is DeckCardsItem.Menu.First || inventoryHolder is DeckCardsItem.Menu.Second) {
                val type = ItemsManager.getType(item)
                if(item != null) {
                    if(type != null && type != "card") event.isCancelled = true

                    val deckCardsItem = DeckCardsItem.getFromHands(player)!!

                    when (type) {
                        "nextPage" -> {
                            player.openInventory(DeckCardsItem.Menu().getInventory(deckCardsItem, DeckCardsItem.Menu.Second())!!)
                            return
                        }
                        "previousPage" -> {
                            player.openInventory(DeckCardsItem.Menu().getInventory(deckCardsItem, DeckCardsItem.Menu.First())!!)
                            return
                        }
                    }
                }

                val slotToMove = if(isMove) getSlotToMove(rawSlot, inventoryView, item) else rawSlot
                if(slotToMove < 54 && type == null && inventoryView.cursor.type == Material.AIR) event.isCancelled = true

                Bukkit.getScheduler().runTaskLater(PokerCards.instance, Runnable {
                    delayPlayerInventoryClickEvent(inventoryView, slotToMove, player)
                }, 1)
            }
        }

        private fun getSlotToMove(rawSlot: Int, inventoryView: InventoryView, duplicateItem: ItemStack?): Int {
            val range = if(rawSlot < 54) (inventoryView.countSlots() - 6) downTo 54 else 0..53

            for(i in range) {
                val item = inventoryView.getItem(i)
                if(item != null && duplicateItem != null && Api.equalDisplayName(duplicateItem, item.itemMeta.displayName)) return i
            }

            for(i in range) {
                inventoryView.getItem(i) ?: return i
            }

            return -1
        }

        private fun delayPlayerInventoryClickEvent(inventoryView: InventoryView, slotToMove: Int, player: Player) {
            val inventory = inventoryView.topInventory
            val item = inventoryView.getItem(slotToMove)

            DeckCardsItem.getFromHands(player)?.let {
                if(item != null) {
                    if(ItemsManager.getType(item).equals("card")) {
                        DeckCardsItem.Menu.updateDeckCardsInventory(inventory, it)
                        DeckCardsItem.playMoveCardSoundAround(player)
                    }

                    if(DeckCardsItem.Menu.isMenu(inventory)) {
                        if(ItemsManager.getType(item) == "randomizeCards") {
                            val randomItems = DeckOfCardsContainer.getInventoryItems(it)!!.shuffled() as MutableList

                            for(i in randomItems.indices) {
                                try {
                                    randomItems[i] ?: randomItems.removeAt(i)
                                }catch (ignored: IndexOutOfBoundsException) {
                                    break
                                }
                            }

                            DeckOfCardsContainer.setInventoryItemsAndSetCountInLore(it, randomItems as ArrayList<ItemStack?>)
                            player.openInventory(DeckCardsItem.Menu().getInventory(it, inventory.holder!!)!!)

                            DeckCardsItem.playRandomizeCardsSoundAround(player)
                        }
                    }
                }
            }
        }
    }
}