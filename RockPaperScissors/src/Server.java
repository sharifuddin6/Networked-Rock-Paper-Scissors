
import java.io.IOException;
import java.net.ServerSocket;

/*
 * The Server listens on port 4242 as it waits for 2 clients to connect and
 * pairing them together. It creates a new game instance each time for each pair.
 */
public class Server {
    ServerSocket listener = null;

    /*
     * Constructor: Sets the server to listen on port 4242 as declared in main
     * function
     */
    public Server(int port) throws IOException {
        try {
            listener = new ServerSocket(port);
            System.out.println("Server is Running");

        } catch (IOException e) {
            System.err.printf("Server: could not listen on port: %d.", port);
            System.exit(-1);
        }

        ServerLoop();
    }

    /*
     * ServerLoop: Runs the Serverloop accepting client connections on
     * serversocket, starting threads for each player when both players connect.
     */
    public void ServerLoop() throws IOException {
        try {
            while (true) {
                Game game = new Game();

                Game.Player player1 = game.new Player(listener.accept(), '1');
                Game.Player player2 = game.new Player(listener.accept(), '2');

                player1.setOpponent(player2);
                player2.setOpponent(player1);

                game.currentPlayer = player1;

                player1.start();
                player2.start();
            }
        } finally {
            listener.close();
        }
    }

    public static void main(String[] args) throws Exception {
        new Server(4242);
    }

}
