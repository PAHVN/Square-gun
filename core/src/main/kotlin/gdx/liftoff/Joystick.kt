package gdx.liftoff

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import kotlin.math.sqrt

class Joystick(

    x: Float,
    y: Float,
    val radius: Float

) {

    val center = Vector2(x, y)

    var moveX = 0f
        private set

    var moveY = 0f
        private set

    val knobRadius = 45f

    fun update() {

        if (!Gdx.input.isTouched) {

            moveX = 0f
            moveY = 0f
            return

        }

        val tx = Gdx.input.x.toFloat()

        val ty =
            Gdx.graphics.height -
            Gdx.input.y.toFloat()

        val dx = tx - center.x
        val dy = ty - center.y

        val dist =
            sqrt(dx * dx + dy * dy)

        if (dist <= radius) {

            moveX = dx / radius
            moveY = dy / radius

        } else {

            moveX = dx / dist
            moveY = dy / dist

        }

    }

}
