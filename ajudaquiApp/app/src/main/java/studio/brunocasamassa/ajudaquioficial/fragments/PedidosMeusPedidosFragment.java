package studio.brunocasamassa.ajudaquioficial.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import studio.brunocasamassa.ajudaquioficial.adapters.PedidosMeusPedidosAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.PedidoCriadoActivity;
import studio.brunocasamassa.ajudaquioficial.helper.User;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * A simple {@link Fragment} subclass.
 */

public class PedidosMeusPedidosFragment extends Fragment {
    private int premium;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private ArrayList<Pedido> pedidos;
    private ArrayAdapter pedidoArrayAdapter;
    private ListView meusPedidos;
    private DatabaseReference databasePedidos;
    private FloatingActionButton fab;
    private ValueEventListener valueEventListenerPedidos;
    private User usuario = new User();
    private DatabaseReference dbUser;
    private PedidosActivity pa;
    private PedidosMeusPedidosAdapter pedidosAdapter;
    private SwipeRefreshLayout refresh;
    private ImageView iconEmpty;


    public PedidosMeusPedidosFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        if (pa.getArrayMeusPedidosAdapter() != null) {
            pedidoArrayAdapter = pa.getArrayMeusPedidosAdapter();
        }
        super.onStart();
        databasePedidos.addListenerForSingleValueEvent(valueEventListenerPedidos);
        dbUser = FirebaseConfig.getFireBase().child("usuarios");

        dbUser.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getPedidosNotificationCount() != 0) {
                    //Toast.makeText(getApplicationContext(),"Parabens, voce possui um pedido atendido", Toast.LENGTH_LONG).show();
                    user.setPedidosNotificationCount(/*user.getChatNotificationCount() - mensagens.size()*/ 0);
                    user.setId(userKey);
                    user.save();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


//      dbGroups.addListenerForSingleValueEvent(valueEventListenerAllGroups);

    }


    @Override
    public void onStop() {
        super.onStop();
        databasePedidos.removeEventListener(valueEventListenerPedidos);
        //dbGroups.removeEventListener(valueEventListenerAllGroups);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos_meuspedidos, container, false);
        pa = (PedidosActivity) getActivity();
        pedidos = new ArrayList<>();
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        meusPedidos = (ListView) view.findViewById(R.id.meusPedidos_list);
        System.out.println("GRUPO NA POSICAO " + pedidos.isEmpty());
        iconEmpty = (ImageView) view.findViewById(R.id.icon_empty_list);


        pedidosAdapter = new PedidosMeusPedidosAdapter(getContext(), pedidos);




        if (pa.getArrayMeusPedidosAdapter() != null) {
            pedidoArrayAdapter = pa.getArrayMeusPedidosAdapter();
        } else pedidoArrayAdapter = pedidosAdapter;

        meusPedidos.setDivider(null);

        pa.setArrayMeusPedidosAdapter(pedidoArrayAdapter);

        meusPedidos.setAdapter(pedidoArrayAdapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        /*fab = (FloatingActionButton) view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CriaPedidoActivity.class);
                intent.putExtra("premium", premium);
                startActivity(intent);
            }
        });*/


        DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        databaseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usuario.setPedidosFeitos(user.getPedidosFeitos());
                usuario.setItensDoados(user.getItensDoados());
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

                    try {
                        Pedido pedido = dados.getValue(Pedido.class);
                        if (usuario.getPedidosFeitos() != null) {
                            if (!pedidos.contains(pedido.getIdPedido()) && usuario.getPedidosFeitos().contains(pedido.getIdPedido())) {
                                pedidos.add(pedido);
                                System.out.println("pedido meus pedidos " + pedido.getTitulo());

                            }
                        }
                        if (usuario.getItensDoados() != null) {
                            System.out.println("crazy beast "+usuario.getItensDoados().toArray());
                            if (!pedidos.contains(pedido.getIdPedido()) && usuario.getItensDoados().contains(pedido.getIdPedido())) {
                                if(pedido.getQtdAtual() > 0) {
                                    pedidos.add(pedido);
                                    System.out.println("pedido meus pedidos " + pedido.getTitulo());
                                }
                                }
                        }

                        System.out.println("PMPF: pilha pedidos na view " + pedidos);

                    } catch (Exception e){
                         System.out.println("exception getting order "+ pedidos.get(pedidos.size()-1).getTitulo()+"  : "+ e);
                    }

                        }


                    pedidoArrayAdapter.notifyDataSetChanged();

                System.out.println("PMPF: pilha pedidos na view2 " + pedidos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                pedidos.clear();

            }
        };
