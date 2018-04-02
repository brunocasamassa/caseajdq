package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;

public class FaqAdapter extends ArrayAdapter<String> {

    private ArrayList<String> perguntas;
    private Context context;

    public FaqAdapter(Context c, ArrayList<String> objects) {
        super(c, 0, objects);
        this.perguntas = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if( perguntas != null ){

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.model_faq_pergunta, parent, false);

            // recupera elemento para exibição
            TextView perguntaView = (TextView) view.findViewById(R.id.pergunta);

            String notificacao = perguntas.get(position);

            perguntaView.setText(notificacao);

        }

        return view;

    }
}