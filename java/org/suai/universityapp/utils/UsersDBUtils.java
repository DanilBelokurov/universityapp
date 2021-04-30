package org.suai.universityapp.utils;

import android.net.Uri;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.suai.universityapp.database.Database;
import org.suai.universityapp.model.Message;
import org.suai.universityapp.model.User;

/**
 * Класс, имплементирующий утилиты для объекта "Пользователь"
 */
public class UsersDBUtils {

    /**
     * Метод добавления нового пользователя
     * @param newUser новый пользователь
     * @param db база данных
     */
    public static void addNewUser(User newUser, Database db) {
        db.getUsersFromDB().child(newUser.getUid()).setValue(newUser);
    }

    /**
     * Метод бана пользователя в чате
     * @param pickedMessage выбранное сообщение
     * @param db база данных
     * @return сообщение для админа
     */
    public static String userMute(Message pickedMessage, Database db) {
        String badBoyId = pickedMessage.getUser().getId();

        db.getUsersFromDB().child(badBoyId).child("ban").setValue(1);

        return pickedMessage.getUser().getName() + " идет в бан!";
    }
}
