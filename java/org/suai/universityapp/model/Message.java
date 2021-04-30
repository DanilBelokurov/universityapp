package org.suai.universityapp.model;

import android.net.Uri;

import androidx.annotation.Nullable;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.MessageContentType;

import java.util.Date;

/**
 * Класс, имплементирующий объект "Сообщение"
 */
public class Message implements IMessage, MessageContentType.Image {

    private String id;            // Уникальное id сообщения
    private String text;          // Текст сообщения
    private final Date createdAt; // Время отправки
    private final Author author;  // Автора сообщения
    private String imgURL;        // Картинка в сообщении

    /**
     * Конструктор для отображения сообщений
     * @param id id
     * @param text текст
     * @param author автор
     */
    public Message(String id, String text, Author author) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = new Date();
    }

    /**
     * Конструктор для отображения сообщений
     * @param text текст
     * @param author автор
     */
    public Message(String text, Author author) {
        this.text = text;
        this.author = author;
        this.createdAt = new Date();
    }

    //Конструктор для отображения старых сообщений
    /**
     * Конструктор для отображения старых сообщений
     * @param id id
     * @param text текст
     * @param author автор
     * @param date дата отправки
     */
    public Message(String id, String text, Author author, String date) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = new Date(date);
    }

    /**
     * Конструктор для отображения сообщений c изображениями
     * @param id id
     * @param text текст
     * @param imgURL картинка
     * @param author автор
     */
    public Message(String id, String text, Uri imgURL, Author author) {
        this.id        = id;
        this.text      = text;
        this.author    = author;
        this.imgURL    = imgURL.toString();
        this.createdAt = new Date();
    }

    /**
     * Конструктор для отображения старых сообщений
     * @param id id
     * @param text текст сообщения
     * @param imgURL катринка
     * @param author автор
     * @param date время отправки
     */
    public Message(String id, String text, String imgURL, Author author, String date) {
        this.id        = id;
        this.text      = text;
        this.author    = author;
        this.imgURL    = imgURL;
        this.createdAt = new Date(date);
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setText(String text){
        this.text = text;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public boolean isWithImg(){
        return imgURL != null;
    }

    @Nullable
    @Override
    public String getImageUrl() {
        return imgURL;
    }
}