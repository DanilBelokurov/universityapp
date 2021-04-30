package org.suai.universityapp.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.suai.universityapp.R;
import org.suai.universityapp.activities.WebviewActivity;
import org.suai.universityapp.database.Database;

/**
 * Класс, имплементирующий новостную ленту
 */
public class NewsFragment extends Fragment {

    private Database db; // База данных

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = new Database();

        // View всего фрагмента
        View view = inflater.inflate(R.layout.fragment_news, container, false);

        // Контейнер для новостей
        LinearLayout linear = view.findViewById(R.id.linear_news_container);

        db.getNewsFromDB().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (final DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    // View отдельной новости
                    View news      = inflater.inflate(R.layout.one_news, null);

                    // Название новости
                    TextView title = news.findViewById(R.id.news_title);
                    title.setText(snapshot.child("title").getValue().toString());

                    // Лого новости
                    ImageView logo = news.findViewById(R.id.news_logo);

                    if (isAdded()) {
                        Glide.with(requireContext())
                                .load(snapshot.child("backgroundURL").getValue())
                                .into(logo);
                    }

                    // Ссылка новости
                    String link = snapshot.child("textURL").getValue().toString();

                    news.setOnClickListener(newsView -> {
                        Intent intent = new Intent(newsView.getContext(), WebviewActivity.class);
                        intent.putExtra("link", link);
                        intent.putExtra("pageName", title.getText());
                        startActivity(intent);
                    });

                    linear.addView(news);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }

        });

        return view;
    }
}