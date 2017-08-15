package com.zapps.yourwallpaper;

/**
 * Created by Zimincom on 2017. 8. 15..
 */

public class User {
    public String nickname;
    public String phone;
    public String mate;
    public boolean isCouple;

    public User(){}

    public User(String phone, String mate) {
        this.phone = phone;
        this.mate = mate;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", mate='" + mate + '\'' +
                ", isCouple=" + isCouple +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setIsCouple(boolean isCouple) {
        this.isCouple = isCouple;
    }
}
