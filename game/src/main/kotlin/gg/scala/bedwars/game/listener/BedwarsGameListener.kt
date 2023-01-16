package gg.scala.bedwars.game.listener

import gg.scala.bedwars.game.ScalaBedwarsGame
import gg.scala.bedwars.game.death.BedwarsRespawnRunnable
import gg.scala.bedwars.game.event.BedwarsBedDestroyEvent
import gg.scala.bedwars.game.loadout.BedwarsLoadoutService
import gg.scala.bedwars.game.shop.BedwarsShopCurrency
import gg.scala.bedwars.game.shop.categories.BedwarsShopBlockCategory.team
import gg.scala.bedwars.shared.BedwarsCgsStatistics
import gg.scala.bedwars.shared.team.BedwarsCgsGameTeam
import gg.scala.cgs.common.CgsGameEngine
import gg.scala.cgs.common.player.handler.CgsGameDisqualificationHandler
import gg.scala.cgs.common.player.handler.CgsPlayerHandler
import gg.scala.cgs.common.player.handler.CgsSpectatorHandler
import gg.scala.cgs.common.teams.CgsGameTeamService
import gg.scala.commons.annotations.Listeners
import gg.scala.flavor.inject.Inject
import net.evilblock.cubed.util.CC
import net.evilblock.cubed.util.bukkit.Constants
import net.evilblock.cubed.util.bukkit.ItemBuilder
import net.evilblock.cubed.util.bukkit.Tasks
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer
import net.kyori.adventure.title.Title
import net.minecraft.server.v1_8_R3.BlockStainedGlass
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.BlockState
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.EntityType
import org.bukkit.entity.Fireball
import org.bukkit.entity.Player
import org.bukkit.entity.Silverfish
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.*
import org.bukkit.metadata.FixedMetadataValue
import java.util.*


@Listeners
object BedwarsGameListener : Listener
{
    @Inject
    lateinit var plugin: ScalaBedwarsGame

    @EventHandler
    fun onQuit(event: PlayerQuitEvent)
    {
        val team = (CgsGameTeamService.getTeamOf(event.player) as BedwarsCgsGameTeam)

        if (team.bedDestroyed)
        {
            CgsGameDisqualificationHandler.disqualifyPlayer(event.player)
        }
    }

    @EventHandler
    fun onRejoin(
        event: CgsGameEngine.CgsGameParticipantReconnectEvent
    )
    {
        if (!event.connectedWithinTimeframe)
        {
            return
        }
    }

    @EventHandler
    fun onInteract(
        event: PlayerInteractAtEntityEvent
    )
    {
        if (event.rightClicked is ArmorStand)
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlace(event: BlockPlaceEvent)
    {
        event.blockPlaced.setMetadata("placed", FixedMetadataValue(plugin, true))
    }

    @EventHandler
    fun onBreak(event: BedwarsBedDestroyEvent)
    {
        event.team.bedDestroyed = true

        CgsGameEngine.INSTANCE.playSound(Sound.ENDERDRAGON_GROWL)

        if (event.destroyer != null)
        {
            val stats = (CgsGameEngine.INSTANCE.getStatistics(CgsPlayerHandler.find(event.destroyer)!!) as BedwarsCgsStatistics)

            stats.bedsBroken++
            stats.gameBedsBroken++

            CgsGameEngine.INSTANCE.giveCoins(
                event.destroyer, ((100..125).random() to "Breaking a Bed")
            )
        }

        CgsGameEngine.INSTANCE.sendMessage("")
        CgsGameEngine.INSTANCE.sendMessage(
            CC.B_WHITE + "Bed Destroyed ${CC.GRAY}${Constants.DOUBLE_ARROW_RIGHT} ${event.team.name}'s${CC.RED} bed has been destroyed${
                if (event.destroyer != null) " by " + event.destroyer.displayName else ""
            }${CC.RED}!"
        )
        CgsGameEngine.INSTANCE.sendMessage("")

        event.team.alive
            .mapNotNull { Bukkit.getPlayer(it) }
            .forEach {
                val audience = CgsGameEngine
                    .INSTANCE.audience.player(it)

                audience.showTitle(
                    Title.title(
                        LegacyComponentSerializer.legacySection()
                            .deserialize("${CC.B_RED}BED DESTROYED"),
                        LegacyComponentSerializer.legacySection()
                            .deserialize("${CC.WHITE}You will no longer respawn!"),
                    )
                )
            }

        event.team.alive
            .filter {
                Bukkit.getPlayer(it) == null
            }
            .forEach {
                CgsGameDisqualificationHandler.disqualifyPlayer(it)
            }

        if (
            event.team.alive
                .mapNotNull { Bukkit.getPlayer(it) }
                .none { !it.hasMetadata("spectator") }
        )
        {
            event.team.broadcastElimination()
        }
    }

