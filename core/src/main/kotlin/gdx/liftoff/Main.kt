package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.InputMultiplexer
import com.badlogic.gdx.Preferences
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.utils.ScreenUtils
import com.badlogic.gdx.utils.viewport.ScreenViewport
import gdx.liftoff.network.NetworkClient
import kotlin.math.sqrt

class Main : ApplicationAdapter() {

    // =========================
    // Render
    // =========================

    private lateinit var shape: ShapeRenderer
    private lateinit var camera: OrthographicCamera

    // =========================
    // UI
    // =========================

	private lateinit var joystick: Joystick

    private lateinit var stage: Stage
    private lateinit var skin: Skin

    private lateinit var urlField: TextField
    private lateinit var connectButton: TextButton

    // =========================
    // Network
    // =========================

    private lateinit var network: NetworkClient
    private lateinit var prefs: Preferences

    private var serverUrl = ""

    // =========================
    // Player
    // =========================

    private var playerX = 600f
    private var playerY = 400f

    private val playerSize = 100f
    private val moveSpeed = 450f

    // =========================
    // Camera
    // =========================

    private val cameraLerp = 0.12f

    // =========================
    // Joystick
    // =========================

    private val joystickCenter =
        Vector2(180f,180f)

    private val joystickRadius = 120f
    private val knobRadius = 45f

    private var moveX = 0f
    private var moveY = 0f

    // =========================
    // Remote Players
    // =========================

    private data class RemotePlayer(

        val id:String,

        var x:Float = 0f,
        var y:Float = 0f

    )

    private val remotePlayers =
        mutableMapOf<String,RemotePlayer>()

    // =========================
    // CONNECT
    // =========================

    private fun connectToServer(){

        serverUrl = urlField.text.trim()

        if(serverUrl.isEmpty()){

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

                Gdx.app.postRunnable{

                    remotePlayers[id] =
                        RemotePlayer(id)

                    connectButton.setText(
                        "CONNECTED"
                    )

                }

            },

            onPlayerLeave = { id ->

                Gdx.app.postRunnable{

                    remotePlayers.remove(id)

                }

            },

            onPlayerMove = { id,x,y ->

                Gdx.app.postRunnable{

                    remotePlayers[id]?.let{

                        it.x = x
                        it.y = y

                    }

                }

            }

        )

        network.connect()

    }

    // =========================
    // CREATE
    // =========================

    override fun create(){

        shape = ShapeRenderer()

        camera = OrthographicCamera()

        camera.setToOrtho(
            false,
            Gdx.graphics.width.toFloat(),
            Gdx.graphics.height.toFloat()
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

            object:ClickListener(){

                override fun clicked(

                    event:InputEvent?,
                    x:Float,
                    y:Float

                ){

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

		joystick = Joystick(
    180f,
    180f,
    120f
)

    }

    // =========================
    // UPDATE INPUT
    // =========================

    private fun updateInput(){

        if(Gdx.input.isTouched){

            val tx =
                Gdx.input.x.toFloat()

            val ty =
                Gdx.graphics.height -
                Gdx.input.y.toFloat()

            val dx =
                tx - joystickCenter.x

            val dy =
                ty - joystickCenter.y

            val dist =
                sqrt(dx*dx + dy*dy)

            if(dist < joystickRadius){

                moveX =
                    dx / joystickRadius

                moveY =
                    dy / joystickRadius

            }else{

                moveX =
                    dx / dist

                moveY =
                    dy / dist

            }

        }else{

            moveX = 0f
            moveY = 0f

        }

    }

    // =========================
    // UPDATE PLAYER
    // =========================

    private fun updatePlayer(){

        playerX +=
            moveX *
            moveSpeed *
            Gdx.graphics.deltaTime

        playerY +=
            moveY *
            moveSpeed *
            Gdx.graphics.deltaTime

        if(::network.isInitialized){

            network.send(
                "MOVE $playerX $playerY"
            )

        }

    }

    // =========================
    // UPDATE CAMERA
    // =========================

    private fun updateCamera(){

        camera.position.x +=

            (playerX -
             camera.position.x)
                * cameraLerp

        camera.position.y +=

            (playerY -
             camera.position.y)
                * cameraLerp

        camera.update()

        shape.projectionMatrix =
            camera.combined

    }

    // =========================
    // DRAW WORLD
    // =========================

    private fun drawWorld(){

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        // Player mình

        shape.color = Color.RED

        shape.rect(

            playerX -
                playerSize/2,

            playerY -
                playerSize/2,

            playerSize,

            playerSize

        )

        // Player khác

        shape.color = Color.BLUE

        for(player in remotePlayers.values){

            shape.rect(

                player.x -
                    playerSize/2,

                player.y -
                    playerSize/2,

                playerSize,

                playerSize

            )

        }

        shape.end()

    }

    // =========================
    // DRAW HUD
    // =========================

    private fun drawHud(){

        shape.projectionMatrix =
            stage.camera.combined

        shape.begin(
            ShapeRenderer.ShapeType.Filled
        )

        shape.color =
            Color.DARK_GRAY

        shape.circle(

            joystickCenter.x,

            joystickCenter.y,

            joystickRadius

        )

        shape.color =
            Color.WHITE

        shape.circle(

            joystickCenter.x +
                moveX *
                joystickRadius,

            joystickCenter.y +
                moveY *
                joystickRadius,

            knobRadius

        )

        shape.end()

    }

    // =========================
    // RENDER
    // =========================

    override fun render(){

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

moveX = joystick.moveX
moveY = joystick.moveY

        updatePlayer()

        updateCamera()

        drawWorld()

        drawHud()

        stage.draw()

    }

    // =========================
    // DISPOSE
    // =========================

    override fun dispose(){

        if(::network.isInitialized){

            network.disconnect()

        }

        stage.dispose()

        skin.dispose()

        shape.dispose()

    }

}
