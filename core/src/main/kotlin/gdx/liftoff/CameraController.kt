package gdx.liftoff

import com.badlogic.gdx.graphics.OrthographicCamera

class CameraController(

    private val camera: OrthographicCamera

) {

    private val lerp = 0.12f

    fun update(

        targetX: Float,
        targetY: Float

    ) {

        camera.position.x +=
            (targetX - camera.position.x) * lerp

        camera.position.y +=
            (targetY - camera.position.y) * lerp

        camera.update()

    }

}
