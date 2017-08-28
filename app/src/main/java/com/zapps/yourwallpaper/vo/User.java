package com.zapps.yourwallpaper.vo;

/**
 * Created by Zimincom on 2017. 8. 15..
 */

public class User {
    public String nickname;
    public String userPhone;
    public String matePhone;
    private String mateKey;
    public boolean isCouple;

    public User(){}

    public User(String nickname, String userPhone, String matePhone) {
        this.nickname = nickname;
        this.userPhone = userPhone;
        this.matePhone = matePhone;
    }

    @Override
    public String toString() {
        return "User{" +
                "nickname='" + nickname + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", matePhone='" + matePhone + '\'' +
                ", isCouple=" + isCouple +
                '}';
    }

    public String getNickname() {
        return nickname;
    }

    public void setIsCouple(boolean isCouple) {
        this.isCouple = isCouple;
    }

    public String getMateKey() {
        return mateKey;
    }
}
