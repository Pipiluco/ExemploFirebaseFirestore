package org.sistemafiesc.lucasftecnico.exemplofirebasefirestore;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_TITULO = "titulo";
    private static final String KEY_DESCRICAO = "descricao";

    private EditText edtTitulo, edtDescricao;
    private TextView tvNota;

    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private DocumentReference documentReference = firestore.document("Notas/Minha primeira nota");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtTitulo = findViewById(R.id.edtTitulo);
        edtDescricao = findViewById(R.id.edtDescricao);
        tvNota = findViewById(R.id.tvNota);
    }

    @Override
    protected void onStart() {
        super.onStart();
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(getApplicationContext(), "Erro equanto carregava!", Toast.LENGTH_LONG).show();
                    Log.d(TAG, e.toString());
                    return;
                }

                if (documentSnapshot.exists()) {
                    Nota nota = documentSnapshot.toObject(Nota.class);

                    tvNota.setText("Título: " + nota.getTitulo() + "\n" + "Descrição: " + nota.getDescricao());
                } else {
                    tvNota.setText("");
                }
            }
        });
    }

    public void salvarNota(View view) {
        String titulo = edtTitulo.getText().toString();
        String descricao = edtDescricao.getText().toString();

        Nota nota = new Nota(titulo, descricao);

        documentReference.set(nota).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Nota salva!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            }
        });
    }

    public void carregarNota(View view) {
        documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Nota nota = documentSnapshot.toObject(Nota.class);

                    tvNota.setText("Título: " + nota.getTitulo() + "\n" + "Descrição: " + nota.getDescricao());

                } else {
                    Toast.makeText(getApplicationContext(), "Documento não existe!", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Erro: " + e.getMessage(), Toast.LENGTH_LONG).show();
                Log.d(TAG, e.toString());
            }
        });
    }

    public void atualizarDescricao(View view) {
        String descricao = edtDescricao.getText().toString();
        // Map<String, Object> nota = new HashMap<>();
        //nota.put(KEY_DESCRICAO, descricao);

        // documentReference.set(nota, SetOptions.merge());
        documentReference.update(KEY_DESCRICAO, descricao);
    }

    public void excluirDescricao(View view) {
        //Map<String, Object> nota = new HashMap<>();
        //nota.put(KEY_DESCRICAO, FieldValue.delete());
        //documentReference.update(nota);

        documentReference.update(KEY_DESCRICAO, FieldValue.delete());
    }

    public void excluirNota(View view) {
        documentReference.delete();
    }
}
