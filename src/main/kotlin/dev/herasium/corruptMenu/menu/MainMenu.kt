package dev.herasium.corruptMenu.menu

import dev.herasium.corruptCore.open.RayTrace
import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.CorruptMenu
import dev.herasium.corruptMenu.internal.MenuComponent
import dev.herasium.corruptMenu.internal.menu.Button
import dev.herasium.corruptMenu.internal.menu.Cursor
import dev.herasium.corruptMenu.internal.menu.Menu
import dev.herasium.corruptMenu.internal.menu.Title
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.Player
import org.bukkit.util.BoundingBox
import dev.herasium.corruptCinematic.internal.PlayCinematic
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToMarket
import dev.herasium.corruptCinematic.CorruptCinematic
import dev.herasium.corruptCinematic.cinematics.Cinematic
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToCrates
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToWarp
import kotlinx.coroutines.*
import kotlinx.coroutines.delay
import org.bukkit.plugin.Plugin
import org.bukkit.scheduler.BukkitRunnable
import kotlin.properties.Delegates


class MainMenu(private val player: Player, private val plugin: CorruptMenu): dev.herasium.corruptMenu.internal.Menu {

    private val parser = TextParser()

    private val world = player.world
    private val SpawnLocation = Location(world,43.5,0.0,35.5,0f,0f)
    private val RayOriginLocation = Location(world,43.5,1.0,35.5,0f,0f)

    val ScreenBoundingBox = BoundingBox(51.0,6.0,41.5,35.0,-4.0,43.5)
    val MarketBoundingBox = BoundingBox(49.0,2.1,41.5,46.0,1.0,43.5)
    val WarpBoundingBox = BoundingBox(45.0,0.0,41.5,42.0,-1.0,43.5)
    val CratesBoundingBox = BoundingBox(41.0,2.1,41.5,38.0,1.0,43.5)

    val MarketLocation = Location(world,47.5,2.0,42.0)
    val WarpLocation = Location(world,43.5,0.0,42.0)
    val CratesLocation = Location(world,39.5,2.0,42.0)
    val TitleLocation = Location(world,43.5,5.0,42.0)

    private val MainMenu = Menu(parser,SpawnLocation,player)
    private val MarketButton = Button(MarketBoundingBox,MarketLocation,parser,player)
    private val WarpButton = Button(WarpBoundingBox,WarpLocation,parser,player)
    private val CratesButton = Button(CratesBoundingBox,CratesLocation,parser,player)
    private val MainTitle = Title(TitleLocation,player,parser)

    private val Cursor = Cursor(SpawnLocation,player,parser)

    val childList = listOf<MenuComponent>(MarketButton,WarpButton,CratesButton,MainTitle,Cursor, MainMenu)

    var taskId by Delegates.notNull<Int>()

    fun startCinematic(toLaunch: Cinematic) {
        MainMenu.standEntity.removePassenger(player)
        val CinematicPlugin = Bukkit.getServer().getPluginManager().getPlugin("CorruptCinematic")
        val corruptCinematic = CinematicPlugin as CorruptCinematic
        PlayCinematic(toLaunch, player, corruptCinematic).run()
    }

    override fun setup() {
        MainMenu.setup("")
        MarketButton.setup(":main_menu_market:")
        WarpButton.setup(":main_menu_warp:")
        CratesButton.setup(":main_menu_crates:")
        MainTitle.setup(":main_menu_title:")
        Cursor.setup("\uD83D\uDDE1")

        plugin.menuList.add(this)
        plugin.playerMenu[player] = this

        var lastYaw = 0f
        var lastPitch = 0f

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            var currentYaw = player.location.yaw
            var currentPitch = player.location.pitch

            if (currentYaw == 0f && currentPitch == 0f) {
                currentPitch = lastPitch
                currentYaw = lastYaw
            }

            lastPitch = currentPitch
            lastYaw = currentYaw

            var EyeLocation = player.eyeLocation.clone()
            EyeLocation.yaw = currentYaw
            EyeLocation.pitch = currentPitch
            EyeLocation.x = SpawnLocation.x
            EyeLocation.y = SpawnLocation.y + 1
            EyeLocation.z = SpawnLocation.z

            val ent = MainMenu.standEntity as CraftEntity
            ent.getHandle().setPos(SpawnLocation.x + (0-currentYaw/30),SpawnLocation.y+ (0-currentPitch/30), SpawnLocation.z)

            val rayTrace = RayTrace(RayOriginLocation.toVector(),EyeLocation.direction)
            val targetLocation = rayTrace.positionOfIntersection(ScreenBoundingBox,10.0,0.01)
            if (targetLocation != null) {
                Cursor.move(targetLocation.toLocation(world))
            }

            MarketButton.update(rayTrace,false)
            WarpButton.update(rayTrace,false)
            CratesButton.update(rayTrace,false)

            if (plugin.playerClick[player] == true) {
                player.sendMessage("Click Detected")
                if (MarketButton.isHovered) {
                    val cratesMenu = CratesMenu(player, plugin)
                    startCinematic(ToMarket())
                    scheduleTask(plugin, 70) {cratesMenu.setup()}
                    this.quit()
                }
                if (WarpButton.isHovered) {
                    val cratesMenu = CratesMenu(player,plugin)
                    startCinematic(ToWarp())
                    scheduleTask(plugin, 70) {cratesMenu.setup()}
                    this.quit()
                }
                if (CratesButton.isHovered) {
                    val cratesMenu = CratesMenu(player,plugin)
                    startCinematic(ToCrates())
                    scheduleTask(plugin, 70) {cratesMenu.setup()}
                    this.quit()
                }
            }
            plugin.playerClick[player] = false

        }, 0L, 1L)
    }

    override fun quit() {
        Bukkit.getScheduler().cancelTask(taskId)
        for (child in childList) {
            child.kill()
        }
    }

    private fun scheduleTask(plugin: Plugin, delay: Long = 0L, task: () -> Unit) {
        object : BukkitRunnable() {
            override fun run() { task() }
        }.runTaskLater(plugin, delay)
    }
}