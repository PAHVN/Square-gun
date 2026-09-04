package gdx.liftoff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ScreenAdapter
import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.VertexAttributes
import com.badlogic.gdx.graphics.g3d.Environment
import com.badlogic.gdx.graphics.g3d.Material
import com.badlogic.gdx.graphics.g3d.Model
import com.badlogic.gdx.graphics.g3d.ModelBatch
import com.badlogic.gdx.graphics.g3d.ModelInstance
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder
import com.badlogic.gdx.graphics.PerspectiveCamera

class Test3DScreen(
    private val game: SquareGunGame
) : ScreenAdapter() {

    private val modelBatch = ModelBatch()

    private val environment = Environment().apply {
        set(
            ColorAttribute.createAmbientLight(
                1f,
                1f,
                1f,
                1f
            )
        )
    }

    private val camera = PerspectiveCamera(
        67f,
        Gdx.graphics.width.toFloat(),
        Gdx.graphics.height.toFloat()
    ).apply {

        position.set(
            0f,
            8f,
            12f
        )

        lookAt(
            0f,
            0f,
            0f
        )

        near = 0.1f
        far = 100f

        update()
    }

    private val model: Model
    private val cube: ModelInstance

    init {

        val builder = ModelBuilder()

        model = builder.createBox(
            2f,
            2f,
            2f,
            Material(
                ColorAttribute.createDiffuse(
                    com.badlogic.gdx.graphics.Color.RED
                )
            ),
            VertexAttributes.Usage.Position.toLong() or
VertexAttributes.Usage.Normal.toLong()
        )

        cube = ModelInstance(model)
    }

    override fun render(delta: Float) {

        Gdx.gl.glViewport(
            0,
            0,
            Gdx.graphics.width,
            Gdx.graphics.height
        )

        Gdx.gl.glClearColor(
            0.15f,
            0.15f,
            0.20f,
            1f
        )

        Gdx.gl.glClear(
            GL20.GL_COLOR_BUFFER_BIT or
            GL20.GL_DEPTH_BUFFER_BIT
        )

        modelBatch.begin(camera)
        modelBatch.render(cube, environment)
        modelBatch.end()
    }

    override fun dispose() {

        model.dispose()
        modelBatch.dispose()

    }
}
