package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;

public class NotificacoesAdapter extends ArrayAdapter<String> {

    private ArrayList<String> notificacoes;
    private Context context;

    public NotificacoesAdapter(Context c, ArrayList<String> objects) {
        super(c, 0, objects);
        this.notificacoes = objects;
        this.context = c;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está vazia
        if( notificacoes != null ){

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.model_notificacoes, parent, false);

            // recupera elemento para exibição
            TextView mensagem = (TextView) view.findViewById(R.id.notify_description);
            ImageView imagem = (ImageView) view.findViewById(R.id.notify_img);

            String notificacao = notificacoes.get(position);
            imagem.setImageDrawable(ContextCompat.getDrawable(getContext(),R.drawable.logo_notify));
            mensagem.setText(notificacao);

        }

        return view;

    }
}