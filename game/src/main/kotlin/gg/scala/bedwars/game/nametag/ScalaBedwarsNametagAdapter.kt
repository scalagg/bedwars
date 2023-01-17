package gg.scala.bedwars.game.nametag

import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.player.CgsGamePlayer
import gg.scala.cgs.common.player.handler.CgsSpectatorHandler
import gg.scala.cgs.common.player.nametag.CgsGameNametagAdapter
import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.nametag.NametagInfo
import net.evilblock.cubed.nametag.NametagProvider
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object ScalaBedwarsNametagAdapter : CgsGameNametagAdapter
{
    override fun computeNametag(
        viewer: CgsGamePlayer,
        target: CgsGamePlayer
    ): NametagInfo
    {
        val targetPlayer = Bukkit.getPlayer(viewer.uniqueId)
        val team = (CgsGameTeamService.getTeamOf(targetPlayer) as BedwarsCgsGameTeam?)

        if (team == null || targetPlayer.hasMetadata("spectator"))
        {
            return NametagProvider.createNametag(CC.GRAY, "", "z")
        }

        return NametagProvider.createNametag(
            "${team.color}${CC.BOLD}${
                if (team.color == ChatColor.LIGHT_PURPLE)
                    "P" else team.color.name.substring(0, 1)
            } ${team.color}",
            ""
        )
    }
}
