package erika.roxana.com.sistemaventas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import erika.roxana.com.sistemaventas.R;

public class MenuActivity extends AppCompatActivity {

    private Button btnMenuPerfil;
    private Button btnMenuRegProd;
    private Button btnMenuLisProd;
    private Button btnMenuComprar;
    private Button btnAcerca;
    private Button btnMenuExit;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btnMenuPerfil = findViewById(R.id.btnMenuPerfil);
        btnMenuRegProd = findViewById(R.id.btnMenuRegProd);
        btnMenuLisProd = findViewById(R.id.btnMenuLisProd);
        btnMenuComprar = findViewById(R.id.btnMenuComprar);
        btnAcerca = findViewById(R.id.btnAcerca);
        btnMenuExit = findViewById(R.id.btnMenuExit);

        btnMenuPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        btnMenuRegProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        btnMenuLisProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, ProductlistActivity.class);
                startActivity(intent);
            }
        });

        btnMenuComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnAcerca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnMenuExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MenuActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
