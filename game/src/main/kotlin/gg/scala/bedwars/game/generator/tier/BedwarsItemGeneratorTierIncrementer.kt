package gg.scala.bedwars.game.generator.tier

import gg.scala.bedwars.game.generator.BedwarsItemGeneratorService
import gg.scala.bedwars.game.generator.impl.BedwarsDiamondItemGenerator
import gg.scala.bedwars.game.generator.impl.BedwarsEmeraldItemGenerator
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.environment.EditableFieldEntry
import gg.scala.cgs.common.environment.editor.EnvironmentEditorService
import gg.scala.cgs.common.states.CgsGameState
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.time.TimeUtil
import org.bukkit.scheduler.BukkitRunnable

/**
 * @author GrowlyX
 * @since 5/26/2022
 */
object BedwarsItemGeneratorTierIncrementer : BukkitRunnable()
{
    private val engine by lazy {
        CgsGameEngine.INSTANCE
    }

    var countdown = 300

    private var current = 0

    private val updates = listOf(
        18000, 14400, 10800, 7200, 3600, 2700, 1800, 900, 600, 300,
        240, 180, 120, 60, 50, 40, 30, 15, 10, 5, 4, 3, 2, 1
    )

    private val colors = mapOf(
        BedwarsDiamondItemGenerator::class to CC.AQUA,
        BedwarsEmeraldItemGenerator::class to CC.D_GREEN
    )

    private val generators = mapOf(
        BedwarsDiamondItemGenerator::class to "Diamond",
        BedwarsEmeraldItemGenerator::class to "Emerald"
    )

    val numerals = mapOf(
        1 to "I",
        2 to "II",
        3 to "III",
        4 to "IV"
    )

    // very stupid
    private val mappings = listOf(
        BedwarsDiamondItemGenerator::class to 1,
        BedwarsEmeraldItemGenerator::class to 1,
        BedwarsDiamondItemGenerator::class to 2,
        BedwarsEmeraldItemGenerator::class to 2,
        BedwarsDiamondItemGenerator::class to 3,
        BedwarsEmeraldItemGenerator::class to 3,
        BedwarsDiamondItemGenerator::class to 4,
        BedwarsEmeraldItemGenerator::class to 4
    )

    fun currentEvent(): Pair<String, Int>
    {
        val current = this.mappings[current]

        return Pair(
            generators[current.first]!!,
            current.second
        )
    }

    private fun current() =
        this.mappings[current]

    fun formatted(
        colored: Boolean = true
    ): String
    {
        val current = this.current()

        return "${
            if (colored) colors[current.first] else ""
        }${
            generators[current.first]
        } ${
            numerals[current.second]
        }"
    }

    override fun run()
    {
        if (
            engine.gameState != CgsGameState.STARTED
        )
        {
            return
        }

        val current = this.mappings[current]

        if (this.updates.contains(this.countdown))
        {
            this.engine.sendMessage(
                "${colors[current.first]}${generators[current.first]} ${numerals[current.second]}${CC.SEC} will commence in ${CC.PRI}${
                    TimeUtil.formatIntoDetailedString(this.countdown)
                }${CC.SEC}."
            )
        }

        if (this.countdown == 0)
        {
            BedwarsItemGeneratorService
                .generators
                .filterIsInstance(current.first.java)
                .forEach {
                    it.tier = current.second
                }

            this.engine.sendMessage(
                "${colors[current.first]}${generators[current.first]} ${numerals[current.second]}${CC.SEC} has commenced."
            )

            if (
                this.current !=
                    mappings.size - 1
            )
            {
                this.current++
                this.countdown = 300
            } else
            {
                this.cancel()
            }

            return
        }

        countdown--
    }
}
