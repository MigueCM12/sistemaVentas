package erika.roxana.com.sistemaventas.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.model.Cliente;

public class MainActivity extends AppCompatActivity {

    private EditText etLogEmail;
    private EditText etLogPass;
    private Button btnLogin;
    private Button btnRegister;

    private Boolean existeEmail;
    private Boolean existePass;

    private String email;
    private String pass;

    private TextView tvRecover;

    private FirebaseFirestore firestore;
    private CollectionReference collectionCliente;

    private SharedPreferences sharedPreferences;

    @Override    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etLogEmail = findViewById(R.id.etLogEmail);
        etLogPass = findViewById(R.id.etLogPass);

        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);

        tvRecover = findViewById(R.id.tvRecover);

        firestore = FirebaseFirestore.getInstance();
        collectionCliente = firestore.collection("Cliente");

        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = etLogEmail.getText().toString();
                pass = etLogPass.getText().toString();

                if (email.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Error: El email esta vacio", Toast.LENGTH_SHORT).show();
                }else if(pass.isEmpty()){
                    Toast.makeText(MainActivity.this, "Error: La contraseña esta vacio", Toast.LENGTH_SHORT).show();
                }else{
                    iniciarSession();
                }
            }
        });

        tvRecover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecoverActivity.class);
                startActivity(intent);
            }
        });

    }

    private boolean iniciarSession() {

        existeEmail = false;
        existePass = false;

        collectionCliente.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                Cliente cliente = null;

                if(task.getException() != null){
                    Toast.makeText(MainActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                }

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                    existeEmail = false;
                    existePass = false;
                    Cliente objCliente = documentSnapshot.toObject(Cliente.class);

                    if(objCliente.getEmail().equals(email)){
                        existeEmail = true;

                        if(objCliente.getPass().equals(pass)){
                            existePass = true;
                            cliente = objCliente;
                        }
                        break;
                    }
                }

                if(existeEmail && existePass){

                    sharedPreferences.edit()
                            .putString("dni_cliente",cliente.getDni())
                            .apply();

                    Intent intent = new Intent(MainActivity.this, MenuActivity.class);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "Error: El email y/o contraseña invalido", Toast.LENGTH_SHORT).show();
                }

            }
        });

        return  existeEmail && existePass;
    }

}
