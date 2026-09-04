package gdx.liftoff

import com.badlogic.gdx.scenes.scene2d.ui.TextField
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import com.badlogic.gdx.scenes.scene2d.ui.TextButton
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener
import com.badlogic.gdx.scenes.scene2d.InputEvent
import com.badlogic.gdx.utils.viewport.ScreenViewport

class MainMenuScreen(
    private val game: SquareGunGame
) : ScreenAdapter() {

private val prefs =
    Gdx.app.getPreferences("squaregun")

    private lateinit var nicknameField: TextField

private lateinit var shapeButton: TextButton
private lateinit var colorButton: TextButton

private val shapes = listOf(
    "square",
    "circle",
    "triangle"
)

private val colors = listOf(
    "red",
    "blue",
    "green",
    "yellow"
)

private var currentShape =
    shapes.indexOf(
        prefs.getString("shape", "square")
    ).coerceAtLeast(0)

private var currentColor =
    colors.indexOf(
        prefs.getString("color", "red")
    ).coerceAtLeast(0)
    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    init {


	nicknameField = TextField(
    prefs.getString(
        "nickname",
        "Player"
    ),
    skin
)

nicknameField.setSize(
    700f,
    90f
)

nicknameField.setPosition(
    160f,
    520f
)

stage.addActor(nicknameField)

		shapeButton =
    TextButton(
        "Shape: ${shapes[currentShape]}",
        skin
    )

shapeButton.setSize(
    700f,
    90f
)

shapeButton.setPosition(
    160f,
    400f
)

shapeButton.addListener(object : ClickListener() {

    override fun clicked(
        event: InputEvent?,
        x: Float,
        y: Float
    ) {

        currentShape =
            (currentShape + 1) % shapes.size

        shapeButton.setText(
            "Shape: ${shapes[currentShape]}"
        )
    }
})

stage.addActor(shapeButton)

colorButton =
    TextButton(
        "Color: ${colors[currentColor]}",
        skin
    )
colorButton.setSize(500f, 80f)
colorButton.setPosition(250f, 280f)

colorButton.addListener(object : ClickListener() {
    override fun clicked(
        event: InputEvent?,
        x: Float,
        y: Float
    ) {
        currentColor =
            (currentColor + 1) % colors.size

        colorButton.setText(
            "Color: ${colors[currentColor]}"
        )
    }
})

stage.addActor(colorButton)



        val play = TextButton("PLAY", skin)

        play.setSize(400f, 100f)
        play.setPosition(300f, 120f)

        play.addListener(object : ClickListener() {
            override fun clicked(
                event: InputEvent?,
                x: Float,
                y: Float
            ) {

prefs.putString(
    "nickname",
    nicknameField.text
)

prefs.putString(
    "shape",
    shapes[currentShape]
)

prefs.putString(
    "color",
    colors[currentColor]
)

prefs.flush()
                game.screen = GameScreen(game)
            }
        })

        stage.addActor(play)

        Gdx.input.inputProcessor = stage
    }

    override fun render(delta: Float) {

        Gdx.gl.glClearColor(0.15f,0.15f,0.2f,1f)
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT)

        stage.act(delta)
        stage.draw()
    }

    override fun dispose() {
        stage.dispose()
        skin.dispose()
    }
}
