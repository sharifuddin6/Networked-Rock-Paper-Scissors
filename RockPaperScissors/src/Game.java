
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashSet;

public class Game {
    Player currentPlayer;
    int count = 0;
    int p1, p2;

    private HashSet<String> names = new HashSet<String>();
    private HashSet<PrintWriter> writers = new HashSet<PrintWriter>();

    /*
     * result: returns the result of game, in the form of a string.
     */
    public String result(int player, int hand) {
        count += 1;
        if (count == 1) {
            p1 = hand;
        } else if (count == 2) {
            p2 = hand;
            count = 0;
            // CHECK FOR WINNER
            String score = hasWinner1() ? "P1 WINS"
                    : hasWinner2() ? "P2 WINS" : hasDraw() ? "TIE" : "SOMEONE DIDN'T PLAY";
            return score;
        }
        return null;
    }

    /*
     * hasDraw: returns whether game resulted in a tie with a boolean value
     * true.
     */
    public boolean hasDraw() {
        if (p1 == p2) {
            return true;
        }
        return false;
    }

    /*
     * hasWinner1: returns whether game resulted in player1's favor with a
     * boolean value true.
     */
    public boolean hasWinner1() {
        if (p1 == 1 && p2 == 3 || p1 == 2 && p2 == 1 || p1 == 3 && p2 == 2) {
            return true;
        }
        return false;
    }

    /*
     * hasWinner2: returns whether game resulted in player2's favor with a
     * boolean value true.
     */
    public boolean hasWinner2() {
        if (p1 == 1 && p2 == 2 || p1 == 2 && p2 == 3 || p1 == 3 && p2 == 1) {
            return true;
        }
        return false;
    }

    /*
     * legalMove: returns whether player chose a hand with a boolean value true.
     */
    public synchronized boolean legalMove(int hand, Player player) {
        if (player == currentPlayer && hand >= 0 && hand <= 3) {
            currentPlayer = currentPlayer.opponent;
            currentPlayer.otherPlayerPlayed(hand);
            return true;
        }
        return false;
    }

    /*
     * Class Player: this inner class handles communications between the client
     * and the server. It handles communications such as messages as well as
     * choices within the game such as game initiation and player's hand
     * choices.
     */
    public class Player extends Thread {
        char number;
        String name;
        Player opponent;
        Socket socket;
        BufferedReader input;
        PrintWriter output;

        /*
         * Constructor: initialises information for each client, and sets
         * input/output for communications between client and server.
         */
        public Player(Socket socket, char number) {
            this.socket = socket;
            this.number = number;
            try {
                input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                output = new PrintWriter(socket.getOutputStream(), true);

                // COLLECT NAME
                while (true) {
                    output.println("SUBMITNAME");
                    name = input.readLine();
                    if (name == null) {
                        return;
                    }

                    // keeps names unique between clients
                    synchronized (names) {
                        if (!names.contains(name)) {
                            names.add(name);
                            break;
                        } else {
                            name = name + "1";
                            names.add(name);
                            break;
                        }
                    }
                }

                // ACCEPT CLIENT
                output.println("NAMEACCEPTED");
                writers.add(output);

                output.println("RULE Keep chat conversation at a family friendly level, "
                        + "as there are a wide range of ages in our chat. "
                        + "Self-censorship of swearing is not considered " + "'family friendly'.");
                output.println("WELCOME Welcome Player " + number);
                output.println("MESSAGE Waiting for opponent to connect");
            } catch (IOException e) {
                System.out.println("Player has left the game.");
                // Broadcast player has left the game to the other client
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + "has left the game.\nCreate a new game.");
                }
            }
        }

        /*
         * run method: this is where the main communications loop is contained.
         * Constantly reading data being passed between clients and server.
         */
        public void run() {
            try {
                // The thread is only started after everyone connects.
                output.println("MESSAGE All players connected.");

                // Tell player1 to make their choice.
                if (number == '1') {
                    output.println("MESSAGE Select Your Hand First.\n");
                    output.println("PLAYER1"); // Enables the pickHand button
                                               // for player1
                }
                // Tell player2 to make their choice.
                if (number == '2') {
                    output.println("MESSAGE Select Your Hand After Opponent.\n");
                }

                // Repeatedly get commands from the client and process them.
                while (true) {
                    String command = input.readLine();
                    if (input == null) {
                        return;
                    }

                    if (command.startsWith("MOVE")) {
                        int hand = Integer.parseInt(command.substring(4));
                        if (legalMove(hand, this)) {
                            // Retreive hand played
                            if (number == '1') {
                                result(number, hand);
                            } else if (number == '2') {
                                // BROADCAST RESULT
                                String victor = result(number, hand);
                                for (PrintWriter writer : writers) {
                                    writer.println("RESULT" + victor);
                                }
                            }
                        }
                    }
                    // Broadcasts every other message to both clients (for chat
                    // client)
                    else {
                        for (PrintWriter writer : writers) {
                            writer.println("MESSAGE " + name + ": " + command);
                        }
                    }
                }
            } catch (IOException e) {
                System.err.println("Player has left the game.");
                // Broadcast player has left the game to the other client
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE Player 1 has left the game.\n");
                }

            } catch (NullPointerException e) {
                System.err.println("Player has left the game.");
                // Broadcast player has left the game to the other client
                for (PrintWriter writer : writers) {
                    writer.println("MESSAGE " + name + " has left the game.\n");
                }

            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                }
            }
        }

        /*
         * setOpponent: Sets opponent for each player.
         */
        public void setOpponent(Player opponent) {
            this.opponent = opponent;
        }

        /*
         * otherPlayerPlayed: Sends player's hand choice to the opponent.
         */
        public void otherPlayerPlayed(int hand) {
            // CURRENT CLIENTS HAND, passed as opponents to other client
            output.println("OPPONENT_PLAYED" + hand);
        }

    }

}