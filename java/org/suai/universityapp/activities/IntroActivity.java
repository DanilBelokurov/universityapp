package org.suai.universityapp.activities;


import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import org.suai.universityapp.R;

public class IntroActivity extends com.heinrichreimersoftware.materialintro.app.IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFullscreen(true);

        addSlide(new SimpleSlide.Builder()
                .title("Удобный интерфейс")
                .description("Все новости университета в твоем телефоне.")
                .image(R.drawable.news)
                .background(R.color.colorBackgroundMenu)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Делись яркими впечатлениями")
                .description("Обсуди новости университета в общем чате.")
                .image(R.drawable.chat)
                .background(R.color.colorBackgroundMenu)
                .scrollable(false)
                .build());

        addSlide(new SimpleSlide.Builder()
                .title("Надежность")
                .description("Наша приоритетная задача - это комфорт и безопасность.")
                .image(R.drawable.guard)
                .background(R.color.colorBackgroundMenu)
                .scrollable(false)
                .build());
    }
}