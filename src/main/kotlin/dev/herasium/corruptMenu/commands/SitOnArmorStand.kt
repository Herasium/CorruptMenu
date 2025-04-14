package dev.herasium.corruptMenu.commands

import dev.herasium.corruptCore.open.RayTrace
import org.bukkit.*
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.math.max
import kotlin.math.min
import dev.herasium.corruptCore.open.TextParser
import org.bukkit.entity.*
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.util.BoundingBox


class SitOnArmorStand(private val plugin:JavaPlugin) : CommandExecutor, Listener {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command!")
            return false
        }

        val parser = TextParser()

        val player = sender
        val world = player.world

        val SpawnLocation = Location(world,43.5,0.0,35.5,0f,0f)

        val entity = world.spawn(SpawnLocation, ArmorStand::class.java)
        entity.maxHealth = 1.0
        entity.setAI(false)
        entity.isInvisible = true
        entity.setCanMove(false)

        val debugEntity = world.spawn(SpawnLocation, TextDisplay::class.java)
        debugEntity.text(parser.parseText("◦",player))
        debugEntity.isSeeThrough = true
        debugEntity.backgroundColor = Color.fromARGB(0,0,0,0)
        debugEntity.billboard = Display.Billboard.CENTER

        player.gameMode = GameMode.SPECTATOR

        var NewPlayerLocation = SpawnLocation.clone()
        NewPlayerLocation.yaw = 0f
        NewPlayerLocation.pitch = 0f
        player.teleport(NewPlayerLocation)

        player.spectatorTarget = entity
        entity.addPassenger(player)

        val MainMenuLogo = world.spawn(Location(world,43.5,5.0,42.0),TextDisplay::class.java)
        MainMenuLogo.text(parser.parseText(":main_menu_title:",player))
        MainMenuLogo.isSeeThrough = true
        MainMenuLogo.backgroundColor = Color.fromARGB(0,0,0,0)
        MainMenuLogo.billboard = Display.Billboard.CENTER

        val MainMenuMarket = world.spawn(Location(world,47.5,2.0,42.0),TextDisplay::class.java)
        MainMenuMarket.text(parser.parseText(":main_menu_market:",player))
        MainMenuMarket.isSeeThrough = true
        MainMenuMarket.backgroundColor = Color.fromARGB(0,0,0,0)
        MainMenuMarket.billboard = Display.Billboard.CENTER

        val MainMenuWarp = world.spawn(Location(world,43.5,0.0,42.0),TextDisplay::class.java)
        MainMenuWarp.text(parser.parseText(":main_menu_warp:",player))
        MainMenuWarp.isSeeThrough = true
        MainMenuWarp.backgroundColor = Color.fromARGB(0,0,0,0)
        MainMenuWarp.billboard = Display.Billboard.CENTER

        val MainMenuCrates = world.spawn(Location(world,39.5,2.0,42.0),TextDisplay::class.java)
        MainMenuCrates.text(parser.parseText(":main_menu_crates:",player))
        MainMenuCrates.isSeeThrough = true
        MainMenuCrates.backgroundColor = Color.fromARGB(0,0,0,0)
        MainMenuCrates.billboard = Display.Billboard.CENTER

        val ScreenBoundingBox = BoundingBox(51.0,6.0,41.5,35.0,-4.0,43.5)
        val MarketBoundingBox = BoundingBox(49.0,2.1,41.5,46.0,1.0,43.5)
        val WarpBoundingBox = BoundingBox(45.0,0.0,41.5,42.0,-1.0,43.5)
        val CratesBoundingBox = BoundingBox(41.0,2.1,41.5,38.0,1.0,43.5)

        val currentScreenBorder = 0f

        Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, {
            var currentYaw = player.location.yaw
            var currentPitch = player.location.pitch

            currentYaw = max(currentYaw,-5200f + currentScreenBorder)
            currentYaw = min(currentYaw, 5000f - currentScreenBorder)

            currentPitch = max(currentPitch,-2400f + currentScreenBorder)
            currentPitch = min(currentPitch,6700f - currentScreenBorder)

            var EyeLocation = player.eyeLocation.clone()
            EyeLocation.yaw = currentYaw
            EyeLocation.pitch = currentPitch

            val rayTrace = RayTrace(EyeLocation.toVector(),EyeLocation.direction)
            val targetLocation = rayTrace.positionOfIntersection(ScreenBoundingBox,10.0,0.01)
            if (targetLocation != null) {
                debugEntity.teleport(targetLocation.toLocation(world))
            }

            if (rayTrace.intersects(MarketBoundingBox, 10.0,0.1)) {
                MainMenuMarket.location.z = 40.0
                MainMenuMarket.teleport(MainMenuMarket.location)
            } else {
                MainMenuMarket.location.z = 42.0
                MainMenuMarket.teleport(MainMenuMarket.location)
            }
            if (rayTrace.intersects(WarpBoundingBox, 10.0,0.1)) {
                MainMenuWarp.location.z = 40.0
                MainMenuWarp.teleport(MainMenuWarp.location)
            } else {
                MainMenuWarp.location.z = 42.0
                MainMenuWarp.teleport(MainMenuWarp.location)
            }
            if (rayTrace.intersects(CratesBoundingBox, 10.0,0.1)) {
                MainMenuCrates.location.z = 40.0
                MainMenuCrates.teleport(MainMenuCrates.location)
            } else {
                MainMenuCrates.location.z = 42.0
                MainMenuCrates.teleport(MainMenuCrates.location)
            }
        }, 0L, 1L)
        return true
    }


}