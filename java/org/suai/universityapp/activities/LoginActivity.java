package org.suai.universityapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;

import org.suai.universityapp.R;
import org.suai.universityapp.database.Database;

import java.util.Objects;


/**
 * Класс, имплементирующий авторизацию пользователя
 */
public class LoginActivity extends AppCompatActivity {

    private EditText userMail,userPassword;     // Поля ввода информации
    private Button loginBtn, registerBtn;       // Кнопки (авторизации, регистрации)
    private ImageView anonymousBtn;             // Кнопка продолжения без авторизации
    private ProgressBar loginProgress;          // Прогресс бар

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final Activity thisActivity = this;

        // Инициализация основных полей
        initialization();

        // Нажатие на кнопку авторизациии
        loginBtn.setOnClickListener(view -> {
            loginProgress.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.INVISIBLE);

            String mail = userMail.getText().toString();
            String password = userPassword.getText().toString();

            // Проверка заполнения полей
            if (mail.isEmpty() || password.isEmpty()) {
                showMessage("Please verify all fields!");
                loginBtn.setVisibility(View.VISIBLE);
                loginProgress.setVisibility(View.INVISIBLE);
            } else {
                signIn(mail, password);
            }
        });

        // Нажатие на кнопку "Регистрация"
        registerBtn.setOnClickListener(view -> {
            Intent intent = new Intent(thisActivity, RegistrationActivity.class);
            startActivity(intent);
        });

        // Нажатие на кнопку "Продолжить без авторизации"
        anonymousBtn.setOnClickListener(view -> {
            anonymousBtn.setVisibility(View.INVISIBLE);
            thisActivity.finish();
        });
    }

    /**
     * Метод, инициализирующий основные поля
     */
    private void initialization(){
        userMail      = findViewById(R.id.login_mail);
        userPassword  = findViewById(R.id.login_password);
        loginBtn      = findViewById(R.id.loginBtn);
        registerBtn   = findViewById(R.id.registerBtn);
        loginProgress = findViewById(R.id.login_progress);
        anonymousBtn  = findViewById(R.id.anonimusBtn);

        loginProgress.setVisibility(View.INVISIBLE);
    }

    /**
     * Метод авторизации пользователя
     * @param mail e-mail
     * @param password пароль
     */
    private void signIn(String mail, String password) {

        // Подключение к БД
        Database db = new Database();

        // Попытка авторизации
        db.getAuthFromDB().signInWithEmailAndPassword(mail,password)
                .addOnCompleteListener(task -> {
                    // Успешная авторизация
                    if (task.isSuccessful()) {
                        loginProgress.setVisibility(View.INVISIBLE);
                        loginBtn.setVisibility(View.VISIBLE);
                        updateUI();
                    }
                    else {
                        showMessage(Objects.requireNonNull(task.getException()).getMessage());
                        loginBtn.setVisibility(View.VISIBLE);
                        loginProgress.setVisibility(View.INVISIBLE);
                    }
                });
    }

    /**
     * Метод, выполняющий переход на "Главную"
     */
    private void updateUI() {
        loginProgress.setVisibility(View.INVISIBLE);
        startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Метод, выводящий сообщение пользователю
     * @param text сообщение для пользователя
     */
    private void showMessage(String text) {
        Toast.makeText(getApplicationContext(),text,Toast.LENGTH_LONG).show();
    }

}