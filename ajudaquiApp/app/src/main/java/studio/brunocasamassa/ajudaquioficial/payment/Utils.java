package studio.brunocasamassa.ajudaquioficial.payment;

/**
 * Created by bruno on 22/03/2017.
 */
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;


public class Utils {

    public PaymentObj getInformacao(String repoUrl){
        String json;
        PaymentObj retorno;
        System.out.println("Repourl CARAIO: "+ repoUrl);
        json = NetworkUtils.getJSONFromAPI(repoUrl);
        System.out.println("RESULTADO CARAIO: "+ json);
        retorno = parseJson(json);

        return retorno;
    }

    private PaymentObj parseJson(String json){
        try {
            PaymentObj pessoa = new PaymentObj();

            JSONObject jsonObj = new JSONObject(json);
            //JSONArray array = jsonObj.getJSONArray("results");

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Date data;

            JSONObject objArray = jsonObj.getJSONObject(json); //maybe  referencia "dsdws"

            JSONObject obj = objArray;//.getJSONObject("user");

            //Atribui os objetos que estão nas camadas mais altas

            //Nome da pessoa é um objeto, instancia um novo JSONObject

            return pessoa;
        }catch (JSONException e){

            System.out.println("JSON EXCEPTION CARAIO:" +e);
            e.printStackTrace();
            return null;

        }
    }

}
