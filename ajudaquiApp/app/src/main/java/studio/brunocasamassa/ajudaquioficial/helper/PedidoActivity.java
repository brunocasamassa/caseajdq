package studio.brunocasamassa.ajudaquioficial.helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;

/**
 * Created by bruno on 04/06/2017.
 */

public class PedidoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView nomePedido;
    private TextView descricao;
    private TagGroup tagsCategoria;
    private TagGroup tagsGrupo;
    private Button atenderPedido;
    private Pedido pedido;
    private String userName;
    private String criadorId;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private User user = new User();
    private DatabaseReference firebase;
    private DatabaseReference dbUserDestinatario;
    private CircleImageView circleImgGroup;
    private TextView textNameGroup;

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        final Bundle extra = getIntent().getExtras();
        toolbar = (Toolbar) findViewById(R.id.toolbar_donation_activity);
        nomePedido = (TextView) findViewById(R.id.nome_doacao);
        descricao = (TextView) findViewById(R.id.descricao_doacao);
        circleImgGroup = (CircleImageView) findViewById(R.id.circle_img_group);
        textNameGroup = (TextView) findViewById(R.id.text_name_group);
        tagsCategoria = (TagGroup) findViewById(R.id.tags_pedido_categoria);
        tagsGrupo = (TagGroup) findViewById(R.id.tags_pedido_grupo);
        atenderPedido = (Button) findViewById(R.id.donation_button);

        Preferences preferencias = new Preferences(PedidoActivity.this);
        userName = preferencias.getNome();

        if (extra != null) {

            pedido = new Pedido();
            pedido.setIdPedido(extra.getString("idPedido"));
            pedido.setTagsCategoria(extra.getStringArrayList("tagsCategoria"));
            pedido.setDescricao(extra.getString("descricao"));
            pedido.setQtdDoado(extra.getInt("qtdDoado"));
            pedido.setQtdAtual(extra.getInt("qtdAtual"));
            pedido.setDonationContact(extra.getString("donationContact"));
            pedido.setEndereco(extra.getString("endereco"));
            pedido.setLongitude(extra.getDouble("longitude"));
            pedido.setLatitude(extra.getDouble("latitude"));
            pedido.setTitulo(extra.getString("titulo"));
            pedido.setGrupo(extra.getString("tagsGrupo"));
            pedido.setGroupId(extra.getString("groupId"));
            pedido.setStatus(extra.getInt("status"));
            pedido.setTipo(extra.getString("tipo"));
            pedido.setCriadorId(extra.getString("criadorId"));
            criadorId = pedido.getCriadorId();

            if(pedido.getTipo().equals("Doacao")){
                pedido.setDonationType(extra.getInt("donationType"));
            }

        }

        nomePedido.setText(pedido.getTitulo().toString());
        toolbar.setTitle("Detalhe do Pedido");


        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        descricao.setText(pedido.getDescricao());

        tagsCategoria.setTags(pedido.getTagsCategoria());

        if (pedido.getGrupo() != null) {
            insertGroupData(pedido.getGrupo(), pedido.getGroupId());
        }

            atenderPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoActivity.this);

                    alertDialog.setTitle("Atender Pedido");
                    alertDialog.setMessage("Deseja atender este pedido?");
                    alertDialog.setCancelable(false);

                    alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            final DatabaseReference firebase = FirebaseConfig.getFireBase().child("Pedidos");
                            firebase.child(pedido.getIdPedido());
                            System.out.println("pedido id " + pedido.getIdPedido());
                            firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    Pedido dbPedido = dataSnapshot.getValue(Pedido.class);
                                    System.out.println("dbPedido " + dbPedido.getTitulo());
                                    dbPedido.setCriadorId(pedido.getCriadorId());
                                    dbPedido.setDescricao(pedido.getDescricao());
                                    dbPedido.setIdPedido(pedido.getIdPedido());
                                    dbPedido.setStatus(1);
                                    dbPedido.setLatitude(pedido.getLatitude());
                                    dbPedido.setLongitude(pedido.getLongitude());
                                    dbPedido.setGrupo(pedido.getGrupo());
                                    dbPedido.setGroupId(pedido.getGroupId());
                                    dbPedido.setTagsCategoria(pedido.getTagsCategoria());
                                    dbPedido.setTipo(pedido.getTipo());
                                    dbPedido.setTitulo(pedido.getTitulo());
                                    dbPedido.setAtendenteId(userKey);

                                    if(dbPedido.getTipo().equals("Doacao")){
                                        dbPedido.setDonationType(pedido.getDonationType());
                                    }
                                    dbPedido.save();
                                    firebase.child(pedido.getIdPedido()).setValue(dbPedido);

                                    sendNotiication(pedido.getCriadorId());
                                    Toast.makeText(PedidoActivity.this, "Parabéns, voce já pode conversar com o criador do pedido.", Toast.LENGTH_LONG).show();
                                    finish();
                                    startActivity(new Intent(PedidoActivity.this, PedidosActivity.class));
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {
                                    System.out.println("error create atendimento " + databaseError);
                                }
                            });


                            DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");

                            dbUser.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    final User usuario = dataSnapshot.getValue(User.class);
                                    user = usuario;
                                    ArrayList<String> pedidosAtendidos = new ArrayList<String>();
                                    if (usuario.getPedidosAtendidos() != null) {
                                        pedidosAtendidos = usuario.getPedidosAtendidos();
                                        pedidosAtendidos.add(pedidosAtendidos.size(), extra.getString("idPedido"));
                                    } else pedidosAtendidos.add(0, extra.getString("idPedido"));

                                    usuario.setPedidosAtendidos(pedidosAtendidos);
                                    usuario.save();

                                    DateFormat formatter = new SimpleDateFormat("HH:mm");
                                    formatter.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
                                    String currentTime = formatter.format(new Date());
                                    System.out.println("formatter: " + currentTime);

                                    // salvando Conversa para o remetente
                                    Conversa conversa = new Conversa();
                                    conversa.setIdUsuario(criadorId);
                                    conversa.setNome(pedido.getTitulo());
                                    conversa.setMensagem("bem vindo");
                                    conversa.setTime(currentTime);
                                    Boolean retornoConversaRemetente = salvarConversa(userKey, criadorId, conversa);
                                    System.out.println("SALVANDO CONVERSA PARA O REMETENTE(atendente pedido): " + userKey);
                                    if (!retornoConversaRemetente) {
                                        System.out.println("PROBLEMA AO CRIAR CAMPO DE CONVERSA PARA O ATENDENTE");
                                    } else {

                                        // salvando Conversa para o Destinatario
                                        System.out.println("SALVANDO CONVERSA PARA O DESTINATARIO(criador pedido): " + criadorId);
                                        conversa = new Conversa();
                                        conversa.setIdUsuario(userKey);
                                        conversa.setNome(pedido.getTitulo());
                                        conversa.setMensagem("bem vindo");
                                        conversa.setTime(currentTime);

                                        dbUserDestinatario = FirebaseConfig.getFireBase().child("usuarios");

                                        dbUserDestinatario.child(criadorId).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                User user = dataSnapshot.getValue(User.class);
                                                user.setPedidosNotificationCount(user.getPedidosNotificationCount() + 1);
                                                user.setId(criadorId);


                                                user.save();
                                            }

                                            @Override
                                            public void onCancelled(DatabaseError databaseError) {

                                            }
                                        });

                                        Boolean retornoConversaDestinatario = salvarConversa(criadorId, userKey, conversa);
                                        if (!retornoConversaDestinatario) {
                                            System.out.println("PROBLEMA AO CRIAR CAMPO DE CONVERSA PARA O CRIADOR DO PEDIDO");
                                        }

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                        }
                    }).create().show();


                }
            });

    }

    private void insertGroupData(String grupo, String groupId) {

        StorageReference storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
        storage.child(groupId+".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).resize(68, 68).into(circleImgGroup);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Picasso.with(getApplicationContext()).load(R.drawable.add_group_logo).resize(68, 68).into(circleImgGroup);
            }
        });

        textNameGroup.setText(grupo.toString());

    }

    private void sendNotiication(String criadorId) {
    DatabaseReference dbDestinatario =FirebaseConfig.getFireBase().child("usuarios");
        dbDestinatario.child(criadorId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                Notification notifs = new Notification();
                notifs.setToken(user.getNotificationToken());
                notifs.setUid(user.getId());
                notifs.setMessage("Seu pedido '"+pedido.getTitulo()+"' foi atendido" );
                System.out.println("tiro tiro "+ pedido.getTitulo());
                notifs.setCommand("pedidos");
                notifs.setTitle("AJUDAQUI - Pedido Atendido");

                FirebaseConfig.getNotificationRef().child(user.getId()).setValue(notifs);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }



    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa) {
        try {
            firebase = FirebaseConfig.getFireBase().child("conversas");
            firebase.child(idRemetente)
                    .child(pedido.getIdPedido())
                    .setValue(conversa);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
