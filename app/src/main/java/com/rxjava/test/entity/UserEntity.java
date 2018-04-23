package com.rxjava.test.entity;


public class UserEntity {
    private String appUID = "";
    private int userId;
    private String userName = "";

    public String getAppUID() {
        return appUID;
    }

    public void setAppUID(String appUID) {
        this.appUID = appUID;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserEntity{" +
                "appUID='" + appUID + '\'' +
                ", userId=" + userId +
                ", userName='" + userName + '\'' +
                '}';
    }
}
