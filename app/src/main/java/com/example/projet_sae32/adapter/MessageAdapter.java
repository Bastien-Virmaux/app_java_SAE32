package com.example.projet_sae32.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_sae32.Message;
import com.example.projet_sae32.R;

import java.util.ArrayList;
import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private List<Message> messages;
    private Context context;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);

        holder.pseudoMessage.setText(message.getPseudo());
        holder.dateMessage.setText(message.getDate());
        holder.textMessage.setText(message.getContent());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView pseudoMessage;
        TextView dateMessage;
        TextView textMessage;
        Button likeButton;

        MessageViewHolder(View itemView) {
            super(itemView);
            pseudoMessage = itemView.findViewById(R.id.pseudoMessage);
            dateMessage = itemView.findViewById(R.id.dateMessage);
            textMessage = itemView.findViewById(R.id.textMessage);
            likeButton = itemView.findViewById(R.id.likeMessage);
        }
    }
}