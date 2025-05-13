package ru.hukm.pokercards.utils.configuration

import org.bukkit.configuration.file.FileConfiguration

import ru.hukm.pokercards.PokerCards.Companion.instance
import ru.hukm.pokercards.utils.Logger

class MainConfig {

    companion object {

        lateinit var config: FileConfiguration
            private set

        fun init() {
            instance.saveDefaultConfig()
            config = instance.config
        }

        fun getLanguage(): String {
            val language = config.getString("language")
            if(language != null) return language
            else{
                setLanguage("en")
                Logger.warn("Language not found in config.yml, setting to default: en")
                return "en"
            }
        }

        fun setLanguage(language: String) {
            config.set("language", language)
            instance.saveConfig()
        }

    }

}