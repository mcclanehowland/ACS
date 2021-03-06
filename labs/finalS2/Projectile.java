import java.awt.Graphics;
import java.awt.Color;

public class Projectile extends Thing {
    double length = 100;
    int rangeCounter = 0;
    int range = 10;
    public Projectile(double x, double y, double dx, double dy) {
        super("projectile", x, y, dx, dy);
        this.height = 1;
        this.width = 1;
        this.lives = 1;
    }
    public Projectile(double x, double y, double dx, double dy, int range) {
        super("projectile", x, y, dx, dy);
        this.height = 1;
        this.width = 1;
        this.lives = 1;
        this.range = range;
    }
    public void render(Graphics g) {
        g.setColor(Colors.PROJECTILE);
        g.drawLine((int)(x), (int)(y), (int)(x + dx*length), (int)(y + dy*length));
    }
    public void move() {
        rangeCounter++;
        if(rangeCounter > range) {
            lives = 0;
        }
        x += dx * 20;
        y += dy * 20;
    }
    public boolean collision(Thing thing) {
        if(thing.type.equals("weapon") || thing.type.equals("death_circle") || thing.type.equals("emote") || thing.type.equals("sound")) {
            return false;
        }
        int steps = 100;
        for(int i = 0;i < steps;i++) {
            int nx = (int)(x + i*dx*length/steps);
            int ny = (int)(y + i*dy*length/steps);
            if(nx < thing.x + thing.width && nx > thing.x && ny < thing.y + thing.height && ny > thing.y) {
                return true;
            }
        }
        return false;
    }
}
