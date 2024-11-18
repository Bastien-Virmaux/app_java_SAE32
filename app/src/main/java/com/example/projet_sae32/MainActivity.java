package com.example.projet_sae32;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_sae32.adapter.MessageAdapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;

    TextView _textMessage;
    TextView _dateMessage;
    RecyclerView _recyclerView;

    Button _btnSend;
    EditText _inpSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisez le RecyclerView ici
        recyclerView = findViewById(R.id.recyclerView);

        _btnSend = findViewById(R.id.btnSend);
        _inpSend = findViewById(R.id.inpSend);

        _textMessage = findViewById(R.id.textMessage);
        _dateMessage = findViewById(R.id.dateMessage);

        // Configuration du RecyclerView
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Initialisation de l'adapter
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);

        _btnSend.setOnClickListener(v -> envoyerMessage());
    }

    private void envoyerMessage(){
        //récupérer le texte du message
        String messageTexte = _inpSend.getText().toString().trim();

        // Vérifier que le message n'est pas vide
        if (!messageTexte.isEmpty()) {
            // Obtenir l'heure actuelle
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.getDefault());
            String heureActuelle = sdf.format(new Date());

            // Créer un nouveau message
            Message nouveauMessage = new Message("Moi", messageTexte, heureActuelle);

            // Ajouter le message à l'adaptateur
            messageAdapter.addMessage(nouveauMessage);

            // Effacer le champ de texte
            _inpSend.setText("");

            // Défiler jusqu'au dernier message
            recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
        }
    }

    private void ajouterMessagesTest() {
        messageAdapter.addMessage(new Message("test", "test", "10:30"));
        messageAdapter.addMessage(new Message("User2", "Moi aussi ! On devrait y aller ensemble.", "10:32"));
    }
}