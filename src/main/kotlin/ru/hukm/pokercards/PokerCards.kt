package ru.hukm.pokercards

import org.bukkit.plugin.java.JavaPlugin
import ru.hukm.pokercards.crafts.DeckCardsCraft
import ru.hukm.pokercards.events.*
import ru.hukm.pokercards.items.DeckCardsItem
import ru.hukm.pokercards.utils.CustomMapRender
import ru.hukm.pokercards.utils.configuration.MainConfig
import ru.hukm.pokercards.utils.configuration.Localization

class PokerCards : JavaPlugin() {

    companion object {
        lateinit var instance: PokerCards
            private set
    }

    override fun onEnable() {
        instance = this

        MainConfig.init()
        Localization.init("localization_${MainConfig.getLanguage()}.yml")

        CustomMapRender.createMapViews()
        DeckCardsItem.addCards()
        DeckCardsCraft.init()

        server.pluginManager.registerEvents(PlayerClickEvent(), this)
        server.pluginManager.registerEvents(PlayerInventoryClickEvent(), this)
        server.pluginManager.registerEvents(PlayerClickEntityEvent(), this)
        server.pluginManager.registerEvents(PlayerHurtEntityEvent(), this)
        server.pluginManager.registerEvents(PlayerDragInventoryEvent(), this)
        server.pluginManager.registerEvents(HangingBreakEvent(), this)
    }

}