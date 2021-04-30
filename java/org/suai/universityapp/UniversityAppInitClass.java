package org.suai.universityapp;

import android.app.Application;

import com.yandex.metrica.YandexMetrica;
import com.yandex.metrica.YandexMetricaConfig;
import com.yandex.metrica.push.YandexMetricaPush;

public class UniversityAppInitClass extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Creating an extended library configuration.
        YandexMetricaConfig config = YandexMetricaConfig
                .newConfigBuilder("fd13f2ca-5117-4b30-8cd8-2086f332cb9f").build();

        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(getApplicationContext(), config);

        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this);

        YandexMetricaPush.init(getApplicationContext());
    }
}