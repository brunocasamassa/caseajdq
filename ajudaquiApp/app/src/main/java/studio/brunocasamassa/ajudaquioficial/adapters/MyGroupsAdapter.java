package studio.brunocasamassa.ajudaquioficial.adapters;

/**
 * Created by bruno on 09/05/2017.
 */

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;


public class MyGroupsAdapter extends ArrayAdapter<Grupo> {

    private ArrayList<Grupo> grupos;
    private Context context;
    private ArrayList<Grupo> gruposFiltrado;
    private GruposFiltro filtrador;
    private StorageReference storage;

    public MyGroupsAdapter(Context c, ArrayList<Grupo> objects) {
        super(c, 0, objects);
        this.grupos = objects;
        this.gruposFiltrado =objects;
        this.context = c;

        getFilter();

    }



    public ArrayList<Grupo> getGruposFiltrado() {
        return gruposFiltrado;
    }

    public void setGruposFiltrado(ArrayList<Grupo> gruposFiltrado) {
        this.gruposFiltrado = gruposFiltrado;
    }


    @Override
    public int getCount() {
        return gruposFiltrado.size();
    }

    @Nullable
    @Override
    public Grupo getItem(int position) {
        return gruposFiltrado.get(position);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
        View view = null;

        // Verifica se a lista está vazia
        if (grupos != null) {

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);

            // Monta view a partir do xml
            view = inflater.inflate(R.layout.model_group, parent, false);

            // recupera elemento para exibição
            ImageView lock = (ImageView) view.findViewById(R.id.lock_icon);
            TextView nomeGrupo = (TextView) view.findViewById(R.id.nomeGrupo);
            TextView qtdMmebros = (TextView) view.findViewById(R.id.qtd_membros);
            final CircleImageView imgGrupo = (CircleImageView) view.findViewById(R.id.groupImg);

            final Grupo grupo = gruposFiltrado.get(position);
            try {
                if (!grupo.getEstado().equals("") || !grupo.getCidade().equals("")) {
                    nomeGrupo.setText(grupo.getNome() + " - " + grupo.getCidade() + "\n " + grupo.getEstado().toUpperCase());
                } else nomeGrupo.setText(grupo.getNome());
            } catch (Exception e) {
                nomeGrupo.setText(grupo.getNome());
            }
            qtdMmebros.setText("Membros: " + String.valueOf(grupo.getQtdMembros()));
            if (!grupo.isOpened()) {
                lock.setBackgroundResource(R.drawable.ic_lock);
            }
            // DOWNLOAD GROUP IMG FROM STORAGE
            storage.child(grupo.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    try {
                        Picasso.with(getContext()).load(uri).resize(680, 680).into(imgGrupo);
                        //Glide.with(getContext()).load(uri).override(68, 68).into(imgGrupo);
                        //grupo.setGrupoImg(uri.toString());
                    } catch (Exception e) {
                        imgGrupo.setImageURI(uri);
                        System.out.println("EXCEPTION PedidosAdapter " + e);
                    }

                    System.out.println("my groups lets seee2" + uri);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {

                }
            });

        }

        return view;
    }

    @Override
    public Filter getFilter() {
        if (filtrador == null) {
            filtrador = new GruposFiltro();
        }

        return filtrador;
    }


    private class GruposFiltro extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                ArrayList<Grupo> tempList = new ArrayList<>();

                for (Grupo grupo : grupos) {
                    if (grupo.getNome().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(grupo);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = grupos.size();
                filterResults.values = grupos;
            }

            return filterResults;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            gruposFiltrado = (ArrayList<Grupo>) results.values;
            notifyDataSetChanged();
        }
    }


}