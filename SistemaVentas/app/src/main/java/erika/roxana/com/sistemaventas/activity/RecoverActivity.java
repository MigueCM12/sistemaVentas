package erika.roxana.com.sistemaventas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.model.Cliente;

public class RecoverActivity extends AppCompatActivity {

    private EditText etRecEmail;
    private EditText etRecRespuesta;
    private EditText etRecPass;

    private Button btnRecSave;

    private FirebaseFirestore firestore;
    private CollectionReference collectionCliente;

    private String respuesta;
    private String email;
    private String dni;
    private boolean existeEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recover);

        etRecEmail = findViewById(R.id.etRecEmail);
        etRecRespuesta = findViewById(R.id.etRecRespuesta);
        etRecPass = findViewById(R.id.etRecPass);

        btnRecSave = findViewById(R.id.btnRecSave);

        firestore = FirebaseFirestore.getInstance();
        collectionCliente = firestore.collection("Cliente");

        btnRecSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = etRecEmail.getText().toString();
                String pass = etRecPass.getText().toString();
                String respuesta = etRecRespuesta.getText().toString();

                if(email.isEmpty()){
                    Toast.makeText(RecoverActivity.this, "Error: El email esta vacio", Toast.LENGTH_SHORT).show();
                }else if(pass.isEmpty()){
                    Toast.makeText(RecoverActivity.this, "Error: LA clave esta vacio", Toast.LENGTH_SHORT).show();
                }else if(respuesta.isEmpty()){
                    Toast.makeText(RecoverActivity.this, "Error: La respuesta esta vacio", Toast.LENGTH_SHORT).show();
                }else{
                    RecuperarContrasenia();
                }


            }
        });
    }

    private void RecuperarContrasenia(){

        existeEmail = false;

        collectionCliente.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {



                if(task.getException() != null){
                    Toast.makeText(RecoverActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                }

                Cliente cliente = null;

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                    Cliente objCliente = documentSnapshot.toObject(Cliente.class);

                    if(objCliente.getEmail().equals(etRecEmail.getText().toString())){
                        existeEmail = true;
                        cliente = objCliente;
                        break;
                    }
                }

                Toast.makeText(RecoverActivity.this,cliente.getRespuesta(),Toast.LENGTH_SHORT).show();

                if(existeEmail && cliente.getRespuesta().equals(etRecRespuesta.getText().toString())){

                    cliente.setPass(etRecPass.getText().toString());

                    collectionCliente.document(cliente.getDni()).set(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(RecoverActivity.this,"Se Recupero Contraseña",Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(RecoverActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                    });

                }else{
                    Toast.makeText(RecoverActivity.this, "Error: Datos incorrectos", Toast.LENGTH_SHORT).show();
                }
            }
        });



    }
}
