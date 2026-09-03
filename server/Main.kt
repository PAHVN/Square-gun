import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress
import java.util.UUID

data class Player(
    val id: String,
    val connection: WebSocket
)

class GameServer(port: Int) : WebSocketServer(InetSocketAddress(port)) {

    private val players = mutableMapOf<WebSocket, Player>()

    override fun onOpen(conn: WebSocket, handshake: ClientHandshake) {
	println("=== onOpen CALLED ===")
    System.out.flush()
	println(handshake.resourceDescriptor)
    System.out.flush()

    val id = UUID.randomUUID().toString().take(8)

    println("Player connected: $id")
    System.out.flush()

        // Gửi danh sách player đang online cho player mới
        for (player in players.values) {
            conn.send("PLAYER_JOIN ${player.id}")
        }

        players[conn] = Player(id, conn)

        println("Player connected: $id")

        // Gửi ID của chính mình
        conn.send("WELCOME $id")

        // Báo cho các player cũ
        broadcastExcept(
            conn,
            "PLAYER_JOIN $id"
        )
    }

    override fun onClose(
        conn: WebSocket,
        code: Int,
        reason: String,
        remote: Boolean
    ) {
        val player = players.remove(conn)

        if (player != null) {
            println("Player disconnected: ${player.id}")

            broadcastAll("PLAYER_LEAVE ${player.id}")
        }
    }

    override fun onMessage(conn: WebSocket, message: String) {

	println("MESSAGE: $message")
System.out.flush()

        val player = players[conn] ?: return

        println("${player.id}: $message")

        if (message.startsWith("MOVE ")) {
            broadcastExcept(
                conn,
                "PLAYER_MOVE ${player.id} ${message.removePrefix("MOVE ")}"
            )
        }
    }

    override fun onError(conn: WebSocket?, ex: Exception) {
        ex.printStackTrace()

    }

    override fun onStart() {
        println("Square Gun Server V0.2c")
        println("WebSocket server listening on port 8080")
    }

    private fun broadcastAll(message: String) {
        for (player in players.values) {
            player.connection.send(message)
        }
    }

    private fun broadcastExcept(
        except: WebSocket,
        message: String
    ) {
        for (player in players.values) {
            if (player.connection != except) {
                player.connection.send(message)
            }
        }
    }
}

fun main() {

    try {

        val server = GameServer(8080)

        server.start()

println("START CALLED")

Thread.sleep(3000)

println("Address = ${server.address}")
println("Port    = ${server.port}")

try {
    val socket = java.net.Socket("127.0.0.1", 8080)
    println("LOCAL SOCKET OK")
    socket.close()
} catch (e: Exception) {
    println("LOCAL SOCKET FAIL: ${e.javaClass.simpleName}")
    e.printStackTrace()
}

while (true) {
    Thread.sleep(1000)
}

    } catch (e: Exception) {

        e.printStackTrace()

    }
}
