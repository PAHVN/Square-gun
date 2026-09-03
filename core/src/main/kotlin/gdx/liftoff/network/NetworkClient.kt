package gdx.liftoff.network

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class NetworkClient(
    serverUrl: String,
    private val nickname: String,
    private val shape: String,
    private val color: String,

    private val onConnected: () -> Unit,
    private val onDisconnected: () -> Unit,

    private val onPlayerJoin: (String) -> Unit,
    private val onPlayerLeave: (String) -> Unit,
    private val onPlayerMove: (String, Float, Float) -> Unit,
    private val onPlayerInfo: (String, String, String, String) -> Unit
) {

	private val onConnected: () -> Unit,

    private val client = object : WebSocketClient(URI(serverUrl)) {

        override fun onOpen(handshake: ServerHandshake?) {
    println("NETWORK: CONNECTED ✓")

    onConnected()

    send("HELLO $nickname $shape $color")
}

        override fun onMessage(message: String?) {
            println("NETWORK ← SERVER: $message")

            if (message == null) return
	
            val parts = message.split(" ")

            when (parts[0]) {
                "PLAYER_JOIN" -> {
                    if (parts.size >= 2) {
                        onPlayerJoin(parts[1])
                    }
                }

		"PLAYER_INFO" -> {

    if (parts.size >= 5) {

        onPlayerInfo(
            parts[1],
            parts[2],
            parts[3],
            parts[4]
        )

    }

}

                "PLAYER_LEAVE" -> {
                    if (parts.size >= 2) {
                        onPlayerLeave(parts[1])
                    }
                }

                "PLAYER_MOVE" -> {
                    if (parts.size >= 4) {
                        val id = parts[1]
                        val x = parts[2].toFloatOrNull()
                        val y = parts[3].toFloatOrNull()

                        if (x != null && y != null) {
                            onPlayerMove(id, x, y)
                        }
                    }
                }
            }
        }

override fun onClose(
    code: Int,
    reason: String?,
    remote: Boolean
) {
    onDisconnected()

    println("NETWORK: DISCONNECTED")
}
        override fun onError(ex: Exception?) {
            println("NETWORK ERROR: ${ex?.message}")
        }
    }

    fun connect() {
        client.connect()
    }

    fun disconnect() {
        client.close()
    }


	fun isConnected(): Boolean {
    return client.isOpen
}


    fun send(message: String) {
        if (client.isOpen) {
            client.send(message)
        }
    }
}
