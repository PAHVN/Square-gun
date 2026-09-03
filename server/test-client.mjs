const url = "wss://close-pure-together-wrestling.trycloudflare.com";
const playerName = process.argv[2] || "Player";

console.log(`${playerName}: Connecting...`);

const ws = new WebSocket(url);

ws.addEventListener("open", () => {
    console.log(`${playerName}: CONNECTED ✓`);

    ws.send(`HELLO ${playerName} square red`);

    ws.send("MOVE 100 200");

    setTimeout(() => {
        ws.send("MOVE 150 250");
    }, 2000);
});

ws.addEventListener("message", (event) => {
    console.log(`${playerName} ← SERVER:`, event.data);
});

ws.addEventListener("error", (error) => {
    console.log(`${playerName}: ERROR`, error);
});

ws.addEventListener("close", () => {
    console.log(`${playerName}: Connection closed.`);
});
