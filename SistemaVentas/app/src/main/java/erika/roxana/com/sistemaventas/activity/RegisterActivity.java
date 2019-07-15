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

public class RegisterActivity extends AppCompatActivity {

    private EditText etRegDni;
    private EditText etRegNombres;
    private EditText etRegApellidos;
    private EditText etRegEmail;
    private EditText etRegPass;
    private EditText etRegRespuesta;

    private Button btnRegSave;
    private Button btnRegCancel;

    private FirebaseFirestore firestore;
    private CollectionReference collectionCliente;

    private boolean existeDni;
    private boolean existeEmail;
    private boolean guardo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etRegDni = findViewById(R.id.etRegDni);
        etRegNombres = findViewById(R.id.etRegNombres);
        etRegApellidos = findViewById(R.id.etRegApellidos);
        etRegEmail = findViewById(R.id.etRegEmail);
        etRegPass = findViewById(R.id.etRegPass);
        etRegRespuesta = findViewById(R.id.etRegRespuesta);

        btnRegSave = findViewById(R.id.btnRegSave);
        btnRegCancel = findViewById(R.id.btnRegCancel);

        firestore = FirebaseFirestore.getInstance();
        collectionCliente = firestore.collection("Cliente");

        btnRegSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCliente();
            }
        });

        btnRegCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean guardarCliente(){

        String dni = etRegDni.getText().toString();
        String nombres = etRegNombres.getText().toString();
        String apellidos = etRegApellidos.getText().toString();
        String email = etRegEmail.getText().toString();
        String pass = etRegPass.getText().toString();
        String respuesta = etRegRespuesta.getText().toString();



        if(dni.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: El dni esta vacio", Toast.LENGTH_SHORT).show();
        }else if(nombres.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: El nombre esta vacio", Toast.LENGTH_SHORT).show();
        }else if(apellidos.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: El apellido esta vacio", Toast.LENGTH_SHORT).show();
        }else if(email.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: El email esta vacio", Toast.LENGTH_SHORT).show();
        }else if(pass.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: LA clave esta vacio", Toast.LENGTH_SHORT).show();
        }else if(respuesta.isEmpty()){
            Toast.makeText(RegisterActivity.this, "Error: La respuesta esta vacio", Toast.LENGTH_SHORT).show();
        }else{

            existeDni = false;
            existeEmail = false;
            guardo = true;
            collectionCliente.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.getException() != null){
                        Toast.makeText(RegisterActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        guardo = false;
                    }

                    if(guardo){
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                            Cliente objCliente = documentSnapshot.toObject(Cliente.class);

                            if(documentSnapshot.getId().equals(etRegDni.getText().toString())){
                                existeDni = true;
                                break;
                            }

                            if(objCliente.getEmail().equals(etRegEmail.getText().toString())){
                                existeEmail = true;
                                break;
                            }

                        }

                        if(existeDni){
                            Toast.makeText(RegisterActivity.this,"Error: El DNI ya esta Registrado",Toast.LENGTH_SHORT).show();
                        }else if(existeEmail){
                            Toast.makeText(RegisterActivity.this,"Error: El Email ya esta Registrado",Toast.LENGTH_SHORT).show();
                        }else{

                            Cliente cliente = new Cliente();
                            cliente.setNombres(etRegNombres.getText().toString());
                            cliente.setApellidos(etRegApellidos.getText().toString());
                            cliente.setEmail(etRegEmail.getText().toString());
                            cliente.setPass(etRegPass.getText().toString());
                            cliente.setRespuesta(etRegRespuesta.getText().toString());
                            cliente.setDni(etRegDni.getText().toString());

                            collectionCliente.document(etRegDni.getText().toString()).set(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(RegisterActivity.this,"Se inserto Correctamente",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            });

                            etRegDni.setText("");
                            etRegApellidos.setText("");
                            etRegEmail.setText("");
                            etRegNombres.setText("");
                            etRegPass.setText("");
                            etRegRespuesta.setText("");
                        }
                    }
                }
            });

        }

        return guardo;
    }
}
