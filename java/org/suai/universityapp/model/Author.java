package org.suai.universityapp.model;

import com.stfalcon.chatkit.commons.models.IUser;

/**
 * Класс, имплементирующий объект автора сообщений в чате
 */
public class Author implements IUser {

    private final String id;     // Уникальное id автора
    private final String name;   // Имя автора
    private final String avatar; // Юзерпик автора

    /**
     * Конструктор
     * @param id id
     * @param name имя
     * @param avatar юзерпик
     */
    public Author(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getAvatar() {
        return this.avatar;
    }
}
