package model.dto.request;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String posCode;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String email, String phoneNumber, String address, String posCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.posCode = posCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPosCode() {
        return posCode;
    }

    public void setPosCode(String posCode) {
        this.posCode = posCode;
    }
}
