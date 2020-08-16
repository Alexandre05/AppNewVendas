package com.example.praiademanoelviana.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.example.praiademanoelviana.activity.Helper.UsuarioFirebase;
import com.example.praiademanoelviana.activity.model.Empresa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracaoEmpresaActivity extends AppCompatActivity {
    private EditText editEmpresaNome, tefe,cnpj,
            editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagePerfilEmpresa;
  private TextView categoria;
    private static final int SELECAO_GALERIA = 200;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada ;
    private RadioButton mercado,loja;
    private RadioGroup opcaoCate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
//Configurações iniciais
        inicializarComponentes();
        storageReference = ConfiruFirebase.getReferenciaStorage();
        firebaseRef = ConfiruFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Perfil");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );
                if( i.resolveActivity(getPackageManager()) != null ){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();
  EscoleCategoria();

    }

    private void recuperarDadosEmpresa(){

        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child( idUsuarioLogado );

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Empresa empresa = dataSnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    categoria.setText(empresa.getCategoria());
                    editEmpresaTaxa.setText(empresa.getPrecoEntrega().toString());
                    editEmpresaTempo.setText(empresa.getTempo());
                     cnpj.setText(empresa.getCnpj());
                     tefe.setText(empresa.getTefoneEmpresa());
                    urlImagemSelecionada = empresa.getUrlImagem();
                    if( urlImagemSelecionada != "" ){
                        Picasso.get()
                                .load(urlImagemSelecionada)
                                .into(imagePerfilEmpresa);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public  void EscoleCategoria(){

     opcaoCate.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
         @Override
         public void onCheckedChanged(RadioGroup group, int checkedId) {
             if(checkedId== R.id.mercado){
                 categoria.setText("Mercado");
             }else if(checkedId==R.id.lojaDeRoupa){
                 categoria.setText("Loja de Roupas");

             }
         }
     });



    }
    public void validarDadosEmpresa(View view){

        //Valida se os campos foram preenchidos
        String nome = editEmpresaNome.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();
        String Categoria = categoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();
        String cnpf = cnpj.getText().toString();
        String tele= tefe.getText().toString();

        if( !nome.isEmpty()){
            if( !taxa.isEmpty()){

                    if( !cnpf.isEmpty()){

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario( idUsuarioLogado );
                        empresa.setNome( nome );
                        empresa.setPrecoEntrega( Double.parseDouble(taxa) );
                        empresa.setCategoria(Categoria);
                        empresa.setTempo( tempo );
                        empresa.setCnpj(cnpf);
                        empresa.setTefoneEmpresa(tele);
                        empresa.setUrlImagem( urlImagemSelecionada );

                        empresa.salvar();
                        finish();

                    }else{
                        exibirMensagem("Digite a Taxa de Entrega");
                    }

            }else{
                exibirMensagem("Digite Cnpj");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }
        EscoleCategoria();
    }

    private void exibirMensagem(String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if( resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if( imagem != null){

                    imagePerfilEmpresa.setImageBitmap( imagem );

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes( dadosImagem );
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracaoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemSelecionada = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(ConfiguracaoEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();

                        }
                    });

                }

            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }
// inicia os componentes

    private void inicializarComponentes(){
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        mercado = findViewById(R.id.mercado);
        loja= findViewById(R.id.lojaDeRoupa);

        editEmpresaTaxa = findViewById(R.id.editEmpresaTaxa);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempo);
        imagePerfilEmpresa = findViewById(R.id.FotoEmpresa);
        opcaoCate = findViewById(R.id.opcaoCategoria);
        categoria = findViewById(R.id.Cate);
        tefe= findViewById(R.id.tefenoEmpresa);
        cnpj= findViewById(R.id.Cnpf);
    }

}