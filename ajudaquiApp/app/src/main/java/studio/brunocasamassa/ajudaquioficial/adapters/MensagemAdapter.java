package studio.brunocasamassa.ajudaquioficial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Mensagem;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;

public class MensagemAdapter extends ArrayAdapter<Mensagem> {

    private Context context;
    private ArrayList<Mensagem> mensagens;
    private int trigger;

    public MensagemAdapter(Context c, ArrayList<Mensagem> objects) {
        super(c, 0, objects);
        this.context = c;
        this.mensagens = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;

        // Verifica se a lista está preenchida
        if (mensagens != null) {

            // Recupera dados do usuario remetente
            Preferences preferencias = new Preferences(context);
            String idUsuarioRementente = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());

            // Inicializa objeto para montagem do layout
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);


            // Recupera mensagem
            Mensagem mensagem = mensagens.get(position);

            // Monta view a partir do xml
            if (idUsuarioRementente.equals(mensagem.getIdUsuario())) {
                if (mensagem.getFile() == null) {
                    view = inflater.inflate(R.layout.item_mensagem_direita, parent, false);
                    trigger = 1;
                } else {
                    view = inflater.inflate(R.layout.item_message_right_img, parent, false);
                    trigger = 2;

                }
            } else if (mensagem.getFile() == null) {
                view = inflater.inflate(R.layout.item_mensagem_esquerda, parent, false);
                trigger = 3;
            } else {
                view = inflater.inflate(R.layout.item_message_left_img, parent, false);
                trigger = 4;
            }

            // Recupera elemento para exibição
            TextView textoMensagem = (TextView) view.findViewById(R.id.tv_mensagem);
            ImageView image = (ImageView) view.findViewById(R.id.img_chat);

            if (trigger == 1) {
                textoMensagem.setText(mensagem.getMensagem());
            }
            if (trigger == 2) {
                image.setBackgroundResource(R.drawable.squared_corner_message_right);
                Glide.with(image.getContext()).load(mensagem.getFile().getUrl_file()).override(68,68).into(image);
            }
            if (trigger == 3) {
                textoMensagem.setText(mensagem.getMensagem());
            } ;
            if (trigger == 4) {
                image.setBackgroundResource(R.drawable.squared_corner_message_left);
                Glide.with(image.getContext()).load(mensagem.getFile().getUrl_file()).override(68, 68).fitCenter().into(image);
            } ;


        }

        return view;

    }


}
