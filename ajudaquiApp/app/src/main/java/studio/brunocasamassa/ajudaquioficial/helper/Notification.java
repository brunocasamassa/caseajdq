package studio.brunocasamassa.ajudaquioficial.helper;

/**
 * Created by bruno on 07/07/2017.
 */

public class Notification {

    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    private String imageUrl;
    private String email;
    private String uid;
    private String text;
    private String token;
    private String message;
    private String title;
    private String command;

    public Notification(){

    }

    public Notification(String username , String imageUrl , String email, String uid, String text, String token){
        this.username = username;
        this.imageUrl = imageUrl;
        this.email = email;
        this.uid = uid;
        this.text = text;
        this.token = token;

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
