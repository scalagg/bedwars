package gg.scala.bedwars.game.shop.contextual.providers

import gg.scala.bedwars.game.armor.BedwarsArmorService
import gg.scala.bedwars.game.armor.BedwarsArmorType
import gg.scala.bedwars.game.loadout.BedwarsLoadoutService
import gg.scala.bedwars.game.shop.BedwarsShopItem
import gg.scala.bedwars.game.shop.contextual.BedwarsShopItemContextualProvider
import net.evilblock.cubed.util.CC
import org.bukkit.Sound
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/27/2022
 */
object ArmorShopItemContextual : BedwarsShopItemContextualProvider
{
    private val mappings = mapOf(
        "Permanent Chainmail Armor" to BedwarsArmorType.CHAINMAIL,
        "Permanent Iron Armor" to BedwarsArmorType.IRON,
        "Permanent Diamond Armor" to BedwarsArmorType.DIAMOND
    )

    override fun provide(
        item: BedwarsShopItem, player: Player
    )
    {
        val mapping = mappings[item.name]!!

        BedwarsArmorService.updateType(
            player.uniqueId, mapping
        )

        BedwarsArmorService.applyArmor(player)
    }

    override fun allowed(
        player: Player, item: BedwarsShopItem
    ): Boolean
    {
        val armor = BedwarsArmorService
            .armor[player.uniqueId]
            ?: BedwarsArmorType.LEATHER

        val mapping = this.mappings[item.name]!!

        if (armor == mapping)
        {
            player.sendMessage(
                "${CC.RED}You already own this armor!"
            )
            player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
            return false
        }

        val purchasing = this.mappings
            .values.indexOf(mapping)

        val current = this.mappings
            .values.indexOf(armor)

        if (purchasing < current)
        {
            player.sendMessage(
                "${CC.RED}You already own better armor!"
            )
            player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
            return false
        }

        return true
    }
}
