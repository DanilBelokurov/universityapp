package org.suai.universityapp.utils;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.suai.universityapp.database.Database;
import org.suai.universityapp.model.Author;
import org.suai.universityapp.model.Message;
import org.suai.universityapp.model.MessageForDB;

import java.util.Objects;

public class MessageDatabaseUtils {

    /**
     * Метод, создающий сообщение из данных БД
     * @param snapshot снэпшот БД
     * @return упакованное сообщение
     */
    public static Message messageCreateFromDB(@NonNull DataSnapshot snapshot) {

        String id   = snapshot.child("id").getValue().toString();               // ID сообщения
        String text = snapshot.child("text").getValue().toString();             // текст сообщения

        Author author = new Author(                                             // Автор сообщения
                snapshot.child("author").child("id").getValue().toString(),
                snapshot.child("author").child("name").getValue().toString(),
                snapshot.child("author").child("avatar").getValue().toString()
        );

        String createdAt = snapshot.child("createdAt").getValue().toString();   // Время отправки

        Message message = new Message(id, text, author, createdAt);

        if (snapshot.hasChild("imgURL")) {
            String imgURl = snapshot.child("imgURL").getValue().toString();
            message.setImgURL(imgURl);
        }

        return message;
    }

    /**
     * Метод, публикующий новое сообщение в БД
     * @param input новое сообщение
     * @param db БД
     */
    public static void newMessageSubmit(CharSequence input, Database db) {

        FirebaseUser user = db.getUserFromDB();                           // Пользователь
        DatabaseReference messages = db.getMessagesFromDB();              // БД с сообщениями

        String messageText = StringFormatter.format(input);               // Формитирование сообщения
        if (!messageText.equals("")) {

            String senderName = user.getDisplayName();

            Author author = new Author(
                    user.getUid(),
                    senderName,
                    user.getPhotoUrl().toString()
            );

            Message messageForChat = new Message(
                    IdGenerator.generate(messageText, user.getUid()),
                    messageText,
                    author);

            //Экспорт соощений в БД
            messages.push().setValue(new MessageForDB(messageForChat));
        }

    }

    /**
     * Метод удаления всех сообщений пользователя
     * @param pickedMessage выбранное сообщение пользователя
     * @param db база данных
     * @return сообщение для админа
     */
    public static String deleteAllMessages(Message pickedMessage, Database db) {

        final DatabaseReference messages = db.getMessagesFromDB();              // БД с сообщениями

        final String authorName = pickedMessage.getUser().getName();            // Автор сообщения

        messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("author").child("name")
                            .getValue().toString().equals(authorName)) {
                        String messageID = snapshot.getKey();
                        assert messageID != null;
                        messages.child(messageID).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return "Все сообщения от " + pickedMessage.getUser().getName() + " удалены";
    }

    /**
     * Удаление одного сообщения
     * @param pickedMessage выбранное сообщение
     * @param db база данных
     * @return сообщение для админа
     */
    public static String messageDelete(Message pickedMessage, Database db) {

        final DatabaseReference messages = db.getMessagesFromDB();

        final String badText = pickedMessage.getText();
        final String[] badTextKey = new String[1];

        messages.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (snapshot.child("text").getValue().toString().equals(badText)) {
                        badTextKey[0] = snapshot.getKey();
                        assert badTextKey[0] != null;
                        messages.child(badTextKey[0]).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        return "\"" + pickedMessage.getText() + "\" от "
                + pickedMessage.getUser().getName()
                + " удалено!";
    }

    /**
     * Метод отправки сообщений с картинками
     * @param pickedImgUri ссылка на картинку
     * @param db база данных
     */
    public static void postPhoto(Uri pickedImgUri, Database db) {

        final FirebaseUser user = db.getUserFromDB();                   // Пользователь
        final DatabaseReference messages = db.getMessagesFromDB();      // База данных с сообщениями

        StorageReference pics = FirebaseStorage.getInstance()           // База данных с картинками
                .getReference().child("message_pics");

        final StorageReference imageFilePath = pics
                .child(user.getUid() + "_" + (Math.random() * 10000000 ));

        imageFilePath.putFile(pickedImgUri).addOnSuccessListener(taskSnapshot ->
                imageFilePath.getDownloadUrl().addOnSuccessListener(uri -> {

                    String senderName = user.getDisplayName();

                    Author author = new Author(
                            user.getUid(),
                            senderName,
                            Objects.requireNonNull(user.getPhotoUrl()).toString()
                    );

                    //Экспорт соощений в БД
                    messages.push().setValue(new MessageForDB(
                            user.getUid(),
                            "",
                            author,
                            uri.toString()
                    ));
                }));
    }
}