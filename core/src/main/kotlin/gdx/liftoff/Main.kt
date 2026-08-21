package gdx.liftoff

import com.badlogic.gdx.ApplicationAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.glutils.ShapeRenderer
import com.badlogic.gdx.utils.ScreenUtils

class Main : ApplicationAdapter() {

    private lateinit var shape: ShapeRenderer

    private var squareX = 200f
    private var squareY = 300f

    private val squareSize = 100f

    override fun create() {
        shape = ShapeRenderer()
    }

    override fun render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f)

        // Touch / drag
        if (com.badlogic.gdx.Gdx.input.isTouched) {
            squareX = com.badlogic.gdx.Gdx.input.x.toFloat()
            squareY =
                (com.badlogic.gdx.Gdx.graphics.height - com.badlogic.gdx.Gdx.input.y).toFloat()
        }

        shape.begin(ShapeRenderer.ShapeType.Filled)

        shape.color.set(1f, 0f, 0f, 1f)
        shape.rect(
            squareX - squareSize / 2,
            squareY - squareSize / 2,
            squareSize,
            squareSize
        )

        shape.end()
    }

    override fun dispose() {
        shape.dispose()
    }
}
