package gg.scala.bedwars.shared.arena.eight

import gg.scala.bedwars.shared.arena.BedwarsArena
import org.bukkit.Material

object TestTwoArena : BedwarsArena()
{
    override val diamondGenerators = mutableListOf(
        buildSpawnPoint(80105.500, 18.000 + 3, 79941.500),
        buildSpawnPoint(80105.500, 18.000 + 3, 79848.500)
    )

    override val emeraldGenerators = mutableListOf(
        buildSpawnPoint(80126.500, 15.000 + 3, 79916.500),
        buildSpawnPoint(80126.500, 15.000 + 3, 79874.500),
    )

    override val teamShopLocations = mutableMapOf(
        1 to buildSpawnPoint(80127.500, 19.000, 79971.500, -90.0F, 0.0F),
        2 to buildSpawnPoint(80140.500, 19.000, 79819.500, 90.0F, 0.0F),
    )

    override val teamGeneratorLocations = mutableMapOf(
        1 to buildSpawnPoint(80134.500, 18.500 + 3, 79979.500),
        2 to buildSpawnPoint(80133.500, 18.500 + 3, 79811.500),
    )

    override val teamItemUpgradeLocations = mutableMapOf(
        1 to buildSpawnPoint(80127.500, 19.000, 79972.500, -90.0F, 0.0F),
        2 to buildSpawnPoint(80140.500, 19.000, 79818.500, 90.0F, 0.0F),
    )

    override val teamBedLocations = mutableMapOf(
        1 to buildSpawnPoint(80134.500, 19.000, 79962.500),
        2 to buildSpawnPoint(80133.500, 19.000, 79828.500)
    )

    override val teamEnderChestLocations = mutableMapOf(
        1 to buildSpawnPoint(80137.500, 19.000, 79970.500),
        2 to buildSpawnPoint(80130.500, 19.000, 79820.500),
    )

    override val teamChestLocations = mutableMapOf(
        1 to buildSpawnPoint(80131.500, 19.000, 79970.500),
        2 to buildSpawnPoint(80136.500, 19.000, 79820.500),
    )

    override val teamSpawnPoints = mutableMapOf(
        1 to buildSpawnPoint(80134.500, 19.000, 79974.500, -180.0F, 0.0F),
        2 to buildSpawnPoint(80133.500, 19.000, 79816.500, 0.0F, 0.0F)
    )

    override fun getDescription(): String
    {
        return "TEST - Bedwars Arena (two)"
    }

    override fun getId(): String
    {
        return "tropical"
    }

    override fun getMaterial(): Pair<Material, Int>
    {
        return Material.SAPLING to 0
    }

    override fun getBukkitWorldName() = "tropical"

    override fun getName(): String
    {
        return "Tropical"
    }
}
