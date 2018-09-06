package com.karimtimer.sugarcontrol.userAccount;

public class UserInfo {


    private String email;

    public UserInfo(){

    }

    public UserInfo(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}