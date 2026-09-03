package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
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
    private lateinit var camera: OrthographicCamera
    private lateinit var cameraController: CameraController

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    private lateinit var urlField: TextField
    private lateinit var connectButton: TextButton

    private lateinit var prefs: Preferences
    private lateinit var joystick: Joystick

    private lateinit var player: Player

	private var lastSentX = Float.NaN
private var lastSentY = Float.NaN

private var sendTimer = 0f
private val sendInterval = 0.05f // 20 lần/giây

    private lateinit var network: NetworkClient

    private var serverUrl = ""

    private val remotePlayers =
        mutableMapOf<String, RemotePlayer>()

    private fun connectToServer() {

        serverUrl = urlField.text.trim()

        if (serverUrl.isEmpty()) {

            connectButton.setText("EMPTY URL")
            return

        }

        prefs.putString(
            "serverUrl",
            serverUrl
        )

        prefs.flush()

        connectButton.setText(
            "CONNECTING..."
        )

        network = NetworkClient(

            serverUrl,

            onPlayerJoin = { id ->

                Gdx.app.postRunnable {

                    remotePlayers[id] =
                        RemotePlayer(id)

                    connectButton.setText(
                        "CONNECTED"
                    )

                }

            },

            onPlayerLeave = { id ->

                Gdx.app.postRunnable {

                    remotePlayers.remove(id)

                }

            },

            onPlayerMove = { id, x, y ->

                Gdx.app.postRunnable {

                    remotePlayers[id]?.x = x
                    remotePlayers[id]?.y = y

                }

            }

        )

        network.connect()

    }
    override fun create() {

        shape = ShapeRenderer()

        camera = OrthographicCamera()

        camera.setToOrtho(
            false,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        cameraController =
            CameraController(camera)

        player = Player()

        joystick =
            Joystick(
                180f,
                180f,
                120f
            )

        prefs =
            Gdx.app.getPreferences(
                "squaregun"
            )

        serverUrl =
            prefs.getString(
                "serverUrl",
                ""
            )

        skin =
            Skin(
                Gdx.files.internal(
                    "uiskin.json"
                )
            )

        stage =
            Stage(
                ScreenViewport()
            )

        urlField =
            TextField(
                serverUrl,
                skin
            )

        urlField.setSize(
            900f,
            90f
        )

        urlField.setPosition(
            40f,
            600f
        )

        connectButton =
            TextButton(
                "CONNECT",
                skin
            )

        connectButton.setSize(
            350f,
            90f
        )

        connectButton.setPosition(
            40f,
            480f
        )

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

        stage.addActor(
            urlField
        )

        stage.addActor(
            connectButton
        )

        Gdx.input.inputProcessor =
            InputMultiplexer(
                stage
            )

    }
    private fun drawWorld() {

        shape.projectionMatrix =
            camera.combined

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        shape.color = Color.RED

        shape.rect(

            player.x -
                player.size / 2f,

            player.y -
                player.size / 2f,

            player.size,

            player.size

        )

        shape.color = Color.BLUE

        for (remote in remotePlayers.values) {

            shape.rect(

                remote.x -
                    player.size / 2f,

                remote.y -
                    player.size / 2f,

                player.size,

                player.size

            )

        }

        shape.end()

    }

    private fun drawHud() {

        shape.projectionMatrix =
            stage.camera.combined

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        shape.color =
            Color.DARK_GRAY

        shape.circle(

            joystick.center.x,
            joystick.center.y,
            joystick.radius

        )

        shape.color =
            Color.WHITE

        shape.circle(

            joystick.center.x +
                joystick.moveX *
                joystick.radius,

            joystick.center.y +
                joystick.moveY *
                joystick.radius,

            joystick.knobRadius

        )

        shape.end()

    }

    override fun render() {

        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.20f,
            1f
        )

        stage.act(
            Gdx.graphics.deltaTime
        )

        joystick.update()

        player.update(

    joystick.moveX,
    joystick.moveY
)

if (::network.isInitialized) {

    sendTimer += Gdx.graphics.deltaTime

    if (sendTimer >= sendInterval) {

        sendTimer = 0f

        if (player.x != lastSentX ||
            player.y != lastSentY) {

            network.send(
                "MOVE ${player.x} ${player.y}"
            )

            lastSentX = player.x
            lastSentY = player.y

        }

    }

}

        cameraController.update(
            player.x,
            player.y
        )

        drawWorld()

        drawHud()

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
