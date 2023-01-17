package gg.scala.bedwars.game.shop

import gg.scala.bedwars.game.quickbuy.BedwarsQuickBuyConfigMenu
import gg.scala.bedwars.game.shop.categories.BedwarsShopCategories
import gg.scala.bedwars.shared.BedwarsCgsStatistics
import gg.scala.bedwars.shared.quickbuy.BedwarsQuickBuyData
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import net.evilblock.cubed.menu.Button
import net.evilblock.cubed.menu.pagination.PaginatedMenu
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.ItemBuilder
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag

/**
 * @author GrowlyX
 * @since 5/27/2022
 */
class BedwarsShopMenu : PaginatedMenu()
{
    private var current: BedwarsShopCategory? = null

    private var lastPurchased = System
        .currentTimeMillis()

    companion object
    {
        @JvmStatic
        private val placeholders =
            listOf(10, 11, 12, 13, 14, 15, 16)
    }

    init
    {
        updateAfterClick = true
        placeholdBorders = true
    }

    override fun size(buttons: Map<Int, Button>) = 54

    override fun getAllPagesButtonSlots() =
        listOf(
            10, 11, 12, 13, 14, 15, 16,
            19, 20, 21, 22, 23, 24, 25,
            28, 29, 30, 31, 32, 33, 34
        ).map {
            it + 9
        }

    override fun getAllPagesButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                if (current == null)
                {
                    return@apply
                }

