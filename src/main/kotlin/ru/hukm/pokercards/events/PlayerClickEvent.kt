package ru.hukm.pokercards.events

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.Container
import org.bukkit.entity.EntityType
import org.bukkit.entity.ItemFrame
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.entity.DeckCardsEntity
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.ItemsManager

class PlayerClickEvent: Listener{
    @EventHandler
    fun onPlayerClickEvent(event: PlayerInteractEvent) {
        val player = event.player

        val action = event.action
        val clickedBlock = event.clickedBlock

        if(action != Action.RIGHT_CLICK_AIR && action != Action.RIGHT_CLICK_BLOCK) return

        val item = event.item ?: return

        val deckCardsInventory = DeckCardsItem.Menu().getInventory(item, DeckCardsItem.Menu.First())

        if(action == Action.RIGHT_CLICK_AIR || (action == Action.RIGHT_CLICK_BLOCK && clickedBlock !is Container)) {
            if(deckCardsInventory != null) {
                if(!placeItemIfDeckOfCards(clickedBlock, player, item)) player.openInventory(deckCardsInventory)
                event.isCancelled = true
            }
            placeItemIfCard(clickedBlock, item, event.blockFace, event.interactionPoint)
        }
    }

    companion object {
        fun placeItemIfDeckOfCards(clickedBlock: Block?, player: Player, item: ItemStack): Boolean {
            if(clickedBlock?.type?.isSolid == true && player.isSneaking) {
                val deckCardsLocation = clickedBlock.location.add(0.0, 1.0, 0.0)
                DeckCardsEntity.spawn(deckCardsLocation, item)

                if(item.amount == 1) item.type = Material.AIR
                item.amount -= 1

                return true
            }

            return false
        }

        fun placeItemIfCard(clickedBlock: Block?, item: ItemStack, blockFace: BlockFace, point: Location?) {
            if(ItemsManager.getType(item).equals("card") && clickedBlock != null && point != null) {
                  val location = clickedBlock.location.clone().add(0.5, 0.5, 0.5)

                  when (blockFace) {
                      BlockFace.NORTH -> location.z -= 0.51
                      BlockFace.SOUTH -> location.z += 0.51
                      BlockFace.EAST -> location.x += 0.51
                      BlockFace.WEST -> location.x -= 0.51
                      BlockFace.UP -> location.y += 0.51
                      BlockFace.DOWN -> location.y -= 0.51
                  else -> return
                }

                val itemFrame = clickedBlock.world.spawnEntity(location, EntityType.ITEM_FRAME) as ItemFrame

                itemFrame.setFacingDirection(blockFace, true)
                itemFrame.setItem(item)

                item.amount -= 1

                Bukkit.getOnlinePlayers().forEach { player ->
                    player.hideEntity(PokerCards.instance, itemFrame)
                }

                Bukkit.getScheduler().runTaskLater(PokerCards.instance, Runnable {
                    Bukkit.getOnlinePlayers().forEach { player ->
                        player.showEntity(PokerCards.instance, itemFrame)
                    }
                }, 2)

            }
        }
    }
}