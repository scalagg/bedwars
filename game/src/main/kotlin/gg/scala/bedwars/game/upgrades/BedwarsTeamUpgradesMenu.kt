package gg.scala.bedwars.game.upgrades

import gg.scala.bedwars.game.shop.BedwarsShopCurrency
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.Menu
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

/**
 * @author GrowlyX
 * @since 5/28/2022
 */
class BedwarsTeamUpgradesMenu : Menu(
    "Team Upgrades & Traps"
)
{
    init
    {
        updateAfterClick = true
        placeholdBorders = true
    }

    private val mode = CgsGameEngine.INSTANCE.gameMode
    private val gameMode = if (isDuosOrSolos()) "eight" else "four"

    private fun isDuosOrSolos(): Boolean
    {
        return this.mode.isSoloGame() || this.mode.getTeamSize() == 2
    }

    override fun size(
        buttons: Map<Int, Button>
    ) = 36

    override fun getButtons(
        player: Player
    ): Map<Int, Button>
    {
        val buttons =
            mutableMapOf<Int, Button>()

        val team = CgsGameTeamService
            .getTeamOf(player)!! as BedwarsCgsGameTeam

        val tracker = Tracker.of(team)

        listOf(13, 13 + 9).forEach {
            buttons[it] = ItemBuilder
                .copyOf(
                    PaginatedMenu.PLACEHOLDER
                        .getButtonItem(player)
                )
                .name(
                    "${CC.D_GRAY}${
                        Constants.DOUBLE_ARROW_LEFT
                    } ${CC.GRAY}Upgrades"
                )
                .addToLore(
                    "${CC.GRAY}Traps ${CC.D_GRAY}${
                        Constants.DOUBLE_ARROW_RIGHT
                    }"
                )
                .toButton()
        }

        val diamonds = player
            .inventory.contents
            .filterNotNull()
            .filter {
                it.type == Material.DIAMOND
            }
            .sumOf {
                it.amount
            }

        for (upgrade in BedwarsTeamUpgrades.values())
        {
            val description = mutableListOf<String>()

            val tier = tracker
                .upgrades[upgrade] ?: 0

            if (upgrade.names.size == 1)
            {
                val price = upgrade
                    .costs[this.gameMode]!!
                    .values.first()

                description += "${CC.GRAY}Price: ${CC.AQUA}$price Diamond${
                    if (price == 1) "" else "s"
                }"
            } else
            {
                description += "${CC.GRAY}Viewing tiers:"

                for (entry in upgrade.names)
                {
                    val price = upgrade
                        .costs[this.gameMode]!![entry.key]

                    description += " ${CC.WHITE}- ${
                        if (tier >= entry.key)
                            CC.GREEN else CC.GRAY
                    }${entry.value}${CC.WHITE}: $price Diamond${
                        if (price == 1) "" else "s"
                    }"
                }
            }

            description += ""
            description += if (tier == upgrade.maxLevel)
            {
                "${CC.GREEN}${
                    if (upgrade.names.size == 1) "${CC.RED}You already own this!" else "${CC.RED}Already at max level!"
                }"
            } else
            {
                if (upgrade.names.size == 1)
                {
                    val price = upgrade
                        .costs[this.gameMode]!!
                        .values.first()

                    if (diamonds < price)
                    {
                        "${CC.RED}You cannot afford this!"
                    } else
                    {
                        "${CC.GREEN}Click to purchase."
                    }
                } else
                {
                    val price = upgrade
                        .costs[this.gameMode]!![tier + 1]!!

                    if (diamonds < price)
                    {
                        "${CC.RED}You cannot afford this!"
                    } else
                    {
                        "${CC.GREEN}Click to upgrade."
                    }
                }
            }

            buttons[upgrade.position] = ItemBuilder
                .of(upgrade.item)
                .name("${CC.YELLOW}${upgrade.display}")
                .addFlags(
                    ItemFlag.HIDE_ATTRIBUTES,
                    ItemFlag.HIDE_ENCHANTS,
                    ItemFlag.HIDE_POTION_EFFECTS,
                    ItemFlag.HIDE_UNBREAKABLE,
                    ItemFlag.HIDE_PLACED_ON
                )
                .setLore(description)
                .toButton { _, _ ->
                    if (tier == upgrade.maxLevel)
                    {
                        player.sendMessage("${CC.RED}You cannot upgrade this any further!")
                        player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
                        return@toButton
                    }

                    val price = if (upgrade.names.size == 1)
                    {
                        upgrade
                            .costs[this.gameMode]!!
                            .values.first()
                    } else
                    {
                        upgrade
                            .costs[this.gameMode]!![tier + 1]!!
                    }

                    if (upgrade.names.size == 1)
                    {
                        if (diamonds < price)
                        {
                            player.sendMessage(
                                "${CC.RED}You need ${CC.BOLD}${price - diamonds}${CC.RED} more diamond${
                                    if (price - diamonds == 1) "" else "s"
                                } to purchase this item!"
                            )
                            player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
                            return@toButton
                        }
                    } else
                    {
                        if (diamonds < price)
                        {
                            player.sendMessage(
                                "${CC.RED}You need ${CC.BOLD}${price - diamonds}${CC.RED} more diamond${
                                    if (price - diamonds == 1) "" else "s"
                                } to purchase this item!"
                            )
                            player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
                            return@toButton
                        }
                    }

                    tracker.upgrades[upgrade] = tier + 1

                    BedwarsShopCurrency.DIAMOND.removeFrom
                        .invoke(player, price)

                    team.participants
                        .mapNotNull {
                            Bukkit.getPlayer(it)
                        }
                        .filter {
                            !it.hasMetadata("spectator")
                        }
                        .forEach {
                            upgrade.context.invoke(it)
                        }

                    player.sendMessage("${CC.GREEN}You purchased ${CC.YELLOW}${
                        upgrade.display
                    }${CC.GREEN} tier ${CC.GOLD}${tier + 1}${CC.GREEN}.")
                    player.playSound(player.location, Sound.NOTE_PLING, 0.5F, 1.0F)
                }
        }

        return buttons
    }
}
