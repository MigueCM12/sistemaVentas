package erika.roxana.com.sistemaventas.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import erika.roxana.com.sistemaventas.R;
import erika.roxana.com.sistemaventas.holder.ProductHolder;
import erika.roxana.com.sistemaventas.model.Producto;

public class ProductAdapter extends RecyclerView.Adapter<ProductHolder> {

    private List<Producto> productoList;
    private Context mContext;

    public ProductAdapter(Context mContext){
        productoList = new ArrayList<>();
        this.mContext = mContext;
    }

    public void agregarProducto(Producto producto){
        productoList.add(producto);
        notifyDataSetChanged();
    }

    public void reiniciarLista(){
        productoList  = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_view_producto,viewGroup,false);
        return new ProductHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductHolder productHolder, int i) {
        productHolder.init(productoList.get(i),mContext);
    }

    @Override
    public int getItemCount() {
        return productoList.size();
    }

}
