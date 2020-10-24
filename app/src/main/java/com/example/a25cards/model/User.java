package com.example.a25cards.model;

public class User {
    private String nickname;
    private String username;
    private String pwd;

    public User(String username, String pwd, String nickname){
        this.username = username;
        this.pwd = pwd;
        this.nickname = nickname;
    }
    public String getNickname() {
        return nickname;
    }

    public String getUsername() {
        return username;
    }

    public String getPwd() {
        return pwd;
    }

    public void setNike(String nickname) {
        this.nickname = nickname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public boolean login(){

        return false;
    }

}
