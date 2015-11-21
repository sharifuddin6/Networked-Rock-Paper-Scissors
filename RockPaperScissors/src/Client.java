import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import layout.TableLayout;

public class Client implements ActionListener {
    String host = "";
    BufferedReader input;
    PrintWriter output;
    int yourHand, oppoHand;
    int player;

    // Swing Component variables
    JFrame frame;
    JPanel panel1, panel2, top, mid1, mid2, bottom;
    JButton signinButton, pickButton, leaveButton, resetButton;
    JTextArea textarea;
    JTextField usernameField, chatField, timer;
    JRadioButton butt1, butt2, butt3;
    JLabel user, icon1, icon2, icon3, you, opponent, result;
    ImageIcon pic0, pic1, pic2, pic3, pic4;

    /*
     * createDialog: creates dialogbox for input, to collect serverhost address.
     */
    private void createDialog() {
        JTextField hostname = new JTextField("localhost");
        final JComponent[] inputs = new JComponent[] { new JLabel("Enter Serverhost address:"), hostname, };
        JOptionPane.showMessageDialog(null, inputs, "Server", JOptionPane.PLAIN_MESSAGE);
        host = hostname.getText();
    }

    /*
     * createComponent: creates and initialises all swing components.
     */
    private void createComponents() {
        panel1 = new JPanel();
        panel2 = new JPanel();
        top = new JPanel();
        mid1 = new JPanel();
        mid2 = new JPanel();
        bottom = new JPanel();

        pic0 = new ImageIcon(getClass().getResource("images/default1.jpg"));
        pic1 = new ImageIcon(getClass().getResource("images/default2.jpg"));
        pic2 = new ImageIcon(getClass().getResource("images/rock.jpg"));
        pic3 = new ImageIcon(getClass().getResource("images/paper.jpg"));
        pic4 = new ImageIcon(getClass().getResource("images/scissors.jpg"));

        signinButton = new JButton("Sign-in");
        signinButton.addActionListener(this);
        pickButton = new JButton("Pick Hand");
        pickButton.addActionListener(this);
        pickButton.setEnabled(false);
        resetButton = new JButton("Reset");
        resetButton.setEnabled(false);
        resetButton.addActionListener(this);
        leaveButton = new JButton("Leave");
        leaveButton.addActionListener(this);

        timer = new JTextField("TIME");
        timer.setHorizontalAlignment(JTextField.CENTER);
        timer.setFont(new Font("Arial", Font.BOLD, 32));
        timer.setEditable(false);
        timer.addActionListener(this);

        icon1 = new JLabel(pic0);
        icon2 = new JLabel(pic1);
        icon2.setVisible(false);
        icon3 = new JLabel(pic1);
        you = new JLabel("YOU", SwingConstants.CENTER);
        you.setLabelFor(icon1);
        opponent = new JLabel("OPPONENT", SwingConstants.CENTER);
        opponent.setLabelFor(icon2);
        result = new JLabel("", SwingConstants.CENTER);

        butt1 = new JRadioButton("Rock");
        butt1.addActionListener(this);
        butt2 = new JRadioButton("Paper");
        butt2.addActionListener(this);
        butt3 = new JRadioButton("Scissor");
        butt3.addActionListener(this);
        butt1.setEnabled(false);
        butt2.setEnabled(false);
        butt3.setEnabled(false);
        ButtonGroup group = new ButtonGroup();
        group.add(butt1);
        group.add(butt2);
        group.add(butt3);

        textarea = new JTextArea("", 25, 20);
        textarea.setEditable(false);
        textarea.setLineWrap(true);
        textarea.setWrapStyleWord(true);
        usernameField = new JTextField(20);
        usernameField.addActionListener(this);
        chatField = new JTextField(20);
        chatField.addActionListener(this);
        chatField.setEditable(false);
        user = new JLabel("User: ");
        user.setLabelFor(usernameField);
        user.setHorizontalAlignment(4);
    }

    /*
     * createFrame: creates frame and sets layouts for the gui.
     */
    private void createFrame() {
        frame = new JFrame("RockPaperScissors Application");
        frame.setMinimumSize(new Dimension(600, 300));
        frame.setResizable(false);

        // Define Layouts
        double f = TableLayout.FILL;
        double p = TableLayout.PREFERRED;
        double[][] t1 = { { 3, f, 3 }, // columns
                { 3, p, 3, f, 3, p, 3, p, 3 } // rows
        };

        double[][] t2 = { { 3, p, 3, 100, 3, p, 3 }, // columns
                { 3, p, 25, p, 3 } // rows
        };

        double[][] t3 = { { 3, p, 3, f, 3, p, 3 }, // columns
                { 3, p, 5, f, 5, p, 3 } // rows
        };

        TableLayout layout1 = new TableLayout(t1);
        TableLayout layout2 = new TableLayout(t2);
        TableLayout layout3 = new TableLayout(t3);

        // GAME AREA
        top.add(timer);

        mid1.setLayout(layout2);
        mid1.add(icon1, "1,1");
        mid1.add(icon2, "5,1");
        mid1.add(icon3, "5,1");
        mid1.add(result, "3,1");
        mid1.add(you, "1,3");
        mid1.add(opponent, "5,3");

        mid2.setLayout(new FlowLayout());
        mid2.add(butt1);
        mid2.add(butt2);
        mid2.add(butt3);

        bottom.setLayout(new FlowLayout());
        bottom.add(pickButton);
        bottom.add(resetButton);
        bottom.add(leaveButton);

        panel1.setLayout(layout1);
        panel1.add(top, "1,1");
        panel1.add(mid1, "1,3");
        panel1.add(mid2, "1,5");
        panel1.add(bottom, "1,7");

        // TEXT CHAT AREA
        panel2.setLayout(layout3);
        panel2.add(user, "1,1");
        panel2.add(usernameField, "3,1");
        panel2.add(signinButton, "5,1");
        panel2.add(new JScrollPane(textarea), "1,3, 5,3");
        panel2.add(chatField, "1,5,5,5");

        // ADD PANELS TO FRAME
        frame.setLayout(new BorderLayout());
        frame.add(panel1, BorderLayout.WEST);
        frame.add(panel2, BorderLayout.EAST);

        // DISPLAY WINDOW
        frame.pack();
        frame.setVisible(true);
    }

