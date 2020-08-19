package com.example.praiademanoelviana.activity.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Adpter.AdapterEmpresa;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.example.praiademanoelviana.activity.lister.RecyclerItemClickListener;
import com.example.praiademanoelviana.activity.model.Empresa;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaRef;
    private AdapterEmpresa adapterEmpresa;
    String categoria;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        inicializarComponentes();
        firebaRef = ConfiruFirebase.getFirebase();
        autenticacao = ConfiruFirebase.getFirebaseAutenticacao();


        /* Recupera a categoria passada na Intent */
        categoria = getIntent().getStringExtra("Categoria");

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Listas de Empresas");
        setSupportActionBar(toolbar);
        //configura recycle
        recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));

        recyclerEmpresa.setHasFixedSize(true);
//        adapterEmpresa = new AdapterEmpresa(empresas);
//        recyclerEmpresa.setAdapter(adapterEmpresa);

//Recupera produtos para empresa
        recuparEmpresas();
        searchView.setHint("Pesquisar Empresas");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
        });

        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerEmpresa,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {
                                Empresa empresaSelecionada = empresas.get(position);
                                Intent i = new Intent(HomeActivity.this, CartapioActivity.class);
                                i.putExtra("empresas", empresaSelecionada);
                                startActivity(i);
                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            }
                        }
                )
        );
    }

    private void pesquisarEmpresas(String pesquisa) {
        DatabaseReference empresasRef = firebaRef

                .child("empresas");

        Query query = empresasRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf8ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Empresa e = ds.getValue(Empresa.class);

                    /* Filtra a empresa por categoria */
                    if (e.getCategoria().equals(categoria)) {
                        empresas.add(e);
                    }

                }

                adapterEmpresa = new AdapterEmpresa(empresas);
                recyclerEmpresa.setAdapter(adapterEmpresa);

                adapterEmpresa.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void recuparEmpresas() {
        DatabaseReference empresaRef = firebaRef
                .child("empresas");


        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                empresas.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Empresa e = ds.getValue(Empresa.class);

                    /* Filtra a empresa por categoria */
                    if (e.getCategoria().equals(categoria)) {
                        empresas.add(e);
                    }


                }

                adapterEmpresa = new AdapterEmpresa(empresas);
                recyclerEmpresa.setAdapter(adapterEmpresa);

                adapterEmpresa.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfigurações:
                abrirConfiguracoes();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes() {
        searchView = findViewById(R.id.materialS);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);


    }

    private void deslogarUsuario() {
        try {
            autenticacao.signOut();
            finish();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this, ConfiguracoesUsuariosActivity.class));
    }

}
