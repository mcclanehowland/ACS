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
import java.util.Collections;


class Screen extends JPanel implements MouseListener, MouseMotionListener {
    Input input;
    ClientInterface ci;
    int playerHashCode;
    HashMap<Integer, Thing> state;
    ArrayList<Thing> orderedState;
    static HashMap<String, Integer> drawOrder;
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
        orderedState = new ArrayList<Thing>();
        
        drawOrder = new HashMap<String, Integer>();
        drawOrder.put("player", 0);
        drawOrder.put("enemy", 1);
        drawOrder.put("weapon", 2);
        drawOrder.put("projectile", 3);
        drawOrder.put("obstacle", 4);
        drawOrder.put("tree", 5);
        drawOrder.put("barrel", 6);
        drawOrder.put("text", 7);
    }
    public synchronized void update(HashMap<Integer, Thing> state) { // this functions as an animate
        synchronized(state) {
            //System.out.println(state);
            this.state = state;
            orderedState = new ArrayList<Thing>();
            for(int key : state.keySet()) {
                orderedState.add(state.get(key));
            }
            Collections.sort(orderedState);
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
                // p - cheat key
                if(input.keyboard[80]) {
                    ci.send(new Event("cheat", playerHashCode));
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
        synchronized(orderedState) {
            if(state.containsKey(playerHashCode)) {
                //g.translate(0, 0);
                g.translate(-(int)state.get(playerHashCode).x+600, -(int)state.get(playerHashCode).y+400);
            }
            try {
                for(Thing thing : orderedState) {
                    if(!state.containsKey(playerHashCode)) {
                        thing.render(g);
                    }
                    else if(thing.inView(state.get(playerHashCode))) {
                        thing.render(g);
                    }
                    else if(thing.type.equals("text")) {
                        if(state.containsKey(playerHashCode)) {
                            Text text = (Text)(thing);
                            text.render(g, (int)(state.get(playerHashCode).x)+400, (int)(state.get(playerHashCode).y)- 200);
                        }
                    }
                }
            } catch(NullPointerException e) {
                System.out.println("oooh another null pointer exception");
            }
            if(view == 0) {
                int playerCount = 0;
                for(Thing thing : orderedState){
                    if(thing.type.equals("player")) {
                        playerCount++;
                    }
                }
                ImageReader.drawImage(g, "images/start_screen.png", 0, 0, 1200, 800);
                g.setColor(Color.black);
                g.drawString(""+playerCount, 1020, 620);
            }
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