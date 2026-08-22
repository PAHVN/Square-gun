package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils
import gdx.liftoff.network.NetworkClient

class Main : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer
    private lateinit var network: NetworkClient

    private var squareX = 200f
    private var squareY = 300f

    private val squareSize = 100f

	private var otherPlayerVisible = false
private var otherPlayerX = 400f
private var otherPlayerY = 300f

    override fun create() {
        shape = ShapeRenderer()

        network = NetworkClient(
    "wss://regime-sports-paid-dryer.trycloudflare.com",

    onPlayerJoin = { id ->
        println("OTHER PLAYER JOINED: $id")
        otherPlayerVisible = true
    },

    onPlayerLeave = { id ->
        println("OTHER PLAYER LEFT: $id")
        otherPlayerVisible = false
    },

    onPlayerMove = { id, x, y ->
        otherPlayerX = x
        otherPlayerY = y
    }
)

        println("Connecting to Square Gun server...")
        network.connect()
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)

        if (com.badlogic.gdx.Gdx.input.isTouched) {
            squareX = com.badlogic.gdx.Gdx.input.x.toFloat()
            squareY =
                (com.badlogic.gdx.Gdx.graphics.height -
                 com.badlogic.gdx.Gdx.input.y).toFloat()

            network.send("MOVE $squareX $squareY")
        }

        shape.begin(ShapeRenderer.ShapeType.Filled)

        shape.color.set(1f, 0f, 0f, 1f)

        shape.rect(
            squareX - squareSize / 2,
            squareY - squareSize / 2,
            squareSize,
            squareSize
        )

	if (otherPlayerVisible) {
    shape.color.set(0f, 0f, 1f, 1f)

    shape.rect(
        otherPlayerX - squareSize / 2,
        otherPlayerY - squareSize / 2,
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
