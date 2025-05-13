package ru.hukm.pokercards.items

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import org.hukm.api.Api
import ru.hukm.pokercards.items.deckInventory.PageScrollingItems
import ru.hukm.pokercards.items.deckInventory.RandomizeCardsItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import ru.hukm.pokercards.utils.configuration.Localization.Companion.getString
import kotlin.collections.ArrayList

class DeckCardsItem {

    companion object {
        private var cardsItems: ArrayList<ItemStack?> = arrayListOf()

        val suits = listOf("hearts", "diamonds", "clubs", "spades")
        val ranks = listOf("joker", "ace", "king", "queen", "jack", "10", "9", "8", "7", "6", "5", "4", "3", "2")

        fun get(): ItemStack {
            addCards()

            val item = ItemStack(Material.MAP)
            val meta = item.itemMeta

            meta.setDisplayName(getString("deckCards.title"))

            item.setItemMeta(meta)

            DeckOfCardsContainer.setInventoryItemsAndSetCountInLore(item, cardsItems)
            return item
        }

        fun get(items: ArrayList<ItemStack?>): ItemStack {
            addCards()

            val item = ItemStack(Material.MAP)
            val meta = item.itemMeta

            meta.setDisplayName(getString("deckCards.title"))

            item.setItemMeta(meta)

            DeckOfCardsContainer.setInventoryItemsAndSetCountInLore(item, items)
            return item
        }

        fun addCards() {
            cardsItems = CardItem.getAllCards()
        }

        fun playRandomizeCardsSoundAround(player: Player) {
            player.world.playSound(player.location, Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 2.0f, 0.3f)
        }

        fun playMoveCardSoundAround(player: Player) {
            player.world.playSound(player.location, Sound.ENTITY_VILLAGER_WORK_LIBRARIAN, 2.0f, 1.0f)
        }

        fun getFromHands(player: Player): ItemStack?{
            val playerInventory = player.inventory

            val offItem = playerInventory.itemInOffHand
            val mainItem = playerInventory.itemInMainHand

            var item: ItemStack? = null

            if(DeckOfCardsContainer.isDeckCards(offItem) && !DeckOfCardsContainer.isDeckCards(mainItem)) item = offItem
            else if(DeckOfCardsContainer.isDeckCards(mainItem)) item = mainItem

            return item
        }
    }


    class Menu: InventoryHolder {
        class First : InventoryHolder {
            override fun getInventory(): Inventory {
                val inventory = Menu().getInventory(this)
                inventory.setItem(53, PageScrollingItems.getNextPageItem())
                return inventory
            }
        }

        class Second : InventoryHolder {
            override fun getInventory(): Inventory {
                val inventory = Menu().getInventory(this)
                inventory.setItem(45, PageScrollingItems.getPreviousPageItem())
                return inventory
            }
        }

        companion object {
            val slotsForItems = arrayListOf(
                10, 11, 12, 13, 14, 15, 16,
                19, 20, 21, 22, 23, 24, 25,
                28, 29, 30, 31, 32, 33, 34,
                37, 38, 39, 40, 41, 42, 43
            )

            fun isMenu(inventory: Inventory) = inventory.holder is First || inventory.holder is Second

            fun updateDeckCardsInventory(inventory: Inventory, deckOfCardsItem: ItemStack) {
                val items = subInventory(inventory)
                var itemInventory = DeckOfCardsContainer.getInventoryItems(deckOfCardsItem)!!

                if(inventory.holder is First) {
                    items.addAll(ArrayList(itemInventory.subList(slotsForItems.size, itemInventory.size)))
                    DeckOfCardsContainer.setInventoryItemsAndSetCountInLore(deckOfCardsItem, items)
                }else{
                    itemInventory = ArrayList(itemInventory.subList(0, slotsForItems.size))
                    itemInventory.addAll(items)
                    DeckOfCardsContainer.setInventoryItemsAndSetCountInLore(deckOfCardsItem, itemInventory)
                }
            }

            private fun subInventory(inventory: Inventory) = slotsForItems.map {inventory.getItem(it)} as ArrayList<ItemStack?>
        }

        override fun getInventory(): Inventory {
            val inventory = Bukkit.createInventory(this, 54, getString("deckCards.inventory.title"))
            setServiceItems(inventory)
            return inventory
        }

        fun getInventory(inventoryHolder: InventoryHolder): Inventory {
            val inventory = Bukkit.createInventory(inventoryHolder, 54, getString("deckCards.inventory.title"))
            setServiceItems(inventory)
            return inventory
        }

        private fun setServiceItems(inventory: Inventory) {
            inventory.setItem(49, RandomizeCardsItem.get())
            Api.setGrayGlassPane(inventory, 0, slotsForItems)
        }

        fun getInventory(itemStack: ItemStack, holder: InventoryHolder): Inventory? {
            val inventoryItems: ArrayList<ItemStack?> = DeckOfCardsContainer.getInventoryItems(itemStack) ?: return null
            val inventory = holder.inventory

            if (holder is First) addItems(inventoryItems, 0, slotsForItems.size, inventory)
            else addItems(inventoryItems, slotsForItems.size, inventoryItems.size, inventory)

            return inventory
        }

        private fun addItems(items: ArrayList<ItemStack?>, startIndex: Int, endIndex: Int, inventory: Inventory) {
            val missSlots = arrayListOf<Int>()

            ArrayList(items.subList(startIndex, endIndex)).forEachIndexed { index, it ->
                if (it == null) missSlots.add(slotsForItems[index])
                else addItemToNextFreeSlot(inventory, it, missSlots)
            }
        }

        private fun addItemToNextFreeSlot(inventory: Inventory, addItem: ItemStack, missSlots: ArrayList<Int>) {
            for (i in slotsForItems) {
                val item = inventory.getItem(i)
                if (item == null && !missSlots.contains(i)) {
                    inventory.setItem(i, addItem)
                    return
                }
            }
        }
    }

}