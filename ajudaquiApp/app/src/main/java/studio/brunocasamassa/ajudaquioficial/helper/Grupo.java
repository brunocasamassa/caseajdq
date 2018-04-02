package studio.brunocasamassa.ajudaquioficial.helper;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by bruno on 26/04/2017.
 */

public class Grupo {

    public boolean isOpened() {
        return opened;
    }

    public void setOpened(boolean opened) {
        this.opened = opened;
    }

    public boolean opened;
    public String id;
    public String nome;
    public String grupoImg;
    public int qtdMembros;
    public String descricao;
    public ArrayList<String> idAdms;

    private String cidade;

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    private String estado;



    public ArrayList<String> getIdMembros() {
        return idMembros;
    }

    public void setIdMembros(ArrayList<String> idMembros) {
        this.idMembros = idMembros;
    }

    public ArrayList<String> idMembros;
    public ArrayList<String> trocas;
    public ArrayList<String> emprestimos;
    public ArrayList<String> servicos;
    public ArrayList<String> doacoes;

    public Grupo getGrupo() {
        return grupo;
    }

    private Grupo grupo;

    public ArrayList<String> getIdAdms() {
        return idAdms;
    }

    public void setIdAdms(ArrayList<String> idAdms) {
        this.idAdms = idAdms;
    }

    public ArrayList<String> getTrocas() {
        return trocas;
    }

    public void setTrocas(ArrayList<String> trocas) {
        this.trocas = trocas;
    }

    public ArrayList<String> getEmprestimos() {
        return emprestimos;
    }

    public void setEmprestimos(ArrayList<String> emprestimos) {
        this.emprestimos = emprestimos;
    }

    public ArrayList<String> getServicos() {
        return servicos;
    }

    public void setServicos(ArrayList<String> servicos) {
        this.servicos = servicos;
    }

    public ArrayList<String> getDoacoes() {
        return doacoes;
    }

    public void setDoacoes(ArrayList<String> doacoes) {
        this.doacoes = doacoes;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getGrupoImg() {
        return grupoImg;
    }

    public void setGrupoImg(String grupoImg) {
        this.grupoImg = grupoImg;
    }

    public int getQtdMembros() {
        //this.qtdMembros = idMembros.size() + idAdms.size();
        return qtdMembros;
    }

    public void setQtdMembros(int qtdMembros) {
        this.qtdMembros = qtdMembros;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void save() {
        DatabaseReference referenciaFirebase = FirebaseConfig.getFireBase();
       // referenciaFirebase.child("gruposId").setValue(getId());
        referenciaFirebase.child("grupos").child(getId()).setValue(this);
    }

    public String getId() {
        return id;
    }

    public void setId(String Id) {
        this.id = Id;
    }
}
