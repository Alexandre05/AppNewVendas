package com.example.praiademanoelviana.activity.model;

import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String tempo;
    private String categoria;
    private Double precoEntrega;
    private String Cnpj;
    private String tefoneEmpresa;


    public Empresa() {
    }

    public void salvar(){

        DatabaseReference firebaseRef = ConfiruFirebase.getFirebase();
        DatabaseReference empresaRef = firebaseRef.child("empresas")
                .child( getIdUsuario() );
        empresaRef.setValue(this);

    }

    public String getCnpj() {
        return Cnpj;
    }

    public void setCnpj(String cnpj) {
        Cnpj = cnpj;
    }

    public String getTefoneEmpresa() {
        return tefoneEmpresa;
    }

    public void setTefoneEmpresa(String tefoneEmpresa) {
        this.tefoneEmpresa = tefoneEmpresa;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getUrlImagem() {
        return urlImagem;
    }

    public void setUrlImagem(String urlImagem) {
        this.urlImagem = urlImagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTempo() {
        return tempo;
    }

    public void setTempo(String tempo) {
        this.tempo = tempo;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Double getPrecoEntrega() {
        return precoEntrega;
    }

    public void setPrecoEntrega(Double precoEntrega) {
        this.precoEntrega = precoEntrega;
    }
}