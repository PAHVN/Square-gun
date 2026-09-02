package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import gdx.liftoff.network.NetworkClient

class Main : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    private lateinit var urlField: TextField
    private lateinit var connectButton: TextButton

    private lateinit var prefs: Preferences
    private lateinit var network: NetworkClient

    private var serverUrl = ""

    private var playerX = 200f
    private var playerY = 300f

    private val squareSize = 100f

    private data class RemotePlayer(
        val id: String,
        var x: Float = 400f,
        var y: Float = 300f
    )

    private val remotePlayers =
        mutableMapOf<String, RemotePlayer>()

    private fun connectToServer() {

        serverUrl = urlField.text.trim()

        if (serverUrl.isEmpty()) {
            connectButton.setText("EMPTY URL")
            return
        }

        prefs.putString("serverUrl", serverUrl)
        prefs.flush()

        connectButton.setText("CONNECTING...")

        network = NetworkClient(

            serverUrl,

            onPlayerJoin = { id ->

                Gdx.app.postRunnable {

                    remotePlayers[id] =
                        RemotePlayer(id)

                    connectButton.setText("CONNECTED")
                }
            },

            onPlayerLeave = { id ->

                Gdx.app.postRunnable {

                    remotePlayers.remove(id)
                }
            },

            onPlayerMove = { id, x, y ->

                Gdx.app.postRunnable {

                    remotePlayers[id]?.let {

                        it.x = x
                        it.y = y
                    }
                }
            }
        )

        network.connect()
    }

    override fun create() {

        shape = ShapeRenderer()

        prefs =
            Gdx.app.getPreferences("squaregun")

        serverUrl =
            prefs.getString("serverUrl", "")

        skin =
            Skin(Gdx.files.internal("uiskin.json"))

        stage =
            Stage(ScreenViewport())

        Gdx.input.inputProcessor = stage

        urlField =
            TextField(serverUrl, skin)

        urlField.setSize(900f, 90f)
        urlField.setPosition(40f, 600f)

        connectButton =
            TextButton("CONNECT", skin)

        connectButton.setSize(350f, 90f)
        connectButton.setPosition(40f, 480f)

        connectButton.addListener(

            object : ClickListener() {

                override fun clicked(
                    event: InputEvent?,
                    x: Float,
                    y: Float
                ) {

                    connectToServer()
                }
            }
        )

        stage.addActor(urlField)
        stage.addActor(connectButton)
    }

    override fun render() {

        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.20f,
            1f
        )

        // Cho UI xử lý trước
        stage.act(Gdx.graphics.deltaTime)

        // Chỉ di chuyển khi KHÔNG chạm vào UI
	if (Gdx.input.isTouched) {

    playerX = Gdx.input.x.toFloat()

    playerY =
        (Gdx.graphics.height -
         Gdx.input.y).toFloat()

    if (::network.isInitialized) {
        network.send("MOVE $playerX $playerY")
    }
}

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        // Player của mình (đỏ)
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

        // Player khác (xanh)
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

        stage.draw()
    }

    override fun dispose() {

        if (::network.isInitialized) {
            network.disconnect()
        }

        stage.dispose()
        skin.dispose()
        shape.dispose()
    }
}
