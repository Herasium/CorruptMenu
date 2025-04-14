package dev.herasium.corruptMenu.internal.menu

import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.internal.MenuComponent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay

class Cursor(private var SpawnLocation: Location, private var player: Player, private var parser: TextParser): MenuComponent {
    lateinit var CursorEntity: TextDisplay

    override fun setup(cursorText: String) {
        CursorEntity = SpawnLocation.world.spawn(SpawnLocation, TextDisplay::class.java)
        CursorEntity.text(parser.parseText(cursorText,player))
        CursorEntity.isSeeThrough = true
        CursorEntity.backgroundColor = Color.fromARGB(0,0,0,0)
        CursorEntity.billboard = Display.Billboard.CENTER
    }

    fun move(newLocation: Location) {
        CursorEntity.teleport(newLocation)
    }

    override fun kill() {
        CursorEntity.remove()
    }
}