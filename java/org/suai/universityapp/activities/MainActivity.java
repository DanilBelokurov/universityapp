package org.suai.universityapp.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.WindowManager;
import android.webkit.CookieSyncManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.yandex.metrica.YandexMetrica;

import org.suai.universityapp.R;
import org.suai.universityapp.fragments.ChatFragment;
import org.suai.universityapp.fragments.NewsFragment;
import org.suai.universityapp.fragments.UserFragment;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_INTRO = 1;

    public static final String FIRST_START_KEY = "FIRST_START";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Флаг первого запуска приожения */
        boolean firstStart = PreferenceManager
                .getDefaultSharedPreferences(this)
                .getBoolean(FIRST_START_KEY,true);

        /* Запуск интро, если это первый запуск */
        if(firstStart){
            Intent intro = new Intent(this, IntroActivity.class);
            startActivityForResult(intro, REQUEST_CODE_INTRO);
        }

        // Проверка авторизации
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() == null) {
            auth.signInAnonymously().addOnCompleteListener(task -> drawView());
        } else {
            drawView();
        }

    }

    /**
     * Метод, отрисовывающий основной интерфейс
     */
    private void drawView() {

        // Установка экшн-бара
        Toolbar actionBar = findViewById(R.id.toolbar_actionbar);
        actionBar.setTitle("Новости");
        setSupportActionBar(actionBar);

        // Обработка событий из меню навигации
        BottomNavigationView navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(menuItem -> {
            int id = menuItem.getItemId();
            Fragment fragment = new Fragment();
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            CharSequence title = "";
            switch (id) {
                case (R.id.news):
                    title = "Новости";
                    fragment = new NewsFragment();
                    break;
                case (R.id.chat):
                    title = "Чат";
                    fragment = new ChatFragment();
                    break;
                case (R.id.favorites):
                    title = "Профиль";
                    fragment = new UserFragment();
                    break;
            }

            Bundle fragmentsBundle = new Bundle();
            fragment.setArguments(fragmentsBundle);

            actionBar.setTitle(title);
            fragmentTransaction.replace(R.id.frame_layout, fragment);
            fragmentTransaction.commitAllowingStateLoss();
            return true;
        });
        navigationView.setSelectedItemId(R.id.news);
    }


    /**
     * Метод отправки события в метрику
     * @param requestCode код запроса
     * @param resultCode возвращаемый код
     * @param data возвращаемые данные
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_INTRO) {
            if (resultCode == RESULT_OK) {
                PreferenceManager.getDefaultSharedPreferences(this).edit()
                        .putBoolean(FIRST_START_KEY, false)
                        .apply();
                //YandexMetrica.reportEvent("Пройдено интро");
            } else { finish(); }
        }
    }

}