package gg.scala.bedwars.shared

import gg.scala.bedwars.shared.quickbuy.BedwarsQuickBuyData
import gg.scala.cgs.common.player.statistic.GameSpecificStatistics
import gg.scala.cgs.common.player.statistic.value.CgsGameStatistic
import gg.scala.store.controller.DataStoreObjectControllerCache
import gg.scala.store.storage.type.DataStoreStorageType
import java.util.*

/**
 * @author GrowlyX
 * @since 12/17/2021
 */
class BedwarsCgsStatistics(uniqueId: UUID) : GameSpecificStatistics(uniqueId)
{
    var finalKills = CgsGameStatistic()
    var bedsBroken = CgsGameStatistic()
    var gameBedsBroken = CgsGameStatistic()
    var gameFinalKills = CgsGameStatistic()

    val quickBuyData = BedwarsQuickBuyData()

    override fun getAbstractType() =
        BedwarsCgsStatistics::class.java

    override fun save() = DataStoreObjectControllerCache
        .findNotNull<BedwarsCgsStatistics>()
        .save(this, DataStoreStorageType.MONGO)
}
