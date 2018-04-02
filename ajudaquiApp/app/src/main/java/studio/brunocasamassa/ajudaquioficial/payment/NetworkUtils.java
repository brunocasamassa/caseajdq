package studio.brunocasamassa.ajudaquioficial.payment;

/**
 * Created by bruno on 22/03/2017.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class NetworkUtils {


    //Responsavel por carregar o Objeto JSON
    public static String getJSONFromAPI(String url){
        String retorno = "";
        try {

            URL resourceUrl = new URL(url);
            int codigoResposta;
            HttpURLConnection conexao;
            InputStream is;
            conexao = (HttpURLConnection) resourceUrl.openConnection();
            conexao.setRequestMethod("GET");
            conexao.setReadTimeout(15000);
            conexao.setConnectTimeout(15000);
            conexao.connect();

            codigoResposta = conexao.getResponseCode();
            if(codigoResposta < HttpURLConnection.HTTP_BAD_REQUEST){
                is = conexao.getInputStream();
            }else{
                is = conexao.getErrorStream();
            }

            retorno = converterInputStreamToString(is);
            is.close();
            conexao.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
            System.out.println("EXCEPTION CARAIO1: "+ e);
        }catch (IOException e){
            e.printStackTrace();
            System.out.println("EXCEPTION CARAIO2: "+ e);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("EXCEPTION CARAIO3: "+ e);
        }

        return retorno;


    }

    private static String converterInputStreamToString(InputStream is){
        StringBuffer buffer = new StringBuffer();
        try{
            BufferedReader br;
            String linha;

            br = new BufferedReader(new InputStreamReader(is));
            while((linha = br.readLine())!=null){
                buffer.append(linha);
            }

            br.close();
        }catch(IOException e){
            e.printStackTrace();
        }

        return buffer.toString();
    }

}