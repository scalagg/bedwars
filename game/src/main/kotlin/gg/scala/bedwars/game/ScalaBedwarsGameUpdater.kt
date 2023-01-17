package gg.scala.bedwars.game

import gg.scala.cloudsync.shared.discovery.CloudSyncDiscoveryService
import gg.scala.commons.annotations.plugin.SoftDependency
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import gg.scala.flavor.service.ignore.IgnoreAutoScan

/**
 * @author GrowlyX
 * @since 4/26/2022
 */
@Service
@IgnoreAutoScan
@SoftDependency("cloudsync")
object ScalaBedwarsGameUpdater
{
    @Configure
    fun configure()
    {
        CloudSyncDiscoveryService
            .discoverable.assets
            .apply {
                add("gg.scala.bedwars:game:scala-bedwars-game")
            }
    }
}