    @EventHandler
    fun onItem(
        event: ItemSpawnEvent
    )
    {
        if (
            event.entity.itemStack.type
                .name.contains("BED")
        )
        {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onBreak(event: BlockBreakEvent)
    {
        if (event.block.type == Material.BED_BLOCK)
        {
            if (event.block.hasMetadata("team"))
            {
                val meta = event.block
                    .getMetadata("team")

                val teamId = meta[0].asInt()

                val team = CgsGameTeamService.getTeamOf(event.player) as BedwarsCgsGameTeam?

                if (team == null)
                {
                    event.isCancelled = true
                    return
                }

                if (team.id == teamId)
                {
                    event.isCancelled = true
                    event.player.sendMessage("${CC.RED}You cannot break your own team's bed!")
                    return
                }

                BedwarsBedDestroyEvent(
                    CgsGameTeamService.teams[meta[0].asInt()] as BedwarsCgsGameTeam,
                    event.player
                ).callNow()
            } else
            {
                event.isCancelled = true
            }
        } else if (!event.block.hasMetadata("placed"))
        {
            event.player.sendMessage("${CC.RED}You can only break blocks placed by players!")
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onChestInteract(
        event: PlayerInteractEvent
    )
    {
        if (event.clickedBlock != null)
        {
            val block = event.clickedBlock

            if (block.type == Material.CHEST)
            {
                val metaData = block
                    .getMetadata("team")

                if (metaData.isNullOrEmpty())
                {
                    return
                }

                val teamId = metaData[0].asInt()

                val team = CgsGameTeamService
                    .getTeamOf(event.player) as BedwarsCgsGameTeam?

                val chestTeam = CgsGameTeamService.teams.values
                    .filterIsInstance<BedwarsCgsGameTeam>()
                    .firstOrNull { it.id == teamId }

                if (team == null || chestTeam == null)
                {
                    event.isCancelled = true
                    return
                }

                if (team.id != teamId && !chestTeam.bedDestroyed)
                {
                    event.isCancelled = true
                    event.player.sendMessage(
                        "${CC.RED}You cannot open ${chestTeam.color}${chestTeam.name}'s${CC.RED} team chest as their bed has not been destroyed."
                    )
                    return
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    fun onInventoryClick(event: InventoryClickEvent)
    {
        if (event.clickedInventory == null)
        {
            return
        }

        if (event.clickedInventory.type == InventoryType.PLAYER)
        {
            if (event.slotType == InventoryType.SlotType.ARMOR)
            {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onTntIgnite(
        event: BlockPlaceEvent
    )
    {
        if (event.block.type == Material.TNT)
        {
            event.block.state
        }
    }

    @EventHandler
    fun onExplosion(
        event: EntityExplodeEvent
    )
    {
        event.blockList().removeIf {
            it.type == Material.GLASS ||
                    !it.hasMetadata("placed")
        }
    }

    private val lastUpdated = mutableMapOf<UUID, Long>()

    @EventHandler
    fun onMove(event: PlayerMoveEvent) {
        if (event.to.y < 0) {
            if (!event.player.hasMetadata("respawning")) {
                onDeath(PlayerDeathEvent(event.player, event.player.inventory.contents.toList(), 0, null))
            }
        }
    }

    @EventHandler(
        ignoreCancelled = true
    )
    fun onEntityDamage(
        event: EntityDamageEvent
    )
    {
        if (
            event.cause == EntityDamageEvent.DamageCause.VOID &&
            event.entity is Player
        )
        {
            val player = event.entity as Player
            val updated = lastUpdated[player.uniqueId]

            if (
                updated != null &&
                System.currentTimeMillis() - updated < 100L
            )
            {
                return
            }

            player.health = 0.0
            lastUpdated[player.uniqueId] = System.currentTimeMillis()
        }
    }

    @EventHandler(
        priority = EventPriority.HIGHEST,
        ignoreCancelled = true
    )
    fun onMoveItem(
        event: InventoryMoveItemEvent
    )
    {
        if (event.item.type == Material.WOOD_SWORD)
        {
            event.isCancelled = true
            return
        }

        val player =
            event.destination
                .viewers.first() as Player

        val swords = player.inventory
            .contents.filter {
                it != null && it.type.name.contains("SWORD")
            }

        if (swords.isEmpty())
        {
            Tasks.delayed(1L) {
                BedwarsLoadoutService
                    .applyLoadout(player)
            }
        } else
        {
            val woodSwords = swords
                .filter {
                    it.type != Material.WOOD_SWORD
                }

            if (woodSwords.isNotEmpty())
            {
                player.inventory.remove(Material.WOOD_SWORD)
            }
        }
    }

    @EventHandler
    fun onPickup(
        event: PlayerPickupItemEvent
    )
    {
        val stack = event.item.itemStack

        if (
            stack != null &&
            stack.type.name.contains("SWORD") &&
            stack.type != Material.WOOD_SWORD
        )
        {
            event.player.inventory.remove(Material.WOOD_SWORD)
        }

        BedwarsLoadoutService
            .affectItem(event.player, stack)
    }

    @EventHandler
    fun onDrop(
        event: PlayerDropItemEvent
    )
    {
        val stack = event.itemDrop.itemStack

        if (
            stack != null
        )
        {
            if (stack.type == Material.WOOD_SWORD)
            {
                event.isCancelled = true
                return
            }

            if (
                stack.type.name.contains("SWORD") &&
                stack.type != Material.WOOD_SWORD
            )
            {
                val swords = event.player.inventory
                    .contents.filter {
                        it != null && it.type.name.contains("SWORD")
                    }

                if (swords.isEmpty())
                {
                    Tasks.delayed(1L) {
                        BedwarsLoadoutService
                            .applyLoadout(event.player)
                    }
                }
            }
        }

        if (!event.isCancelled)
        {
            stack.removeEnchantment(Enchantment.DAMAGE_ALL)
        }
    }

    @EventHandler
    fun onProjectileLand(event: ProjectileHitEvent) {
        val silverfish = event.entity.location.world.spawnEntity(event.entity.location, EntityType.SILVERFISH) as Silverfish
        silverfish.isCustomNameVisible = true
        silverfish.customName = "${(event.entity.shooter as Player).displayName}'s Bedbug"
        // add custom AI here for silverfish to not target team
    }

    private val lastThrown = mutableMapOf<UUID, Long>()

    @EventHandler
    fun onFireball(
        event: PlayerInteractEvent
    )
    {
        if (
            event.item != null &&
            event.item.type == Material.FIREBALL &&
            event.action.name.contains("RIGHT")
        )
        {
            event.isCancelled = true

            val lastThrown = lastThrown[event.player.uniqueId]

            if (
                lastThrown != null &&
                System.currentTimeMillis() - lastThrown < 350L
            )
            {
                return
            }

            event.player.inventory
                .remove(
                    ItemBuilder.of(Material.FIREBALL).amount(1).build()
                )
            event.player.updateInventory()

            val eye = event.player.eyeLocation
            val location = eye.add(
                eye.direction.multiply(1.2)
            )

            val fireball = location.world
                .spawnEntity(location, EntityType.FIREBALL) as Fireball

            fireball.velocity = location
                .direction.normalize().multiply(0.80)

            fireball.shooter = event.player

            this.lastThrown[event.player.uniqueId] =
                System.currentTimeMillis()
        }
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent)
    {
        if (
            event.entity
                .hasMetadata("spectator")
        )
        {
            return
        }

        event.drops.removeIf {
            it.type !in BedwarsShopCurrency.values().map { currency ->
                currency.material
            }
        }

        val cause = event.entity.lastDamageCause
        val team = event.entity.team()!!

        if (cause is EntityDamageByEntityEvent)
        {
            if (cause.damager is Player)
            {
                if (!(cause.damager as Player).isOnline)
                {
                    return
                }

                event.drops.forEach {
                    val currency = BedwarsShopCurrency
                        .values().firstOrNull { currency ->
                            it.type == currency.material
                        }

                    if (currency != null)
                    {
                        cause.damager.sendMessage(
                            "${currency.color}+${it.amount} ${
                                if (it.amount > 1)
                                {
                                    currency.displayPlural
                                } else
                                {
                                    currency.displaySingular
                                }
                            }"
                        )

                        (cause.damager as Player)
                            .inventory.addItem(it)
                    }
                }

                event.drops.clear()

                if (team.bedDestroyed)
                {
                    val stats = CgsGameEngine.INSTANCE.getStatistics(
                        CgsPlayerHandler.find(cause.damager as Player)!!
                    ) as BedwarsCgsStatistics

                    stats.finalKills++
                    stats.gameFinalKills++

                    CgsGameEngine.INSTANCE.giveCoins(
                        cause.damager as Player, ((25..50).random() to "Getting a Final Kill")
                    )
                }

                CgsGameEngine.INSTANCE.giveCoins(
                    cause.damager as Player, ((5..15).random() to "Getting a Kill")
                )
            }
        }

        if (team.bedDestroyed)
        {
            CgsGameDisqualificationHandler.disqualifyPlayer(
                player = event.entity, broadcastNotification = true, setSpectator = true
            )
            event.keepLevel

            if (team.alive.isEmpty())
            {
                team.broadcastElimination()
            }
        } else
        {
            BedwarsRespawnRunnable(event.entity)
                .runTaskTimer(
                    this.plugin, 1L, 20L
                )
        }
    }
}
