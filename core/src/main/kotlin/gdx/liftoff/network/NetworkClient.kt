package gdx.liftoff.network

import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

class NetworkClient(
    serverUrl: String
) {

    private val client = object : WebSocketClient(URI(serverUrl)) {

        override fun onOpen(handshake: ServerHandshake?) {
            println("NETWORK: CONNECTED ✓")
            send("HELLO")
        }

        override fun onMessage(message: String?) {
            println("NETWORK ← SERVER: $message")
        }

        override fun onClose(
            code: Int,
            reason: String?,
            remote: Boolean
        ) {
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

    fun send(message: String) {
        if (client.isOpen) {
            client.send(message)
        }
    }
}
