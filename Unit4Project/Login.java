import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

//User screen for login and registration
public class Login extends JPanel{
    //Text input elements for username and password
    private JTextField userField = new JTextField(15);
    private JPasswordField passField = new JPasswordField(15);
    //Buttons for login and registration
    private JButton actionButton = new JButton("Login");
    private JButton switchButton = new JButton("Register a new account");
    //Game title
    private JLabel titleLabel = new JLabel("Giraffe Gobble!", SwingConstants.CENTER);
    
    private boolean isLoginMode=true; //login screen originally visible
    private UserManager userManager;
    private Runnable onSuccess; //For callback execution
    
    private String loggedInUser = "";
    
    //Login constructor with layout components
    public Login(UserManager manager, Runnable onSuccess){
        this.userManager = manager;
        this.onSuccess = onSuccess;
        
        //Background
        this.setBackground(new Color(245, 230, 200));
        this.setLayout(new GridBagLayout());
        
        //Grid spacing
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10,10,10,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        //Title display
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(100,65,15));
        
        //Title constraints
        gbc.gridx=0;
        gbc.gridy=0;
        gbc.gridwidth=2;
        add(titleLabel, gbc);
        
        //Username text prompt position
        gbc.gridwidth=1;
        gbc.gridy=1;
        gbc.gridx=0;
        add(new JLabel("Username:"), gbc);
        gbc.gridx=1;
        add(userField, gbc);
        
        //Password text prompt position
        gbc.gridx=0;
        gbc.gridy=2;
        add(new JLabel("Password:"), gbc);
        gbc.gridx=1;
        add(passField, gbc);
        
        //Action button appearance
        gbc.gridx=0;
        gbc.gridy=3;
        gbc.gridwidth=2;
        actionButton.setBackground(new Color(230, 189, 140));
        actionButton.setFont(new Font("Arial", Font.BOLD, 14));
        add(actionButton, gbc);
        
        //switch button appearance
        gbc.gridy=4;
        switchButton.setBorderPainted(false);
        switchButton.setContentAreaFilled(false);
        switchButton.setForeground(Color.BLUE);
        add(switchButton, gbc);
        
        //action listeners
        actionButton.addActionListener(e -> handleLogin());
        switchButton.addActionListener(e -> toggleMode());
    }
    
    //Toggling between login vs registration screens
    private void toggleMode(){
        isLoginMode = !isLoginMode;
        if(isLoginMode){
            titleLabel.setText("Welcome to Giraffe Gobble!");
            actionButton.setText("Login");
            switchButton.setText("Register a new account");
        }else{
            titleLabel.setText("Create New Account");
            actionButton.setText("Register");
            switchButton.setText("Already have an account? Login");
        }
    }
    
    //User login button handling
    private void handleLogin(){
        String username = userField.getText().trim();
        String password = new String(passField.getPassword()).trim();
        
        if(username.isEmpty() || password.isEmpty()){  //checks if fields are both filled
            JOptionPane.showMessageDialog(this, "Field cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if(isLoginMode){ //if on login screen, checks username/password
            if(userManager.loginUser(username, password)){
                loggedInUser = username;
                onSuccess.run();
            }else{
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }else{
            if(userManager.registerUser(username, password)){
                JOptionPane.showMessageDialog(this, "Registered! You can now log in.");
                toggleMode();
            }else{
                JOptionPane.showMessageDialog(this, "Username already taken or invalid.", "Registration Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    //Returns user identification information
    public String getLoggedInUser(){
        return loggedInUser;
    }
}