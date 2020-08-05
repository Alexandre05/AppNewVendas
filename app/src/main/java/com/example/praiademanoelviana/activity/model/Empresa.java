package com.example.praiademanoelviana.activity.model;

import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;
import com.google.firebase.database.DatabaseReference;

import java.io.Serializable;

public class Empresa implements Serializable {

    private String idUsuario;
    private String urlImagem;
    private String nome;
    private String nome_filtro;
    private String informacoes;
    private String ramo;
    private Double valor;

    public String getNome_filtro() {
        return nome_filtro;
    }

    public void setNome_filtro(String nome_filtro) {
        this.nome_filtro = nome_filtro;
    }

    public Empresa() {
    }
    public  void salvar(){
        DatabaseReference firebaseRef =
                ConfiruFirebase.getFirebase();

DatabaseReference empresaRef = firebaseRef.child("empresas")
        .child(getIdUsuario());
   empresaRef.setValue(this);

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
        this.nome = nome.toLowerCase();
    }

    public String getInformacoes() {
        return informacoes;
    }

    public void setInformacoes(String informacoes) {
        this.informacoes = informacoes;
    }

    public String getRamo() {
        return ramo;
    }

    public void setRamo(String ramo) {
        this.ramo = ramo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }
}
