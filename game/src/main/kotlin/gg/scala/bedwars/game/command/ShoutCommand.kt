package gg.scala.bedwars.game.command

import com.google.common.cache.CacheBuilder
import gg.scala.bedwars.game.shop.BedwarsShopMenu
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.commons.acf.annotation.CommandAlias
import gg.scala.commons.acf.annotation.CommandPermission
import gg.scala.commons.annotations.commands.AutoRegister
import gg.scala.commons.command.ScalaCommand
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.TimeUnit

/**
 * @author GrowlyX
 * @since 5/27/2022
 */
@AutoRegister
object ShoutCommand : ScalaCommand()
{
    private val lastShouts = CacheBuilder.newBuilder()
        .expireAfterAccess(1L, TimeUnit.MINUTES)
        .build<UUID, Long>()

    @CommandAlias("shout")
    fun onShout(
        player: Player,
        message: String
    )
    {
        val lastShout = this.lastShouts.getIfPresent(player.uniqueId)

        if (player.hasMetadata("spectator"))
        {
            player.sendMessage("${CC.RED}You cannot shout as a spectator!")
            return
        }

        if (CgsGameEngine.INSTANCE.gameState != CgsGameState.STARTED)
        {
            player.sendMessage("${CC.RED}You cannot shout when the game has not yet started!")
            return
        }

        if (lastShout != null)
        {
            player.sendMessage("${CC.RED}You must wait ${CC.BOLD}${
                TimeUtil.formatIntoDetailedString(
                    ((System.currentTimeMillis() - lastShout) / 1000L).toInt()
                )
            }${CC.RED} before shouting again.")
            return
        }

        CgsGameEngine.INSTANCE.sendMessage(
            "${CC.GREEN}[SHOUT] ${player.displayName}${CC.WHITE}: $message"
        )

        this.lastShouts.put(
            player.uniqueId, System.currentTimeMillis()
        )
    }
}
