package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.app.Dialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.R;

public class MedalhasAdapter extends RecyclerView.Adapter {

    private ArrayList<Integer> badges;
    private Context context;

    public MedalhasAdapter(ArrayList<Integer> objects, Context context) {
        this.context = context;
        this.badges = objects;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;

        System.out.println("contagem de itens no adapter " + getItemCount());
        LayoutInflater inflater = (LayoutInflater) parent.getContext().getSystemService(context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.model_badges, parent, false);
        MedalhasViewHolder holder = new MedalhasViewHolder(view);

        return holder;

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MedalhasViewHolder medalhasHolder = (MedalhasViewHolder) holder;

         int badge = badges.get(position);

        System.out.println("entrei badge position " + position);

        switch (badge) {
            case 0:
                System.out.println("entrei badge " + badge);
                medalhasHolder.badgeView.setImageResource(R.drawable.badge_back);
                break;
            case 1:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_02);
                System.out.println("entrei badge " + badge);
                break;
            case 2:
                System.out.println("entrei badge " + badge);
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_03);
                break;
            case 3:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_04);
                System.out.println("entrei badge " + badge);
                break;
            case 4:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_02);
                System.out.println("entrei badge " + badge);
                break;
            case 5:
                System.out.println("entrei badge " + badge);
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_06);
                break;
            case 6:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_07);
                System.out.println("entrei badge " + badge);
                break;
            case 7:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_02);
                System.out.println("entrei badge " + badge);
                break;
            case 8:
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_04);
                System.out.println("entrei badge " + badge);
                break;
            case 9:
                System.out.println("entrei badge " + badge);
                medalhasHolder.badgeView.setImageResource(R.drawable.medalhas_01);
                break;
        }

        final int badgeT  = badge;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBadgeDialog(position, badgeT);

            }
        });

    }

    private void showBadgeDialog(int position, int badge) {
        final Dialog alertDialog = new Dialog(context);
        View image = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.badge_image, null);
        final ImageButton cross = (ImageButton) image.findViewById(R.id.x_button);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();

            }
        });

        final TextView titleBadge = (TextView) image.findViewById(R.id.badge_title);
        final TextView messageBadge = (TextView) image.findViewById(R.id.badge_message);
        final CircleImageView circleBadge = (CircleImageView) image.findViewById(R.id.badge_image);
        circleBadge.setImageResource(verifyBadge(position, badge));
        titleBadge.setText(verifyTitleBadge(position,badge));
        messageBadge.setText(verifyMessageBadge(position,badge));
        //circleBadge.setImageResource(R.drawable.medalha_one);

        int w = 500;
        int h = 300;
        image.setMinimumWidth(w);
        image.setMinimumHeight(h);
        alertDialog.setContentView(image);

      /*  View description = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.badge_message, null);
        final TextView title = (TextView) description.findViewById(R.id.badge_title);
        final TextView message = (TextView) description.findViewById(R.id.badge_message);
        String texto = "R.string.medalha"+String.valueOf(position);
        message.setText(texto);

        alertDialog.setView(description);*/
        alertDialog.show();
    }

    private String verifyTitleBadge(int position, int badge) {
        if (badge == 0) {
            return "Medalha não Adquirida";
        } else {
            switch (position) {
                case 0:
                    return context.getString(R.string.medalha1_title);
                case 1:
                    return context.getString(R.string.medalha2_title);
                case 2:
                    return context.getString(R.string.medalha3_title);
                case 3:
                    return context.getString(R.string.medalha4_title);
                case 4:
                    return context.getString(R.string.medalha5_title);
                case 5:
                    return context.getString(R.string.medalha6_title);
                case 6:
                    return context.getString(R.string.medalha7_title);
                case 7:
                    return context.getString(R.string.medalha8_title);
                case 8:
                    return context.getString(R.string.medalha9_title);

            }
        }
        return "Medalha Nº" + String.valueOf(badge);

    }

    private String verifyMessageBadge(int position, int badge) {
        if (badge == 0) {
            return "Vamos Lá, continue ajudando para liberar esta medalha...não desista :D ";
        } else {
            switch (position) {
                case 0:
                    return context.getString(R.string.medalha1_message);
                case 1:
                    return context.getString(R.string.medalha2_message);
                case 2:
                    return context.getString(R.string.medalha3_message);
                case 3:
                    return context.getString(R.string.medalha4_message);
                case 4:
                    return context.getString(R.string.medalha5_message);
                case 5:
                    return context.getString(R.string.medalha6_message);
                case 6:
                    return context.getString(R.string.medalha7_message);
                case 7:
                    return context.getString(R.string.medalha8_message);
                case 8:
                    return context.getString(R.string.medalha9_message);

            }
        }
        return "Medalha Nº" + String.valueOf(badge);

    }


    private int verifyBadge(int position, int badge) {
        if (badge == 0) {
            return R.drawable.badge_back;
        } else {
            switch (position) {
                case 0:
                    return R.drawable.medalhas_02;
                case 1:
                    return R.drawable.medalhas_03;
                case 2:
                    return R.drawable.medalhas_04;
                case 3:
                    return R.drawable.medalhas_02;
                case 4:
                    return R.drawable.medalhas_06;
                case 5:
                    return R.drawable.medalhas_07;
                case 6:
                    return R.drawable.medalhas_02;
                case 7:
                    return R.drawable.medalhas_04;
                case 8:
                    return R.drawable.medalhas_01;

            }
        }
        return R.drawable.badge_back;
    }

    @Override
    public int getItemCount() {
        return badges.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}


class MedalhasViewHolder extends RecyclerView.ViewHolder {


    final CircleImageView badgeView;

    public MedalhasViewHolder(View view) {
        super(view);

        // recupera elemento para exibição
        badgeView = (CircleImageView) view.findViewById(R.id.img_badge_id);


    }


}