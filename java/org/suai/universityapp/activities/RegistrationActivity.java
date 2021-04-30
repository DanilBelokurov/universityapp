package org.suai.testapp.activities;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.suai.testapp.R;
import org.suai.testapp.database.Database;
import org.suai.testapp.model.User;
import org.suai.testapp.utils.UsersDBUtils;

import java.util.Objects;

/**
 * Класс, имплементирующий регистрацию нового пользователя
 */
public class RegistrationActivity extends AppCompatActivity {

    private ImageView userPic;                                          // Юзерпик
    private EditText userEmail, userPassword, userPassword2, userName;  // Поля данных
    private ProgressBar loadingProgress;                                // Прогрессбар
    private Button regBtn;                                              // Кнопка регистрации
    private ImageView backToMain;                                       // Кнопка отмены регистрации

    private Uri pickedImgUri;                                           // Изображение для юзерпика
    private User newUser = new User();                                  // Объект "Пользователь"

    private Database db;                                                // Базаданных


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        // Инициализация основного View
        final Activity thisActivity = this;
        TextView toLogin = findViewById(R.id.btn_to_login_reg);

        // Подключение к базе данных
        db = new Database();

        // Инициализация основных полей
        initialization();

        // Регистрация нового пользователя
        regBtn.setOnClickListener(view -> {
            regBtn.setVisibility(View.INVISIBLE);
            loadingProgress.setVisibility(View.VISIBLE);

            String email     = userEmail.getText().toString();
            String password  = userPassword.getText().toString();
            String password2 = userPassword2.getText().toString();
            String name      = userName.getText().toString();

            if( email.isEmpty() || name.isEmpty() || password.isEmpty()  || !password.equals(password2)) {
                showMessage("Please verify all fields!") ;
                regBtn.setVisibility(View.VISIBLE);
                loadingProgress.setVisibility(View.INVISIBLE);
            } else
                createNewUser(email, name, password);
        });

        // Отмена регистрации
        backToMain.setOnClickListener(view -> {
            backToMain.setVisibility(View.INVISIBLE);
            thisActivity.finish();
        });

        // Переход к авторизации
        toLogin.setOnClickListener(view -> thisActivity.finish());

        // Выбор юзерпика
        userPic.setOnClickListener(view -> {
            if (Build.VERSION.SDK_INT >= 22)
                checkAndRequestForPermission();
            else
                openGallery();
        });
    }

    /**
     * Метод инициализации основных полей
     */
    private void initialization(){
        userPic         = findViewById(R.id.regUserPhoto);
        userName        = findViewById(R.id.regName);
        userEmail       = findViewById(R.id.regMail);
        userPassword    = findViewById(R.id.regPassword);
        userPassword2   = findViewById(R.id.regPassword2);
        regBtn          = findViewById(R.id.regBtn);
        backToMain      = findViewById(R.id.back_to_main_in_anonim);
        loadingProgress = findViewById(R.id.regProgressBar);
        loadingProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Метод создания нового аккаунта
     * @param email e-mail пользователя
     * @param name имя пользователя
     * @param password пароль пользователя
     */
    private void createNewUser(final String email, final String name, final String password) {
        db.getAuthFromDB()
                .createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {

                    // Успешная регистрация, записываем открытую информацию в БД
                    if (task.isSuccessful()) {

                        newUser = new User(name,
                                email,
                                db.getUserFromDB().getUid(),
                                0,0);

                        UsersDBUtils.addNewUser(newUser, db);

                        // Добавляем юзерпик
                        updateUserInfo(name, pickedImgUri, db.getUserFromDB());

                        //YandexMetrica.reportEvent("Регистрация");
                        showMessage("Account created!");
                    } else {
                        showMessage("Oops! Error: "
                                + Objects.requireNonNull(task.getException()).getMessage());
                        regBtn.setVisibility(View.VISIBLE);
                        loadingProgress.setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     * Метод добавления юзерпика
     * @param name имя пользователя
     * @param pickedImgUri изображение для юзерпика
     * @param currentUser объект пользователя в БД
     */
    private void updateUserInfo(final String name, Uri pickedImgUri, final FirebaseUser currentUser) {

        final UserProfileChangeRequest[] profileUpdate = new UserProfileChangeRequest[1];

        if (pickedImgUri == null) {
            String emptyUri = "https://firebasestorage.googleapis.com/v0/b/slotick-5e305.appspot.com/o/users_photos%2Fprofile_man4.jpg?alt=media&token=533ddbe6-7930-4eb5-9351-6c11a2329b84";

            newUser.setAvatarUri(emptyUri);
            db.getUsersFromDB().child(newUser.getUid()).child("uri").setValue(emptyUri);

            profileUpdate[0] = new UserProfileChangeRequest.Builder()
                    .setDisplayName(name)
                    .setPhotoUri(Uri.parse(emptyUri))
                    .build();

            currentUser.updateProfile(profileUpdate[0])
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showMessage("Register complete");
                            updateUI();
                        }
                    });
        } else {
            String uid = db.getUserFromDB().getUid();
            db.getImageFilePath().child(uid).putFile(pickedImgUri)
                    .addOnSuccessListener(taskSnapshot ->
                            db.getImageFilePath().child(uid).getDownloadUrl().addOnSuccessListener(uri -> {

                                newUser.setAvatarUri(uri.toString());
                                db.getUsersFromDB().child(newUser.getUid()).child("uri").setValue(uri.toString());

                                profileUpdate[0] = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(uri)
                                        .build();

                                currentUser.updateProfile(profileUpdate[0])
                                        .addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                showMessage("Register complete");
                                                updateUI();
                                            }
                                        });
                            }));
        }
    }

    /**Метод возвращения на "Главную"*/
    private void updateUI() {
        Intent homeActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(homeActivity);
        finish();
    }

    /**
     * Метод отрисовки сообщения пользователю
     * @param message сообщение для пользователя
     */
    private void showMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    /**Метод открытия галереи пользователя*/
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,1);
    }

    /**Метод проверки разрешений*/
    private void checkAndRequestForPermission() {
        int permission = ContextCompat.checkSelfPermission(RegistrationActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            boolean showRequest = ActivityCompat
                    .shouldShowRequestPermissionRationale(
                            RegistrationActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

            if (showRequest)
                showMessage("Please accept for required permission");
            else
                ActivityCompat
                        .requestPermissions(
                                RegistrationActivity.this,
                                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                1);
        }
        else
            openGallery();
    }

    /**
     * Логика завершения активити (обрезка изображения в овал)
     * @param requestCode код запроса
     * @param resultCode код завершения активити
     * @param data переданная информация
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                pickedImgUri = result.getUri();
                userPic.setImageURI(pickedImgUri);
                Log.d("TAG", "onActivityResult: kjdfkfkj");
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                showMessage(result.getError().getMessage());
            }
        }
        if (resultCode == RESULT_OK && requestCode == 1 && data != null ) {
            pickedImgUri = data.getData();
            CropImage.activity(pickedImgUri)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .setMaxCropResultSize(1000, 1000)
                    .start(this);
        }
    }

}
