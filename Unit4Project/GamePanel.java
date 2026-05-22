import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

//GamePanel manages the active game play
public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Timer for game loop
    private Timer timer;
    // Random object
    private Random random;
    // ArrayLists for objects
    private ArrayList<Coin> coins;
    private ArrayList<BadObject> badObjects;
    private ArrayList<Shield> shields;
    //User information
    private String currentUsername;
    private UserManager userManager;
    //Home screen
    private String gameState = "MENU";
    private boolean gameOver = false;
    private JButton startButton;
    private JButton upgradeSpeedButton;
    // Player
    private Player player;
    // Game variables
    private int coinsCollected;
    private int lives;
    private int highScore;
    private int speedLevel = 0;
    private final int baseSpeed = 5;
    private final int maxSpeed = 10;
    // UI
    private JLabel statsLabel;
    private JButton speedButton;
    private JButton logoutButton;
    //movement
    private boolean left;
    private boolean right;
    //background image
    private BufferedImage backgroundImg;
    //game header
    private JPanel topPanel;
    
    public GamePanel(Runnable onLogout) {
        // Layout Manager, size
        setPreferredSize(new Dimension(800, 600));
        setLayout(null);
        
        //Initialize buttons
        startButton = new JButton("START");
        upgradeSpeedButton = new JButton("Upgrade Speed (10 leaves)");
        
        //start button location
        startButton.setBounds(330,340,120,40);
        add(startButton);
        
        //upgrade speed button location (invisible when not on restart screen);
        upgradeSpeedButton.setBounds(280, 350, 240, 35);
        upgradeSpeedButton.setBackground(new Color(230, 189, 140));
        upgradeSpeedButton.setFont(new Font("Arial", Font.BOLD, 12));
        upgradeSpeedButton.setVisible(false);
        add(upgradeSpeedButton);
        
        //Start button action listener
        startButton.addActionListener(e->{
            gameState="PLAYING";
            updateUIState();
            startButton.setVisible(false);
            focusGame();
        });
        
        //Speed button action listener
        upgradeSpeedButton.addActionListener(e -> {
            buySpeedBoost();
        });
        
        setFocusable(true);
        addKeyListener(this);
        
        // Initialize variables
        player = new Player(320, 400);
        
        coins = new ArrayList<>();
        badObjects = new ArrayList<>();
        shields = new ArrayList<>();
        random = new Random();
        
        coinsCollected = 0;
        lives = 3;
        
        //Loading safari background image
        try{
            backgroundImg=ImageIO.read(new File("safari.png"));
        }catch(IOException e){
            System.out.println("Background image not found");
        }
        
        //TOP PANEL
        topPanel = new JPanel();
        statsLabel = new JLabel();
        
        statsLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 15));
        speedButton = new JButton("Buy Speed Boost (10 leaves)");
        
        //Top panel colour
        Color beige = new Color(217, 164, 98);
        Color buttonColor = new Color(230, 189, 140);
        topPanel.setBackground(beige);
        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        
        speedButton.setBackground(buttonColor);
        
        topPanel.add(statsLabel);
        topPanel.add(speedButton);
        
        //Bounds on topPanel
        topPanel.setBounds(0,0,700,37);
        add(topPanel);
        topPanel.setVisible(false); //hidden on initial menu
        
        // Main game timer
        timer = new Timer(20, this);
        timer.start();
        
        //Logout button
        logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.BOLD, 12));
        logoutButton.setBackground(new Color(210, 100, 100));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusable(false);
        
        //Logout button action listener
        logoutButton.addActionListener(e-> {
            if(timer!=null){
                timer.stop();
            }
            gameState = "MENU";
            currentUsername = null;
            startButton.setVisible(true); //Visible during next login
            
            onLogout.run();
        });
        topPanel.add(logoutButton);
    }
    
    //Log-in player information
    public void setPlayerSession(String username, int savedHighScore, UserManager manager){
        this.currentUsername=username;
        this.userManager=manager;
        this.highScore=savedHighScore;
        
        //User account information updated
        updateStats();
        resetGame();
    }
    
    //Speed boost conditions
    private void buySpeedBoost(){
        if(coinsCollected >= 10){
            coinsCollected -= 10;
            speedLevel++;
            int newSpeed = baseSpeed + speedLevel;
            if(newSpeed > maxSpeed){
                newSpeed = maxSpeed;
                speedLevel = maxSpeed - baseSpeed;
            }
            player.setMovementSpeed(newSpeed);
            JOptionPane.showMessageDialog(this, "Speed Level Up! !" + speedLevel + ")");
        }else{
            JOptionPane.showMessageDialog(this, "Not enough leaves!");
        }
        focusGame();
    }
    
    //Updating game activity state
    private void updateUIState(){
        boolean playing = gameState.equals("PLAYING");
        topPanel.setVisible(playing);
        
        //Panel is full width of game
        topPanel.setSize(this.getWidth(), topPanel.getHeight());
        
        //Logout button position
        if(logoutButton!=null){
            logoutButton.setBounds(topPanel.getWidth()-90,4,80,26);
        }
        this.revalidate();
        this.repaint();
    }
    
    
    private void focusGame(){
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void resetGame(){
        //objects are cleared from screen
        coins.clear();
        badObjects.clear();
        shields.clear();
        
        //coins and lives are reset
        coinsCollected = 0;
        lives = 3;
        
        //character attributes reset
        player.x = 320;
        player.y = 400;
        
        player.setMovementSpeed(baseSpeed + speedLevel);
        gameOver = false;
        
        //speed button no longer visible
        upgradeSpeedButton.setVisible(false);
        
        timer.start();
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    //GAME LOOP
    @Override
    public void actionPerformed(ActionEvent e) {
        if(!gameState.equals("PLAYING")){
            repaint();
            return;
        }
        spawnObjects();
        moveObjects();
        checkCollisions();
        removeOffScreenObjects();
        updateStats();
        movePlayer();
        repaint();
    }
    
    //MOVEPLAYER
    private void movePlayer(){
        if(left){
            player.moveLeft();
        }
        if(right){
            player.moveRight();
        }
        
        //Window boundaries
        if(player.x < -30){
            player.x = -30;
        }
        if(player.x > getWidth() - (player.width-30)){
            player.x = getWidth() - (player.width-30);
        }
    }
        
    //Spawn coins, rocks, apricots
    private void spawnObjects() {
        
        // Spawn coin
        if (random.nextInt(20) == 0) {
            int x = random.nextInt(getWidth()-40);
            coins.add(new Coin(x, 0));
        }
            
        // Spawn bad object
        if (random.nextInt(35) == 0) {
            int x = random.nextInt(getWidth()-40);
            badObjects.add(new BadObject(x, 0));
        }
        
        //Spawn apricot shield
        if(random.nextInt(300)==0){
            int spawnX = random.nextInt(getWidth()-40);
            Shield newShield = new Shield(spawnX, 0);
            //falls faster dependent on current speed level
            newShield.speed = 4+speedLevel;
            shields.add(newShield);
        }
    }
        
    //Move coins, rocks, apricots
    private void moveObjects() {
        for (Coin c : coins) {
            c.move();
        }
        for (BadObject b : badObjects) {
            b.move();
        }
        for(Shield s : shields){
            s.move();
        }
    }
        
    //Collision detection with players and coins, rocks, apricots
    private void checkCollisions() {
        // Coin collisions
        for (int i = 0; i < coins.size(); i++) {
            Coin c = coins.get(i);
            if (player.getBounds().intersects(c.getBounds())) {
                coinsCollected++;
                coins.remove(i);
                i--;
            }
        }
        
        // Bad object collisions
        for (int i = 0; i < badObjects.size(); i++) {
        
            BadObject b = badObjects.get(i);
        
            if (player.getBounds().intersects(b.getBounds())) {
                if (!player.isImmune()) {
                    lives--;
                }
                
                badObjects.remove(i);
                i--;
                
                if (lives <= 0) {
                    endGame();
                }
            }
        }
        
        //Apricot shield collision
        for(int i=0; i<shields.size(); i++){
            Shield s = shields.get(i);
            if(player.getBounds().intersects(s.getBounds())){
                shields.remove(i);
                i--;
                
                player.setImmune(true);
                
                //Immunity stops after 5s
                Timer immunityTimer = new Timer(5000, new ActionListener(){
                    @Override
                    public void actionPerformed(ActionEvent e){
                        player.setImmune(false);
                    }
                });
                immunityTimer.setRepeats(false);
                immunityTimer.start();
            }
                
        }
    }
        
    //Removing objects from the screen
    private void removeOffScreenObjects() {
        coins.removeIf(c -> c.y > getHeight());
        badObjects.removeIf(b -> b.y > getHeight());
        shields.removeIf(s -> s.y > getHeight());
    }
        
    //Updating top panel with game statistics
    private void updateStats() {
        statsLabel.setText(
        "Leaves: " + coinsCollected +
        " | Lives: " + lives +
        " | Speed: " + player.getMovementSpeed() +
        " | High Score: " + highScore
        );
    }
        
    //End of game settings
    private void endGame() {
        if (coinsCollected > highScore) {
            highScore = coinsCollected; //updating high score
            
            //Ensure user is logged into account
            if(currentUsername !=null && userManager!=null){
                userManager.updateHighScore(currentUsername, highScore);
            }
        }
        gameOver = true;
        timer.stop();
        
        //Show upgrade speed button once game ends
        upgradeSpeedButton.setVisible(true);
        revalidate();
        repaint();
    }
    
    //Drawing game components
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        //Menu Background setup
        if(gameState.equals("MENU")){
            if(backgroundImg!=null){
                g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
            }else{
                g.setColor(new Color(217, 164, 98));
                g.fillRect(0,0,getWidth(),getHeight());
            }
            Graphics2D g2 = (Graphics2D) g;
            
            //semi-transparent menu box
            int boxW = 400;
            int boxH = 260;
            int x = getWidth()/2 - boxW/2;
            int y = getHeight()/2 - boxH/2 - 20;
            
            g2.setColor(new Color(0,0,0,180));
            g2.fillRoundRect(x,y,boxW,boxH,25,25);
            
            //Game title
            g2.setColor(Color.WHITE);
            g2.setFont(new Font("Arial", Font.BOLD, 26));
            g2.drawString("GIRAFFE GOBBLE!", x+70, y+50);
            
            //Game instructions
            g2.setFont(new Font("Arial", Font.PLAIN, 13));
            g2.setColor(Color.WHITE);
            g2.drawString("Move L/R using A/D or arrow keys", x+40, y+115);
            g2.drawString("Catch leaves for points and apricots for immunity!", x+40, y+140);
            g2.drawString("Avoid falling rocks!", x+40, y+165);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 16));
            g2.drawString("Press START to begin", x+120, y+80);
            return;
        }
        
        //Active Game Screen Background
        if(backgroundImg!=null){
            g.drawImage(backgroundImg, 0, 0, getWidth(), getHeight(), null);
        }
        
        // Draw player, coins, rocks
        player.draw(g);
        for (Coin c : coins) {
            c.draw(g);
        }
        for (BadObject b : badObjects) {
            b.draw(g);
        }
        for(Shield s : shields){
            s.draw(g);
        }
        
        // Instructions
        g.setColor(Color.BLACK);
        g.drawString("Use A/D or L/R ARROW KEYS to move",20,60);
        
        //Shield status
        Graphics2D g2b = (Graphics2D) g;
        
        int w = 140;
        int h = 30;
        int x = getWidth() - w - 20;
        int y = 42;
        
        Color beige = new Color(217, 164, 98);
        g2b.setColor(beige);
        g2b.fillRoundRect(x, y, w, h, 10, 10);
        
        g2b.setColor(Color.BLACK);
        g2b.setStroke(new BasicStroke(1.5f));
        g2b.drawRoundRect(x, y, w, h, 10, 10);
        
        g2b.setFont(new Font("Arial", Font.BOLD, 12));
        if(player.isImmune()){
            g2b.setColor(new Color(20, 120, 40));
            g2b.drawString("SHIELD: ACTIVE", x+18, y+19);
        }else{
            g2b.setColor(new Color(90, 80, 70));
            g2b.drawString("SHIELD: NONE", x+26, y+19);
        }
        
        //Game over text
        if(gameOver){
            Graphics2D g2 = (Graphics2D)g;
            
            //Semi-transparent game over box
            int sW = 500;
            int sH = 400;
            int sX = (getWidth()-sW)/2;
            int sY = (getHeight()-sH)/2;
            
            //Semi-transparent background
            g2.setColor(new Color(0, 0, 0, 140));
            g2.fillRoundRect(sX, sY, sW, sH, 40, 40);
            
            //Opaque game over box
            int gW = 440;
            int gH = 340;
            int gX = (getWidth() - gW)/2;
            int gY = (getHeight() - gH)/2;
            int cornerRadius = 30;
            
            Color restartColor = new Color(217, 164, 98);
            g2.setColor(restartColor);
            g2.fillRoundRect(gX, gY, gW, gH, cornerRadius, cornerRadius);
            
            //Outline
            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(3));
            g2.drawRoundRect(gX, gY, gW, gH, cornerRadius, cornerRadius);
            
            //Restart box text
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("Arial", Font.BOLD, 28));
            g2.drawString("GAME OVER", gX+130, gY+55);
            
            g2.setFont(new Font("Arial", Font.PLAIN, 18));
            g2.drawString("Leaves Eaten: " + coinsCollected, gX+140, gY+140);
            g2.drawString("Current Speed Level: " + speedLevel, gX+120, gY+175);
            
            g2.setFont(new Font("Arial", Font.BOLD, 13));
            g2.drawString("Press 'R' to restart", gX+150, gY+85);
        }
    }
        
    //Button events
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        //If user pressed A/D or L/R  keys, character moves left/right
        if(key==KeyEvent.VK_A || key==KeyEvent.VK_LEFT) left=true;
        if(key==KeyEvent.VK_D || key==KeyEvent.VK_RIGHT) right=true;
        
        //restart function
        if(gameOver && key==KeyEvent.VK_R){
            resetGame();
            gameState="PLAYING";
            updateUIState();
        }
    }
    //Button events
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        
        if(key==KeyEvent.VK_A || key==KeyEvent.VK_LEFT) left=false;
        if(key==KeyEvent.VK_D || key==KeyEvent.VK_RIGHT) right=false;
    }
        
    @Override
    public void keyTyped(KeyEvent e) {
    }
}