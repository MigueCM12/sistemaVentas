package erika.roxana.com.sistemaventas.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class ProfileActivity extends AppCompatActivity {

    private EditText etProfiDni;
    private EditText etProfiNombres;
    private EditText etProfiApellidos;
    private EditText etProfiEmail;

    private Button btnProfiActualizar;
    private Button btnProfiCancelar;

    private FirebaseFirestore firestore;
    private CollectionReference collectionCliente;

    private SharedPreferences sharedPreferences;

    private String dni;
    private String pass;
    private String respuesta;

    private boolean existeDni;
    private boolean existeEmail;
    private boolean guardo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        etProfiDni = findViewById(R.id.etProfiDni);
        etProfiNombres = findViewById(R.id.etProfiNombres);
        etProfiApellidos = findViewById(R.id.etProfiApellidos);
        etProfiEmail = findViewById(R.id.etProfiEmail);

        btnProfiActualizar = findViewById(R.id.btnProfiActualizar);
        btnProfiCancelar = findViewById(R.id.btnProfiCancelar);

        firestore = FirebaseFirestore.getInstance();
        collectionCliente = firestore.collection("Cliente");

        sharedPreferences = getSharedPreferences("Data", Context.MODE_PRIVATE);

        cargarData();

        btnProfiActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actualizarPerfil();
            }
        });

        btnProfiCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

    }

    private void cargarData(){
        dni = sharedPreferences.getString("dni_cliente","");

        if(dni.isEmpty()){

            Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
            startActivity(intent);

        }else{
            collectionCliente.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.getException() != null){
                        Toast.makeText(ProfileActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                    Cliente cliente = null;

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                        if(documentSnapshot.getId().equals(dni)){

                            cliente = documentSnapshot.toObject(Cliente.class);

                            break;
                        }
                    }

                    if(cliente == null){
                        Toast.makeText(ProfileActivity.this,"Error: El cliente no existe",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                        startActivity(intent);
                    }else{


                        etProfiDni.setText(cliente.getDni());
                        etProfiApellidos.setText(cliente.getApellidos());
                        etProfiNombres.setText(cliente.getNombres());
                        etProfiEmail.setText(cliente.getEmail());
                        pass = cliente.getPass();
                        respuesta = cliente.getRespuesta();

                    }

                }
            });
        }

    }

    private void actualizarPerfil(){

        String dni = etProfiDni.getText().toString();
        String nombres = etProfiNombres.getText().toString();
        String apellidos = etProfiApellidos.getText().toString();
        String email = etProfiEmail.getText().toString();

        if(dni.isEmpty()){
            Toast.makeText(ProfileActivity.this, "Error: El dni esta vacio", Toast.LENGTH_SHORT).show();
        }else if(nombres.isEmpty()){
            Toast.makeText(ProfileActivity.this, "Error: El nombre esta vacio", Toast.LENGTH_SHORT).show();
        }else if(apellidos.isEmpty()){
            Toast.makeText(ProfileActivity.this, "Error: El apellido esta vacio", Toast.LENGTH_SHORT).show();
        }else if(email.isEmpty()){
            Toast.makeText(ProfileActivity.this, "Error: El email esta vacio", Toast.LENGTH_SHORT).show();
        }else{

            existeDni = false;
            existeEmail = false;
            guardo = true;
            collectionCliente.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.getException() != null){
                        Toast.makeText(ProfileActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                        guardo = false;
                    }

                    if(guardo){
                        for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                            Cliente objCliente = documentSnapshot.toObject(Cliente.class);

                            if(documentSnapshot.getId().equals(etProfiDni.getText().toString())){
                                existeDni = true;
                                if(existeEmail){
                                    break;
                                }
                            }else if(objCliente.getEmail().equals(etProfiEmail.getText().toString())){
                                existeEmail = true;
                                if(existeDni){
                                    break;
                                }
                            }

                        }

                        if(!existeDni){
                            Toast.makeText(ProfileActivity.this,"Error: El DNI no existe",Toast.LENGTH_SHORT).show();
                        }else if(existeEmail){
                            Toast.makeText(ProfileActivity.this,"Error: El Email ya esta Registrado",Toast.LENGTH_SHORT).show();
                        }else{

                            Cliente cliente = new Cliente();
                            cliente.setNombres(etProfiNombres.getText().toString());
                            cliente.setApellidos(etProfiApellidos.getText().toString());
                            cliente.setEmail(etProfiEmail.getText().toString());
                            cliente.setPass(pass);
                            cliente.setRespuesta(respuesta);
                            cliente.setDni(etProfiDni.getText().toString());

                            collectionCliente.document(etProfiDni.getText().toString()).set(cliente).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProfileActivity.this,"Se actualizo Correctamente",Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ProfileActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                }
                            });

                            etProfiEmail.setText("");
                            etProfiApellidos.setText("");
                            etProfiNombres.setText("");
                            etProfiDni.setText("");
                        }
                    }
                }
            });

        }

    }

}
