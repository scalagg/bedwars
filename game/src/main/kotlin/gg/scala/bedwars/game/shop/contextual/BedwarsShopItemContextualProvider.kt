package gg.scala.bedwars.game.shop.contextual

import gg.scala.bedwars.game.shop.BedwarsShopItem
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/27/2022
 */
interface BedwarsShopItemContextualProvider
{
    fun provide(item: BedwarsShopItem, player: Player)

    fun allowed(player: Player, item: BedwarsShopItem): Boolean
    {
        return true
    }
}
