import java.awt.*;

//GameObject is an abstract class used for objects such as player, coins, rocks, and apricots
public abstract class GameObject {
        
    protected int x; //x-coord of object
    protected int y; //y-coord of object
    protected int width; //width of object
    protected int height; //height of object
    protected int speed; //speed of object
    
    //attributes of objects are initialized
    public GameObject(int x, int y, int width, int height, int speed) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = speed;
    }
        
    // Abstract methods
    public abstract void move();
        
    public abstract void draw(Graphics g);
        
    // Collision detection
    public Rectangle getBounds() {
        return new Rectangle(x+8, y+20, width-8, height-30);
    }
}