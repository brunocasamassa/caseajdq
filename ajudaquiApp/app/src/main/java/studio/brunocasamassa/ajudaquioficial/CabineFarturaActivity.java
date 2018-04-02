package studio.brunocasamassa.ajudaquioficial;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import studio.brunocasamassa.ajudaquioficial.adapters.PedidosAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 05/07/2017.
 */

public class CabineFarturaActivity extends AppCompatActivity {
    private ListView doacoes;
    private Toolbar toolbar;
    private ArrayList<Pedido> listaDoacoes;
    private ArrayAdapter doacoesArrayAdapter;
    private FloatingActionButton fab;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private DatabaseReference dbPedidos = FirebaseConfig.getFireBase().child("Pedidos");
    private DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cabine_fartura);

        toolbar = (Toolbar) findViewById(R.id.toolbar_cabine_fartura);
        toolbar.setTitle("Cabine da Fartura");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        listaDoacoes = new ArrayList<>();

        doacoes = (ListView) findViewById(R.id.cabine_fartura_list);

        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                dbPedidos.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        Location userLocation = new Location("my location");
                        if(user.getLatitude() != null) {
                            userLocation.setLatitude(user.getLatitude() / 1e6);
                            userLocation.setLongitude(user.getLongitude()/ 1e6);
                            System.out.println("my location Lat> " + user.getLatitude() + "  LOn> " + user.getLongitude());
                        } else {
                            userLocation.setLatitude(0.0);
                            userLocation.setLongitude(0.0);
                        }
                        for (DataSnapshot pedidos : dataSnapshot.getChildren()) {
                            Pedido pedido = pedidos.getValue(Pedido.class);
                            System.out.println("PEDIDO " + pedido.getTipo() + "  " + pedido.getNaCabine());
                            if (pedido.getLongitude() != null && pedido.getLatitude() != null) {
                                Location pedidoLocation = new Location(" pedido " + pedido.getTitulo() + " Location: ");
                                pedidoLocation.setLongitude(pedido.getLongitude()/ 1e6);
                                pedidoLocation.setLatitude(pedido.getLatitude()/ 1e6);
                                System.out.println("doacao location Lat> " + pedidoLocation.getLatitude() + "  LOn> " + pedidoLocation.getLongitude());

                                double distance = distFrom(userLocation.getLatitude(),userLocation.getLongitude(),pedidoLocation.getLatitude(),pedidoLocation.getLongitude());

                                pedido.setDistanceInMeters(distance);
                                System.out.println("distance in meters 2" + distance);
                                System.out.println("distance in meters 2" + pedido.getDistanceInMeters());

                            }
                            if (pedido.getTipo().equals("Doacoes") && pedido.getNaCabine() == 1 && !pedido.getCriadorId().equals(userKey)) {
                                if (pedido.getQtdAtual() > 0) {
                                    if (!listaDoacoes.isEmpty()) {
                                        System.out.println("doacoes adicioandas " + pedido.getTitulo());
                                        System.out.println("doacoes adicioandas " + pedido.getDistanceInMeters());
                                        listaDoacoes.add(listaDoacoes.size(), pedido);

                                    } else listaDoacoes.add(0, pedido);
                                }
                            }

                        }

                        System.out.println(listaDoacoes.size());
                        Collections.sort(listaDoacoes, new Comparator<Pedido>() {
                            @Override
                            public int compare(Pedido o1, Pedido o2) {
                                if (o1.getDistanceInMeters() == null) {
                                    listaDoacoes.remove(o1);
                                } else if (o2.getDistanceInMeters() == null) {
                                    listaDoacoes.remove(o2);
                                }

                                return o1.getDistanceInMeters().compareTo(o2.getDistanceInMeters());
                            }
                        });

                        doacoesArrayAdapter = new PedidosAdapter(CabineFarturaActivity.this, listaDoacoes);

                        if (!listaDoacoes.isEmpty()) {
                            doacoes.setAdapter(doacoesArrayAdapter);

                            doacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    try {
                                        Intent intent = new Intent(CabineFarturaActivity.this, DoacaoCriadaActivity.class);

                                        //recupera dados a serem passados
                                        Pedido pedido = listaDoacoes.get(position);

                                        // enviando dados para pedido activity
                                        intent.putExtra("status", pedido.getStatus());
                                        intent.putExtra("titulo", pedido.getTitulo());
                                        intent.putExtra("tagsCategoria", pedido.getTagsCategoria());
                                        intent.putExtra("idPedido", pedido.getIdPedido());
                                        intent.putExtra("latitude", pedido.getLatitude());
                                        intent.putExtra("longitude", pedido.getLongitude());
                                        intent.putExtra("qtdAtual", pedido.getQtdAtual());
                                        intent.putExtra("qtdDoado", pedido.getQtdDoado());
                                        intent.putExtra("criadorId", pedido.getCriadorId());
                                        intent.putExtra("tipo", pedido.getTipo());
                                        intent.putExtra("dadosDoador", pedido.getDadosDoador());
                                        intent.putExtra("endereco", pedido.getEndereco());
                                        intent.putExtra("donationContact", pedido.getDonationContact());
                                        intent.putExtra("cameFrom", 2);

                                        if (pedido.getGrupo() != null) {
                                            intent.putExtra("tagsGrupo", pedido.getGrupo());
                                        }

                                        intent.putExtra("descricao", pedido.getDescricao());

                                        System.out.println("titulo enviando " + pedido.getTitulo() + "\n" + "grupo " + pedido.getGrupo() + "\n" + "desxcricao " + pedido.getDescricao() + "\n");
                                        startActivity(intent);
                                    } catch (Exception e) {
                                        System.out.println("Exception grupos " + e);
                                    }

                                }
                            });
                        } else
                            Toast.makeText(getApplication(), " Nao existem doações disponiveis no momento", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                fab = (FloatingActionButton) findViewById(R.id.fab_cabine);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(CabineFarturaActivity.this, CriaDoacaoNaCabineActivity.class);
                        intent.putExtra("latitude", user.getLatitude());
                        intent.putExtra("longitude", user.getLongitude());
                        startActivity(intent);
                    }
                });
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

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
