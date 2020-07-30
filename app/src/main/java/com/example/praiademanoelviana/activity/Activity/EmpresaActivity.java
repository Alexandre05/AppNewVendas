package com.example.praiademanoelviana.activity.Activity;


import android.content.Intent;
import android.os.Bundle;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.google.firebase.auth.FirebaseAuth;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);
        autenticacao = ConfiruFirebase.getFirebaseAutenticacao();



        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Ad. Praia de Manoel Viana");
        setSupportActionBar(toolbar);


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

    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracaoEmpresaActivity.class));
    }
    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

}
