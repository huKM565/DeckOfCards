package ru.hukm.pokercards.entity

import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Location
import org.bukkit.entity.EntityType
import org.bukkit.entity.Interaction
import org.bukkit.entity.ItemDisplay
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitTask
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.items.DisplayCardItem
import ru.hukm.pokercards.utils.DeckOfCardsContainer
import java.util.UUID
import kotlin.concurrent.timerTask
import kotlin.math.abs
import kotlin.random.Random

class DeckCardsEntity {
    companion object {
        fun spawn(blockPos: Location, item: ItemStack) {
            blockPos.add(0.5, 0.0, 0.5)

            spawnInteraction(blockPos, item, spawnCards(blockPos.clone()))
        }

        private fun spawnInteraction(pos: Location, item: ItemStack, itemDisplaysUUID: ArrayList<UUID>) {
            val interaction = pos.world.spawnEntity(pos, EntityType.INTERACTION) as Interaction

            interaction.interactionWidth = 0.5F
            interaction.interactionHeight = 0.5F

            DeckOfCardsContainer.setInventoryItems(interaction, DeckOfCardsContainer.getInventoryItems(item)!!)
            DeckOfCardsContainer.setItemDisplayesUUID(interaction, itemDisplaysUUID)
            setCountCardsInName(interaction)
        }

        private fun spawnCards(pos: Location): ArrayList<UUID> {
            val arr = arrayListOf<UUID>()
            for(i in 0..5) {
                val itemDisplay = pos.world.spawnEntity(pos.add(0.0, 0.04, 0.0), EntityType.ITEM_DISPLAY) as ItemDisplay

                itemDisplay.setItemStack(DisplayCardItem.get())

                val transformation = itemDisplay.transformation
                transformation.scale.set(0.7)
                itemDisplay.transformation = transformation

                itemDisplay.setRotation(getRandomCardRotation(), 270f)
                arr.add(itemDisplay.uniqueId)
            }

            return arr;
        }

        fun setCountCardsInName(interaction: Interaction) {
            interaction.customName = ChatColor.GREEN.toString() + DeckOfCardsContainer.getInventoryItems(interaction).count{ it != null }.toString()
        }

        fun startTakeCardAnimation(whoTaken: Player, deckOfCardEntity: Interaction) {
            val itemDisplay = whoTaken.world.spawnEntity(deckOfCardEntity.location, EntityType.ITEM_DISPLAY) as ItemDisplay
            itemDisplay.setItemStack(DisplayCardItem.get())

            val transformation = itemDisplay.transformation
            transformation.scale.set(0.7)
            itemDisplay.transformation = transformation

            var speed = 1.0

            lateinit var task: BukkitTask
            task = Bukkit.getScheduler().runTaskTimer(PokerCards.instance, Runnable {
                try{
                    val itemDisplayLocation = itemDisplay.location
                    val deltaLocation = whoTaken.eyeLocation.subtract(itemDisplay.location).add(0.0, -0.4, 0.0)

                    itemDisplayLocation.add(deltaLocation.x / 5 * speed, deltaLocation.y / 5 * speed, deltaLocation.z / 5 * speed)
                    itemDisplayLocation.setDirection(deltaLocation.toVector())
                    itemDisplay.teleport(itemDisplayLocation)
                    itemDisplay.setRotation(itemDisplay.yaw, itemDisplay.pitch + 90)

                    speed += 0.1

                    if(whoTaken.eyeLocation.distance(itemDisplayLocation) < 0.6) {
                        itemDisplay.remove()
                        task.cancel()
                    }
                }catch (ex: Exception) {
                    itemDisplay.remove()
                    task.cancel()
                }
            }, 0, 1)
        }

        private fun getRandomCardRotation() = Random.nextInt(0, 360).toFloat()
    }
}