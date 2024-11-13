package com.example.projet_sae32;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

public class Message {
    private EditText inpSend;
    private String content;
    private ArrayList<String> contentTab;
    private Context context;
    private LinearLayout messageContainer;

    Message(Context context, EditText inpSend, Button btnSend, LinearLayout messageContainer) {
        this.context = context;
        this.inpSend = inpSend;
        this.contentTab = new ArrayList<>();
        this.messageContainer = messageContainer;
    }

    public void sendMessage() {
        content = inpSend.getText().toString();
        contentTab.add(content);
    }

    public void showMessage() {
        // Création du layout principal pour le message
        LinearLayout messageLayout = new LinearLayout(context);
        LinearLayout.LayoutParams mainParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageLayout.setOrientation(LinearLayout.VERTICAL);
        messageLayout.setPadding(32, 32, 32, 32);
        messageLayout.setLayoutParams(mainParams);

        // Username TextView
        TextView username = new TextView(context);
        username.setText("VIRMAUX Bastien");
        username.setTextSize(18);
        username.setTextColor(Color.parseColor("#795548")); // color_500
        username.setTypeface(null, android.graphics.Typeface.BOLD);

        // Message TextView
        TextView messageText = new TextView(context);
        messageText.setText(content);
        messageText.setTextSize(16);
        messageText.setTextColor(Color.BLACK);
        LinearLayout.LayoutParams messageParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        messageParams.setMargins(32, 16, 0, 0);
        messageText.setLayoutParams(messageParams);

        // Layout horizontal pour date et bouton like
        LinearLayout bottomLayout = new LinearLayout(context);
        bottomLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams bottomParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        bottomParams.setMargins(0, 16, 0, 0);
        bottomLayout.setLayoutParams(bottomParams);

        // Date TextView
        TextView date = new TextView(context);
        date.setText("11/11/24");
        date.setTextColor(Color.parseColor("#795548")); // color_500
        LinearLayout.LayoutParams dateParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1
        );
        date.setLayoutParams(dateParams);

        // Bouton Like
        Button likeButton = new Button(context);
        likeButton.setText("Like");

        // Ajout des vues au layout
        messageLayout.addView(username);
        messageLayout.addView(messageText);
        bottomLayout.addView(date);
        bottomLayout.addView(likeButton);
        messageLayout.addView(bottomLayout);

        // Ajout du message layout au container
        messageContainer.addView(messageLayout, 0);

        // Vider le champ de saisie
        inpSend.setText("");
    }
}