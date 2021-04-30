package org.suai.universityapp.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import org.suai.universityapp.R;
import org.suai.universityapp.activities.LoginActivity;
import org.suai.universityapp.database.Database;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Фрагмент с информацией о пользователе
 */
public class UserFragment extends Fragment {

    private Database db; // База данных

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {

        // Главная view фрагмента
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        // Всплывашка
        TextView favoriteTip = view.findViewById(R.id.favorite_tip);
        favoriteTip.setVisibility(View.INVISIBLE);

        db = new Database();

        CircleImageView userPic = view.findViewById(R.id.userPic); // userpic
        TextView userName = view.findViewById(R.id.userNameField); // имя пользователя

        Uri photoUrl;
        String name;

        Button btnToReg = view.findViewById(R.id.btn_to_reg_in_favorite); // кнопка перехада к регистрации
        btnToReg.setVisibility(View.INVISIBLE);

        // Проверка на регистрацию
        if (db.getUserFromDB().isAnonymous()) {
            photoUrl = Uri.parse("https://firebasestorage.googleapis.com/v0/b/slotick-5e305.appspot.com/o/users_photos%2Fprofile_man4.jpg?alt=media&token=533ddbe6-7930-4eb5-9351-6c11a2329b84");
            name = "Анонимный пользователь";

            favoriteTip.setVisibility(View.VISIBLE);
            btnToReg.setVisibility(View.VISIBLE);
            btnToReg.setOnClickListener(view1 -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        } else {
            photoUrl = Objects.requireNonNull(db.getUserFromDB()).getPhotoUrl();
            name = db.getUserFromDB().getDisplayName();
        }

        Glide.with(getContext()).load(photoUrl).into(userPic);
        userName.setText(name);

        return view;
    }

}