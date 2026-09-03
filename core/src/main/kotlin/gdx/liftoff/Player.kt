package gdx.liftoff

import com.badlogic.gdx.Gdx

class Player(

    var x: Float = 600f,
    var y: Float = 400f

) {

    val size = 100f

    private val speed = 450f

    fun update(

        moveX: Float,
        moveY: Float

    ) {

        x +=
            moveX *
            speed *
            Gdx.graphics.deltaTime

        y +=
            moveY *
            speed *
            Gdx.graphics.deltaTime

    }

}
