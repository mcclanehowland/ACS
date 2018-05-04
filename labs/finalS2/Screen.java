import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JFrame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import java.util.ArrayList;
import java.util.HashMap;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseEvent;


class Screen extends JPanel implements MouseListener, MouseMotionListener {
    Input input;
    int playerCount = 0;
    ClientInterface ci;
    int playerHashCode;
    HashMap<Integer, Thing> state;
    int view = 0;
    public Screen(ClientInterface ci) {
        this.setLayout(null);
        this.setFocusable(true);

        input = new Input(this);

        addMouseListener(this);
        addMouseMotionListener(this);

        this.ci = ci;
        playerHashCode = (int)(Math.random()*100000);
        //ci.send(new Event("player_connect", playerHashCode));

        state = new HashMap<Integer, Thing>();
    }
    public synchronized void update(HashMap<Integer, Thing> state) { // this functions as an animate
        synchronized(state) {
            //System.out.println(state);
            this.state = state;
            if(state.containsKey(playerHashCode)) {
                // send a move event for smooth movement.
                if(input.keyboard[87]) {
                    ci.send(new Event("player_move", playerHashCode, "up"));
                }
                // s - down
                if(input.keyboard[83]) {
                    ci.send(new Event("player_move", playerHashCode, "down"));
                }
                // d - right
                if(input.keyboard[68]) {
                    ci.send(new Event("player_move", playerHashCode, "right"));
                }
                // a - left
                if(input.keyboard[65]) {
                    ci.send(new Event("player_move", playerHashCode, "left"));
                }
            }
        }
        repaint();
    }
    public Dimension getPreferredSize() {
        return new Dimension(1200, 800);
    }
    public void paintComponent(Graphics g) {
        g.setColor(Colors.GRASS);
        g.fillRect(0, 0, 1200, 800);
        synchronized(state) {
            if(state.containsKey(playerHashCode)) {
                //g.translate(0, 0);
                g.translate(-(int)state.get(playerHashCode).x+600, -(int)state.get(playerHashCode).y+400);
            }
            try {
                // draw all the players
                for(int key : state.keySet()) {
                    if(state.get(key).type.equals("player")) {
                        state.get(key).render(g);
                    }
                }
                // draw all the projectiles
                for(int key : state.keySet()) {
                    if(state.get(key).type.equals("projectile")) {
                        state.get(key).render(g);
                    }
                }
                // draw all the obstacles
                for(int key : state.keySet()) {
                    if(state.get(key).type.equals("obstacle")) {
                        state.get(key).render(g);
                    }
                }
                // draw all the trees
                for(int key : state.keySet()) {
                    if(state.get(key).type.equals("tree")) {
                        state.get(key).render(g);
                    }
                }
            } catch(NullPointerException e) {
                System.out.println("oooh another null pointer exception");
            }
            if(view == 0) {
                ImageReader.drawImage(g, "images/start_screen.png", 0, 0, 1200, 800);
                g.setColor(Color.black);
                g.drawString(""+playerCount, 1020, 620);
            }
        }
    }
    public void mousePressed(MouseEvent e) {
        if(view == 0) {
            ci.send(new Event("player_connect", playerHashCode));
            view++;
        }
        else if(view == 1) {
            int x = e.getX();
            int y = e.getY();
            int px = 600;
            int py = 400;
            if(state.containsKey(playerHashCode)) {
                //px = (int)state.get(playerHashCode).x;
                //py = (int)state.get(playerHashCode).y;
            }
            // translate mouse coordinates to be relative to the player
            //x -= px;
            //y -= py;
            // find the direction of the click relative to the plaer
            double dx = x - px;
            double dy = y - py;
            // normalize the direction
            double magnitude = Math.sqrt(dx*dx + dy*dy);
            dx /= magnitude;
            dy /= magnitude;
            // send it to the server!
            ci.send(new Event("player_shoot", playerHashCode, dx, dy));
        }
    }
    public void mouseReleased(MouseEvent e) {

    }
    public void mouseMoved(MouseEvent e) {

    }
    public void mouseDragged(MouseEvent e) {
        mouseMoved(e);
    }
    public void keyReleased(KeyEvent e) {}
    public void keyTyped(KeyEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
}