package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import gdx.liftoff.network.NetworkClient

class Main : ApplicationAdapter() {

	private var serverUrl =
    "wss://regime-sports-paid-dryer.trycloudflare.com"
    private lateinit var shape: ShapeRenderer
    private lateinit var network: NetworkClient

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

    if (!connectPressed &&
        touchX >= connectButtonX &&
        touchX <= connectButtonX + connectButtonWidth &&
        touchY >= connectButtonY &&
        touchY <= connectButtonY + connectButtonHeight
    ) {
        connectPressed = true
        connectToServer()
    }

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

	// CONNECT BUTTON
shape.color.set(0f, 0.8f, 0f, 1f)

shape.rect(
    connectButtonX,
    connectButtonY,
    connectButtonWidth,
    connectButtonHeight
)

        shape.end()
    }

    override fun dispose() {
    if (::network.isInitialized) {
        network.disconnect()
    }
    shape.dispose()
}

}
