package erika.roxana.com.sistemaventas.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.adapter.ProductAdapter;
import erika.roxana.com.sistemaventas.model.Producto;

public class ProductlistActivity extends AppCompatActivity {

    private RecyclerView rvProducto;
    private ProductAdapter adaptador;

    private FirebaseFirestore firestore;
    private CollectionReference collectionProducto;

    private EditText etProdList;
    private Button btnProdListSearch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productlist);

        rvProducto = findViewById(R.id.rvProducto);
        adaptador = new ProductAdapter(ProductlistActivity.this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        etProdList = findViewById(R.id.etProdList);
        btnProdListSearch = findViewById(R.id.btnProdListSearch);

        firestore = FirebaseFirestore.getInstance();
        collectionProducto = firestore.collection("Producto");

        rvProducto.setLayoutManager(linearLayoutManager);
        rvProducto.setAdapter(adaptador);

        //AgregarLista();

        btnProdListSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BuscarProducto();
            }
        });

    }

    private void AgregarLista(){

        collectionProducto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                    Producto producto = documentSnapshot.toObject(Producto.class);
                    adaptador.agregarProducto(producto);
                }

            }
        });
    }

    private void BuscarProducto(){

        adaptador.reiniciarLista();

        rvProducto.removeAllViews();

        if(etProdList.getText().toString().isEmpty()){
            AgregarLista();
        }else{
            collectionProducto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                        Producto producto = documentSnapshot.toObject(Producto.class);
                        if(producto.getProducto().toLowerCase().contains(etProdList.getText().toString().toLowerCase())){
                            adaptador.agregarProducto(producto);
                        }
                    }

                }
            });
        }

    }

}
