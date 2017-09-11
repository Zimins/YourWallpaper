package com.zapps.yourwallpaper.vo;

/**
 * Created by Zimincom on 2017. 8. 15..
 */

public class User {
    private String nickname;
    private String userPhone;
    private String matePhone;
    private String mateKey;
    private boolean isCouple;

    public User(){}

    public User(String nickname, String userPhone, String matePhone) {
        this.nickname = nickname;
        this.userPhone = userPhone;
        this.matePhone = matePhone;
    }

    public String getNickname() {
        return nickname;
    }

    public boolean getIsCouple() {
        return isCouple;
    }

    public void setCouple(boolean couple) {
        isCouple = couple;
    }

    public void setIsCouple(boolean isCouple) {
        this.isCouple = isCouple;
    }

    public String getMateKey() {
        return mateKey;
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

}
