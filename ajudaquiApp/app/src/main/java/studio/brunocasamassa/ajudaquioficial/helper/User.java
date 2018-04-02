package studio.brunocasamassa.ajudaquioficial.helper;

import android.net.Uri;
import android.support.v7.app.NotificationCompat;

import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

/**
 * Created by bruno on 26/04/2017.
 */

public class User implements DatabaseReference.CompletionListener {

    private String id;

    private boolean termosAceitos = true;

    private int maxDistance;

    public int getPremiumUser() {
        return premiumUser;
    }

    public void setPremiumUser(int premiumUser) {
        this.premiumUser = premiumUser;
    }

    private int premiumUser;  //0==free , 1==premium

    private ArrayList<String> entradas;

    private Double latitude;

    public String getPremiumDate() {
        return premiumDate;
    }

    public void setPremiumDate(String premiumDate) {
        this.premiumDate = premiumDate;
    }

    private String premiumDate;

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

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    private ArrayList<Integer> medalhas;

    public String getNotificationToken() {
        return notificationToken;
    }

    public void setNotificationToken(String notificationToken) {
        this.notificationToken = notificationToken;
    }

    private String notificationToken;

    private int creditos = 3;

    private int pontos;

    private NotificationCompat.Builder pedidoCanceladoNotification;
    private ArrayList<String> gruposSolicitados;

    private int chatNotificationCount;


    private int pedidosNotificationCount;

    public int getProfileNotificationCount() {
        return profileNotificationCount;
    }

    public void setProfileNotificationCount(int profileNotificationCount) {
        this.profileNotificationCount = profileNotificationCount;
    }

    private int profileNotificationCount;

    private ArrayList<String> pedidosAtendidos;

    private ArrayList<String> itensDoados;
    private ArrayList<String> pedidosFeitos;

    public String getMessageNotification() {
        return MessageNotification;
    }

    public void setMessageNotification(String messageNotification) {
        MessageNotification = messageNotification;
    }

    private String MessageNotification;
    private String senha;

    private ArrayList<String> grupos;

    private String profileImg;

    private Uri profileImageURL;

    public String name;

    private String email;


    private ArrayList<String> msgSolicitacoes;

    public String getCpf_cnpj() {
        return cpf_cnpj;
    }

    public void setCpf_cnpj(String cpf_cnpj) {
        this.cpf_cnpj = cpf_cnpj;
    }

    public String cpf_cnpj;


    public User() {

    }



    public ArrayList<Integer> getMedalhas() {
        return medalhas;
    }

    public void setMedalhas(ArrayList<Integer> medalhas) {
        this.medalhas = medalhas;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }


    public int getPontos() {
        return pontos;
    }

    public void setPontos(int pontos) {
        this.pontos = pontos;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public ArrayList<String> getPedidosAtendidos() {

        return pedidosAtendidos;
    }

    public void setPedidosAtendidos(ArrayList<String> pedidosAtendidos) {
        this.pedidosAtendidos = pedidosAtendidos;
    }


    public ArrayList<String> getPedidosFeitos() {
        return pedidosFeitos;
    }

    public void setPedidosFeitos(ArrayList<String> pedidosFeitos) {
        this.pedidosFeitos = pedidosFeitos;
    }


    public ArrayList<String> getGrupos() {
        return grupos;
    }

    public void setGrupos(ArrayList<String> grupos) {

        this.grupos = grupos;
    }


    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }


    public Uri getProfileImageURL() {
        return profileImageURL;
    }

    public void setProfileImageURL(Uri profileImageURL) {
        this.profileImageURL = profileImageURL;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public void save() {
        DatabaseReference referenciaFirebase = FirebaseConfig.getFireBase();
        referenciaFirebase.child("usuarios").child(getId()).setValue(this);
    }

    public void update() {
        DatabaseReference referenciaFirebase = FirebaseConfig.getFireBase();
        referenciaFirebase.child("usuarios").child(getId()).setValue(this);
    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

    }

    public int getCreditos() {
        return creditos;
    }

    public void setCreditos(int creditos) {
        this.creditos = creditos;
    }


    public ArrayList<String> getMsgSolicitacoes() {
        return msgSolicitacoes;
    }

    public void setMsgSolicitacoes(ArrayList<String> msgSolicitacoes) {
        this.msgSolicitacoes = msgSolicitacoes;
    }

    public int getChatNotificationCount() {
        return chatNotificationCount;
    }

    public void setChatNotificationCount(int chatNotificationCount) {
        this.chatNotificationCount = chatNotificationCount;
    }

    public ArrayList<String> getGruposSolicitados() {
        return gruposSolicitados;
    }

    public void setGruposSolicitados(ArrayList<String> gruposSolicitados) {
        this.gruposSolicitados = gruposSolicitados;
    }

    public int getPedidosNotificationCount() {
        return pedidosNotificationCount;
    }

    public void setPedidosNotificationCount(int pedidosNotificationCount) {
        this.pedidosNotificationCount = pedidosNotificationCount;
    }

    public NotificationCompat.Builder getPedidoCanceladoNotification() {
        return pedidoCanceladoNotification;
    }

    public void setPedidoCanceladoNotification(NotificationCompat.Builder pedidoCanceladoNotification) {
        this.pedidoCanceladoNotification = pedidoCanceladoNotification;
    }

    public int getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(int maxDistance) {
        this.maxDistance = maxDistance;
    }

    public ArrayList<String> getItensDoados() {
        return itensDoados;
    }

    public void setItensDoados(ArrayList<String> itensDoados) {
        this.itensDoados = itensDoados;
    }

    public ArrayList<String> getEntradas() {
        return entradas;
    }

    public void setEntradas(ArrayList<String> entradas) {
        this.entradas = entradas;
    }


    public boolean isTermosAceitos() {
        return termosAceitos;
    }

    public void setTermosAceitos(boolean termosAceitos) {
        this.termosAceitos = termosAceitos;
    }
}
