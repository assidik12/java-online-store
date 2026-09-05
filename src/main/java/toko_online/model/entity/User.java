package toko_online.model.entity;

import toko_online.model.enums.Role;

public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private Role role;
    private String phoneNumber;
    private String address;
    private String posCode;

    public User() {
    }

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = Role.USER;
    }

    public User(String username, String password, String email, Role role, String phoneNumber, String address,
            String posCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role != null ? role : Role.USER;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.posCode = posCode;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
