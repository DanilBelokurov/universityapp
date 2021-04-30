package org.suai.universityapp.model;

import android.view.View;
import android.widget.TextView;

import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.suai.universityapp.R;


public class CustomIncomingMessageViewHolder extends MessagesListAdapter.IncomingMessageViewHolder<Message>{

    public CustomIncomingMessageViewHolder(View itemView) {
        super(itemView);
    }

    @Override
    public void onBind(Message message) {
        super.onBind(message);
        TextView userName = itemView.findViewById(R.id.userName);
        userName.setText(message.getUser().getName());
    }
}
