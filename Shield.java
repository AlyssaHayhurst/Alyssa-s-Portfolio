import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//Shield inherits properties from GameObject for apricot immunity objects
public class Shield extends GameObject{
    private BufferedImage shieldImg;
    
    public Shield(int x, int y){ //object coords, sizing, speed
        super(x, y, 40, 40, 4);
        
        try{ //loading apricot image
            File imgFile = new File("apricot.png");
            if(imgFile.exists()){
                shieldImg = ImageIO.read(imgFile);
            }
        }catch(IOException e){
            System.out.println("Shield image not found");
        }
        
    }
    
    @Override
    public void move(){ //apricot falling speed
        y+=speed;
    }
    
    @Override
    public void draw(Graphics g){ //draws apricots to screen
        if(shieldImg!=null){
            g.drawImage(shieldImg, x, y, width, height, null);
        }else{
            g.setColor(Color.GREEN);
            g.fillOval(x,y,width,height);
        }
    }
}