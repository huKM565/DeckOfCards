package ru.hukm.pokercards.utils.configuration

import org.bukkit.configuration.file.YamlConfiguration
import ru.hukm.pokercards.PokerCards.Companion.instance
import ru.hukm.pokercards.utils.Logger
import java.io.File

class Localization {

    companion object {

        private lateinit var messages: YamlConfiguration

        fun init(fileName: String) {
            val file = File(instance.dataFolder, fileName)

            if (!file.exists()) {
                try {
                    instance.saveResource(fileName, false)
                }catch (err: IllegalArgumentException) {
                    Logger.warn("Cannot find $fileName in folder, set \"en\" localization")
                    MainConfig.setLanguage("en")
                    return init("localization_en.yml")
                }

            }

            messages = YamlConfiguration.loadConfiguration(file)
        }

        fun getString(path: String): String {
            return messages.getString(path) ?: throw IllegalArgumentException("Cannot find $path in localization file. To restore the localization file, delete it")
        }

    }

}