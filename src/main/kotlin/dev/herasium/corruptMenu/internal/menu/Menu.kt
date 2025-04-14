package dev.herasium.corruptMenu.internal.menu

import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.internal.MenuComponent
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.entity.ArmorStand
import org.bukkit.entity.Player

class Menu(private val parser: TextParser, private val PlayerLocation: Location, private val player: Player): MenuComponent {

    val world = PlayerLocation.world
    lateinit var standEntity: ArmorStand


    override fun setup(text: String) {
        standEntity = world.spawn(PlayerLocation, ArmorStand::class.java)

        standEntity.maxHealth = 1.0 //Hide armorstand Hearths on client
        standEntity.setAI(false)
        standEntity.isInvisible = true
        standEntity.setGravity(false)
        standEntity.setCanMove(true) // Make it hidden and stop it from being affected by gravity

        player.gameMode = GameMode.SPECTATOR

        var NewPlayerLocation = PlayerLocation.clone()
        NewPlayerLocation.yaw = 0f
        NewPlayerLocation.pitch = 0f
        player.teleport(NewPlayerLocation)

        player.spectatorTarget = standEntity
        standEntity.addPassenger(player)

    }

    override fun kill() {
        standEntity.remove()
    }



}