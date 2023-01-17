package gg.scala.bedwars.game.scoreboard

import gg.scala.bedwars.game.ScalaBedwarsGameEngine
import gg.scala.bedwars.game.generator.tier.BedwarsItemGeneratorTierIncrementer
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.player.scoreboard.CgsGameScoreboardRenderer
import gg.scala.cgs.common.runnable.state.StartingStateRunnable
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.teams.CgsGameTeamService
import gg.scala.lemon.Lemon
import gg.scala.lemon.LemonConstants
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.entity.Player
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
object ScalaBedwarsScoreboard : CgsGameScoreboardRenderer
{
    private val dateFormat =
        SimpleDateFormat("MM/dd/yyyy")
            .format(
                Date()
            )
            .replace(
                "20", ""
            )
            .replace(
                "0", ""
            )

    private val serverId = Lemon.instance.settings
        .id.replace("bedwars", "mg")

    override fun getTitle() = "${CC.B_PRI}Bedwars"

    override fun render(lines: LinkedList<String>, player: Player, state: CgsGameState)
    {
        lines.add("${CC.GRAY}$dateFormat ${CC.D_GRAY}$serverId")
        lines.add("")

        if (state == CgsGameState.WAITING || state == CgsGameState.STARTING)
        {
            lines.add(
                "Map: ${CC.PRI}${
                    CgsGameEngine.INSTANCE.gameArena!!.getName()
                }"
            )
            lines.add(
                "Players: ${CC.PRI}${
                    Bukkit.getOnlinePlayers().size
                }/${
                    Bukkit.getMaxPlayers()
                }"
            )

            lines.add("")

            lines.add(
                if (state == CgsGameState.WAITING)
                {
                    "Waiting..."
                } else
                {
                    "Starting in ${CC.GREEN}${StartingStateRunnable.PRE_START_TIME}s"
                }
            )

            lines.add("")
            lines.add(
                "Mode: ${CC.PRI}${
                    CgsGameEngine.INSTANCE.gameMode.getName()
                }"
            )
            lines.add(
                "Version: ${CC.GRAY}${
                    CgsGameEngine.INSTANCE.gameInfo.gameVersion
                }"
            )
        } else if (state.isAfter(CgsGameState.STARTED))
        {
            val cgsGamePlayer = CgsPlayerHandler
                .find(player)
                ?: return

            val statistics = ScalaBedwarsGameEngine.INSTANCE
                .getStatistics(cgsGamePlayer)

            val team = CgsGameTeamService
                .getTeamOf(player)!! as BedwarsCgsGameTeam

            lines.add("${
                BedwarsItemGeneratorTierIncrementer
                    .formatted(colored = false)
            }${CC.WHITE} in ${CC.GREEN}${
                TimeUtil.formatIntoMMSS(
                    BedwarsItemGeneratorTierIncrementer.countdown
                )
            }${CC.WHITE}")

            lines.add("")

            CgsGameTeamService.teams
                .map {
                    it.value as BedwarsCgsGameTeam
                }
                .forEach {
                    lines.add(
                        "${it.color}${CC.BOLD}${
                            if (it.color == ChatColor.LIGHT_PURPLE)
                                "P" else it.color.name.substring(0, 1)
                        } ${CC.WHITE}${ChatColor.stripColor(it.name)}: ${
                            if (it.bedDestroyed)
                            {
                                if (it.alive.isEmpty()) CC.RED + "✘"
                                else CC.GREEN + it.alive.size
                            } else CC.GREEN + "✔"
                        }${
                            if (team == it) "${CC.GRAY} (You)" else ""
                        }"
                    )
                }

            if (ScalaBedwarsGameEngine.INSTANCE.gameMode.getMaxTeams() <= 4)
            {
                lines.add("")
                lines.add("Kills: " + CC.PRI + statistics.gameKills.value)
                lines.add("Final Kills: " + CC.PRI + statistics.gameFinalKills.value)
                lines.add("Beds Broken: " + CC.PRI + statistics.gameBedsBroken.value)
            }
        }

        lines.add("")
        lines.add("${CC.GRAY}${LemonConstants.WEB_LINK}")
    }
}
