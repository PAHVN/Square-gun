package gdx.liftoff

import com.badlogic.gdx.Game

class SquareGunGame : Game() {

    override fun create() {
        setScreen(MainMenuScreen(this))
    }
}