/*
        if(pedidos.isEmpty()){
            iconEmpty.setVisibility(View.VISIBLE);
        } else {
            iconEmpty.setVisibility(View.INVISIBLE);
        }*/

        meusPedidos.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final Pedido pedidoPressed = pedidosAdapter.getPedidosFiltrado().get(position);

                if (pedidoPressed.getStatus() == 2) {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());

                    alertDialog.setTitle("Apagar pedido");
                    alertDialog.setMessage("deseja excluir este pedido? ");

                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            apagaPedido(pedidoPressed);

                        }
                    });

                    alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
                return false;
            }
        });
        meusPedidos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(getActivity(), PedidoCriadoActivity.class);
                Intent intentDonation = new Intent(getActivity(), DoacaoCriadaActivity.class);

                // recupera dados a serem passados
                Pedido selectedPedido = pedidosAdapter.getPedidosFiltrado().get(position);

                if (selectedPedido.getStatus() == 2) {//finalizado
                    Toast.makeText(getApplicationContext(), "Pedido finalizado", Toast.LENGTH_SHORT).show();
                } else if (!selectedPedido.getTipo().equals("Doacoes")) {

                    // enviando dados para grupo activity
                    // enviando dados para pedido activity
                    intent.putExtra("status", selectedPedido.getStatus());
                    intent.putExtra("titulo", selectedPedido.getTitulo());
                    intent.putExtra("tagsCategoria", selectedPedido.getTagsCategoria());
                    intent.putExtra("idPedido", selectedPedido.getIdPedido());
                    intent.putExtra("criadorId", selectedPedido.getCriadorId());
                    intent.putExtra("tipo", selectedPedido.getTipo());
                    intent.putExtra("atendenteId", selectedPedido.getAtendenteId());
                    if (selectedPedido.getGrupo() != null) {
                        intent.putExtra("tagsGrupo", selectedPedido.getGrupo());
                        intent.putExtra("groupId", selectedPedido.getGroupId());
                    }

                    if(selectedPedido.getTipo().equals("Doacao")){
                        intent.putExtra("donationType", selectedPedido.getDonationType());
                    }

                    intent.putExtra("descricao", selectedPedido.getDescricao());
                    startActivity(intent);

                } else {
                    intentDonation.putExtra("status", selectedPedido.getStatus());
                    intentDonation.putExtra("titulo", selectedPedido.getTitulo());
                    intentDonation.putExtra("tagsCategoria", selectedPedido.getTagsCategoria());
                    intentDonation.putExtra("idPedido", selectedPedido.getIdPedido());
                    intentDonation.putExtra("criadorId", selectedPedido.getCriadorId());
                    intentDonation.putExtra("dadosDoador", selectedPedido.getDadosDoador());
                    intentDonation.putExtra("tipo", selectedPedido.getTipo());

                    intentDonation.putExtra("atendenteId", selectedPedido.getAtendenteId());
                    intentDonation.putExtra("endereco", selectedPedido.getEndereco());
                    intentDonation.putExtra("donationContact", selectedPedido.getDonationContact());
                    intentDonation.putExtra("descricao", selectedPedido.getDescricao());
                    intentDonation.putExtra("cameFrom", 1);

                    startActivity(intentDonation);
                }

            }
        });

        return view;

    }

    private void refresh() {
        Intent intent = new Intent(getActivity(), PedidosActivity.class);
        getActivity().finish();
        startActivity(intent);
    }

    private void apagaPedido(Pedido pedido) {
        DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");
        DatabaseReference dbPedidos = FirebaseConfig.getFireBase().child("Pedidos");
        dbPedidos.child(pedido.getIdPedido()).removeValue();
        dbConversa.child(userKey).child(pedido.getIdPedido()).removeValue();
        dbConversa.child(pedido.getAtendenteId()).child(pedido.getIdPedido()).removeValue();
        Toast.makeText(getApplicationContext(), "Pedido Apagado", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

}
