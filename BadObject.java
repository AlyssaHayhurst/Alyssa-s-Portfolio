import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//BadObject includes rocks which the giraffe must avoid
public class BadObject extends GameObject { //Properties inherited from GameObject
    
    private BufferedImage rockImg; //rock image
    
    public BadObject(int x, int y) {
        super(x, y, 40, 40, 6); //GamObject constructor with coords, width/height, speed
        
        try{ //Loading rock image
            rockImg = ImageIO.read(new File("rock.png"));
        }catch(IOException e){
            System.out.println("Rock image not found");
        }
    }
        
    @Override
    public void move() {
        y += speed; //Adjusts y-coords of rock object
    }
        
    @Override
    public void draw(Graphics g) {
        if(rockImg!=null){
            g.drawImage(rockImg, x, y, width, height, null); //rock image is drawn at coords
        }else{
            //If rock image is inaccessible, red rectangle is drawn
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}