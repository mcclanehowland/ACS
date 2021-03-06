import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JButton;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;

import java.io.*;
import java.net.*;

public class ClientScreen extends JPanel implements ActionListener, MouseListener {
    
    ObjectOutputStream outObj;
    Game game;
    private int gridX = 50;
    private int gridY = 50;
    private int squareSize = 50;
    private boolean sent = false;
    
    private JButton ai;
    
	public ClientScreen() throws IOException{
		
		this.setLayout(null);
		this.setFocusable(true);
        addMouseListener(this);
        
        game = new Game();
        
        ai = new JButton("Play Against AI");
        ai.setBounds(500,300, 300,30);
        ai.addActionListener(this);
        this.add(ai);
	}
	public Dimension getPreferredSize() {
        return new Dimension(800,600);
	}
	public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawString("You are O", 50, 10);
        game.drawMe(g, gridX, gridY, squareSize);
        if(game.status.contains("o wins")) {
            game.playSound("win.wav");
        }
        else if(game.status.contains("x wins")) {
            game.playSound("lose.wav");
        }
	}
	public void actionPerformed(ActionEvent e) {
        if(e.getSource() == ai) {
            game.ai = true;
        }
    }
    public void mousePressed(MouseEvent e) {
        repaint();
        int row = (e.getY() - gridY)/squareSize;
        int column = (e.getX() - gridX)/squareSize;
        if(game.move(row, column, "O")) {
            try {
                outObj.writeObject(game);
            } catch (IOException err) {
                System.out.println(err);
            }
            repaint();
        }
    }
    public void mouseReleased(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void poll() {
        String hostName = "localhost";
        int portNumber = 3000;
         
        try {
             
            Socket serverSocket = new Socket(hostName, portNumber);
             
            ObjectInputStream inObj = new ObjectInputStream(serverSocket.getInputStream());
            outObj = new ObjectOutputStream(serverSocket.getOutputStream());
             
            //Receive connection message
            //Waits for and receives an object
            //readObject() requires a ClassNOtFoundException
            String serverMessage = (String) inObj.readObject(); 
            System.out.println(serverMessage);
             
            while(true) {
                game = (Game) inObj.readObject();
                repaint();
            }
 
        } catch (ClassNotFoundException e) {
            System.err.println("Class does not exist" + e);
            System.exit(1);
        } catch (IOException e) {
            System.out.println(e);
            System.err.println("Couldn't get I/O for the connection to " + hostName);
            System.exit(1);
        }
    }
}
