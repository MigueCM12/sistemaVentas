package erika.roxana.com.sistemaventas.holder;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.activity.ProductActivity;
import erika.roxana.com.sistemaventas.activity.ProductlistActivity;
import erika.roxana.com.sistemaventas.model.Producto;

public class ProductHolder extends RecyclerView.ViewHolder {

    //Variables
    private ImageView imgProducto;

    private TextView tvNombre;
    private TextView tvStock;
    private TextView tvPrecio;

    public ProductHolder(@NonNull View itemView) {
        super(itemView);

        imgProducto = itemView.findViewById(R.id.imgProducto);

        tvNombre = itemView.findViewById(R.id.tvNombre);
        tvStock = itemView.findViewById(R.id.tvStock);
        tvPrecio = itemView.findViewById(R.id.tvPrecio);
    }

    public void init(Producto producto, Context mContext){
        //int idImage = R.drawable.productos;
        //imgProducto.setImageResource(idImage);

        if(producto.getImagen() != null && !producto.getImagen().isEmpty()){
            File file = new File(producto.getImagen());
            Uri imageUri = Uri.fromFile(file);

            Glide.with(mContext)
                    .load(imageUri)
                    .fitCenter()
                    .centerCrop()
                    .into(imgProducto);
            imgProducto.setImageURI(imageUri);
        }else{
            int idImage = R.drawable.productos;
            imgProducto.setImageResource(idImage);
        }


        tvNombre.setText(producto.getProducto());
        tvStock.setText(producto.getStock()+" Unidades disponibles en Stock");
        tvPrecio.setText("S/. "+producto.getPrecioVenta());
    }

}
