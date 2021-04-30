package org.suai.universityapp.model;

import java.util.Date;

/**
 * Класс, имплементирующий пользователя чата
 */
public class User {

    private String name;                // Ник
    private String email;               // E-mail
    private String registrationDate;    // Дата регистрации
    private String uid;                 // Уникальный id
    private String avatarUri;           // Юзерпик
    private int    admin;               // Статус админа
    private int    ban;                 // Флаг бана

    /**
     * Конструктор по умолчанию
     */
    public User(){ }

    /**
     * Конструктор с параметрами
     * @param name Имя пользователя
     * @param email E-mail
     * @param uid уикальный id
     * @param isAdmin является ли пользователь админом
     * @param isBanned забанен ли пользователь
     */
    public User(String name,String email, String uid, int isAdmin, int isBanned) {
        this.email = email;
        this.name  = name;
        this.uid   = uid;
        this.admin = isAdmin;
        this.ban   = isBanned;

        Date date  = new Date();
        this.registrationDate = date.toString();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid(){
        return uid;
    }

    public String getAvatarUri() {
        return avatarUri;
    }

    public void setAvatarUri(String avatarUri) {
        this.avatarUri = avatarUri;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setUid(String uid){
        this.uid = uid;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }

    public int getBan() {
        return ban;
    }

    public void setBan(int ban) {
        this.ban = ban;
    }

}
