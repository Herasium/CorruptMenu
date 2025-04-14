package dev.herasium.corruptMenu

import dev.herasium.corruptMenu.commands.MainMenu
import org.bukkit.plugin.java.JavaPlugin;
import dev.herasium.corruptMenu.commands.SitOnArmorStand
import dev.herasium.corruptMenu.internal.Menu
import dev.herasium.corruptMenu.commands.CratesMenu
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerQuitEvent


class CorruptMenu : JavaPlugin(), Listener {

    val menuList = mutableListOf<dev.herasium.corruptMenu.internal.Menu>()

    val playerClick: MutableMap<Player, Boolean> = mutableMapOf()
    val playerMenu : MutableMap<Player, Menu> = mutableMapOf()

    override fun onEnable() {
        this.getCommand("mainmenu")?.setExecutor(MainMenu(this))
        this.getCommand("cratesmenu")?.setExecutor(CratesMenu(this))
        Bukkit.getPluginManager().registerEvents(this,this)
    }

    override fun onDisable() {
        for (menu in menuList) {
            menu.quit()
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent){
        val player = event.player
        playerClick[player] = true
    }

    @EventHandler
    fun onPlayerQuit(event:PlayerQuitEvent) {
        if (event.player in playerMenu) {
            playerMenu[event.player]?.quit()
        }
    }
}
