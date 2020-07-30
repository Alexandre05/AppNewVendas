package com.example.praiademanoelviana.activity.Helper;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ConfiruFirebase {
private  static DatabaseReference referenciaFarebase;
private  static FirebaseAuth referenciaAutenticacao;
private static StorageReference referenciaStorage;





// retorna  a referecnai do database
    public  static  DatabaseReference getFirebase(){

        if( referenciaFarebase== null){
           referenciaFarebase= FirebaseDatabase.getInstance().getReference();


        }

return referenciaFarebase;


    }
// retorna a instancia do firebaseAutencicação
public static FirebaseAuth getFirebaseAutenticacao (){
     if ( referenciaAutenticacao== null){
        referenciaAutenticacao= FirebaseAuth.getInstance();


     }


        return referenciaAutenticacao;

}

// Reorna instancia do Forebase Storange

    public  static  StorageReference getReferenciaStorage(){
  if ( referenciaStorage== null){

      referenciaStorage= FirebaseStorage.getInstance().getReference();


  }
    return  referenciaStorage;



    }
}


