package studio.brunocasamassa.ajudaquioficial.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.DoacaoCriadaActivity;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.PedidosSelecionadoAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.PedidoAtendidoActivity;
import studio.brunocasamassa.ajudaquioficial.helper.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

public class PedidosEscolhidosFragment extends Fragment {

    private int premium;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private DatabaseReference databasePedidos;
    private ValueEventListener valueEventListenerPedidos;
    private FloatingActionButton fab;
    private User usuario = new User();
    private ArrayList<Pedido> pedidos;
    private ListView pedidosEscolhidos;
    private ArrayAdapter pedidoArrayAdapter;
    private PedidosActivity pa;
    private PedidosSelecionadoAdapter pedidosAdapter;
    private SwipeRefreshLayout refresh;
    private ImageView iconEmpty;

    public PedidosEscolhidosFragment() {
        //Required empty public constructor
    }

    @Override
    public void onStart() {

        if (pa.getArrayEscolhidosAdapter() != null) {
            pedidoArrayAdapter = pa.getArrayEscolhidosAdapter();
        }
        super.onStart();

        databasePedidos.addListenerForSingleValueEvent(valueEventListenerPedidos);

        //dbGroups.addListenerForSingleValueEvent(valueEventListenerAllGroups);

    }

    @Override
    public void onStop() {
        super.onStop();
        databasePedidos.removeEventListener(valueEventListenerPedidos);
        //dbGroups.removeEventListener(valueEventListenerAllGroups);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos_escolhidos, container, false);
        pa = (PedidosActivity) getActivity();
        pedidos = new ArrayList<>();
        pedidosEscolhidos = (ListView) view.findViewById(R.id.pedidos_escolhidos_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh) ;
        iconEmpty = (ImageView) view.findViewById(R.id.icon_empty_list);
        System.out.println("GRUPO NA POSICAO " + pedidos.isEmpty());


        pedidosAdapter = new PedidosSelecionadoAdapter(getContext(), pedidos);



        if (pa.getArrayEscolhidosAdapter() != null) {
            pedidoArrayAdapter = pa.getArrayEscolhidosAdapter();
        } else pedidoArrayAdapter = pedidosAdapter;


        pedidosEscolhidos.setDivider(null);

        pa.setArrayEscolhidosAdapter(pedidoArrayAdapter);

        //pedidoArrayAdapter = new PedidosSelecionadoAdapter(getActivity(), pedidos);

        pedidosEscolhidos.setAdapter(pedidoArrayAdapter);

        /*fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CriaPedidoActivity.class);
                intent.putExtra("premium", premium);
                startActivity(intent);
            }
        });*/

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
                //startListSingle();
            }
        });

        startList();

/*
        if(pedidos.isEmpty()){
            iconEmpty.setVisibility(View.VISIBLE);
        } else {
            iconEmpty.setVisibility(View.INVISIBLE);
        }*/
        pedidosEscolhidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), PedidoAtendidoActivity.class);
                Intent intentDonation = new Intent(getActivity(), DoacaoCriadaActivity.class);

                //recupera dados a serem passados
                Pedido selectedPedido = pedidosAdapter.getPedidosFiltrado().get(position);

                if (selectedPedido.getStatus() == 2) {//finalizado
                    Toast.makeText(getApplicationContext(), "Pedido finalizado", Toast.LENGTH_SHORT).show();
                } else if (!selectedPedido.getTipo().equals("Doacoes")){
                    //enviando dados para grupo activity
                    //enviando dados para pedido activity
                    intent.putExtra("status", selectedPedido.getStatus());
                    intent.putExtra("titulo", selectedPedido.getTitulo());
                    intent.putExtra("tagsCategoria", selectedPedido.getTagsCategoria());
                    intent.putExtra("idPedido", selectedPedido.getIdPedido());
                    intent.putExtra("criadorId", selectedPedido.getCriadorId());
                    intent.putExtra("atendenteId", selectedPedido.getAtendenteId());
                    intent.putExtra("tipo", selectedPedido.getTipo());

                    if(selectedPedido.getTipo().equals("Doacao")){
                        intent.putExtra("donationType", selectedPedido.getDonationType());
                    }
                    if (selectedPedido.getGrupo() != null) {

                        intent.putExtra("tagsGrupo", selectedPedido.getGrupo());
                        intent.putExtra("groupId", selectedPedido.getGroupId());
                    }
                    intent.putExtra("descricao", selectedPedido.getDescricao());

                    startActivity(intent);
                }  else{
                    intentDonation.putExtra("status", selectedPedido.getStatus());
                    intentDonation.putExtra("titulo", selectedPedido.getTitulo());
                    intentDonation.putExtra("tagsCategoria", selectedPedido.getTagsCategoria());
                    intentDonation.putExtra("idPedido", selectedPedido.getIdPedido());
                    intentDonation.putExtra("dadosDoador", selectedPedido.getDadosDoador());
                    intentDonation.putExtra("criadorId", selectedPedido.getCriadorId());
                    intentDonation.putExtra("tipo", selectedPedido.getTipo());
                    intentDonation.putExtra("atendenteId", selectedPedido.getAtendenteId());
                    intentDonation.putExtra("endereco", selectedPedido.getEndereco());
                    intentDonation.putExtra("dadosDoador", selectedPedido.getDadosDoador());
                    intentDonation.putExtra("donationContact", selectedPedido.getDonationContact());
                    intentDonation.putExtra("descricao", selectedPedido.getDescricao());
                    intentDonation.putExtra("cameFrom", 3);

                    startActivity(intentDonation);

                }

            }
        });


        return view;

    }


    private void startList() {

        DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usuario.setPedidosAtendidos(user.getPedidosAtendidos());
                premium = user.getPremiumUser();
                System.out.println("PMPF:idPedidos " + usuario.getPedidosFeitos());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("ERROR GET PEDIDOS USER STRINGS: " + databaseError);
            }
        });

        databasePedidos = FirebaseConfig.getFireBase()
                .child("Pedidos");

        valueEventListenerPedidos = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                pedidos.clear();

                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    System.out.println("get children pedidos " + dados);

                    Pedido pedido = dados.getValue(Pedido.class);
                    System.out.println("pedido " + pedido.getTitulo());
                    if (usuario.getPedidosAtendidos() != null) {
                        if (!pedidos.contains(pedido.getIdPedido()) && usuario.getPedidosAtendidos().contains(pedido.getIdPedido())) {
                            pedidos.add(pedido);
                        }
                    }
                    System.out.println("PMPF: pilha pedidos na view " + pedidos);

                }

                pedidoArrayAdapter.notifyDataSetChanged();
                System.out.println("PMPF: pilha pedidos na view2 " + pedidos);

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                pedidos.clear();

            }
        };

    }

    private void refresh() {
        Intent intent = new Intent(getActivity(), PedidosActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}
