package gg.scala.bedwars.shared.gamemode

import gg.scala.cgs.common.information.mode.CgsGameMode
import gg.scala.bedwars.shared.arena.BedwarsArena
import gg.scala.bedwars.shared.arena.eight.TestEightArena
import gg.scala.bedwars.shared.arena.eight.TestTwoArena
import net.evilblock.cubed.util.CC
import org.bukkit.Material

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object BedwarsDuelsGameMode : CgsGameMode
{
    override fun getId() = "two_two"
    override fun getName() = "Duels"

    override fun getMaterial() = Pair(Material.BED, 0)

    override fun getDescription() = "${CC.GRAY}A duels game of Bedwars!"

    override fun getArenas() = listOf(
        TestTwoArena
    )

    override fun getTeamSize() = 1 // team size
    override fun getMaxTeams() = 2 // max teams
}
