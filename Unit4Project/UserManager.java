import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserManager{
    private static final String file_name = "users.txt";
    
    //store usernames to user profile with password and highscore
    private Map<String, UserProfile> userDatabase = new HashMap<>();
    
    public UserManager(){
        loadUsers();
        
        if(!userDatabase.containsKey("user")){
            userDatabase.put("user", new UserProfile("password", 50));
        }
    }
    
    public static class UserProfile{
        String password;
        int highScore;
        
        UserProfile(String password, int highScore){
            this.password = password;
            this.highScore = highScore;
        }
    }
    
    //loading users from text file
    private void loadUsers(){
        File file = new File(file_name);
        if(!file.exists()) return;
        
        try(BufferedReader br = new BufferedReader(new FileReader(file))){
            String line;
            while((line=br.readLine())!=null){
                String[] parts = line.split(":");
                if(parts.length==3){
                    String username = parts[0];
                    String password = parts[1];
                    int score = Integer.parseInt(parts[2]);
                    userDatabase.put(username, new UserProfile(password, score));
                }
            }
        }catch(IOException e){
            System.out.println("Error loading users: " + e.getMessage());
        }
    }
    //Save users to file
    private void saveUsers(){
        try(PrintWriter pw = new PrintWriter(new FileWriter(file_name))){
            for(Map.Entry<String, UserProfile> entry : userDatabase.entrySet()){
                pw.println(entry.getKey() + ":" + entry.getValue().password + ":" + entry.getValue().highScore);
            }
        } catch(IOException e){
            System.out.println("Error saving users: " + e.getMessage());
        }
    }
    
    //Registering users
    public boolean registerUser(String username, String password){
        if(username.isEmpty() || password.isEmpty() || userDatabase.containsKey(username)){
            return false;
        }
        userDatabase.put(username, new UserProfile(password, 0));
        saveUsers();
        return true;
    }
    
    //Checking login information
    public boolean loginUser(String username, String password){
        UserProfile user = userDatabase.get(username);
        return user != null && user.password.equals(password);
    }
    
    //Accessing high score
    public int getHighScore(String username){
        UserProfile user = userDatabase.get(username);
        if(user!=null){
            return user.highScore;
        }else{
            return 0;
        }
    }
    
    //Updating user's high score
    public void updateHighScore(String username, int newScore){
        UserProfile user = userDatabase.get(username);
        if(user != null && newScore>user.highScore){
            user.highScore=newScore;
            saveUsers();
        }
    }
    
}