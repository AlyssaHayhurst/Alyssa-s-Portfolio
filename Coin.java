import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//Coin inherits properties from GameObjects, coins appear as leaves
public class Coin extends GameObject {
    
    private BufferedImage leafImg;
    
    public Coin(int x, int y) {
        super(x, y, 50, 50, 4); //GameObject constructor with coords, width/height, speed
        try{ //loads leaf image
            leafImg = ImageIO.read(new File("leaf.png"));
        }catch(IOException e){
            System.out.println("Leaf image not found");
        }
    }
        
    @Override
    public void move() {
        y += speed; //adjusts y-coords of leaves
    }
        
    @Override
    public void draw(Graphics g) {
        if(leafImg!=null){
            g.drawImage(leafImg, x, y, width, height, null); //leaf is drawn
        }else{
            g.setColor(Color.YELLOW); //if leaf cannot be drawn, coin is drawn
            g.fillOval(x, y, width, height);
        }
    }
}