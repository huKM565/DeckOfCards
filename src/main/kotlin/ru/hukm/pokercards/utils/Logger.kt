package ru.hukm.pokercards.utils

import org.bukkit.Bukkit

class Logger {
    companion object {
        private const val SUFFIX = "[DeckCards] "
        private val logger = Bukkit.getLogger()

        fun warn(message: String) {
            logger.warning(SUFFIX + message)
        }

        fun info(message: String) {
            logger.info(SUFFIX + message)
        }
    }
}