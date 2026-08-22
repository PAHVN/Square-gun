package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import gdx.liftoff.network.NetworkClient

class Main : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer
    private lateinit var network: NetworkClient

    private var playerX = 200f
    private var playerY = 300f

    private val squareSize = 100f

    private data class RemotePlayer(
        val id: String,
        var x: Float = 400f,
        var y: Float = 300f
    )

    private val remotePlayers = mutableMapOf<String, RemotePlayer>()

    override fun create() {
        shape = ShapeRenderer()

        network = NetworkClient(
            "wss://regime-sports-paid-dryer.trycloudflare.com",

            onPlayerJoin = { id ->
                println("OTHER PLAYER JOINED: $id")

                remotePlayers[id] = RemotePlayer(id)
            },

            onPlayerLeave = { id ->
                println("OTHER PLAYER LEFT: $id")

                remotePlayers.remove(id)
            },

            onPlayerMove = { id, x, y ->
                val player = remotePlayers[id]

                if (player != null) {
                    player.x = x
                    player.y = y
                }
            }
        )

        println("Connecting to Square Gun server...")
        network.connect()
    }

    override fun render() {
        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.2f,
            1f
        )

        if (com.badlogic.gdx.Gdx.input.isTouched) {

            playerX =
                com.badlogic.gdx.Gdx.input.x.toFloat()

            playerY =
                (
                    com.badlogic.gdx.Gdx.graphics.height -
                    com.badlogic.gdx.Gdx.input.y
                ).toFloat()

            network.send(
                "MOVE $playerX $playerY"
            )
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
    }

    override fun dispose() {
        network.disconnect()
        shape.dispose()
    }
}
