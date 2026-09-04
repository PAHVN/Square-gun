package gdx.liftoff

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

    private val stage = Stage(ScreenViewport())
    private val skin = Skin(Gdx.files.internal("uiskin.json"))

    init {

        val play = TextButton("PLAY", skin)

        play.setSize(400f, 100f)
        play.setPosition(300f, 350f)

        play.addListener(object : ClickListener() {
            override fun clicked(
                event: InputEvent?,
                x: Float,
                y: Float
            ) {
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
