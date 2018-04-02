package studio.brunocasamassa.ajudaquioficial.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import studio.brunocasamassa.ajudaquioficial.R;

/**
 * Created by bruno on 18/06/2017.
 */

public class SobreAdapter extends ArrayAdapter {

    private String[] listaSobre;
    private Context context;

    public SobreAdapter(Context applicationContext, String[] vector) {
        super (applicationContext,0,vector);
        this.context = applicationContext;
        this.listaSobre = vector;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

        view = inflater.inflate(R.layout.model_sobre, parent, false);

        TextView nomeSobre= (TextView) view.findViewById(R.id.sobre_nome);
        ImageView image = (ImageView) view.findViewById(R.id.sobre_image);

        if(position ==0){
        nomeSobre.setText(listaSobre[position]);
        Glide.with(getContext()).load(R.drawable.sobre_icon_duvidas).override(68,68).into(image);
                }

        if(position ==1){
            nomeSobre.setText(listaSobre[position]);
            Glide.with(getContext()).load(R.drawable.sobre_icon_faleconosco).override(68,68).into(image);
        }


        if(position ==2){
            nomeSobre.setText(listaSobre[position]);
            Glide.with(getContext()).load(R.drawable.sobre_icon_denuncie).override(68,68).into(image);
        }


        if(position ==3){
            nomeSobre.setText(listaSobre[position]);
            Glide.with(getContext()).load(R.drawable.sobre_icon_facebook).override(68,68).into(image);
        }

        if(position ==4){
            nomeSobre.setText(listaSobre[position]);
            Glide.with(getContext()).load(R.drawable.sobre_icon_site).override(68,68).into(image);
        }
/*
          if(position ==5){
            nomeSobre.setText(listaSobre[position]);
            Glide.with(getContext()).load(R.drawable.pedidos_icon).override(68,68).into(image);
        }*/
        return view;


    }
}
