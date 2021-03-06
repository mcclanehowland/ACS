import java.awt.Graphics;
import java.awt.Color;

public class Barrel extends Thing {
    public Barrel(double x, double y, int radius, int lives) {
        super("barrel", x, y, 0, 0);
        this.width = radius;
        this.height = radius;
        this.lives = lives;
    }
    public void render(Graphics g) {
        g.setColor(Color.black);
        g.fillOval((int)x, (int)y, width, height);
        g.setColor(Colors.BARREL);
        g.fillOval((int)x+5, (int)y+5, width-10, height-10);
        g.setColor(Color.black);
        g.fillOval((int)(x + width/3.5), (int)(y + height/3.5), width/5, height/5);
    }
    public void hit() {
        lives--;
        x += height/8;
        y += width/8;
        height -= height/4;
        width -= width/4;
        /*Sound.playSound("sound/metal_bullet_hit_03.mp3");
        if(lives <= 0) {      
            Sound.playSound("sound/explosion_02.mp3");
        }*/
        
    }
    public boolean collisionIfMoved(double dx, double dy, Thing thing) {
        double distance = Math.sqrt(Math.pow((thing.x+dx+thing.width/2) - (x+width/2), 2) + Math.pow((thing.y+dy+thing.height/2) - (y+height/2), 2));
        return distance < width/2;
    }
    public boolean collision(Thing thing) {
        return collisionIfMoved(0, 0, thing);
    }
}