                for (item in current!!.items)
                {
                    val invoked = item.itemCreator
                        .invoke(player)

                    val amount = player
                        .inventory.contents
                        .filterNotNull()
                        .filter {
                            it.type == item.price.first.material
                        }
                        .sumOf {
                            it.amount
                        }

                    this[size] = ItemBuilder
                        .copyOf(invoked)
                        .name("${CC.YELLOW}${item.name}")
                        .addFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_POTION_EFFECTS,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_PLACED_ON
                        )
                        .setLore(
                            mutableListOf<String>()
                                .apply {
                                    add(
                                        "${CC.GRAY}Price: ${
                                            item.price.first.color
                                        }${
                                            item.price.second
                                        } ${
                                            if (item.price.second == 1)
                                            {
                                                item.price.first.displaySingular
                                            } else
                                            {
                                                item.price.first.displayPlural
                                            }
                                        }"
                                    )
                                    add("")

                                    if (amount < item.price.second)
                                    {
                                        add("${CC.RED}You cannot afford this!")
                                    } else
                                    {
                                        add("${CC.GREEN}Click to purchase.")
                                    }

                                    add("${CC.D_GRAY}Shift-Click to edit quick-buy.")
                                }
                        )
                        .toButton { player, type ->
                            if (type!!.isShiftClick)
                            {
                                BedwarsQuickBuyConfigMenu(item.name)
                                    .openMenu(player!!)
                                return@toButton
                            }

                            if (
                                System.currentTimeMillis() - lastPurchased < 100L
                            )
                            {
                                return@toButton
                            }

                            if (amount < item.price.second)
                            {
                                player!!.sendMessage(
                                    "${CC.RED}You need ${CC.BOLD}${item.price.second - amount}${CC.RED} more ${
                                        if (item.price.second - amount == 1)
                                        {
                                            item.price.first.displaySingular.lowercase()
                                        } else
                                        {
                                            item.price.first.displayPlural.lowercase()
                                        }
                                    } to purchase this item!"
                                )
                                player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
                                return@toButton
                            }

                            if (!item.contextualProvider.allowed(player!!, item))
                            {
                                return@toButton
                            }

                            player.sendMessage(
                                "${CC.GREEN}You purchased ${CC.SEC}${item.name}${CC.GREEN}!"
                            )
                            player.playSound(player.location, Sound.NOTE_PLING, 0.5F, 1.0F)

                            item.price.first.removeFrom
                                .invoke(player, item.price.second)

                            item.contextualProvider.provide(item, player)

                            lastPurchased = System.currentTimeMillis()
                        }
                }
            }

    override fun getGlobalButtons(player: Player) =
        mutableMapOf<Int, Button>()
            .apply {
                placeholders
                    .forEach {
                        this[it] = if (
                            current != null &&
                            it - 9 == current!!.position
                        )
                        {
                            ItemBuilder
                                .copyOf(
                                    PLACEHOLDER
                                        .getButtonItem(player)
                                )
                                .data(5)
                                .toButton()
                        } else
                        {
                            PLACEHOLDER
                        }
                    }

                for (category in BedwarsShopCategories.categories)
                {
                    this[category.position] = ItemBuilder
                        .copyOf(category.icon)
                        .name("${CC.YELLOW}${category.name}")
                        .addToLore(
                            "${CC.GRAY}View all somethings.",
                            "",
                            "${CC.GREEN}Click to view."
                        )
                        .addFlags(
                            ItemFlag.HIDE_ATTRIBUTES,
                            ItemFlag.HIDE_ENCHANTS,
                            ItemFlag.HIDE_POTION_EFFECTS,
                            ItemFlag.HIDE_UNBREAKABLE,
                            ItemFlag.HIDE_PLACED_ON
                        )
                        .toButton { _, _ ->
                            current = category
                        }
                }

                this[49] = ItemBuilder(Material.NETHER_STAR)
                    .name("${CC.YELLOW}Quick Buy")
                    .addToLore(
                        "${CC.GRAY}View all somethings.",
                        "",
                        "${CC.GREEN}Click to view."
                    )
                    .toButton { _, _ ->
                        current = null
                    }

                if (current == null)
                {
                    val stats = CgsGameEngine.INSTANCE
                        .getStatistics(
                            CgsPlayerHandler.find(player)!!
                        ) as BedwarsCgsStatistics

                    for (entry in stats.quickBuyData.positioning)
                    {
                        val selected = BedwarsShopCategories
                            .categories
                            .flatMap { it.items }
                            .firstOrNull {
                                it.name == entry.value
                            }

                        if (selected != null)
                        {
                            val invoked = selected.itemCreator
                                .invoke(player)

                            val amount = player
                                .inventory.contents
                                .filterNotNull()
                                .filter {
                                    it.type == selected.price.first.material
                                }
                                .sumOf {
                                    it.amount
                                }

                            this[entry.key + 9] = ItemBuilder
                                .copyOf(invoked)
                                .name("${CC.YELLOW}${selected.name}")
                                .addFlags(
                                    ItemFlag.HIDE_ATTRIBUTES,
                                    ItemFlag.HIDE_ENCHANTS,
                                    ItemFlag.HIDE_POTION_EFFECTS,
                                    ItemFlag.HIDE_UNBREAKABLE,
                                    ItemFlag.HIDE_PLACED_ON
                                )
                                .setLore(
                                    mutableListOf<String>()
                                        .apply {
                                            add(
                                                "${CC.GRAY}Price: ${
                                                    selected.price.first.color
                                                }${
                                                    selected.price.second
                                                } ${
                                                    if (selected.price.second == 1)
                                                    {
                                                        selected.price.first.displaySingular
                                                    } else
                                                    {
                                                        selected.price.first.displayPlural
                                                    }
                                                }"
                                            )
                                            add("")

                                            if (amount < selected.price.second)
                                            {
                                                add("${CC.RED}You cannot afford this!")
                                            } else
                                            {
                                                add("${CC.GREEN}Click to purchase.")
                                            }
                                        }
                                )
                                .toButton { player, _ ->
                                    if (
                                        System.currentTimeMillis() - lastPurchased < 100L
                                    )
                                    {
                                        return@toButton
                                    }

                                    if (amount < selected.price.second)
                                    {
                                        player!!.sendMessage(
                                            "${CC.RED}You need ${CC.BOLD}${selected.price.second - amount}${CC.RED} more ${
                                                if (selected.price.second - amount == 1)
                                                {
                                                    selected.price.first.displaySingular.lowercase()
                                                } else
                                                {
                                                    selected.price.first.displayPlural.lowercase()
                                                }
                                            } to purchase this item!"
                                        )
                                        player.playSound(player.location, Sound.NOTE_BASS, 0.5F, 0.5F)
                                        return@toButton
                                    }

                                    if (!selected.contextualProvider.allowed(player!!, selected))
                                    {
                                        return@toButton
                                    }

                                    player.sendMessage(
                                        "${CC.GREEN}You purchased ${CC.SEC}${selected.name}${CC.GREEN}!"
                                    )
                                    player.playSound(player.location, Sound.NOTE_PLING, 0.5F, 1.0F)

                                    selected.price.first.removeFrom
                                        .invoke(player, selected.price.second)

                                    selected.contextualProvider.provide(selected, player)

                                    lastPurchased = System.currentTimeMillis()
                                }
                        }
                    }

                    getAllPagesButtonSlots()
                        .forEach {
                            if (this[it] == null)
                            {
                                this[it] = ItemBuilder
                                    .copyOf(
                                        PLACEHOLDER.getButtonItem(player)
                                    )
                                    .data(14)
                                    .name("${CC.RED}Empty Slot")
                                    .addToLore(
                                        "${CC.GRAY}This slot is not bound to any item!",
                                        "${CC.GRAY}Shift-Click a shop item to configure this menu."
                                    )
                                    .toButton()
                            }
                        }

                    return@apply
                }
            }

    override fun getPrePaginatedTitle(player: Player) = "Item Shop"
}
