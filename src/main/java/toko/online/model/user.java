package toko.online.model;

public class user {
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String address;
    private String pos_code;

    public user(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public void address(String phoneNumber, String address, String pos_code){
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.pos_code = pos_code;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getEmail() {
        return this.email;
    }   
    public String getPhoneNumber(){
        return this.phoneNumber;
    }
    public String getAddress(){
        return this.address;
    }
    public String posCode(){
        return this.pos_code;
    }
}
