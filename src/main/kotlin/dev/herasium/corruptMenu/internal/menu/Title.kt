package dev.herasium.corruptMenu.internal.menu

import dev.herasium.corruptCore.open.TextParser
import dev.herasium.corruptMenu.internal.MenuComponent
import org.bukkit.Color
import org.bukkit.Location
import org.bukkit.entity.Display
import org.bukkit.entity.Player
import org.bukkit.entity.TextDisplay

class Title(private var SpawnLocation: Location, private var player: Player, private var parser: TextParser): MenuComponent {
    lateinit var TitleEntity: TextDisplay

    override fun setup(titleText: String) {
        TitleEntity = SpawnLocation.world.spawn(SpawnLocation, TextDisplay::class.java)
        TitleEntity.text(parser.parseText(titleText,player))
        TitleEntity.isSeeThrough = true
        TitleEntity.backgroundColor = Color.fromARGB(0,0,0,0)
        TitleEntity.billboard = Display.Billboard.CENTER
    }

    fun move(newLocation: Location) {
        TitleEntity.teleport(newLocation)
    }

    override fun kill() {
        TitleEntity.remove()
    }
}
