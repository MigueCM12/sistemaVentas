package erika.roxana.com.sistemaventas.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.model.Producto;

public class ProductActivity extends AppCompatActivity {

    private EditText etProdCodigo;
    private EditText etProdProducto;
    private EditText etProdDescripcion;
    private EditText etProdPrecioC;
    private EditText etProdPrecioV;
    private EditText etProdStock;

    private Button btnProdBuscar;
    private Button btnProdGuardar;
    private Button btnProdEditar;
    private Button btnProdEliminar;
    private Button btnProdSalir;
    private Button btnSubirImagen;

    private ImageView ivProduct;

    private FirebaseFirestore firestore;
    private CollectionReference collectionProducto;
    private StorageReference storageReference;

    private Uri uri;
    private boolean existeProducto;
    private boolean existeImagen;
    private String urlImagen;
    private static final int GALLERY_INTENT = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        etProdCodigo = findViewById(R.id.etProdCodigo);
        etProdProducto = findViewById(R.id.etProdProducto);
        etProdDescripcion = findViewById(R.id.etProdDescripcion);
        etProdPrecioC = findViewById(R.id.etProdPrecioC);
        etProdPrecioV = findViewById(R.id.etProdPrecioV);
        etProdStock = findViewById(R.id.etProdStock);

        btnProdBuscar = findViewById(R.id.btnProdBuscar);
        btnProdGuardar = findViewById(R.id.btnProdGuardar);
        btnProdEditar = findViewById(R.id.btnProdEditar);
        btnProdEliminar = findViewById(R.id.btnProdEliminar);
        btnProdSalir = findViewById(R.id.btnProdSalir);
        btnSubirImagen = findViewById(R.id.btnProdSubir);

        ivProduct = findViewById(R.id.imageProduct);

        firestore = FirebaseFirestore.getInstance();
        collectionProducto = firestore.collection("Producto");
        storageReference = FirebaseStorage.getInstance().getReference();

        btnProdBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Buscar();
            }
        });

        btnProdGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crear();
            }
        });

        btnProdEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actualizar();
            }
        });

        btnProdEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Eliminar();
            }
        });

        btnProdSalir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProductActivity.this, MenuActivity.class);
                startActivity(intent);
            }
        });

        btnSubirImagen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(etProdCodigo.getText().toString().isEmpty()){
                    Toast.makeText(ProductActivity.this,"El código esta vacio",Toast.LENGTH_SHORT).show();
                }else{
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");
                    startActivityForResult(intent,GALLERY_INTENT);
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_INTENT && resultCode == RESULT_OK){

            uri = data.getData();
            //StorageReference filePath = storageReference.child("fotos").child(uri.getLastPathSegment());

            /*final StorageReference filePath = storageReference.child("fotos").child(etProdCodigo.getText().toString());
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                   filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                       @Override
                       public void onSuccess(Uri uri) {
                           Toast.makeText(ProductActivity.this,"url: " + uri.toString(), Toast.LENGTH_SHORT).show();
                       }
                   });

                }
            });*/

        }

    }

    private void Crear(){

        existeProducto =false ;

        if(etProdCodigo.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El codigo esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdProducto.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El producto esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdDescripcion.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: La descripcion esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdPrecioC.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El precio de costo esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdPrecioV.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El precio de venta esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdStock.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El stock esta vacio",Toast.LENGTH_SHORT).show();
        }else if(uri == null){
            Toast.makeText(ProductActivity.this,"Error: Debe seleccionar imagen",Toast.LENGTH_SHORT).show();
        }else{

            collectionProducto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    if(task.getException() != null){
                        Toast.makeText(ProductActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                        if(documentSnapshot.getId().equals(etProdCodigo.getText().toString())){
                            existeProducto = true;
                            break;
                        }
                    }

                    if(existeProducto){
                        Toast.makeText(ProductActivity.this,"Error: El codigo ya existe",Toast.LENGTH_SHORT).show();
                    }else{



                        final StorageReference filePath = storageReference.child("fotos").child(etProdCodigo.getText().toString());



                        filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uriDownload) {
                                        //Toast.makeText(ProductActivity.this,"url: " + uri.toString(), Toast.LENGTH_SHORT).show();
                                        /*Glide.with(ProductActivity.this)
                                                .load(uriDownload)
                                                .fitCenter()
                                                .centerCrop()
                                                .into(ivProduct);*/

                                        String codigo = etProdCodigo.getText().toString();
                                        String producto_name = etProdProducto.getText().toString();
                                        String descripcion = etProdDescripcion.getText().toString();
                                        double precioC = Double.parseDouble(etProdPrecioC.getText().toString());
                                        double precioV = Double.parseDouble(etProdPrecioV.getText().toString());
                                        int stock = Integer.parseInt(etProdStock.getText().toString());
                                        String imagen = uriDownload.toString();

                                        Producto producto = new Producto();
                                        producto.setCodigo(codigo);
                                        producto.setProducto(producto_name);
                                        producto.setDescripcion(descripcion);
                                        producto.setPrecioCosto(precioC);
                                        producto.setPrecioVenta(precioV);
                                        producto.setStock(stock);
                                        producto.setImagen(imagen);

                                        collectionProducto.document(etProdCodigo.getText().toString()).set(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(ProductActivity.this,"Se inserto Correctamente",Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                        etProdCodigo.setText("");
                                        etProdProducto.setText("");
                                        etProdDescripcion.setText("");
                                        etProdPrecioC.setText("");
                                        etProdPrecioV.setText("");
                                        etProdStock.setText("");
                                        uri = null;

                                    }
                                });



                            }
                        });




                    }
                }
            });
        }
    }

    private void Buscar(){


        if(etProdCodigo.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El codigo esta vacio",Toast.LENGTH_SHORT).show();
        }else{
            existeProducto = false;
            existeImagen = false;
            urlImagen = "";
            collectionProducto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {

                    String codigo = etProdCodigo.getText().toString();

                    if(task.getException() != null){
                        Toast.makeText(ProductActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }


                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){

                        if(documentSnapshot.getId().equals(etProdCodigo.getText().toString())){
                            Producto producto = documentSnapshot.toObject(Producto.class);

                            etProdProducto.setText( producto.getProducto());
                            etProdDescripcion.setText( producto.getDescripcion());
                            etProdPrecioC.setText( producto.getPrecioCosto() + "");
                            etProdPrecioV.setText( producto.getPrecioVenta() + "");
                            etProdStock.setText(producto.getStock() + "");
                            existeProducto = true;

                            if(producto.getImagen() !=null && !producto.getImagen().isEmpty()){
                                existeImagen = true;
                                urlImagen = producto.getImagen();



                            }

                            break;
                        }
                    }

                    if(!existeProducto){
                        etProdProducto.setText("");
                        etProdDescripcion.setText("");
                        etProdPrecioC.setText( "");
                        etProdPrecioV.setText("");
                        etProdStock.setText("");
                        Toast.makeText(ProductActivity.this,"El codigo no existe", Toast.LENGTH_SHORT).show();
                    }

                    //Toast.makeText(MainActivity.this,lista,Toast.LENGTH_SHORT).show();

                }
            });
        }
    }


    public void Actualizar(){


        existeProducto =false ;

        if(etProdCodigo.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El codigo esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdProducto.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El producto esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdDescripcion.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: La descripcion esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdPrecioC.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El precio de costo esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdPrecioV.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El precio de venta esta vacio",Toast.LENGTH_SHORT).show();
        }else if(etProdStock.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El stock esta vacio",Toast.LENGTH_SHORT).show();
        }else if(uri == null && !existeImagen) {
            Toast.makeText(ProductActivity.this,"Error: Debe seleccionar imagen",Toast.LENGTH_SHORT).show();
        }else{



            collectionProducto.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull final Task<QuerySnapshot> task) {

                    if(task.getException() != null){
                        Toast.makeText(ProductActivity.this,"Error: "+task.getException(), Toast.LENGTH_SHORT).show();
                    }

                    for (DocumentSnapshot documentSnapshot : task.getResult().getDocuments()){
                        if(documentSnapshot.getId().equals(etProdCodigo.getText().toString())){
                            existeProducto = true;
                            break;
                        }
                    }

                    if(!existeProducto){
                        Toast.makeText(ProductActivity.this,"Error: El codigo no existe",Toast.LENGTH_SHORT).show();
                    }else{

                        if(existeImagen && uri == null){

                            String imagen = urlImagen;

                            String codigo = etProdCodigo.getText().toString();
                            String producto_name = etProdProducto.getText().toString();
                            String descripcion = etProdDescripcion.getText().toString();
                            double precioC = Double.parseDouble(etProdPrecioC.getText().toString());
                            double precioV = Double.parseDouble(etProdPrecioV.getText().toString());
                            int stock = Integer.parseInt(etProdStock.getText().toString());


                            Producto producto = new Producto();
                            producto.setCodigo(codigo);
                            producto.setProducto(producto_name);
                            producto.setDescripcion(descripcion);
                            producto.setPrecioCosto(precioC);
                            producto.setPrecioVenta(precioV);
                            producto.setStock(stock);
                            producto.setImagen(imagen);

                            //Toast.makeText(ProductActivity.this,"Su archivo se subio ",Toast.LENGTH_SHORT).show();
                            collectionProducto.document(etProdCodigo.getText().toString()).set(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(ProductActivity.this,"Se actualizo Correctamente",Toast.LENGTH_SHORT).show();
                                }
                            });

                            etProdCodigo.setText("");
                            etProdProducto.setText("");
                            etProdDescripcion.setText("");
                            etProdPrecioC.setText("");
                            etProdPrecioV.setText("");
                            etProdStock.setText("");
                            uri = null;

                        }else{

                            final StorageReference filePath = storageReference.child("fotos").child(etProdCodigo.getText().toString());
                            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uriDownload) {
                                            //Toast.makeText(ProductActivity.this,"url: " + uri.toString(), Toast.LENGTH_SHORT).show();

                                           /* Glide.with(ProductActivity.this)
                                                    .load(uriDownload)
                                                    .fitCenter()
                                                    .centerCrop()
                                                    .into(ivProduct);*/

                                            Toast.makeText(ProductActivity.this,uriDownload.toString(),Toast.LENGTH_SHORT).show();

                                            String imagen = uriDownload.toString();

                                            String codigo = etProdCodigo.getText().toString();
                                            String producto_name = etProdProducto.getText().toString();
                                            String descripcion = etProdDescripcion.getText().toString();
                                            double precioC = Double.parseDouble(etProdPrecioC.getText().toString());
                                            double precioV = Double.parseDouble(etProdPrecioV.getText().toString());
                                            int stock = Integer.parseInt(etProdStock.getText().toString());


                                            Producto producto = new Producto();
                                            producto.setCodigo(codigo);
                                            producto.setProducto(producto_name);
                                            producto.setDescripcion(descripcion);
                                            producto.setPrecioCosto(precioC);
                                            producto.setPrecioVenta(precioV);
                                            producto.setStock(stock);
                                            producto.setImagen(imagen);

                                            //Toast.makeText(ProductActivity.this,"Su archivo se subio ",Toast.LENGTH_SHORT).show();
                                            collectionProducto.document(etProdCodigo.getText().toString()).set(producto).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(ProductActivity.this,"Se actualizo Correctamente",Toast.LENGTH_SHORT).show();
                                                }
                                            });

                                            etProdCodigo.setText("");
                                            etProdProducto.setText("");
                                            etProdDescripcion.setText("");
                                            etProdPrecioC.setText("");
                                            etProdPrecioV.setText("");
                                            etProdStock.setText("");
                                            uri = null;
                                        }
                                    });
                                }
                            });
                        }
                    }

                }
            });



        }


    }

    public  void Eliminar(){

        if(etProdCodigo.getText().toString().isEmpty()){
            Toast.makeText(ProductActivity.this,"Error: El codigo esta vacio",Toast.LENGTH_SHORT).show();
        }else{
            String codigo = etProdCodigo.getText().toString();
            collectionProducto.document(codigo).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Toast.makeText(ProductActivity.this,"Se elimino Correctamente",Toast.LENGTH_SHORT).show();
                }
            });
        }



    }

}
