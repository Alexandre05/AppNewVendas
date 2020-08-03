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
    private EditText edtEmpresaNome, edtRamoEmpre, edtInformaEmpresa, edtValor;
    private ImageView imagePerfilEmpresa;
    private DatabaseReference firebaseRef;
    private static final int SELECAO_GALEREIA = 200;
    private StorageReference storageReference;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao);
// configuração componentes
        inicializarComponentes();
        storageReference= ConfiruFirebase.getReferenciaStorage();
        firebaseRef = ConfiruFirebase.getFirebase();
        idUsuarioLogado= UsuarioFirebase.getIdUsuario();
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imagePerfilEmpresa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALEREIA);


                }
            }
        });
        /*Recuperar dados da empresa*/
        recuperarDadosEmpresa();
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
                    edtEmpresaNome.setText(empresa.getNome());
                    edtRamoEmpre.setText(empresa.getRamo());
                    edtValor.setText(empresa.getValor().toString());
                    edtInformaEmpresa.setText(empresa.getInformacoes());

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

    public void validarDadosEmpresa(View view){

        //Valida se os campos foram preenchidos
        String nome = edtEmpresaNome.getText().toString();
        String valor = edtValor.getText().toString();
        String informacoes = edtInformaEmpresa.getText().toString();
        String ramo = edtRamoEmpre.getText().toString();

        if( !nome.isEmpty()){
            if( !valor.isEmpty()){
                if( !ramo.isEmpty()){
                    if( !valor.isEmpty()){

                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario( idUsuarioLogado );
                        empresa.setNome( nome );
                        empresa.setValor( Double.parseDouble(valor) );
                        empresa.setRamo(ramo);
                        empresa.setInformacoes( informacoes );
                        empresa.setUrlImagem( urlImagemSelecionada );
                        empresa.salvar();
                        finish();

                    }else{
       exibirMensagem("Digite Informações");
                    }
                }else{
      exibirMensagem("Digite Ramo");
                }
            }else{
                exibirMensagem("Digite valor ");
            }
        }else{
            exibirMensagem("Digite um nome para a empresa");
        }

    }
    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT)
                .show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {

                switch (requestCode) {
                    case SELECAO_GALEREIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                if (imagem != null) {

                    imagePerfilEmpresa.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("imagens perfil ")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
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
                               imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                   @Override
                                   public void onComplete(@NonNull Task<Uri> task) {
                                     Uri url= task.getResult();
                                   }
                               });
                        }
                    });

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }





    private  void inicializarComponentes(){

edtEmpresaNome = findViewById(R.id.InformaAcamp);
edtRamoEmpre = findViewById(R.id.editEmpresaRamo);
edtInformaEmpresa = findViewById(R.id.InformacoesEmpre);
edtValor= findViewById(R.id.editValorE);
imagePerfilEmpresa= findViewById(R.id.FotoEmpresa);

    }
}
