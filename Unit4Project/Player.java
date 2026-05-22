import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//Player inherits properties from Gameobject for giraffe character
public class Player extends GameObject {
        
    private int movementSpeed = 7; //player speed
    private boolean immune; //rock immunity boolean value
    private BufferedImage giraffeImg;

    public Player(int x, int y) { //player coords, sizing, speed
        super(x, y, 90, 165, 0);
        
        movementSpeed = 7;
        immune = false; //begins without immunity
        
        try{
            giraffeImg = ImageIO.read(new File("giraffe.png"));
        }catch(IOException e){
            System.out.println("Giraffe image not found");
        }
    }
    
    //Adjusts speed
    public void setMovementSpeed(int speed){
        movementSpeed = speed;
    }
        
    @Override
    public void move() {
    // Movement controlled by keyboard
    }
    
    //Draws giraffe image
    @Override
    public void draw(Graphics g) {
        
        if(giraffeImg!=null){
            g.drawImage(giraffeImg, x, y, width, height, null);
        }else{
            g.setColor(Color.BLUE);
            g.fillOval(x, y, width, height);
        }
        
    }
        
    // Movement methods
    public void moveLeft() {
        x -= movementSpeed;
    }
    public void moveRight() {
        x += movementSpeed;
    }
    
    //Movement speed
    public int getMovementSpeed() {
        return movementSpeed;
    }
     
    //returns boolean immunity value   
    public boolean isImmune() {
        return immune;
    }
    
    //sets player immunity
    public void setImmune(boolean immune) {
        this.immune = immune;
    }
}