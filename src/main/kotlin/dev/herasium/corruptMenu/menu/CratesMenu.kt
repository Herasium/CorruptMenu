package dev.herasium.corruptMenu.menu

import dev.herasium.corruptCinematic.CorruptCinematic
import dev.herasium.corruptCinematic.cinematics.Cinematic
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToCrates
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToMarket
import dev.herasium.corruptCinematic.cinematics.MainMenu.ToWarp
import dev.herasium.corruptCinematic.internal.PlayCinematic
import dev.herasium.corruptCore.open.RayTrace
import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.CorruptMenu
import dev.herasium.corruptMenu.internal.MenuComponent
import dev.herasium.corruptMenu.internal.menu.Button
import dev.herasium.corruptMenu.internal.menu.Cursor
import dev.herasium.corruptMenu.internal.menu.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.craftbukkit.entity.CraftEntity
import org.bukkit.entity.Player
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.BoundingBox
import kotlin.properties.Delegates

class CratesMenu(private val player: Player, private val plugin: CorruptMenu): dev.herasium.corruptMenu.internal.Menu {

    private val parser = TextParser()

    private val world = player.world
    private val SpawnLocation = Location(world,32.5,-5.0,65.5,90f,0f)
    private val RayOriginLocation = Location(world,32.5,-4.0,65.5,90f,0f)

    val ScreenBoundingBox = BoundingBox(27.5,-8.0,72.0,29.5,1.0,58.0)

    val IronCrateBoundingBox = BoundingBox(27.5,-2.0,70.0,29.5,-3.0,69.0)
    val GoldCrateBoundingBox = BoundingBox(27.5,-2.0,62.0,29.5,-3.0,61.0)
    val RubyCrateBoundingBox = BoundingBox(27.5,-2.0,68.0,29.5,-3.0,67.0)
    val RainbowCrateBoundingBox = BoundingBox(27.5,-2.0,64.0,29.5,-3.0,63.0)
    val CorruptedCrateBoundingBox = BoundingBox(27.5,-2.0,66.0,29.5,-3.0,65.0)

    val IronCrateLocation = Location(world,28.5,-2.5,69.5)
    val GoldCrateLocation = Location(world,28.5,-2.5,61.5)
    val RubyCrateLocation = Location(world,28.5,-2.5,67.5)
    val RainbowCrateLocation = Location(world,28.5,-2.5,63.5)
    val CorruptedCrateLocation = Location(world,28.5,-2.5,65.5)

    private val CratesMenu = Menu(parser,SpawnLocation,player)

    private val IronCrateButton = Button(IronCrateBoundingBox,IronCrateLocation,parser,player)
    private val GoldCrateButton = Button(GoldCrateBoundingBox,GoldCrateLocation,parser,player)
    private val RubyCrateButton = Button(RubyCrateBoundingBox,RubyCrateLocation,parser,player)
    private val RainbowCrateButton = Button(RainbowCrateBoundingBox,RainbowCrateLocation,parser,player)
    private val CorruptedCrateButton = Button(CorruptedCrateBoundingBox,CorruptedCrateLocation,parser,player)

    private val Cursor = Cursor(SpawnLocation,player,parser)

    val childList = listOf<MenuComponent>(IronCrateButton,GoldCrateButton,RubyCrateButton,RainbowCrateButton,CorruptedCrateButton,Cursor, CratesMenu)

    var taskId by Delegates.notNull<Int>()

    fun startCinematic(toLaunch: Cinematic) {
        CratesMenu.standEntity.removePassenger(player)
        val CinematicPlugin = Bukkit.getServer().getPluginManager().getPlugin("CorruptCinematic")
        val corruptCinematic = CinematicPlugin as CorruptCinematic
        Bukkit.getScheduler().runTaskAsynchronously(plugin,
            object : BukkitRunnable() {
                override fun run() {
                    PlayCinematic(toLaunch, player, corruptCinematic).run()
                }
            }
        )
    }

    override fun setup() {
        CratesMenu.setup("")
        IronCrateButton.setup(":crates_menu_iron:")
        GoldCrateButton.setup(":crates_menu_gold:")
        RubyCrateButton.setup(":crates_menu_ruby:")
        RainbowCrateButton.setup(":crates_menu_rainbow:")
        CorruptedCrateButton.setup(":crates_menu_corrupted:")
        Cursor.setup("\uD83D\uDDE1")

        plugin.menuList.add(this)
        plugin.playerMenu[player] = this

        var lastYaw = 90f
        var lastPitch = 0f

        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            var currentYaw = player.location.yaw
            var currentPitch = player.location.pitch

            if (currentYaw == 90f && currentPitch == 0f) {
                currentPitch = lastPitch
                currentYaw = lastYaw
            }

            lastPitch = currentPitch
            lastYaw = currentYaw

            var EyeLocation = player.eyeLocation.clone()
            EyeLocation.yaw = currentYaw
            EyeLocation.pitch = currentPitch

            val ent = CratesMenu.standEntity as CraftEntity
            ent.getHandle().setPos(SpawnLocation.x ,SpawnLocation.y+ (0-(currentPitch-0)/100), SpawnLocation.z+ (0-(currentYaw-90)/100))

            val rayTrace = RayTrace(SpawnLocation.toVector(),EyeLocation.direction)
            val targetLocation = rayTrace.positionOfIntersection(ScreenBoundingBox,10.0,0.01)
            if (targetLocation != null) {
                Cursor.move(targetLocation.toLocation(world))
            }

            IronCrateButton.update(rayTrace,false)
            GoldCrateButton.update(rayTrace,false)
            RubyCrateButton.update(rayTrace,false)
            RainbowCrateButton.update(rayTrace,false)
            CorruptedCrateButton.update(rayTrace,false)

            if (plugin.playerClick[player] == true) {
                player.sendMessage("Click Detected")
                if (IronCrateButton.isHovered) {
                    player.sendMessage("Iron pressed")
                    this.quit()
                }
                if (GoldCrateButton.isHovered) {
                    player.sendMessage("Gold pressed")
                    this.quit()
                }
                if (RubyCrateButton.isHovered) {
                    player.sendMessage("Ruby pressed")
                    this.quit()
                }
                if (RainbowCrateButton.isHovered) {
                    player.sendMessage("Rainbow pressed")
                    this.quit()
                }
                if (CorruptedCrateButton.isHovered) {
                    player.sendMessage("Corrupted pressed")
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

}