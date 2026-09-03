package gdx.liftoff

import com.badlogic.gdx.Gdx
import kotlin.math.sqrt

class Joystick(

    private val centerX: Float,
    private val centerY: Float,
    val radius: Float

) {

    var moveX = 0f
        private set

    var moveY = 0f
        private set

    fun update() {

        if (!Gdx.input.isTouched) {
            moveX = 0f
            moveY = 0f
            return
        }

        val tx = Gdx.input.x.toFloat()
        val ty = Gdx.graphics.height - Gdx.input.y.toFloat()

        val dx = tx - centerX
        val dy = ty - centerY

        val dist = sqrt(dx * dx + dy * dy)

        if (dist <= radius) {

            moveX = dx / radius
            moveY = dy / radius

        } else {

            moveX = dx / dist
            moveY = dy / dist

        }
    }
}
