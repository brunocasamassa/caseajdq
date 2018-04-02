package studio.brunocasamassa.ajudaquioficial.helper;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by bruno on 08/05/2017.
 */

public class Pedido {

    private String atendenteId;

    public Pedido() {
    }

    public String getIdPedido() {
        return idPedido;
    }

    private Double latitude;

    //Doação é requerimento ou oferecimento(pedidndo ou doando) == 0 , 1
    private int donationType;
    private String donationContact;
    private String endereco;

    public String getDonationContact() {
        return donationContact;
    }

    public void setDonationContact(String donationContact) {
        this.donationContact = donationContact;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    private String groupId;
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getDistanceInMeters() {
        return distanceInMeters;
    }

    public void setDistanceInMeters(Double distanceInMeters) {
        this.distanceInMeters = distanceInMeters;
    }

    private Double distanceInMeters;

    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }

    private int naCabine;  //0=false , 1=true

    private String idPedido;

    public String getTitulo() {
        return Titulo;
    }

    public void setTitulo(String titulo) {
        Titulo = titulo;
    }

    public String getDescricao() {
        return Descricao;
    }

    public void setDescricao(String descricao) {
        Descricao = descricao;
    }

    public ArrayList<String> getTagsCategoria() {
        return tagsCategoria;
    }

    public void setTagsCategoria(ArrayList<String> tagsCategoria) {
        this.tagsCategoria = tagsCategoria;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getGrupo() {
        return grupo;
    }

    public void setGrupo(String grupo) {
        this.grupo = grupo;
    }

    private String Titulo;
    private String Descricao;
    private ArrayList<String> tagsCategoria = new ArrayList<String>();
    private String dadosDoador;

    public String getCriadorId() {
        return criadorId;
    }

    public void setCriadorId(String criadorId) {
        this.criadorId = criadorId;
    }

    private String criadorId;

    int status;
    private String grupo;

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    private String tipo;

    private int qtdAtual;
    private int qtdDoado;

    public void save() {
        DatabaseReference referenciaFirebase = FirebaseConfig.getFireBase();
        referenciaFirebase.child("Pedidos").child(getIdPedido()).setValue(this);
    }

    public String getAtendenteId() {
        return atendenteId;
    }

    public void setAtendenteId(String atendenteId) {
        this.atendenteId = atendenteId;
    }

    public int getNaCabine() {
        return naCabine;
    }

    public void setNaCabine(int naCabine) {
        this.naCabine = naCabine;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setQtdDoado(int qtdDoado) {
        this.qtdDoado = qtdDoado;
    }

    public int getQtdDoado() {
        return qtdDoado;
    }

    public int getQtdAtual() {
        return qtdAtual;
    }

    public void setQtdAtual(int qtdAtual) {
        this.qtdAtual = qtdAtual;
    }

    public String getDadosDoador() {
        return dadosDoador;
    }

    public void setDadosDoador(String dadosDoador) {
        this.dadosDoador = dadosDoador;
    }

    public int getDonationType() {
        return donationType;
    }

    public void setDonationType(int donationType) {
        this.donationType = donationType;
    }



/* STATUS
aberto - 0
em andamento - 1
finalizado - 2
cancelado - 3
doacoes- 5
*/



}
