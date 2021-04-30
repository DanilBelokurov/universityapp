package org.suai.universityapp.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.ContextMenu;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.suai.universityapp.R;
import org.suai.universityapp.activities.LoginActivity;
import org.suai.universityapp.database.Database;
import org.suai.universityapp.model.CustomIncomingMessageViewHolder;
import org.suai.universityapp.model.Message;
import org.suai.universityapp.utils.MessageDatabaseUtils;
import org.suai.universityapp.utils.UsersDBUtils;

import java.util.Objects;

/**
 * Класс, импелементирующий вкладку чата
 */
public class ChatFragment extends Fragment {

    private View chatView;                         // Основное view фрагмента
    private Button btnToReg;                       // Кнопка перехода на регистрацию
    private Button banMessage;                     // Уведомление о бане
    private MessageInput messageInput;             // Поле ввода сообщения
    private MessagesList messagesList;             // Поле отображения сообщений
    private MessagesListAdapter<Message> adapter;  // Адаптер
    private ImageView bigPic;                      // View для просмотра изображений

    private Database db;                           // База данных

    private final Fragment thisFragment = this;
    private Message pickedMessage;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Инициализация основного view фрагмента
        chatView = inflater.inflate(R.layout.fragment_chat, container, false);

        // Коннект к БД
        db = new Database();

        final boolean[] isClicked = {false};
        final String[] isBanned = {""};

        initialization();

        // Проверка анонимных пользователей
        if (db.getUserFromDB().isAnonymous()) {
            messageInput.setVisibility(View.INVISIBLE);
            btnToReg.setVisibility(View.VISIBLE);
            btnToReg.setOnClickListener(view -> {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
            });
        }

        // Проверка на бан
        db.getUsersFromDB().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (Objects.equals(snapshot.getKey(), db.getUserFromDB().getUid())) {

                        isBanned[0] = snapshot.child("ban").getValue().toString();

                        if (isBanned[0].equals("1")) {
                            messageInput.setVisibility(View.INVISIBLE);
                            banMessage.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        /*Просмотр сообщения с картинкой*/
        adapter.setOnMessageClickListener(message -> {
            if (message.isWithImg() && !isClicked[0]) {
                Uri imgURL = Uri.parse(message.getImgURL());
                Glide.with(getContext()).load(imgURL).into(bigPic);
                bigPic.setVisibility(View.VISIBLE);
                bigPic.bringToFront();
                isClicked[0] = true;
            } else if (isClicked[0]) {
                bigPic.setVisibility(View.INVISIBLE);
                isClicked[0] = false;
            }
        });

        chatView.setOnClickListener(view -> {
            bigPic.setVisibility(View.INVISIBLE);
            isClicked[0] = false;
        });


        // Отправка сообщений с картинками
        messageInput.setAttachmentsListener(() -> {
            if (Build.VERSION.SDK_INT >= 22)
                checkAndRequestForPermission();
            else
                openGallery();
        });


        // Обновление чата
        db.getMessagesFromDB().addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                Message message = MessageDatabaseUtils.messageCreateFromDB(dataSnapshot);
                adapter.addToStart(message, true);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Message message = MessageDatabaseUtils.messageCreateFromDB(dataSnapshot);
                adapter.delete(message);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) { }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        // Отправка новых сообщений
        messageInput.setInputListener(input -> {
            MessageDatabaseUtils.newMessageSubmit(input, db);
            return true;
        });


        return chatView;
    }

    /**
     * Метод инициализации основных полей
     */
    private void initialization() {

        requireActivity().getWindow()
                .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        // View для просмотра картинок
        bigPic = chatView.findViewById(R.id.bigPic);
        bigPic.setVisibility(View.INVISIBLE);

        // Уведомление о попадании в бан
        banMessage = chatView.findViewById(R.id.ban_message_in_chat);
        banMessage.setVisibility(View.INVISIBLE);

        // Адаптер сообщений
        MessagesListAdapter.HoldersConfig holdersConfig = new MessagesListAdapter.HoldersConfig();
        holdersConfig.setIncoming(CustomIncomingMessageViewHolder.class,
                R.layout.item_custom_incoming_text_message);

        adapter = new MessagesListAdapter<>(db.getUserFromDB().getUid(), holdersConfig,
                (imageView, url, payload) -> Glide.with(getContext()).load(url).into(imageView));

        // Контейнер с сообщениями
        messagesList = chatView.findViewById(R.id.messagesList);
        messagesList.setAdapter(adapter);

        // Поле ввода сообщений
        messageInput = chatView.findViewById(R.id.input);

        // Переход на регистрацию
        btnToReg = chatView.findViewById(R.id.btn_to_reg_in_anchat);
        btnToReg.setVisibility(View.INVISIBLE);
    }

    /**
     * Открытие пользовательской галерее
     */
    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, 1);
    }

    /**
     * Проверка разрешений и прав доступа
     */
    private void checkAndRequestForPermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(requireActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(getContext(), "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                int PReqCode = 1;
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PReqCode);
            }
        } else
            openGallery();
    }

    /**
     * Создание контекстного меню админа
     */
    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.chat_context_menu, menu);
    }

    /**
     * Обработка событий контекстного меню админа
     */
    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        String text = "Что-то пошло не так!";
        if (pickedMessage != null) {
            switch (item.getItemId()) {
                case R.id.user_mute:
                    text = UsersDBUtils.userMute(pickedMessage, db);
                    break;
                case R.id.delete_message:
                    text = MessageDatabaseUtils.messageDelete(pickedMessage, db);
                    break;
                case R.id.delete_all_message:
                    text = MessageDatabaseUtils.deleteAllMessages(pickedMessage, db);
                    break;
                default:
                    return super.onContextItemSelected(item);
            }
        }

        Toast toast = Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
        return true;
    }

    /**
     * Завершение работы активити (выбора картинки)
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 1 && data != null) {
            Uri pickedImgUri = data.getData();
            MessageDatabaseUtils.postPhoto(pickedImgUri, db);
        }
    }
}