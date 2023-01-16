package gg.scala.bedwars.game.death

import gg.scala.bedwars.game.armor.BedwarsArmorService
import gg.scala.bedwars.game.loadout.BedwarsLoadoutService
import gg.scala.bedwars.game.tool.BedwarsToolService
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsSpectatorHandler
import gg.scala.cgs.common.teams.CgsGameTeamService
import net.evilblock.cubed.util.CC
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import org.bukkit.entity.Player
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable
import java.time.Duration

/**
 * @author GrowlyX
 * @since 5/26/2022
 */
class BedwarsRespawnRunnable(
    private val player: Player
) : BukkitRunnable()
{
    private var tick = 6

    init {
        player.setMetadata("respawning", FixedMetadataValue(CgsGameEngine.INSTANCE.plugin, true))
    }

    override fun run()
    {
        if (!this.player.isOnline)
        {
            this.cancel()
            return
        }

        val audience = CgsGameEngine.INSTANCE
            .audience.player(this.player)

        BedwarsToolService.decreaseType(this.player.uniqueId)

        when (this.tick)
        {
            6 ->
            {
                CgsSpectatorHandler.setSpectator(this.player)

                this.player.teleport(
                    CgsGameEngine.INSTANCE.gameArena.getPreLobbyLocation()
                )
            }
            5, 4, 3, 2, 1 ->
            {
                audience.showTitle(
                    Title.title(
                        LegacyComponentSerializer.legacySection()
                            .deserialize(
                                "${CC.B_RED}YOU DIED"
                            ),
                        LegacyComponentSerializer.legacySection()
                            .deserialize(
                                "${CC.WHITE}You will respawn in ${CC.GREEN}$tick${CC.WHITE} seconds!"
                            ),
                        Title.Times.times(
                            Duration.ofMillis(0L),
                            Duration.ofMillis(1000L),
                            Duration.ofMillis(0L),
                        )
                    )
                )

                this.player.sendMessage(
                    "${CC.SEC}You will respawn in ${CC.GREEN}$tick${CC.SEC} seconds."
                )
            }
            0 ->
            {
                val team = CgsGameTeamService
                    .getTeamOf(this.player)
                    ?: return this.cancel()

                val bedwarsTeam =
                    team as BedwarsCgsGameTeam

                CgsSpectatorHandler
                    .removeSpectator(this.player)

                bedwarsTeam.spawnPoint?.apply {
                    player.teleport(this)
                }

                audience.showTitle(
                    Title.title(
                        LegacyComponentSerializer.legacySection()
                            .deserialize(
                                "${CC.B_GREEN}RESPAWNED"
                            ),
                        LegacyComponentSerializer.legacySection()
                            .deserialize(
                                "${CC.WHITE}You have respawned!"
                            ),
                        Title.Times.times(
                            Duration.ofMillis(0L),
                            Duration.ofMillis(500L),
                            Duration.ofMillis(250L)
                        )
                    )
                )

                BedwarsArmorService.applyArmor(this.player)
                BedwarsLoadoutService.applyLoadout(this.player)
                BedwarsToolService.applyTools(this.player)

                player.removeMetadata("respawning", CgsGameEngine.INSTANCE.plugin)
                this.player.sendMessage("${CC.GREEN}You have respawned!")

                this.cancel()
            }
        }

        this.tick--
    }
}
