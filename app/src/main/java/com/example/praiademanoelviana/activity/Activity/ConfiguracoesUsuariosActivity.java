package com.example.praiademanoelviana.activity.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.example.praiademanoelviana.activity.Helper.UsuarioFirebase;
import com.example.praiademanoelviana.activity.model.Empresa;
import com.example.praiademanoelviana.activity.model.Usuario;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ConfiguracoesUsuariosActivity extends AppCompatActivity {
    private EditText edtNomeUsuario,
            edtCpfUsuario, edtEnderecoUsuario;
    private  String idUsuario;
    private ImageView imageUsuario;
    private DatabaseReference firebaseRef;
    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_usuarios);
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        inicializarComponentes();


        idUsuario = UsuarioFirebase.getIdUsuario();
        storageReference= ConfiruFirebase.getReferenciaStorage();
        firebaseRef = ConfiruFirebase.getFirebase();


        toolbar.setTitle("Configurações Usuário");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recuperarDadosUsuarios();
    }
    private  void recuperarDadosUsuarios(){
DatabaseReference usuarioREF= firebaseRef
        .child("usuarios")
        .child(idUsuario);
usuarioREF.addListenerForSingleValueEvent(new ValueEventListener() {
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        if(dataSnapshot.getValue()!=null){
Usuario usuario = dataSnapshot.getValue(Usuario.class);
edtNomeUsuario.setText(usuario.getNome());
edtEnderecoUsuario.setText(usuario.getEndereco());
edtCpfUsuario.setText(usuario.getCpf());
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
});



    }
    public void validarDadosUsuario(View view){
        String nome = edtNomeUsuario.getText().toString();
        String endereco = edtEnderecoUsuario.getText().toString();
        String cpf = edtCpfUsuario.getText().toString();

        if( !nome.isEmpty()) {
            if (!endereco.isEmpty()) {
                if (!cpf.isEmpty()) {
                    //if (!valor.isEmpty()) {

                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(idUsuario);
                        usuario.setNome(nome);
                        usuario.setEndereco((endereco));
                        usuario.setCpf(cpf);
                        //empresa.setInformacoes(informacoes);
                        //usuario.setUrlImagemUsuario(urlIm);
                        usuario.salvar();
                        finish();
                         exibirMensagem("Dados Atualizados com Sucesso!!");
                    } else {
                        exibirMensagem("Digite Seu Cpf");
                    }
                } else {
                    exibirMensagem("Digite Seu Endereço");
                }
            } else {
                exibirMensagem("Digite Seu Nome ");
            }
        }



    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }
    private  void inicializarComponentes(){

        imageUsuario= findViewById(R.id.FotoUsuario);
        edtNomeUsuario= findViewById(R.id.NomeUsuario);
        edtCpfUsuario=findViewById(R.id.CpfUsuario);
        edtEnderecoUsuario = findViewById(R.id.editEnderecoUsuario);



    }
}
