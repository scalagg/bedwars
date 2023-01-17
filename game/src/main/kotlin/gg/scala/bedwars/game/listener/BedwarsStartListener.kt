package gg.scala.bedwars.game.listener

import gg.scala.bedwars.game.ScalaBedwarsGame
import gg.scala.bedwars.game.armor.BedwarsArmorService
import gg.scala.bedwars.game.generator.impl.BedwarsDiamondItemGenerator
import gg.scala.bedwars.game.generator.impl.BedwarsEmeraldItemGenerator
import gg.scala.bedwars.game.generator.impl.BedwarsTeamItemGenerator
import gg.scala.bedwars.game.loadout.BedwarsLoadoutService
import gg.scala.bedwars.game.shop.categories.BedwarsShopBlockCategory.team
import gg.scala.bedwars.game.shop.npc.BedwarsShopNpcEntity
import gg.scala.bedwars.game.shop.npc.BedwarsTeamUpgradesNpcEntity
import gg.scala.bedwars.game.team.BedwarsTeamChatChannelComposite
import gg.scala.bedwars.game.upgrades.BedwarsTeamUpgradesTicker
import gg.scala.bedwars.game.upgrades.BedwarsTeamUpgradesTracker
import gg.scala.bedwars.game.upgrades.BedwarsTeamUpgradesTrackerService
import gg.scala.bedwars.shared.BedwarsCgsStatistics
import gg.scala.bedwars.shared.arena.BedwarsArena
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.information.arena.CgsGameArenaHandler
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.cgs.common.teams.CgsGameTeamService
import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import gg.scala.lemon.channel.ChatChannelBuilder
import gg.scala.lemon.channel.ChatChannelService
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.util.CC
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.metadata.FixedMetadataValue


@Listeners
object BedwarsStartListener : Listener
{
    @Inject
    lateinit var plugin: ScalaBedwarsGame

    @EventHandler
    fun onStart(event: CgsGameEngine.CgsGameStartEvent)
    {
        val arena = CgsGameArenaHandler.arena as BedwarsArena

        CgsGameTeamService.teams
            .forEach { (_, u) ->
                val team = u as BedwarsCgsGameTeam

                BedwarsTeamUpgradesTrackerService.trackers[team.id] =
                    BedwarsTeamUpgradesTracker(team)

                val channel = ChatChannelBuilder
                    .newBuilder().import(
                        BedwarsTeamChatChannelComposite(team)
                    )
                    .compose()
                    .override(5000) {
                        it.team()!!.id == team.id && !CgsGameEngine
                            .INSTANCE.gameMode.isSoloGame()
                    }
                    .allowOnlyIf {
                        it.team()!!.id == team.id && !CgsGameEngine
                            .INSTANCE.gameMode.isSoloGame()
                    }
                    .monitor()

                ChatChannelService.register(channel)
            }

        arena.diamondGenerators.forEach {
            BedwarsDiamondItemGenerator(it)
        }

        arena.emeraldGenerators.forEach {
            BedwarsEmeraldItemGenerator(it)
        }

        CgsGameTeamService.teams.values
            .map { it as BedwarsCgsGameTeam }
            .forEach {
                if (it.participants.size > 0)
                {
                    BedwarsTeamItemGenerator(
                        arena.teamGeneratorLocations[it.id]!!, it
                    )
                }

                if (it.participants.size <= 0)
                {
                    it.bedDestroyed = true
                } else
                {
                    val block = arena
                        .teamBedLocations[it.id]!!.block
                    block.type = Material.BED_BLOCK

                    val bedFoot = block
                        .getRelative(
                            block.getFace(block)
                        )
                        .state

                    val bedHead = bedFoot.block
                        .getRelative(BlockFace.SOUTH)
                        .state

                    bedFoot.type = Material.BED_BLOCK
                    bedHead.type = Material.BED_BLOCK

                    bedFoot.setRawData(0x0.toByte())
                    bedHead.setRawData(0x8.toByte())

                    bedFoot.update(true, false)
                    bedHead.update(true, true)

                    listOf(bedFoot, bedHead)
                        .forEach { state ->
                            state.block.setMetadata(
                                "team", FixedMetadataValue(plugin, it.id)
                            )
                        }

                    val shopLocation = arena
                        .teamShopLocations[it.id]!!

                    val shopEntity = BedwarsShopNpcEntity(shopLocation)
                        .apply {
                            initializeData()

                            EntityHandler
                                .trackEntity(this)
                        }

                    val upgradesLocation = arena
                        .teamItemUpgradeLocations[it.id]!!

                    val upgradesEntity = BedwarsTeamUpgradesNpcEntity(upgradesLocation)
                        .apply {
                            initializeData()

                            EntityHandler
                                .trackEntity(this)
                        }

                    val chestLocation = arena
                        .teamChestLocations[it.id]!!

                    chestLocation.block.type = Material.CHEST
                    chestLocation.block.setMetadata(
                        "team", FixedMetadataValue(plugin, it.id)
                    )

                    val enderChestLocation = arena
                        .teamEnderChestLocations[it.id]!!

                    enderChestLocation.block.type = Material.ENDER_CHEST

                    it.participants.forEach { id ->
                        val player = Bukkit.getPlayer(id)
                        player.displayName = it.color.toString() + player.displayName
                        player.playerListName = (if (player.hasMetadata("spectator")) CC.GRAY else it.color.toString()) + player.displayName
                        player.teleport(it.spawnPoint)

                        shopEntity.spawn(player)
                        upgradesEntity.spawn(player)

                        val stats = CgsGameEngine.INSTANCE
                            .getStatistics(
                                CgsPlayerHandler.find(player)!!
                            )

                        player.enderChest.clear()

                        (stats as BedwarsCgsStatistics)
                            .gameKills.update(0)

                        stats.gameBedsBroken.update(0)
                        stats.gameFinalKills.update(0)

                        BedwarsArmorService.applyArmor(player)
                        BedwarsLoadoutService.applyLoadout(player)
                    }
                }
            }

        BedwarsTeamUpgradesTicker
            .runTaskTimer(
                plugin, 0L, 20L
            )
    }
}
