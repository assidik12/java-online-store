package model.dto.response;

public class LoginResponse {
    private long id;
    private String userEmail;
    private String username;
    private String token;

    public LoginResponse() {
    }

    public LoginResponse(long id, String userEmail, String username, String token) {
        this.id = id;
        this.userEmail = userEmail;
        this.username = username;
        this.token = token;

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
