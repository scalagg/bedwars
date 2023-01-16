package gg.scala.bedwars.game

import gg.scala.bedwars.game.generator.hologram.BedwarsUpdatingHologramEntity
import gg.scala.bedwars.game.generator.tier.BedwarsItemGeneratorTierIncrementer
import gg.scala.bedwars.game.shop.categories.BedwarsShopArmorCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopBlockCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopBlockCategory.team
import gg.scala.bedwars.game.shop.categories.BedwarsShopCategories
import gg.scala.bedwars.game.shop.categories.BedwarsShopMeleeCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopPotionCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopRangedCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopToolsCategory
import gg.scala.bedwars.game.shop.categories.BedwarsShopUtilityCategory
import gg.scala.bedwars.game.shop.npc.BedwarsShopNpcEntity
import gg.scala.bedwars.game.shop.npc.BedwarsTeamUpgradesNpcEntity
import gg.scala.bedwars.shared.BedwarsCgsInfo
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.information.arena.CgsGameArenaHandler
import gg.scala.cgs.common.information.mode.CgsGameMode
import gg.scala.cgs.common.states.CgsGameState
import gg.scala.commons.ExtendedScalaPlugin
import gg.scala.commons.annotations.container.ContainerEnable
import gg.scala.commons.config.annotations.ContainerConfig
import gg.scala.commons.core.plugin.Plugin
import gg.scala.commons.core.plugin.PluginAuthor
import gg.scala.commons.core.plugin.PluginDependency
import gg.scala.commons.core.plugin.PluginDependencyComposite
import gg.scala.commons.core.plugin.PluginWebsite
import gg.scala.lemon.channel.channels.DefaultChatChannel
import net.evilblock.cubed.entity.EntityHandler
import net.evilblock.cubed.util.CC
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import org.bukkit.entity.Player

/**
 * @author GrowlyX
 * @since 5/21/2022
 */
@Plugin(
    name = "BedWars",
    version = "%remote%/%branch%/%id%"
)
@PluginAuthor("GrowlyX")
@PluginWebsite("https://scala.gg")
@PluginDependencyComposite(
    PluginDependency("scala-commons"),
    PluginDependency("Lemon"),
    PluginDependency("ScGameFramework"),
    PluginDependency("cloudsync", soft = true)
)
@ContainerConfig(
    value = "orchestration",
    model = ScalaBedwarsOrchestration::class
)
class ScalaBedwarsGame : ExtendedScalaPlugin()
{
    @ContainerEnable
    fun containerEnable()
    {
        val modeClass = Class.forName(
            this.config<ScalaBedwarsOrchestration>().mode
        )

        val modeClassObject =
            modeClass.kotlin.objectInstance!! as CgsGameMode

        CgsGameArenaHandler.configure(modeClassObject)

        val engine = ScalaBedwarsGameEngine(
            this, BedwarsCgsInfo,
            modeClassObject
        )

        this.server.worlds
            .forEach {
                for (entity in it.entities)
                {
                    if (entity is Player)
                        return

                    entity.remove()
                }
            }

        listOf(
            BedwarsUpdatingHologramEntity::class,
            BedwarsShopNpcEntity::class,
            BedwarsTeamUpgradesNpcEntity::class
        ).forEach {
            EntityHandler
                .getEntitiesByType(it.java)
                .forEach(
                    EntityHandler::forgetEntity
                )
        }

        EntityHandler.close()

        CgsGameEngine.INSTANCE = engine
        engine.initialLoad()

        BedwarsShopCategories
            .register(
                BedwarsShopMeleeCategory.category,
                BedwarsShopBlockCategory.category,
                BedwarsShopRangedCategory.category,
                BedwarsShopArmorCategory.category,
                BedwarsShopPotionCategory.category,
                BedwarsShopUtilityCategory.category,
                BedwarsShopToolsCategory.category
            )

        DefaultChatChannel
            .provideAdditionalPrefix {
                val team = it.team()
                    ?: return@provideAdditionalPrefix Component.empty()

                LegacyComponentSerializer.legacySection()
                    .deserialize(
                        if (CgsGameEngine.INSTANCE.gameMode.isSoloGame())
                        {
                            "${
                                if (
                                    CgsGameEngine.INSTANCE.gameState == CgsGameState.STARTED
                                )
                                {
                                    "${team.color}[${
                                        team.name.uppercase()
                                    }] "
                                } else
                                {
                                    ""
                                }
                            }${CC.GRAY}[1✫] ${CC.WHITE}"
                        } else ""
                    )
            }

        BedwarsItemGeneratorTierIncrementer
            .runTaskTimerAsynchronously(
                this, 0L, 20L
            )
    }
}
