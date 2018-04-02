package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.afinal.simplecache.ACache;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.MainActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;

public class PedidosAdapter extends ArrayAdapter<Pedido> implements Filterable {

    private ArrayList<Pedido> pedidos;

    private ArrayList<Pedido> pedidosFiltrado;
    private Context context;
    private PedidosFiltro filtrador;
    private StorageReference storage;
    private StorageReference storageDonation;
    private Preferences preferences;
    private String facebookPhoto;
    private ACache mCache;


    private MainActivity mainActivity = new MainActivity();

    public PedidosAdapter(Context c, ArrayList<Pedido> objects) {
        super(c, 0, objects);
        this.pedidos = objects;
        this.pedidosFiltrado = objects;
        this.context = c;

        getFilter();
    }

    public void setPedidosFiltrado(ArrayList<Pedido> pedidosFiltrado) {
        this.pedidosFiltrado = pedidosFiltrado;
    }

    public ArrayList<Pedido> getPedidosFiltrado() {
        return pedidosFiltrado;
    }

    @Override
    public int getCount() {
        return pedidosFiltrado.size();
    }

    @Override
    public Pedido getItem(int position) {
        return pedidosFiltrado.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        mCache = ACache.get(getContext());


        // Verifica se a lista está vazia
        if (pedidos != null) {

            //IF The user image is from facebook
            if (mainActivity.facebookImg != null) {

                facebookPhoto = mainActivity.facebookImg;

            }

            System.out.println("facebook img pedido: " + facebookPhoto);
            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            if (convertView == null) {
                view = inflater.inflate(R.layout.model_pedido, parent, false);
            } else view = convertView;

            // recupera elemento para exibição
            TextView statusPedido = (TextView) view.findViewById(R.id.text_status);
            TextView distancia = (TextView) view.findViewById(R.id.distance);
            TextView nomePedido = (TextView) view.findViewById(R.id.nomePedido);
            TextView donationqtd = (TextView) view.findViewById(R.id.qtd_pedido);
            TextView descricao = (TextView) view.findViewById(R.id.descricao_pedido);
            TagGroup tagsCategoria = (TagGroup) view.findViewById(R.id.tagPedidos);
            final CircleImageView pedidoImg = (CircleImageView) view.findViewById(R.id.imagePedido);
            int width =  context.getResources().getDisplayMetrics().widthPixels;
            //Toast.makeText(getContext(), width +" ADAPTER ", Toast.LENGTH_SHORT).show();

            if(width <= 480){
                nomePedido.setTextSize(15);
                nomePedido.setTranslationX(30);
                descricao.setTextSize(15);
                descricao.setTranslationX(30);
                tagsCategoria.setTranslationX(Float.valueOf(-10));

            }

            final Pedido pedido = pedidosFiltrado.get(position);

            /*
            if (pedido.getGrupo() != null) {
                storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
            } else if (pedido.getGrupo() == null) {
                storage = FirebaseConfig.getFirebaseStorage().child("userImages");
            }
            */

            storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
            storageDonation = FirebaseConfig.getFirebaseStorage().child("donationImages");

            String status = pedido.getTipo();
            System.out.println("status pedido " + pedido.getTitulo() + ": " + status);
            if (status.equals("Servicos")) {
                //Picasso.with(getContext()).load(R.drawable.tag_aberto).resize(274, 274).into(statusPedido);
                statusPedido.setText(" SERVIÇOS ");
                statusPedido.setBackgroundResource(R.drawable.rounded_background);
                statusPedido.setBackgroundColor(Color.parseColor("#1bb1b7"));
            } else if (status.equals("Troca")) {
                //Picasso.with(getContext()).load(R.drawable.tag_emandameCibfnto)/*.resize(274, 274)*/.into(statusPedido);
                statusPedido.setText(" TROCA ");
                statusPedido.setBackgroundColor(Color.parseColor("#1bb1b7"));
            } else if (status.equals("Doacao")) {
                //Picasso.with(getContext()).load(R.drawable.tag_finalizado).resize(274, 274).into(statusPedido);
                statusPedido.setText(" DOAÇÃO ");
                statusPedido.setBackgroundResource(R.drawable.rounded_background);
                statusPedido.setBackgroundColor(Color.parseColor("#1bb1b7"));
            } else if (status.equals("Emprestimos")) {
                //Picasso.with(getContext()).load(R.drawable.tag_cancelado).resize(274, 274).into(statusPedido);
                statusPedido.setText(" EMPRÉSTIMO ");
                statusPedido.setBackgroundColor(Color.parseColor("#1bb1b7"));
            }

            if (pedido.getDistanceInMeters() != null) {
                int km = pedido.getDistanceInMeters().intValue();
                //if(km < 0){km = (km *-1); }
                int dotIndex = String.valueOf(pedido.getDistanceInMeters()).indexOf(".");
                String meters = String.valueOf(pedido.getDistanceInMeters()).substring(dotIndex,dotIndex+2);
                System.out.println("see meters " + meters);

                distancia.setText(km + meters+" km");

            } else {
                distancia.setTextColor(Color.TRANSPARENT);
            }

            try {
                nomePedido.setText(String.valueOf(pedido.getTitulo().substring(0, 15)) + "...");
                System.out.println("DADOS PEDIDO NO ADAPTER: " + pedido.getTitulo());
            } catch (Exception e) {
                nomePedido.setText(pedido.getTitulo() + "...");
            }

            try {
                descricao.setText(String.valueOf(pedido.getDescricao().substring(0, 20)) + "...");
            } catch (Exception e) {
                descricao.setText(String.valueOf(pedido.getDescricao()) + "...");
            }

            tagsCategoria.setTags(pedido.getTagsCategoria());

            // DOWNLOAD GROUP IMG FROM STORAGE
            if (!pedido.getTipo().equals("Doacoes")) {
                donationqtd.setTextColor(Color.TRANSPARENT);
            }

            final String imgKey = "imgkey: "+pedido.getIdPedido();
            String cacheData = mCache.getAsString(imgKey);
            if (cacheData != null) {
                Picasso.with(getContext()).load(cacheData).resize(680, 680).into(pedidoImg);
            } else {
                if (pedido.getTipo().equals("Doacoes")) {
                donationqtd.setText(String.valueOf(pedido.getQtdAtual()) + "/" + String.valueOf(pedido.getQtdDoado()));
                donationqtd.setTextColor(Color.argb(255, 20, 118, 122));
                storageDonation.child(pedido.getIdPedido() + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("grupo " + pedido.getGrupo());
                        try {
                            Picasso.with(context).load(uri).into(pedidoImg);
                            mCache.put(imgKey, uri.toString(),2 * ACache.TIME_DAY);
                            //Glide.with(getContext()).load(uri).override(68, 68).into(pedidoImg);
                        } catch (Exception e) {
                            Picasso.with(getContext()).load(uri).into(pedidoImg);
                            mCache.put(imgKey, uri.toString(),2 * ACache.TIME_DAY);

                            //pedidoImg.setImageURI(uri);
                            System.out.println("EXCEPTION PedidosAdapter " + e);
                        }
                        System.out.println("my pedidos lets seee2" + uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            }

            if (pedido.getGrupo() != null) {

                storage.child(pedido.getGroupId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("grupo " + pedido.getGrupo());
                        try {
                            Picasso.with(context).load(uri).into(pedidoImg);
                            mCache.put(imgKey, uri.toString(),2 * ACache.TIME_DAY);

                            //Glide.with(getContext()).load(uri).override(68, 68).into(pedidoImg);
                        } catch (Exception e) {
                            Picasso.with(getContext()).load(R.drawable.logo).into(pedidoImg);
                            mCache.put(imgKey, uri.toString(),2 * ACache.TIME_DAY);

                            System.out.println("EXCEPTION PedidosAdapter " + e);
                        }
                        System.out.println("my pedidos lets seee2" + uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });

            } else Picasso.with(getContext()).load(R.drawable.logo).into(pedidoImg);

            //Glide.with(getContext()).load(R.drawable.logo).override(68, 68).into(pedidoImg);

        }}

        return view;

    }

    @Override
    public Filter getFilter() {
        if (filtrador == null) {
            filtrador = new PedidosFiltro();
        }

        return filtrador;
    }

    private class PedidosFiltro extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Pedido> tempList = new ArrayList<>();

                for (Pedido pedido : pedidos) {
                    try {

                        if (pedido.getTitulo().toLowerCase().contains(constraint.toString().toLowerCase())) {
                            tempList.add(pedido);
                        }
                        else if(pedido.getTagsCategoria() != null){
                            for (int i=0; i<pedido.getTagsCategoria().size();i++){
                                System.out.println("pedido tag filtered "+ pedido.getTagsCategoria().get(i).toString());
                                if(pedido.getTagsCategoria().get(i).contains(constraint.toString())){
                                    tempList.add(pedido);
                                }
                            }
                        }

                    } catch (Exception e) {
                        System.out.println("error populating filters in all pedidos: Pedido: "+pedido.getTitulo() + "error: "+ e);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = pedidos.size();
                filterResults.values = pedidos;
            }


            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            pedidosFiltrado = (ArrayList<Pedido>) results.values;
            notifyDataSetChanged();
        }
    }

}

