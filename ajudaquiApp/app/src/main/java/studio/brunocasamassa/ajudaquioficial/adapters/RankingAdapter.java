
package studio.brunocasamassa.ajudaquioficial.adapters;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;


/**
 * Created by bruno on 26/06/2017.
 */


public class RankingAdapter extends ArrayAdapter<User> {
    private ArrayList<User> usersGroup;
    private Context context;
    private StorageReference storage;
    private Preferences preferences;
    private int userPlace;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());


    public RankingAdapter(Context c, ArrayList<User> objects, int userPlace) {
        super(c, 0, objects);
        this.usersGroup = objects;
        this.context = c;
        this.userPlace = userPlace;
    }


    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        View view = null;

        // Verifica se a lista está preenchida
        if (usersGroup != null) {

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.model_ranking, parent, false);

            // recupera elemento para exibição
            TextView ranked_place = (TextView) view.findViewById(R.id.ranked_place);
            TextView nome = (TextView) view.findViewById(R.id.ranking_name);
            TextView pontos = (TextView) view.findViewById(R.id.ranking_points);
            LinearLayout linear = (LinearLayout) view.findViewById(R.id.linear_ranking);

            User usuario = usersGroup.get(position);
            System.out.println("user no adapter " + usuario.getName());
            nome.setText(usuario.getName());
            pontos.setText(String.valueOf(usuario.getPontos()));

            //adjust position after 100º place
            if(ranked_place.getText().length() > 2){
                ranked_place.setTextSize(15);
            }

            if (usuario.getId().equals(userKey) && userPlace != 0) {
                ranked_place.setText(userPlace);
                ranked_place.setTextColor(Color.WHITE);
                pontos.setTextColor(Color.WHITE);
                nome.setTextColor(Color.WHITE);
                linear.setBackgroundResource(R.color.colorBackground);
            } else if (usuario.getId().equals(userKey)) {
                linear.setBackgroundResource(R.color.colorBackground);
                nome.setTextColor(Color.WHITE);
                ranked_place.setTextColor(Color.WHITE);
                pontos.setTextColor(Color.WHITE);
                ranked_place.setText(String.valueOf(position + 1));
            } else ranked_place.setText(String.valueOf(position + 1));

        }
        ;

        return view;
    }

}


