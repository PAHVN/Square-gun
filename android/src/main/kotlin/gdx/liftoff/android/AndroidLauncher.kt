package gdx.liftoff.android

import android.os.Bundle
import com.badlogic.gdx.backends.android.AndroidApplication
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration
import gdx.liftoff.SquareGunGame

class AndroidLauncher : AndroidApplication() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val configuration = AndroidApplicationConfiguration().apply {
            useImmersiveMode = true
        }

        initialize(SquareGunGame(), configuration)
    }
}
