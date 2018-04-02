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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.MainActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;

public class PedidosSelecionadoAdapter extends ArrayAdapter<Pedido> {

    private ArrayList<Pedido> pedidos;
    private Context context;
    private StorageReference storage;
    private Preferences preferences;
    private ArrayList<Pedido> pedidosFiltrado;
    private PedidosFiltro filtrador;
    private String facebookPhoto;


    private MainActivity mainActivity = new MainActivity();
    private StorageReference storageDonation;

    public PedidosSelecionadoAdapter(Context c, ArrayList<Pedido> objects) {
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

        // Verifica se a lista está vazia
        if (pedidos != null) {

            if (mainActivity.facebookImg != null) {
                facebookPhoto = mainActivity.facebookImg;
            }

            System.out.println("facebook img pedido: " + facebookPhoto);
            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.model_pedido_aceito, parent, false);

            // recupera elemento para exibição
            TextView statusPedido = (TextView) view.findViewById(R.id.imageStatus);
            TextView nomePedido = (TextView) view.findViewById(R.id.nomePedido);
            TextView descricao = (TextView) view.findViewById(R.id.descricao_pedido);
            TagGroup tagsCategoria = (TagGroup) view.findViewById(R.id.tagPedidos);
            final CircleImageView pedidoImg = (CircleImageView) view.findViewById(R.id.imagePedido);

            System.out.println("PEDDIDOS NO ADAPTER POSITION "+ position);
            final Pedido pedido = pedidosFiltrado.get(position);/*
            if (pedido.getGrupo() != null) {
                storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
            } else if (pedido.getGrupo() == null) {
                storage = FirebaseConfig.getFirebaseStorage().child("userImages");
            }*/

            if (pedido.getStatus() != 0) {
                int status = pedido.getStatus();
                System.out.println("status pedido " + pedido.getTitulo() + ": " + status);
                if (status == 0) {
                    //Picasso.with(getContext()).load(R.drawable.tag_aberto).resize(274, 274).into(statusPedido);
                    statusPedido.setText("  ABERTO  ");
                    statusPedido.setBackgroundColor(Color.parseColor("#1325ea"));
                } else if (status == 1) {
                    //Picasso.with(getContext()).load(R.drawable.tag_emandamento)/*.resize(274, 274)*/.into(statusPedido);
                    statusPedido.setText("  EM ANDAMENTO  ");
                    statusPedido.setBackgroundResource(R.drawable.rounded_background);
                    statusPedido.setBackgroundColor(Color.parseColor("#ea8e04"));

                } else if (status == 2) {
                    //Picasso.with(getContext()).load(R.drawable.tag_finalizado).resize(274, 274).into(statusPedido);
                    statusPedido.setText("  FINALIZADO  ");
                    statusPedido.setBackgroundColor(Color.parseColor("#1e9b3f"));
                } else if (status == 3) {
                    //Picasso.with(getContext()).load(R.drawable.tag_cancelado).resize(274, 274).into(statusPedido);
                    statusPedido.setText("  CANCELADO  ");
                    statusPedido.setBackgroundColor(Color.parseColor("#c41717"));
                } else if (status == 5) {
                    //Picasso.with(getContext()).load(R.drawable.tag_doacao).into(statusPedido);
                    statusPedido.setText("  DOAÇÃO  ");
                    statusPedido.setBackgroundColor(Color.parseColor("#7725e2"));
                }
            }
            storageDonation = FirebaseConfig.getFirebaseStorage().child("donationImages");

            storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
            try {
                nomePedido.setText(String.valueOf(pedido.getTitulo().substring(0, 17) )+ "...");
                System.out.println("DADOS PEDIDO NO ADAPTER: " + pedido.getTitulo());
            } catch (Exception e){
                nomePedido.setText(pedido.getTitulo() + "...");
            }
            System.out.println("DADOS PEDIDO NO ADAPTER: " + pedido.getTitulo());
            try {
                descricao.setText(String.valueOf(pedido.getDescricao().substring(0,27)) + "...");
            }catch (Exception e){
                descricao.setText(String.valueOf(pedido.getDescricao())+ "...");
            }

            tagsCategoria.setTags(pedido.getTagsCategoria());
            // DOWNLOAD GROUP IMG FROM STORAGE
            if(!pedido.getTipo().equals("Doacoes")){
                //donationqtd.setTextColor(Color.TRANSPARENT);
            }
            if(pedido.getTipo().equals("Doacoes")){
               /* donationqtd.setText(String.valueOf(pedido.getQtdAtual())+"/"+String.valueOf(pedido.getQtdDoado()));
                donationqtd.setTextColor(Color.argb(255,20,118,122));
                */storageDonation.child(pedido.getIdPedido()+ ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("grupo " + pedido.getGrupo());
                        try {
                            Glide.with(getContext()).load(uri).override(68, 68).into(pedidoImg);
                            // Picasso.with(getContext()).load(uri).resize(680, 680).into(pedidoImg);
                        } catch (Exception e) {
                            pedidoImg.setImageURI(uri);
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


            else if (pedido.getGrupo() != null) {
                storage.child(pedido.getGroupId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {

                    @Override
                    public void onSuccess(Uri uri) {
                        System.out.println("grupo " + pedido.getGrupo());
                        //Glide.with(getContext()).load(uri).override(68, 68).into(pedidoImg);
                        Picasso.with(getContext()).load(uri).into(pedidoImg);
                        System.out.println("my pedidos lets seee2" + uri);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {

                    }
                });
            } else Picasso.with(getContext()).load(R.drawable.logo).into(pedidoImg); //Glide.with(getContext()).load(R.drawable.logo).override(68, 68).into(pedidoImg);

        }

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
                    if (pedido.getTitulo().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(pedido);
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