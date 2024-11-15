package toko.online.view;

import toko.online.controller.authController;

import toko.online.model.user;


public class authView {


    public boolean RegisterView(user user) {
        if (user.getUsername().equals("") || user.getPassword().equals("") || user.getEmail().equals("")) {
            return false;
        }
        authController auth = new authController();
        if ((boolean) auth.RegisterController(user.getUsername(), user.getPassword(), user.getEmail())) {
            return true;
        }
        return false;
    }


}
