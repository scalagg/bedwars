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
class BedwarsCgsStatistics(
    uniqueId: UUID,
    var finalKills: CgsGameStatistic = CgsGameStatistic(),
    var bedsBroken: CgsGameStatistic = CgsGameStatistic(),
    var gameBedsBroken: CgsGameStatistic = CgsGameStatistic(),
    var gameFinalKills: CgsGameStatistic = CgsGameStatistic(),
    val quickBuyData: BedwarsQuickBuyData = BedwarsQuickBuyData()
) : GameSpecificStatistics(uniqueId)
{
    override fun getAbstractType() =
        BedwarsCgsStatistics::class.java

    override fun save() = DataStoreObjectControllerCache
        .findNotNull<BedwarsCgsStatistics>()
        .save(this, DataStoreStorageType.MONGO)
}
