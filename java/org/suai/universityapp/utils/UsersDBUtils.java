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


    /**
     * Метод добавления юзерпика
     * @param name Имя пользователя
     * @param pickedImgUri выбранное изображение
     * @param newUser пользователь
     * @param db база данных
     * @return
     */
    public static UserProfileChangeRequest[] updateUserInfo(final String name,
                                                            final Uri pickedImgUri,
                                                            final User newUser,
                                                            final Database db) {

        final UserProfileChangeRequest[] result = new UserProfileChangeRequest[1];

        // Если не выбрал картинку, то стандартную
        if (pickedImgUri.toString().equals("")) {
            String emptyUri = "https://firebasestorage.googleapis.com/v0/b/slotick-5e305.appspot.com/o/users_photos%2Fprofile_man4.jpg?alt=media&token=533ddbe6-7930-4eb5-9351-6c11a2329b84";

            newUser.setAvatarUri(emptyUri);
            db.getUsersFromDB().child(newUser.getUid()).child("uri").setValue(emptyUri);

            result[0] = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(emptyUri))
                    .build();
        } else {
            final FirebaseUser user = db.getUserFromDB();
            StorageReference users_pics = FirebaseStorage.getInstance().getReference().child("test");
            final StorageReference imageFilePath = users_pics.child(user.getUid());

            imageFilePath.putFile(pickedImgUri).addOnSuccessListener(taskSnapshot ->
                imageFilePath.child(pickedImgUri.toString()).getDownloadUrl().addOnSuccessListener(uri -> {
                    newUser.setAvatarUri(uri.toString());
                    db.getUsersFromDB().child(newUser.getUid()).child("uri").setValue(uri.toString());

                    result[0] = new UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .setPhotoUri(uri)
                            .build();
                })).addOnFailureListener(e -> {});
        }
        return result;
    }
}