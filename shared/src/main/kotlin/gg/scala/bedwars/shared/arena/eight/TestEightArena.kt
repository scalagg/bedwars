package gg.scala.bedwars.shared.arena.eight

import gg.scala.bedwars.shared.arena.BedwarsArena
import org.bukkit.Location
import org.bukkit.Material

object TestEightArena : BedwarsArena() {

    override val diamondGenerators = mutableListOf(
        buildSpawnPoint(5.0, 100.0, 0.0),
        buildSpawnPoint(-5.0, 100.0, 0.0),
        buildSpawnPoint(0.0, 100.0, 5.0),
        buildSpawnPoint(0.0, 100.0, -5.0),
    )

    override fun getBukkitWorldName(): String
    {
        TODO("Not yet implemented")
    }
    
    override val emeraldGenerators = mutableListOf(
        buildSpawnPoint(1.0, 100.0, 1.0),
        buildSpawnPoint(-1.0, 100.0, 1.0),
        buildSpawnPoint(1.0, 100.0, -1.0),
        buildSpawnPoint(-1.0, 100.0, -1.0)
    )

    override val teamShopLocations = mutableMapOf(
        1 to buildSpawnPoint(-9.0, 100.0, 17.0),
        2 to buildSpawnPoint(9.0, 100.0, 17.0),

        3 to buildSpawnPoint(9.0, 100.0, -17.0),
        4 to buildSpawnPoint(-9.0, 100.0, -17.0),


        5 to buildSpawnPoint(17.0, 100.0, 9.0),
        6 to buildSpawnPoint(17.0, 100.0, -9.0),

        7 to buildSpawnPoint(-17.0, 100.0, 9.0),
        8 to buildSpawnPoint(-17.0, 100.0, -9.0)
    )

    override val teamGeneratorLocations = mutableMapOf(
        1 to buildSpawnPoint(-8.0, 100.0, 15.0),
        2 to buildSpawnPoint(8.0, 100.0, 15.0),

        3 to buildSpawnPoint(8.0, 100.0, -15.0),
        4 to buildSpawnPoint(-8.0, 100.0, -15.0),


        5 to buildSpawnPoint(15.0, 100.0, 8.0),
        6 to buildSpawnPoint(15.0, 100.0, -8.0),

        7 to buildSpawnPoint(-15.0, 100.0, 8.0),
        8 to buildSpawnPoint(-15.0, 100.0, -8.0)
    )

    override val teamItemUpgradeLocations = mutableMapOf(
        1 to buildSpawnPoint(-7.0, 100.0, 17.0),
        2 to buildSpawnPoint(7.0, 100.0, 17.0),

        3 to buildSpawnPoint(8.0, 100.0, -17.0),
        4 to buildSpawnPoint(-8.0, 100.0, -17.0),


        5 to buildSpawnPoint(17.0, 100.0, 7.0),
        6 to buildSpawnPoint(17.0, 100.0, -7.0),

        7 to buildSpawnPoint(-17.0, 100.0, 7.0),
        8 to buildSpawnPoint(-17.0, 100.0, -7.0)
    )

    override val teamBedLocations = mutableMapOf(
        1 to buildSpawnPoint(-8.0, 100.0, 14.0),
        2 to buildSpawnPoint(8.0, 100.0, 14.0),

        3 to buildSpawnPoint(8.0, 100.0, -14.0),
        4 to buildSpawnPoint(-8.0, 100.0, -14.0),


        5 to buildSpawnPoint(14.0, 100.0, 8.0),
        6 to buildSpawnPoint(14.0, 100.0, -8.0),

        7 to buildSpawnPoint(-14.0, 100.0, 8.0),
        8 to buildSpawnPoint(-14.0, 100.0, -8.0)
    )

    override val teamEnderChestLocations = mutableMapOf(
        1 to buildSpawnPoint(-10.0, 100.0, 15.0),
        2 to buildSpawnPoint(10.0, 100.0, 15.0),

        3 to buildSpawnPoint(10.0, 100.0, -15.0),
        4 to buildSpawnPoint(-10.0, 100.0, -15.0),


        5 to buildSpawnPoint(15.0, 100.0, 10.0),
        6 to buildSpawnPoint(15.0, 100.0, -10.0),

        7 to buildSpawnPoint(-15.0, 100.0, 10.0),
        8 to buildSpawnPoint(-15.0, 100.0, -10.0)
    )

    override val teamChestLocations = mutableMapOf(
        1 to buildSpawnPoint(-6.0, 100.0, 15.0),
        2 to buildSpawnPoint(6.0, 100.0, 15.0),

        3 to buildSpawnPoint(6.0, 100.0, -15.0),
        4 to buildSpawnPoint(-6.0, 100.0, -15.0),


        5 to buildSpawnPoint(15.0, 100.0, 6.0),
        6 to buildSpawnPoint(15.0, 100.0, -6.0),

        7 to buildSpawnPoint(-15.0, 100.0, 6.0),
        8 to buildSpawnPoint(-15.0, 100.0, -6.0)
    )

    override val teamSpawnPoints = mutableMapOf(
        1 to buildSpawnPoint(-8.0, 100.0, 15.0),
        2 to buildSpawnPoint(8.0, 100.0, 15.0),

        3 to buildSpawnPoint(8.0, 100.0, -15.0),
        4 to buildSpawnPoint(-8.0, 100.0, -15.0),


        5 to buildSpawnPoint(15.0, 100.0, 8.0),
        6 to buildSpawnPoint(15.0, 100.0, -8.0),

        7 to buildSpawnPoint(-15.0, 100.0, 8.0),
        8 to buildSpawnPoint(-15.0, 100.0, -8.0)
    )

    override fun getDescription(): String {
        return "TEST - Bedwars Arena (EIGHT)"
    }

    override fun getId(): String {
        return "bw-test-eight"
    }

    override fun getMaterial(): Pair<Material, Int> {
        return Material.GRASS to 0
    }

    override fun getName(): String {
        return "TEST - 8"
    }
}
