package dev.jsinco.lumacarnival.games

import dev.jsinco.lumacarnival.CarnivalMain
import dev.jsinco.lumacarnival.CarnivalToken
import dev.jsinco.lumacarnival.Util
import dev.jsinco.lumacarnival.games.externallistener.AppleBobbingEMFBlocker
import dev.jsinco.lumacarnival.obj.Cuboid
import dev.jsinco.lumaitems.api.LumaItemsAPI
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Item
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.player.PlayerFishEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.persistence.PersistentDataType


class AppleBobbingGame : GameTask() {

    companion object {
        private val key = NamespacedKey(CarnivalMain.instance, "apple-bobbing")
        val regularApple = ItemStack(Material.APPLE).apply {
            itemMeta = itemMeta?.apply {
                // Util.mm: MiniMessage.miniMessage().deserialize
                displayName(Util.mm("<b><gradient:#569BDD:#DD3535>Soaked</gradient><gradient:#DD3535:#DD3535> Apple</gradient></b>"))
                lore(Util.mml("<light_gray>A delicious apple soaked in water"))
                addEnchant(Enchantment.DURABILITY, 10, true)
                persistentDataContainer.set(key, PersistentDataType.BOOLEAN, true)
            }
        }
    }

    private val configSec = CarnivalMain.config.getConfigurationSection("apple-bobbing")
        ?: throw RuntimeException("Missing apple-bobbing section in config")
    private val area: Cuboid = configSec.getString("area")?.let { Util.getArea(it) }
        ?: throw RuntimeException("Invalid area in config")


    override fun enabled(): Boolean {
        return configSec.getBoolean("enabled")
    }

    override fun initializeGame() {
        val recipeKey = NamespacedKey(CarnivalMain.instance, "soaked-apple-recipe")
        if (Bukkit.getRecipe(recipeKey) != null) {
            Bukkit.removeRecipe(recipeKey)
        }
        val recipe = ShapedRecipe(recipeKey, CarnivalToken.CARNIVAL_TOKEN.apply { amount = 8 })
            .shape("AAA", "ABA", "AAA")
            .setIngredient('A', regularApple)
        Bukkit.addRecipe(recipe)

        // block emf
        if (Bukkit.getPluginManager().isPluginEnabled("EvenMoreFish")) {
            Bukkit.getPluginManager().registerEvents(AppleBobbingEMFBlocker(area.world), CarnivalMain.instance)
        }
    }


    @EventHandler
    fun onConsumeItem(event: PlayerItemConsumeEvent) {
        val item = event.item
        if (item.itemMeta?.persistentDataContainer?.has(key, PersistentDataType.BOOLEAN) == true) {
            Util.msg(event.player, "Delicious.")
            event.isCancelled = true
        }
    }

    @EventHandler(priority = EventPriority.NORMAL)
    fun playerFishApple(event: PlayerFishEvent) {
        // Check if the player is in the apple bobbing area
        if (!area.isInWithMarge(event.hook.location, 2.0)) {
            return
        }

        val player = event.player

        var fishingRod = player.inventory.itemInMainHand
        if (fishingRod.type != Material.FISHING_ROD) {
            fishingRod = player.inventory.itemInOffHand
        }
        if (fishingRod.type != Material.FISHING_ROD) {
            return
        }

        if (!LumaItemsAPI().isCustomItem(fishingRod, "carnivalfishingrod")) {
            event.isCancelled = true
            Util.msg(player, "You can only fish with the Carnival Fishing Rod!")
        }

        val item = event.caught as? Item ?: return
        item.itemStack = regularApple
    }
}