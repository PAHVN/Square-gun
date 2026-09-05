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

	private var pointer = -1

fun update() {

    // Nếu đang có pointer nhưng đã nhấc tay
    if (pointer != -1 &&
        !Gdx.input.isTouched(pointer)
    ) {

        pointer = -1
        moveX = 0f
        moveY = 0f

    }

    // Nếu chưa có pointer thì tìm ngón tay nằm trong vùng joystick
    if (pointer == -1) {

        for (i in 0 until 10) {

            if (!Gdx.input.isTouched(i))
                continue

            val tx =
                Gdx.input.getX(i).toFloat()

            val ty =
                Gdx.graphics.height -
                Gdx.input.getY(i).toFloat()

            val dx = tx - center.x
            val dy = ty - center.y

            val dist =
                sqrt(dx * dx + dy * dy)

            if (dist <= radius) {

                pointer = i
                break

            }

        }

    }

    // Chưa có ngón nào điều khiển
    if (pointer == -1) {

        moveX = 0f
        moveY = 0f
        return

    }

    // Cập nhật vị trí joystick
    val tx =
        Gdx.input.getX(pointer).toFloat()

    val ty =
        Gdx.graphics.height -
        Gdx.input.getY(pointer).toFloat()

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
