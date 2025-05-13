package ru.hukm.pokercards.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.map.MapCanvas
import org.bukkit.map.MapPalette
import org.bukkit.map.MapRenderer
import org.bukkit.map.MapView
import ru.hukm.pokercards.PokerCards
import ru.hukm.pokercards.items.DeckCardsItem.Companion.ranks
import ru.hukm.pokercards.items.DeckCardsItem.Companion.suits
import javax.imageio.ImageIO

class CustomMapRender(private val imagePath: String): MapRenderer() {
    companion object {
        val mapViews = arrayListOf<MapView>()

        fun createMapViews() {
            val ids = getIds()

            var i = 0
            for(suit in suits) {
                for(rank in ranks) {
                    if(rank == "joker" && suit != "hearts" && suit != "clubs") continue
                    val mapView = Bukkit.getMap(ids[i])!!

                    mapView.renderers.forEach { mapView.removeRenderer(it) }
                    mapView.addRenderer(CustomMapRender("card/$suit/$rank.png"))

                    i += 1

                    mapViews.add(mapView)
                }
            }
        }

        private fun getIds(): ArrayList<Int> {
            var ids = arrayListOf<Int>()
            if(WorldContainer.hasMapsIds()) {
                ids = WorldContainer.getMapsIds()
            }else {
                for(suit in suits) {
                    for(rank in ranks) {
                        val mapView = Bukkit.createMap(Bukkit.getWorlds()[0])
                        ids.add(mapView.id)
                    }
                }
                WorldContainer.setMapsIds(ids)
            }
            return ids
        }
    }

    private var render = false

    override fun render(map: MapView, canvas: MapCanvas, player: Player) {
        if(render) return

        val image = PokerCards.instance.getResource(imagePath)?.let { ImageIO.read(it) } ?: throw IllegalArgumentException("Card image '$imagePath' not found in plugin. Please, download plugin again")

        canvas.drawImage(0, 0, MapPalette.resizeImage(image))

        render = true
    }
}

