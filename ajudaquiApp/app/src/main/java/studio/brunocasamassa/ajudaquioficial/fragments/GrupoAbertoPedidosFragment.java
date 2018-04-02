package studio.brunocasamassa.ajudaquioficial.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.PedidosAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.PedidoActivity;


/**
 * A simple {@link Fragment} subclass.
 */


public class GrupoAbertoPedidosFragment extends Fragment {

    private ListView listview_nomes;
    private ArrayList<Pedido> arraylist_nomes = new ArrayList<>();
    private ArrayAdapter<Pedido> adapter_nomes;
    private DatabaseReference firebaseDatabase;
    private String contactName;
    private String idGroup;
    private ValueEventListener valueEventListenerPedidosGroup;
    private String nomeGroup;
    private int qtdMembros;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());

    @Override
    public void onStart() {
        super.onStart();
        firebaseDatabase.addListenerForSingleValueEvent(valueEventListenerPedidosGroup);
    }

    public GrupoAbertoPedidosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle extra = getActivity().getIntent().getExtras();

        idGroup = extra.getString("groupId");

        nomeGroup = extra.getString("nome");

        qtdMembros = extra.getInt("qtdmembros");


        View v = inflater.inflate(R.layout.fragment_grupo_pedidos, container, false);


        listview_nomes = (ListView) v.findViewById(R.id.list_pedidos_groups);

        arraylist_nomes = new ArrayList<>();

        adapter_nomes = new PedidosAdapter(getActivity(), arraylist_nomes);

        listview_nomes.setAdapter(adapter_nomes);

        firebaseDatabase = FirebaseConfig.getFireBase().child("Pedidos");

        valueEventListenerPedidosGroup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_nomes.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                    Pedido pedido = snapshot.getValue(Pedido.class);

                    if (pedido.getGrupo() != null && pedido.getGrupo().equals(nomeGroup) && !pedido.getCriadorId().equals(userKey) && pedido.getAtendenteId() == null) {
                        adapter_nomes.add(pedido);
                       // arraylist_nomes.add(pedido);
                        System.out.println("adicionado pedido "+ pedido.getTitulo());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listview_nomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PedidoActivity.class);
                Pedido pedido = adapter_nomes.getItem(position);
                System.out.println("PEDIDO CLICADO NO GRUPO " + pedido.getTitulo());
                // enviando dados para pedido activity
                intent.putExtra("status", pedido.getStatus());
                intent.putExtra("titulo", pedido.getTitulo());
                intent.putExtra("tagsCategoria", pedido.getTagsCategoria());
                intent.putExtra("idPedido", pedido.getIdPedido());
                intent.putExtra("criadorId", pedido.getCriadorId());
                intent.putExtra("tipo", pedido.getTipo());
                if (pedido.getGrupo() != null) {
                    intent.putExtra("tagsGrupo", pedido.getGrupo());
                    intent.putExtra("groupId", pedido.getGroupId());
                }
                intent.putExtra("descricao", pedido.getDescricao());
                arraylist_nomes.clear();
                startActivity(intent);
            }
        });


        // Inflate the layout for this fragment
        return v;

    }


}

