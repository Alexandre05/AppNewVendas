package com.example.praiademanoelviana.activity.model;import com.example.praiademanoelviana.activity.Helper.ConfiruFirebase;import com.google.firebase.database.DatabaseReference;public class Produto {    private String idUsuario;    private  String idProduto;    private String urlImagemproduto;    private String nomePro;    private String InformaAcamp;  private  Double valorAreaAcampa;    public Double getValorAreaAcampa() {        return valorAreaAcampa;    }    public String getIdProduto() {        return idProduto;    }    public void setIdProduto(String idProduto) {        this.idProduto = idProduto;    }    public void setValorAreaAcampa(Double valorAreaAcampa) {        this.valorAreaAcampa = valorAreaAcampa;    }    public Produto() {        DatabaseReference firebaseRef = ConfiruFirebase.getFirebase();        DatabaseReference produtoRef = firebaseRef                .child("produtos");setIdProduto(produtoRef.push().getKey());    }    public String getIdUsuario() {        return idUsuario;    }    public void setIdUsuario(String idUsuario) {        this.idUsuario = idUsuario;    }    public  void salvar(){        DatabaseReference firebaseRef = ConfiruFirebase.getFirebase();        DatabaseReference produtoRef = firebaseRef                .child("produtos")                .child(getIdUsuario())                .child(getIdProduto());        produtoRef.setValue(this);    }public  void remover(){    DatabaseReference firebaseRef = ConfiruFirebase.getFirebase();    DatabaseReference produtoRef = firebaseRef            .child("produtos")            .child(getIdUsuario())            .child(getIdProduto());    produtoRef.setValue(this);    produtoRef.removeValue();}    public String getUrlImagemproduto() {        return urlImagemproduto;    }    public void setUrlImagemproduto(String urlImagemproduto) {        this.urlImagemproduto = urlImagemproduto;    }    public String getNomePro() {        return nomePro;    }    public void setNomePro(String nomePro) {        this.nomePro = nomePro;    }    public String getInformaAcamp() {        return InformaAcamp;    }    public void setInformaAcamp(String informaAcamp) {        InformaAcamp = informaAcamp;    }}