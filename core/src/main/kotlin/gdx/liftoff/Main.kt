package gdx.liftoff

import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.g2d.BitmapFont
import com.badlogic.gdx.graphics.g2d.SpriteBatch
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

class GameScreen(
    private val game: SquareGunGame
) : ScreenAdapter() {

    private lateinit var shape: ShapeRenderer

	private lateinit var batch: SpriteBatch
private lateinit var font: BitmapFont

    private lateinit var camera: OrthographicCamera
    private lateinit var cameraController: CameraController

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    private lateinit var urlField: TextField
    private lateinit var connectButton: TextButton

    private lateinit var prefs: Preferences
private lateinit var moveJoystick: Joystick
private lateinit var aimJoystick: Joystick

	private val mapWidth = 3000f
private val mapHeight = 3000f

    private lateinit var player: Player

	private lateinit var profile: PlayerProfile

	private var lastSentX = Float.NaN
private var lastSentY = Float.NaN

private var sendTimer = 0f
private val sendInterval = 0.05f // 20 lần/giây

    private lateinit var network: NetworkClient

    private var serverUrl = ""

    private val remotePlayers =
        mutableMapOf<String, RemotePlayer>()

    private fun connectToServer() {

	if (::network.isInitialized && network.isConnected()) {
    return
}


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

		profile.nickname,
profile.shape,
profile.color,

            onConnected = {
                Gdx.app.postRunnable {

                    connectButton.setText(
                        "CONNECTED"
                    )

                }

            },


	onDisconnected = {

    Gdx.app.postRunnable {

        connectButton.setText("CONNECT")
        connectButton.isDisabled = false

    }

},

	onPlayerJoin = { id ->

    Gdx.app.postRunnable {

        remotePlayers[id] = RemotePlayer(id)

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

            },

	onPlayerInfo = { id, nickname, shape, color ->

    Gdx.app.postRunnable {

        remotePlayers[id]?.nickname = nickname
        remotePlayers[id]?.shape = shape
        remotePlayers[id]?.color = color

    }

}

        )

	connectButton.isDisabled = true

        network.connect()

    }
    init {

        shape = ShapeRenderer()

	batch = SpriteBatch()
font = BitmapFont()

        camera = OrthographicCamera()

        camera.setToOrtho(
            false,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
        )

        cameraController =
            CameraController(camera)

        player = Player()

moveJoystick = Joystick(
    180f,
    180f,
    120f
)

aimJoystick = Joystick(
    Gdx.graphics.width - 180f,
    180f,
    120f
)

        prefs =
            Gdx.app.getPreferences(
                "squaregun"
            )

	profile = PlayerProfile(

    prefs.getString(
        "nickname",
        "Player"
    ),

    prefs.getString(
        "shape",
        "square"
    ),

    prefs.getString(
        "color",
        "red"
    )

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

	// nền map
shape.color = Color(0.25f, 0.35f, 0.25f, 1f)

shape.rect(
    0f,
    0f,
    mapWidth,
    mapHeight
)
shape.color = Color.DARK_GRAY

val wall = 40f

// dưới
shape.rect(
    0f,
    0f,
    mapWidth,
    wall
)

// trên
shape.rect(
    0f,
    mapHeight - wall,
    mapWidth,
    wall
)

// trái
shape.rect(
    0f,
    0f,
    wall,
    mapHeight
)

// phải
shape.rect(
    mapWidth - wall,
    0f,
    wall,
    mapHeight
)

		shape.color = when (profile.color) {
    "red" -> Color.RED
    "blue" -> Color.BLUE
    "green" -> Color.GREEN
    "yellow" -> Color.YELLOW
    else -> Color.RED
}
when (profile.shape) {

    "square" -> {

        shape.rect(
            player.x - player.size / 2f,
            player.y - player.size / 2f,
            player.size,
            player.size
        )

    }

    "circle" -> {

        shape.circle(
            player.x,
            player.y,
            player.size / 2f
        )

    }

    "triangle" -> {

        val s = player.size

        shape.triangle(
            player.x,
            player.y + s / 2f,
            player.x - s / 2f,
            player.y - s / 2f,
            player.x + s / 2f,
            player.y - s / 2f
        )

    }
}

	for (remote in remotePlayers.values) {

    shape.color = when (remote.color) {
        "red" -> Color.RED
        "blue" -> Color.BLUE
        "green" -> Color.GREEN
        "yellow" -> Color.YELLOW
        else -> Color.BLUE
    }

    when (remote.shape) {

        "square" -> {

            shape.rect(
                remote.x - player.size / 2f,
                remote.y - player.size / 2f,
                player.size,
                player.size
            )

        }

        "circle" -> {

            shape.circle(
                remote.x,
                remote.y,
                player.size / 2f
            )

        }

        "triangle" -> {

            val s = player.size

            shape.triangle(
                remote.x,
                remote.y + s / 2f,
                remote.x - s / 2f,
                remote.y - s / 2f,
                remote.x + s / 2f,
                remote.y - s / 2f
            )

        }

    }

}

        shape.end()

	batch.projectionMatrix = camera.combined
batch.begin()

// nickname của mình
font.draw(
    batch,
    profile.nickname,
    player.x - 20f,
    player.y + player.size
)

// nickname remote
for (remote in remotePlayers.values) {

    font.draw(
        batch,
        remote.nickname,
        remote.x - 20f,
        remote.y + player.size
    )
}

batch.end()

    }

    private fun drawHud() {

        shape.projectionMatrix =
            stage.camera.combined

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

		batch.projectionMatrix = stage.camera.combined

batch.begin()

font.draw(
    batch,
    "DEV-3D-01",
    20f,
    Gdx.graphics.height - 20f
)

batch.end()

// Move joystick
shape.color = Color.DARK_GRAY

shape.circle(
    moveJoystick.center.x,
    moveJoystick.center.y,
    moveJoystick.radius
)

shape.color = Color.WHITE

shape.circle(
    moveJoystick.center.x +
        moveJoystick.moveX * moveJoystick.radius,
    moveJoystick.center.y +
        moveJoystick.moveY * moveJoystick.radius,
    moveJoystick.knobRadius
)

// Aim joystick
shape.color = Color.DARK_GRAY

shape.circle(
    aimJoystick.center.x,
    aimJoystick.center.y,
    aimJoystick.radius
)

shape.color = Color.WHITE

shape.circle(
    aimJoystick.center.x +
        aimJoystick.moveX * aimJoystick.radius,
    aimJoystick.center.y +
        aimJoystick.moveY * aimJoystick.radius,
    aimJoystick.knobRadius
)
        shape.end()

    }

    override fun render(delta: Float) {

        ScreenUtils.clear(
            0.15f,
            0.15f,
            0.20f,
            1f
        )

        stage.act(delta)
player.update(
    moveJoystick.moveX,
    moveJoystick.moveY
)
player.update(
    moveJoystick.moveX,
    moveJoystick.moveY
)
player.x =
    player.x.coerceIn(
        player.size / 2f,
        mapWidth - player.size / 2f
    )

player.y =
    player.y.coerceIn(
        player.size / 2f,
        mapHeight - player.size / 2f
    )

if (::network.isInitialized) {

    sendTimer += delta

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
	batch.dispose()
font.dispose()
	
    }

}
