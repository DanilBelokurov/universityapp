package org.suai.universityapp.activities;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import org.suai.universityapp.R;

import java.util.Objects;

/**
 * Класс, имплементирующий вебвью (браузер в приложении)
 */
public class WebviewActivity extends AppCompatActivity {


    private WebView webView;                        // Объект класса "Вебвью"
    private Toolbar mActionBarToolbar;              // Тулбар

    private boolean isVerticalOrientation = true;   // Флаг ориентации экрана

    private String link;                            // Ссылка на сайт
    private String pageName;                        // Название страницы

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        // Проверка сохраненного состояния (для поворота экрана)
        if (savedInstanceState == null) {

            // Инициализация основных полей
            initialization();

            // Настройка вебвью
            setSettings();

            // Метод поворота экнара
            screenRotation();

            // Переход по ссылке
            webView.loadUrl(link);
        } else {
            webView.restoreState(savedInstanceState);
        }

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }


    /**
     * Метод, инициализирующий основные поля и переменные
     */
    private void initialization() {

        Bundle arguments = getIntent().getExtras();
        assert arguments != null;
        link = Objects.requireNonNull(arguments.get("link")).toString();
        pageName = Objects.requireNonNull(arguments.get("pageName")).toString();

        webView = findViewById(R.id.webview);

        mActionBarToolbar = findViewById(R.id.toolbar_actionbar);
        mActionBarToolbar.setTitle(pageName);
    }

    /**
     * Метод, устанавливающий настройки вебвью
     */
    @SuppressLint("SetJavaScriptEnabled")
    private void setSettings() {
        webView.getSettings().setGeolocationEnabled(false);                 // Отключение геолокации

        webView.getSettings().setJavaScriptEnabled(true);                   // Включение JS
        webView.getSettings().setSupportMultipleWindows(true);              // Включение нескольких вкладок
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(false);

        webView.setFocusable(true);
        webView.setFocusableInTouchMode(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);

        CookieManager cookieManager = CookieManager.getInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            cookieManager.setAcceptThirdPartyCookies(webView, true);
        else
            cookieManager.setAcceptCookie(true);
        CookieManager.setAcceptFileSchemeCookies(true);
    }

    /**
     * Функция поворота экрана
     */
    private void screenRotation() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams
                (ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);

        if (!isVerticalOrientation) {
            mActionBarToolbar.setVisibility(View.INVISIBLE);
            layoutParams.setMargins(0, 0, 0, 0);
        } else {
            mActionBarToolbar.setVisibility(View.VISIBLE);
            layoutParams.setMargins(0, 140, 0, 0);
        }

        webView.setLayoutParams(layoutParams);
    }

}
