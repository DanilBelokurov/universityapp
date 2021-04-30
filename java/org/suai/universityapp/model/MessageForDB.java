package org.suai.universityapp.model;

import java.util.Date;

/**
 * Класс, имплементирующий объект "сообщение"
 * для записи в БД
 */
public class MessageForDB {

    private String id;        // Уникальное id сообщения
    private String text;      // Текст сообщения
    private String createdAt; // Время отправки
    private Author author;    // Автора сообщения
    private String imgURL;    // Картинка в сообщении

    /**
     * Конструктор с параметрами
     * @param id id сообщения
     * @param text текст сообщения
     * @param author автор сообщения
     * @param imgURL ссылка на изображение
     */
    public MessageForDB(String id, String text, Author author, String imgURL){
        this.id     = id;
        this.text   = text;
        this.author = author;
        this.imgURL = imgURL;
        this.createdAt = new Date().toString();
    }

    /**
     * Конструктор создающий объект для записи в БД
     * из сообщения из чата
     * @param message объект сообщения из чата
     */
    public MessageForDB(Message message){
        this.id        = message.getId();
        this.text      = message.getText();
        this.createdAt = message.getCreatedAt().toString();
        this.author    = message.getUser();
        this.imgURL    = message.getImgURL();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
}
