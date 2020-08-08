package com.example.praiademanoelviana.activity.Activity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

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
import com.example.praiademanoelviana.activity.model.Produto;
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

public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private EditText nomeAr, informaAre,Valor;
    private ImageView imageAcampa;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private static final int SELECAO_GALEREIA = 200;
    private String urlImagemPro = "";

    private StorageReference storageReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);
        inicializarComponentes();
        firebaseRef = ConfiruFirebase.getFirebase();

        idUsuarioLogado= UsuarioFirebase.getIdUsuario();
        storageReference= ConfiruFirebase.getReferenciaStorage();
        //Configurações Toolbar
        Toolbar toolbar = findViewById(R.id.toolbarNova);
        toolbar.setTitle("Novo Produto/Serviço");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageAcampa.setOnClickListener(new View.OnClickListener() {
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
        recuperarDadoProdutos();

    }
    private void recuperarDadoProdutos(){

        DatabaseReference produtosRef = firebaseRef
                .child("produtos");
               //.child( idProduto );
        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if( dataSnapshot.getValue() != null ){
                    Produto produto = dataSnapshot.getValue(Produto.class);
                    nomeAr.setText(produto.getNome());
                    informaAre.setText(produto.getInformaAcamp());
                    //edtValor.setText(empresa.getValor().toString());
                    //edtInformaEmpresa.setText(empresa.getInformacoes());

                    urlImagemPro = produto.getUrlImagemproduto();
                    if( urlImagemPro != "" ){
                        Picasso.get()
                                .load(urlImagemPro)
                                .into(imageAcampa);
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void ValidarDadosCAM(View view){

        //Valida se os campos foram preenchidos
        String nome = nomeAr.getText().toString();
        String informacoes = informaAre.getText().toString();
        String valor = Valor.getText().toString();

        if( !nome.isEmpty()){
            if( !informacoes.isEmpty()){
                //if( !ramo.isEmpty()){
                    if( !valor.isEmpty()){

                        Produto produto = new Produto();
                        produto.setIdUsuario( idUsuarioLogado );
                        produto.setNome( nome );
                        produto.setPreco( Double.parseDouble(valor));
                        //empresa.setRamo(ramo);
                        produto.setInformaAcamp( informacoes );
                        produto.setUrlImagemproduto(urlImagemPro );
                        produto.salvar();
                        finish();


 exibirMensagem("Produto Salvo Com Sucesso!!");


                    }else{
                        exibirMensagem("Digite Nome ");
                    }

            }else{
                exibirMensagem("Digite Informações ");
            }
        }else{
            exibirMensagem("Digite valor");
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

                    imageAcampa.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    final StorageReference imagemRef = storageReference
                            .child("produtos")
                            .child("acampamentos")
                            .child(idUsuarioLogado + "jpeg");

                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Erro ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            urlImagemPro = taskSnapshot.getDownloadUrl().toString();
                            Toast.makeText(NovoProdutoEmpresaActivity.this,
                                    "Sucesso ao fazer upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
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
        nomeAr = findViewById(R.id.NomeArea);
        informaAre = findViewById(R.id.InformaAcamp);
          Valor=findViewById(R.id.ValorProduto);
        imageAcampa= findViewById(R.id.FotoP);

    }

}
