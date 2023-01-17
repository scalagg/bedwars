package gg.scala.bedwars.shared.arena

import gg.scala.cgs.common.information.arena.CgsGameArena
import gg.scala.cgs.common.information.arena.slime.CgsSlimeGameArena
import org.bukkit.Bukkit
import org.bukkit.Location
import java.nio.file.Path

/**
 * @author GrowlyX
 * @since 12/3/2021
 */
abstract class BedwarsArena : CgsGameArena
{
    override fun getDirectory(): Path? = null

    override fun getPreLobbyLocation() = Location(
        Bukkit.getWorld(getBukkitWorldName()),
        0.5, 150.0, 0.5
    )

    override fun getSpectatorLocation() = Location(
        Bukkit.getWorld(getBukkitWorldName()), 0.5, Bukkit.getWorld(getBukkitWorldName()).getHighestBlockYAt(0, 0) + 15.0, 0.5
    )

    fun buildSpawnPoint(x: Double, y: Double, z: Double) : Location {
        return Location(Bukkit.getWorld(getBukkitWorldName()), x, y, z)
    }

    fun buildSpawnPoint(x: Double, y: Double, z: Double, yaw: Float, pitch: Float) : Location {
        return Location(Bukkit.getWorld(getBukkitWorldName()), x, y, z, yaw, pitch)
    }

    fun getSpawnPoint(id: Int): Location? {
        return teamSpawnPoints[id]
    }

    abstract val teamSpawnPoints: MutableMap<Int, Location>
    abstract val diamondGenerators: List<Location>
    abstract val emeraldGenerators: List<Location>

    abstract val teamShopLocations: MutableMap<Int, Location>
    abstract val teamGeneratorLocations: MutableMap<Int, Location>
    abstract val teamItemUpgradeLocations: MutableMap<Int, Location>
    abstract val teamBedLocations: MutableMap<Int, Location>

    abstract val teamEnderChestLocations: MutableMap<Int, Location>
    abstract val teamChestLocations: MutableMap<Int, Location>
}
