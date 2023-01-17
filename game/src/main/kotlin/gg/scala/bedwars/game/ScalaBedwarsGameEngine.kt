package gg.scala.bedwars.game

import gg.scala.bedwars.game.nametag.ScalaBedwarsNametagAdapter
import gg.scala.bedwars.game.scoreboard.ScalaBedwarsScoreboard
import gg.scala.bedwars.game.visibility.ScalaBedwarsVisibilityAdapter
import gg.scala.bedwars.shared.BedwarsCgsStatistics
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.information.CgsGameGeneralInfo
import gg.scala.cgs.common.information.mode.CgsGameMode
import gg.scala.cgs.common.snapshot.CgsGameSnapshot
import gg.scala.cgs.common.teams.CgsGameTeam
import gg.scala.commons.ExtendedScalaPlugin

/**
 * @author GrowlyX
 * @since 5/21/2022
 */
class ScalaBedwarsGameEngine(
    plugin: ExtendedScalaPlugin,
    gameInfo: CgsGameGeneralInfo,
    gameMode: CgsGameMode
) : CgsGameEngine<BedwarsCgsStatistics>(
    plugin,
    gameInfo, gameMode,
    BedwarsCgsStatistics::class
)
{
    companion object
    {
        @JvmStatic
        lateinit var INSTANCE: ScalaBedwarsGameEngine
    }

    init
    {
        INSTANCE = this
    }

    override fun getScoreboardRenderer() = ScalaBedwarsScoreboard
    override fun getVisibilityAdapter() = ScalaBedwarsVisibilityAdapter
    override fun getNametagAdapter() = ScalaBedwarsNametagAdapter
    override fun getGameSnapshot(): CgsGameSnapshot
    {
        return object : CgsGameSnapshot
        {
            override fun getExtraInformation(): List<String>
            {
                val topKills = mutableListOf<String>()
/*

                val sortedKills = UhcMeetupListener.killsTracker
                    .entries.sortedByDescending { it.value }

                for (i in 0 until 3.coerceAtMost(sortedKills.size))
                {
                    val (key, value) = sortedKills[i]
                    topKills.add("  ${CC.YELLOW}#${i + 1} ${CC.GRAY}- ${CC.WHITE}${CubedCacheUtil.fetchName(key)}${CC.GRAY} - ${CC.PRI}$value")
                }
*/

                return topKills
            }
        }
    }

    override fun createTeam(id: Int): CgsGameTeam
    {
        return BedwarsCgsGameTeam(id)
    }
}
