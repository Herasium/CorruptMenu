package dev.herasium.corruptMenu.internal.menu

import dev.herasium.corruptCore.open.RayTrace
import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.internal.MenuComponent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay
import org.bukkit.util.BoundingBox
import java.util.function.Consumer


internal class Button(private val boundingBox: BoundingBox, private val SpawnLocation: Location, private val parser: TextParser, private val player: Player): MenuComponent {

    var isHovered = false
    lateinit var ButtonEntity: TextDisplay

    override fun setup(buttonText: String) {
        ButtonEntity = SpawnLocation.world.spawn(SpawnLocation, TextDisplay::class.java)
        ButtonEntity.text(parser.parseText(buttonText,player))
        ButtonEntity.isSeeThrough = true
        ButtonEntity.backgroundColor = Color.fromARGB(0,0,0,0)
        ButtonEntity.billboard = Display.Billboard.CENTER
    }

    fun update(rayTrace: RayTrace, isClicking: Boolean) {
        val hovering = rayTrace.intersects(boundingBox, 10.0, 0.1)

        if (hovering != isHovered) {
            val newLocation = ButtonEntity.location.clone().apply { z = if (hovering) 41.9 else 42.0 }
            ButtonEntity.teleport(newLocation)
            isHovered = hovering
        }


    }

    override fun kill() {
        ButtonEntity.remove()
    }
}