package studio.brunocasamassa.ajudaquioficial.fragments;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;

import es.dmoral.toasty.Toasty;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.PedidosAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.PedidoActivity;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * A simple {@link Fragment} subclass.
 */

public class PedidosDisponiveisFragment extends Fragment {

    private static final String[] LOCATION_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION
    };

    private FloatingActionButton fab;
    private SwipeRefreshLayout refresh;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private ArrayList<Pedido> pedidos;
    public ArrayAdapter pedidosArrayAdapter;
    private ListView pedidosView;
    private DatabaseReference databasePedidos = FirebaseConfig.getFireBase().child("Pedidos");

    private User usuario = new User();

    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private PedidosActivity pa;

    private PedidosAdapter pedidosAdapter;
    private ArrayList<Pedido> pedidosPivot;
    private ImageView iconEmpty;

    public PedidosDisponiveisFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        if (pa.getArrayAdapter() != null) {
            pedidosArrayAdapter = pa.getArrayAdapter();
        }
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Preferences preferencias = new Preferences(getActivity().getApplicationContext());

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pedidos_disponiveis, container, false);
        pa = (PedidosActivity) getActivity();
        pedidos = new ArrayList<>();
        pedidosPivot = new ArrayList<>();
        iconEmpty = (ImageView) view.findViewById(R.id.icon_empty_list);
        pedidosView = (ListView) view.findViewById(R.id.allpedidos_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        pedidosAdapter = new PedidosAdapter(getContext(), pedidos);

        if (pa.getArrayAdapter() != null) {
            pedidosArrayAdapter = pa.getArrayAdapter();
        } else pedidosArrayAdapter = pedidosAdapter;

        System.out.println("inflei");
        pedidosView.setDivider(null);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                final Preferences filterPreferences = new Preferences(getActivity());
                filterPreferences.saveFilterPedido(null);
                refresh();
            }
        });

        pa.setArrayAdapter(pedidosArrayAdapter);

        pedidosView.setAdapter(pedidosArrayAdapter);

        final DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                System.out.println("entrei listenerSingle para messages");
                usuario.setPedidosFeitos(user.getPedidosFeitos());
                if (user.getMessageNotification() != null && !user.getMessageNotification().equals("no message")) {

                    //manual toast delay (nao me julgue)
                    for (int i = 0; i < 2; i++) {
                        Toasty.warning(getContext(), user.getMessageNotification(), Toast.LENGTH_LONG, true).show();
                    }

                    System.out.println("saindo listenerSingle para messages");
                    user.setMessageNotification("no message");
                    user.setId(userKey);
                    user.save();
                }


                databasePedidos.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        pedidos.clear();

                        Location userLocation = new Location("my location");
                        if (user.getLatitude() != null) {
                            userLocation.setLatitude(user.getLatitude());

                            userLocation.setLongitude(user.getLongitude());
                        } else {
                            userLocation.setLatitude(0.0);
                            userLocation.setLongitude(0.0);
                        }

                        System.out.println("PedidosLocation user locations " + user.getLatitude() + "  " + user.getLongitude());

                        for (DataSnapshot dados : dataSnapshot.getChildren()) {
                            System.out.println("get children pedidos " + dados);

                            Pedido pedido = dados.getValue(Pedido.class);
                            if (pedido.getLongitude() != null && pedido.getLatitude() != null) {
                                Location pedidoLocation = new Location(" pedido " + pedido.getTitulo() + " Location: ");
                                pedidoLocation.setLongitude(pedido.getLongitude());
                                pedidoLocation.setLatitude(pedido.getLatitude());

                                double distance = userLocation.distanceTo(pedidoLocation)/1000;

                                //double distance = distFrom(userLocation.getLatitude(), userLocation.getLongitude(), pedidoLocation.getLatitude(), pedidoLocation.getLongitude())/*userLocation.distanceTo(pedidoLocation)*/;

                                if( distance < 0 ) {distance = distance*-1;}

                                pedido.setDistanceInMeters(distance);

                                System.out.println("PedidosLocation DISTANCIA " + distance + "PEDIDO LATITUDE:  " + pedido.getLatitude() + " LONGITUDE: "+ pedido.getLongitude() + "NOME: "+pedido.getTitulo());

                            } else pedido.setDistanceInMeters(10.0);

                            System.out.println("PedidosLocation Distancia do pedido "+ pedido.getDistanceInMeters());

                            try {
                                if (pedido.getStatus() == 0) {
                                    pedidos.add(pedido);
                                    System.out.println("pedidao " + pedido.getTitulo());

                                    //elimina pedidos feitos pelo usuario
                                    if (usuario.getPedidosFeitos() != null) {
                                        if (usuario.getPedidosFeitos().contains(pedido.getIdPedido())) {
                                            System.out.println("pedidao " + pedido.getIdPedido());
                                            pedidos.remove(pedido);
                                        }

                                    }
                                        //Elimina pedidos fora do filtro
                                        if(preferencias.getFilterPedido() != null) {
                                            if (!pedido.getTipo().equals(preferencias.getFilterPedido())) {
                                            pedidos.remove(pedido);
                                        }
                                    }
                                    //elimina pedidos de outros grupos(caso tenha grupo no pedido)
                                    if (pedido.getGroupId() != null) {
                                        if(user.getGrupos() == null){
                                            pedidos.remove(pedido);
                                        }
                                        else if (!user.getGrupos().contains(pedido.getGroupId())){
                                            pedidos.remove(pedido);
                                        }

                                    }

                                    //elimina pedidos fora da distancia estabelecida
                                    if (pedido.getDistanceInMeters().intValue() > user.getMaxDistance()) {
                                        pedidos.remove(pedido);
                                        System.out.println("pedido removido " + pedido.getTitulo());

                                    }

                                    //pedidos sem distancia
                                    if (pedido.getDistanceInMeters() == null) {
                                        pedidos.remove(pedido);

                                    }

                                    //elimina doações da cabine
                                    if (pedido.getTipo().equals("Doacoes")) {
                                        pedidos.remove(pedido);

                                        System.out.println("pedido removido doacao" + pedido.getTitulo());
                                    }

                                }

                            } catch (Exception e) {
                                Log.e("listPedidos", e.toString());
                            }
                            //remover pedidos do usuario na lista de pedidos geral

                        }


                        pedidosArrayAdapter.notifyDataSetChanged();

                        Collections.sort(pedidos, new Comparator<Pedido>() {
                            @Override
                            public int compare(Pedido o1, Pedido o2) {

                                return o1.getDistanceInMeters().compareTo(o2.getDistanceInMeters());
                            }
                        });

                        /*if(pedidos.isEmpty()){
                            iconEmpty.setVisibility(View.VISIBLE);
                        } else {
                            iconEmpty.setVisibility(View.INVISIBLE);
                        }*/

                        pedidosView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                try {
                                    Intent intent = new Intent(getActivity(), PedidoActivity.class);

                                    // recupera dados a serem passados
                                    Pedido pedido = pedidosAdapter.getPedidosFiltrado().get(position);

                                    // enviando dados para pedido activity
                                    intent.putExtra("status", pedido.getStatus());
                                    intent.putExtra("titulo", pedido.getTitulo());
                                    intent.putExtra("tagsCategoria", pedido.getTagsCategoria());
                                    intent.putExtra("idPedido", pedido.getIdPedido());
                                    intent.putExtra("criadorId", pedido.getCriadorId());
                                    //intent.putExtra("distanceMeters", Integer.valueOf(String.valueOf(pedido.getDistanceInMeters())));
                                    intent.putExtra("tipo", pedido.getTipo());
                                    if(pedido.getTipo().equals("Doacao")){
                                        intent.putExtra("donationType", pedido.getDonationType());
                                    }
                                    if (pedido.getGrupo() != null) {
                                        intent.putExtra("tagsGrupo", pedido.getGrupo());
                                        intent.putExtra("groupId", pedido.getGroupId());
                                    }
                                    intent.putExtra("descricao", pedido.getDescricao());

                                    System.out.println("titulo enviando " + pedido.getTitulo() + "\n" + "grupo " + pedido.getGrupo() + "\n" + "desxcricao " + pedido.getDescricao() + "\n");
                                    startActivity(intent);
                                } catch (Exception e) {
                                    System.out.println("Exception grupos " + e);
                                }

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        pedidos.clear();

                    }

                })
                ;

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getContext(), databaseError.toString(), Toast.LENGTH_LONG).show();
                System.out.println("ERROR GET PEDIDOS USER STRINGS: " + databaseError);

            }


        });

        return view;

    }

    private void refresh() {
        Intent intent = new Intent(getActivity(), PedidosActivity.class);
        getActivity().finish();
        startActivity(intent);
    }



    private double distFrom(double lat1, double lng1, double lat2, double lng2) {

        double earthRadius = 6371000; //meters
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        float dist = (float) (earthRadius * c);

        return dist;


    }
}


