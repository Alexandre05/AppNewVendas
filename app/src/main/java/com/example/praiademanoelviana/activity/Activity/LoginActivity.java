package com.example.praiademanoelviana.activity.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;


import com.example.praiademanoelviana.R;
import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.example.praiademanoelviana.activity.Helper.UsuarioFirebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
     private Button botaoAcessar;
     private EditText campoSenha,campoEmail;
     private Switch tipoAcesso,tipoUsuario;

     private LinearLayout linerartipoUsuario;
    private FirebaseAuth autenticacao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

   incializaComponentes();
        autenticacao= ConfiruFirebase.getFirebaseAutenticacao();
        //autenticacao.signOut();

        // METODO VERIFICA USUARIO LOGADO
   vericarUsuarioLogado();
tipoAcesso.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked){// ad. prefeitura

            linerartipoUsuario.setVisibility(View.VISIBLE);


        }else {// usuario normal

            linerartipoUsuario.setVisibility(View.GONE);

        }
    }
});


        botaoAcessar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if(!email.isEmpty()){
                     if (!senha.isEmpty()){
                               if(tipoAcesso.isClickable()){
autenticacao.createUserWithEmailAndPassword(
        email,senha
).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
    @Override
    public void onComplete(@NonNull Task<AuthResult> task) {
        if(task.isSuccessful()){
            Toast.makeText(LoginActivity.this,
                    "Cadastrado Com Sucesso!!",
                    Toast.LENGTH_SHORT).show();
            String tipoUsuario = getTipoUsuario();
            UsuarioFirebase.atualizarTipoUsuario(tipoUsuario);
            abriTelaP(tipoUsuario);


        }else {

            String erroExcecao = "";

            try{
                throw task.getException();
            }catch (FirebaseAuthWeakPasswordException e){
                erroExcecao = "Digite uma senha mais forte!";
            }catch (FirebaseAuthInvalidCredentialsException e){
                erroExcecao = "Por favor, digite um e-mail válido";
            }catch (FirebaseAuthUserCollisionException e){
                erroExcecao = "Este conta já foi cadastrada";

            } catch (Exception e) {
                erroExcecao = "ao cadastrar usuário: "  + e.getMessage();
                e.printStackTrace();
            }

            Toast.makeText(LoginActivity.this,
                    "Erro: " + erroExcecao ,
                    Toast.LENGTH_SHORT).show();

        }
    }
});



                               }else {// Login
                                   autenticacao.signInWithEmailAndPassword(
                                           email, senha
                                   ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                       @Override
                                       public void onComplete(@NonNull Task<AuthResult> task) {
                                           if (task.isSuccessful()) {
                                               Toast.makeText(LoginActivity.this,
                                                       "Logado com sucesso!!",
                                                       Toast.LENGTH_SHORT).show();
                                               String tipoUsuario = task.getResult().getUser().getDisplayName();
                                               abriTelaP(tipoUsuario);

                                           } else {

                                               Toast.makeText(LoginActivity.this,
                                                       "Erro ao Fazer Login:" + task.getException(),
                                                       Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   });


                               }



                     }

                }else {
                    Toast.makeText(LoginActivity.this,
                            "Preencha o E-mail!",
                            Toast.LENGTH_SHORT).show();

                }
            }
        });

}


private  String getTipoUsuario(){
return tipoUsuario.isChecked() ? "E" : "U";


}
    private  void vericarUsuarioLogado(){
        FirebaseUser usuarioAtual=
                autenticacao.getCurrentUser();
        if (usuarioAtual !=null){
            String tipoUsuario= usuarioAtual.getDisplayName();
            abriTelaP(tipoUsuario);

        }


    }
private  void abriTelaP(String tipoUsuario){
if(tipoUsuario.equals("E")){// tipo empresa

    startActivity(new Intent(getApplicationContext(), EmpresaActivity.class));



}else{// tipo usuario
    startActivity(new Intent(getApplicationContext(), HomeActivity.class));




}
    }
private void incializaComponentes() {

      campoEmail= findViewById(R.id.editCadastroEmail);
      campoSenha = findViewById(R.id.editSenha);
      botaoAcessar = findViewById(R.id.buttonAcesso);
        tipoAcesso = findViewById(R.id.switchAcesso);
    tipoUsuario = findViewById(R.id.sTipoAcesso);
    linerartipoUsuario = findViewById(R.id.lineartipoUsuario);
    }
}
