package studio.brunocasamassa.ajudaquioficial.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.AccessToken;

/**
 * Created by bruno on 21/02/2017.
 */

public class Preferences {

    private Context contexto;
    private SharedPreferences preferences;
    private String NOME_ARQUIVO = "ajudaaqui.preferences";
    private int MODE = 0;
    private SharedPreferences.Editor editor;


    private  String ACCESS_TOKEN = "accesstoken";
    private String CHAVE_LATITUDE = "latitude";
    private String CHAVE_TIPO_PEDIDO = "chavePedido";
    private String CHAVE_LONGITUDE = "longitude";
    private String CHAVE_ID = "idUser";
    private String CHAVE_NOME = "nameUser";
    private String FACEBOOK_PHOTO = "facebook_photo";
    private String TOKEN = "token";
    private String CHAVE_SENHA = "senha";
    private String CHAVE_EMAIL = "email";


    public Preferences(Context contextoParametro) {
        contexto = contextoParametro;
        preferences = contexto.getSharedPreferences(NOME_ARQUIVO, MODE);
        editor = preferences.edit();
    }

    public Preferences() {

    }

    public void saveMyCoordinates(Double latitude, Double longitude) {
        editor.putLong(CHAVE_LATITUDE, Double.doubleToRawLongBits(latitude));
        editor.putLong(CHAVE_LONGITUDE, Double.doubleToRawLongBits(longitude));
        editor.commit();

    }

    public void saveData(String idUser, String nameUser) {
        editor.putString(CHAVE_ID, idUser);
        editor.putString(CHAVE_NOME, nameUser);
        editor.commit();
    }

    public void saveLogin(String email, String senha) {
        editor.putString(CHAVE_EMAIL, email);
        editor.putString(CHAVE_SENHA, senha);
        editor.commit();
    }

    public void saveDataImgFacebook(String idUser, String nameUser, String facebookPhoto) {
        editor.putString(FACEBOOK_PHOTO, facebookPhoto);
        editor.putString(CHAVE_ID, idUser);
        editor.putString(CHAVE_NOME, nameUser);
        editor.commit();

    }

    public String getFACEBOOK_PHOTO() {
        return preferences.getString(FACEBOOK_PHOTO, null);
    }


    public long getLatitude() {
        return preferences.getLong(CHAVE_LATITUDE, Long.parseLong(null));
    }


    public long getLongitude() {
        return preferences.getLong(CHAVE_LONGITUDE, Long.parseLong(null));
    }

    public String getIdentificador() {
        return preferences.getString(CHAVE_ID, null);
    }


    public String getNome() {
        return preferences.getString(CHAVE_NOME, null);
    }

    public String getMail() {
        return preferences.getString(CHAVE_EMAIL, null);
    }

    public String getSenha() {
        return preferences.getString(CHAVE_SENHA, null);
    }

    public String getFilterPedido() {
        return preferences.getString(CHAVE_TIPO_PEDIDO, null);
    }

    public void saveFilterPedido(String tipoPedido) {
        editor.putString(CHAVE_TIPO_PEDIDO, tipoPedido);
        editor.commit();
    }


    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public String getToken() {
        return preferences.getString(TOKEN, null);
    }

    public void saveToken(String refreshedToken) {
        editor.putString(TOKEN, refreshedToken);
        editor.commit();
    }

    public void saveAccessToken(AccessToken accessToken) {
       editor.putString(ACCESS_TOKEN, String.valueOf(accessToken));
    }

    public String getAccessToken(){
        return preferences.getString(ACCESS_TOKEN,null);

    }
}
