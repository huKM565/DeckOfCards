package ru.hukm.pokercards.crafts

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import ru.hukm.pokercards.PokerCards.Companion.instance
import ru.hukm.pokercards.items.DeckCardsItem

class DeckCardsCraft {
    companion object {
        private val keyCraft = NamespacedKey(instance, "deck_cards")

        fun init() {
            val recipe = ShapedRecipe(keyCraft, DeckCardsItem.get())

            recipe.shape(
                "AAA",
                "BBB",
                "AAA"
            )

            recipe.setIngredient('A', ItemStack(Material.PAPER))
            recipe.setIngredient('B', ItemStack(Material.INK_SAC))

            Bukkit.addRecipe(recipe)
        }
    }
}