package gdx.liftoff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.utils.viewport.ScreenViewport
import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import gdx.liftoff.network.NetworkClient

class Main : ApplicationAdapter() {

	private lateinit var shape: ShapeRenderer
	private lateinit var stage: Stage
private lateinit var skin: Skin

private lateinit var urlField: TextField
private lateinit var connectButton: TextButton

	private lateinit var prefs: com.badlogic.gdx.Preferences

private var serverUrl = ""

    private var playerX = 200f
    private var playerY = 300f

    private val squareSize = 100f

	private val connectButtonX = 40f
private val connectButtonY = 40f
private val connectButtonWidth = 300f
private val connectButtonHeight = 100f

private var connected = false
private var connectPressed = false

    private data class RemotePlayer(
        val id: String,
        var x: Float = 400f,
        var y: Float = 300f
    )

    private val remotePlayers = mutableMapOf<String, RemotePlayer>()

	private fun connectToServer() {

    network = NetworkClient(
        serverUrl,

        onPlayerJoin = { id ->
            println("OTHER PLAYER JOINED: $id")
            remotePlayers[id] = RemotePlayer(id)
        },

        onPlayerLeave = { id ->
            println("OTHER PLAYER LEFT: $id")
            remotePlayers.remove(id)
        },

        onPlayerMove = { id, x, y ->
            remotePlayers[id]?.let {
                it.x = x
                it.y = y
            }
        }
    )

    println("Connecting to $serverUrl")
    network.connect()
}

    override fun create() {
        shape = ShapeRenderer()
	skin = Skin(Gdx.files.internal("uiskin.json"))

stage = Stage(ScreenViewport())
Gdx.input.inputProcessor = stage

prefs = Gdx.app.getPreferences("squaregun")
serverUrl = prefs.getString("serverUrl", "")

skin = Skin(Gdx.files.internal("uiskin.json"))

stage = Stage(ScreenViewport())
Gdx.input.inputProcessor = stage

urlField = TextField(serverUrl, skin)
    }

    override fun render() {
        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.2f,
            1f
        )

	val touchX = com.badlogic.gdx.Gdx.input.x.toFloat()
val touchY =
    (com.badlogic.gdx.Gdx.graphics.height -
     com.badlogic.gdx.Gdx.input.y).toFloat()

	if (com.badlogic.gdx.Gdx.input.isTouched) {

    playerX = touchX
    playerY = touchY

    if (::network.isInitialized) {
    network.send("MOVE $playerX $playerY")
}
}

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        // YOU = RED
        shape.color.set(
            1f,
            0f,
            0f,
            1f
        )

        shape.rect(
            playerX - squareSize / 2,
            playerY - squareSize / 2,
            squareSize,
            squareSize
        )

        // OTHER PLAYERS = BLUE
        shape.color.set(
            0f,
            0f,
            1f,
            1f
        )

        for (player in remotePlayers.values) {

            shape.rect(
                player.x - squareSize / 2,
                player.y - squareSize / 2,
                squareSize,
                squareSize
            )
        }


        shape.end()
stage.act(Gdx.graphics.deltaTime)
stage.draw()
    }

    override fun dispose() {
    if (::network.isInitialized) {
        network.disconnect()
    }
    shape.dispose()
}

}
