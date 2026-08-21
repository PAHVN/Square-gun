const url = "wss://inform-previews-greeting-consequences.trycloudflare.com";

const playerName = process.argv[2] || "Player";

console.log(`${playerName}: Connecting...`);

const ws = new WebSocket(url);

ws.addEventListener("open", () => {
    console.log(`${playerName}: CONNECTED ✓`);

    // Gửi vị trí ban đầu
    ws.send("MOVE 100 200");

    // Gửi một vị trí mới sau 2 giây
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