    /*
     * action listener method, setup for each event to be handled.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();

        if (src == signinButton) {
            // doesn't allow empty names
            if (usernameField.getText().length() != 0) {
                usernameField.setEditable(false);
                signinButton.setEnabled(false);
                chatField.setEditable(true);
            }
        }

        else if (src == usernameField) {
            // doesn't allow empty names
            if (usernameField.getText().length() != 0) {
                usernameField.setEditable(false);
                signinButton.setEnabled(false);
                chatField.setEditable(true);
            }
        }

        else if (src == chatField) {
            // doesn't allow empty text
            if (chatField.getText().length() != 0) {
                output.println(chatField.getText());
                chatField.setText("");
            }
        }

        else if (src == pickButton) {
            pickButton.setEnabled(false);
            butt1.setEnabled(true);
            butt2.setEnabled(true);
            butt3.setEnabled(true);
            // COUNTDOWN BEFORE PLAY
            countDown("5");
        }

        else if (src == resetButton) {
            if (player == 1) {
                pickButton.setEnabled(true);
            }
            butt1.setEnabled(false);
            butt2.setEnabled(false);
            butt3.setEnabled(false);
            result.setText("");
            timer.setText("TIME");
            resetButton.setEnabled(false);
            icon2.setIcon(pic1);
            icon2.setVisible(false);
        }

        else if (src == leaveButton) {
            System.exit(0);
        }

        // Radiobutton actions
        else if (src == butt1) {
            icon1.setIcon(pic2);
            yourHand = 1;
        } else if (src == butt2) {
            icon1.setIcon(pic3);
            yourHand = 2;
        } else if (src == butt3) {
            icon1.setIcon(pic4);
            yourHand = 3;
        }
    }

    /*
     * Constructor: creates and initialises socket streams for communications
     * and loads GUI.
     */
    public Client() {
        createDialog();
        Socket socket;
        try {
            // CREATE SOCKET & STREAMS
            socket = new Socket(host, 4242);
            input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            output = new PrintWriter(socket.getOutputStream(), true);
            // socket.close();
        } catch (IOException e) {
            System.err.println("Connection refused, server not found?\nClosing Application.");
            System.exit(0);
        }

        // LOAD GUI
        createComponents();
        createFrame();
    }

    /*
     * run method: loop where the communications between client and server takes
     * place.
     */
    private void run() {
        String name = null;
        while (usernameField.isEditable()) {
            System.out.print("");
            if (usernameField.isEditable() == false) {
                name = usernameField.getText();
                break;
            }
        }

        try {
            // WHILE LOOP WHICH IMPLEMENTS COMMUNICATIONS
            while (true) {
                String line;
                line = input.readLine();
                if (line.startsWith("SUBMITNAME")) {
                    output.println(name);
                } else if (line.startsWith("NAMEACCEPTED")) {
                    // ENABLES CHAT CAPABILITIES
                    chatField.setEditable(true);
                } else if (line.startsWith("RULE")) {
                    textarea.append(line.substring(5) + "\n\n");
                } else if (line.startsWith("WELCOME")) {
                    textarea.append(line.substring(8) + "!\n\n");
                    player = Integer.parseInt(line.substring(23, 24));
                } else if (line.startsWith("MESSAGE")) {
                    textarea.append(line.substring(8) + "\n");
                    textarea.setCaretPosition(textarea.getDocument().getLength());
                } else if (line.startsWith("PLAYER1")) {
                    if (player == 1) {
                        pickButton.setEnabled(true);
                    }
                } else if (line.startsWith("OPPONENT_PLAYED")) {
                    if (player == 2) {
                        pickButton.setEnabled(true);
                    }
                    oppoHand = Integer.parseInt(line.substring(15));
                    textarea.append("Opponent has chosen a hand.\n");
                    switch (oppoHand) {
                    case 1:
                        icon2.setIcon(pic2);
                        break;
                    case 2:
                        icon2.setIcon(pic3);
                        break;
                    case 3:
                        icon2.setIcon(pic4);
                        break;
                    }
                } else if (line.startsWith("RESULT")) {
                    result.setText(line.substring(6));
                    textarea.append("# " + line.substring(6) + '\n');
                } else {
                    // DISPLAYS ALL OTHER MESSAGES SENT FROM SERVER
                    // textarea.append("@SYSTEM: " + line + "\n");
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     * countDown method: when timer hits zero it outputs the hand choice to
     * server.
     */
    private void countDown(final String time) {
        final Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            int i = Integer.parseInt(time) + 1;

            public void run() {
                Client.this.timer.setText(String.format("%d", --i));
                if (i == 0) {
                    // outputs the hand chosen after timer ends
                    output.println(String.format("MOVE%d", yourHand));
                    butt1.setEnabled(false);
                    butt2.setEnabled(false);
                    butt3.setEnabled(false);
                    icon2.setVisible(true);
                    resetButton.setEnabled(true);
                } else if (i < 0) {
                    Client.this.timer.setText("0");
                    timer.cancel();
                }
            }
        }, 0, 1000);
    }

    public static void main(String[] args) throws Exception {
        Client client = new Client();
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        client.frame.setVisible(true);
        client.run();
    }

}
