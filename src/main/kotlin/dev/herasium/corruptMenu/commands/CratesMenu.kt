package dev.herasium.corruptMenu.commands

import dev.herasium.corruptMenu.CorruptMenu
import dev.herasium.corruptMenu.menu.CratesMenu
import dev.herasium.corruptMenu.menu.MainMenu
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.plugin.java.JavaPlugin

class CratesMenu(private val plugin: CorruptMenu) : CommandExecutor, Listener {

    override fun onCommand(sender: CommandSender, p1: Command, p2: String, p3: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("${ChatColor.RED}Only players can use this command!")
            return false
        }

        val menu = CratesMenu(sender, plugin)
        menu.setup()

        return true
    }

}