package com.example.praiademanoelviana.activity.Adpter;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.model.Produto;

import java.util.List;



/**
 * Created by Jamilton
 */

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    private List<Produto> produtos;
    private Context context;

    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNomePro());
        holder.informacao.setText(produto.getInformaAcamp());
        holder.valor.setText("R$ " + produto.getValorAreaAcampa());
        //holder.FotoA.setImageBitmap(produto.getUrlImagemproduto());
    }

    @Override
    public int getItemCount() {
        return produtos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView foto;
        TextView nome;
        TextView informacao;
        TextView valor;

        public MyViewHolder(View itemView) {
            super(itemView);
            foto = itemView.findViewById(R.id.ImageProduto);
            nome = itemView.findViewById(R.id.TextNomeProduto);
            informacao = itemView.findViewById(R.id.TextInfomacao);
            valor= itemView.findViewById(R.id.TextValorArea);
        }
    }
}
