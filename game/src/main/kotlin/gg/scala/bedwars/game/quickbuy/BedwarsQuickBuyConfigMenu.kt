package gg.scala.bedwars.game.quickbuy

import gg.scala.bedwars.game.shop.BedwarsShopItem
import gg.scala.bedwars.game.shop.BedwarsShopMenu
import gg.scala.bedwars.game.shop.categories.BedwarsShopCategories
import gg.scala.bedwars.shared.BedwarsCgsStatistics
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.inventory.InventoryView
import org.bukkit.inventory.ItemStack

/**
 * @author GrowlyX
 * @since 5/28/2022
 */
class BedwarsQuickBuyConfigMenu(
    private val item: String
) : PaginatedMenu()
{
    init
    {
        placeholdBorders = true
        updateAfterClick = true
    }

    override fun getAllPagesButtonSlots() =
        listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        )

    override fun getMaxItemsPerPage(player: Player) = getAllPagesButtonSlots().size

    override fun size(buttons: Map<Int, Button>) = 45

    override fun onClose(player: Player, manualClose: Boolean)
    {
        if (manualClose)
        {
            Tasks.delayed(1L) {
                BedwarsShopMenu().openMenu(player)
            }
        }
    }

    override fun getAllPagesButtons(player: Player): Map<Int, Button>
    {
        val buttons =
            mutableMapOf<Int, Button>()

        val stats = CgsGameEngine.INSTANCE
            .getStatistics(
                CgsPlayerHandler.find(player)!!
            ) as BedwarsCgsStatistics

        for (slot in this.getAllPagesButtonSlots())
        {
            val selection = stats
                .quickBuyData.positioning[slot]

            if (selection != null)
            {
                val selected = BedwarsShopCategories
                    .categories
                    .flatMap { it.items }
                    .firstOrNull {
                        it.name == selection
                    }

                if (selected == null)
                {
                    buttons[slot] = EmptyButton(stats, slot)
                } else
                {
                    buttons[slot] = ItemButton(
                        stats, selected, slot
                    )
                }
            } else
            {
                buttons[slot] = EmptyButton(stats, slot)
            }
        }

        return buttons
    }

    override fun getPrePaginatedTitle(player: Player) =
        "Item Shop ${Constants.DOUBLE_ARROW_RIGHT} $item"

    inner class ItemButton(
        private val statistics: BedwarsCgsStatistics,
        private val item: BedwarsShopItem,
        private val position: Int
    ) : Button()
    {
        override fun getButtonItem(
            player: Player
        ): ItemStack
        {
            return ItemBuilder
                .copyOf(
                    item.itemCreator.invoke(player)
                )
                .name("${CC.YELLOW}${item.name}")
                .addToLore(
                    "${CC.GREEN}Shift-Click to reset slot!",
                    "${CC.GREEN}Left-Click to select slot!"
                )
                .build()
        }

        override fun clicked(
            player: Player, slot: Int,
            clickType: ClickType, view: InventoryView
        )
        {
            if (clickType.isShiftClick)
            {
                this.statistics.quickBuyData
                    .positioning.remove(this.position)

                CgsPlayerHandler.find(player)?.save()
            } else if (clickType.isLeftClick)
            {
                this.statistics.quickBuyData
                    .positioning[this.position] =
                    item.name

                CgsPlayerHandler.find(player)?.save()

                BedwarsShopMenu().openMenu(player)
            }
        }
    }

    inner class EmptyButton(
        private val statistics: BedwarsCgsStatistics,
        private val position: Int
    ) : Button()
    {
        override fun getButtonItem(player: Player): ItemStack
        {
            return ItemBuilder
                .copyOf(
                    PLACEHOLDER.getButtonItem(player)
                )
                .data(14)
                .name("${CC.RED}Empty Slot")
                .addToLore(
                    "${CC.GRAY}This slot is not bound to any item!",
                    "",
                    "${CC.YELLOW}Click to select!"
                )
                .build()
        }

        override fun clicked(
            player: Player, slot: Int,
            clickType: ClickType, view: InventoryView
        )
        {
            this.statistics.quickBuyData
                .positioning[this.position] = item

            CgsPlayerHandler.find(player)?.save()

            BedwarsShopMenu().openMenu(player)
        }
    }
}
