package com.example.praiademanoelviana.activity.Activity;


import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Adpter.AdapterProduto;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.example.praiademanoelviana.activity.Helper.UsuarioFirebase;
import com.example.praiademanoelviana.activity.lister.RecyclerItemClickListener;
import com.example.praiademanoelviana.activity.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {
     private RecyclerView recycleProdutos;
     private AdapterProduto adapterProduto;
     private List<Produto> produtos = new ArrayList<>();
     private DatabaseReference firebaseRef;
    private FirebaseAuth autenticacao;
     private  String idUsuarioLogado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        // configurações iniciais
        inicializarComponentes();
        autenticacao = ConfiruFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiruFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Gerência");
        setSupportActionBar(toolbar);
        //configura recycle
        recycleProdutos.setLayoutManager(new LinearLayoutManager(this));
        recycleProdutos.setHasFixedSize(true);
        adapterProduto = new AdapterProduto(produtos, this);
        recycleProdutos.setAdapter(adapterProduto);

//Recupera produtos para empresa
        recuparProdutos();
        //Adiciona evento de clique no recyclerview
        recycleProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recycleProdutos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Produto produtoSelecionado= produtos.get(position);
                                produtoSelecionado.remover();
                                Toast.makeText(EmpresaActivity.this,
                                        "Produto Removido",
                                        Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )

        );

    }
    private  void recuparProdutos(){

DatabaseReference produtosRef = firebaseRef
        .child("produtos")
        // acessa no banco o no idLogado
        .child(idUsuarioLogado);
produtosRef.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
    produtos.clear();
    for(DataSnapshot ds: dataSnapshot.getChildren()){

        produtos.add(ds.getValue(Produto.class));


    }
    adapterProduto.notifyDataSetChanged();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});
    }
   private void inicializarComponentes(){
recycleProdutos= findViewById(R.id.recyclePro);




   }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfigurações :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
            case R.id.menuPedidos :
                abrirPedidos();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void abrirPedidos(){
        startActivity(new Intent(EmpresaActivity.this, PedidosActivity.class));
    }

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracaoEmpresaActivity.class));
    }
    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

}
