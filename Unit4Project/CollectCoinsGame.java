/*
Title: Giraffe Gobble!
Name: Alyssa Hayhurst
Date: 2026-05-21
Project Description: ICS4UR Collect the coins game
*/

import javax.swing.*;
import java.awt.*;

//Main window frame for game
public class CollectCoinsGame extends JFrame {
    
    //Card layout manager
    private CardLayout cardLayout = new CardLayout();
    private JPanel mainContainer = new JPanel(cardLayout);
    
    private UserManager userManager = new UserManager(); //Manager for game and file updates
    private Login loginPanel; //Panel for user registration and login
    private GamePanel gamePanel; //Panel for active game play
    
    public CollectCoinsGame() {
        setTitle("Collect The Coins - ICS4UR");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Initialize game panel
        gamePanel = new GamePanel(() ->{
            cardLayout.show(mainContainer, "AUTH");
        });
        
        //Initialize login panel
        loginPanel = new Login(userManager, () ->{
            //user logs in and information is loaded
            String user = loginPanel.getLoggedInUser();
            int savedHighScore = userManager.getHighScore(user);
            
            //Game panel user information set up
            gamePanel.setPlayerSession(user, savedHighScore, userManager);
            
            //changes to active game screen
            cardLayout.show(mainContainer, "GAME");
            
            gamePanel.requestFocusInWindow();
        });
        
        //Each panel is added
        mainContainer.add(loginPanel, "AUTH");
        mainContainer.add(gamePanel, "GAME");
        
        //Border layout is used and container is centered
        setLayout(new BorderLayout());
        add(mainContainer, BorderLayout.CENTER);
        
        cardLayout.show(mainContainer, "AUTH");
        
        pack();
        setLocationRelativeTo(null);
        setVisible(true); //initially set to visible
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CollectCoinsGame();
        });
    }
}