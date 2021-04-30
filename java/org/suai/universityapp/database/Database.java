package org.suai.universityapp.database;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * Класс, имплементирующий получение ссылок
 * на ветки в базе данных
 */
public class Database {

    private FirebaseAuth auth;         // Авторизация
    private FirebaseDatabase database; // База данных
    private FirebaseStorage storage;   // База данных (файлы)

    private FirebaseUser user;         // Объект "пользователь"

    private DatabaseReference messages; // Ветка с сообщениями
    private DatabaseReference users;    // Ветка с пользователями
    private DatabaseReference news;     // Ветка с новостями

    private StorageReference imageFilePath; // Ссылка на картинки

    public Database() {
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        database = FirebaseDatabase.getInstance();
        messages = database.getReference("messages");
        users    = database.getReference("users");
        news     = database.getReference("news");

        storage = FirebaseStorage.getInstance();
        imageFilePath = storage.getReference().child("users_photos");
    }

    public DatabaseReference getUsersFromDB() {
        return users;
    }

    public FirebaseUser getUserFromDB() {
        return user;
    }

    public DatabaseReference getMessagesFromDB() {
        return messages;
    }

    public DatabaseReference getNewsFromDB() {
        return news;
    }

    public FirebaseAuth getAuthFromDB(){
        return auth;
    }

    public StorageReference getImageFilePath(){
        return imageFilePath;
    }
}
