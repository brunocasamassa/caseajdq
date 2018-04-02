package studio.brunocasamassa.ajudaquioficial.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.ChatActivity;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;

/**
 * Created by bruno on 04/06/2017.
 */

public class PedidoAtendidoActivity extends AppCompatActivity {

    private int statusInt;
    private Toolbar toolbar;
    private TextView nomePedido;
    private TextView descricao;
    private TagGroup tagsCategoria;
    private int donationType;
    private TagGroup tagsGrupo;
    private String SUPPORT_EMAIL = "contato@ajudaqui.com.br";
    private ImageView statusImage;
    private Button cancelarButton;
    private Pedido pedido;
    private DatabaseReference dbPedidos = FirebaseConfig.getFireBase().child("Pedidos");
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private User user = new User();
    private DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");
    private ImageView chatImage;
    private CircleImageView circleImgGroup;
    private TextView textNameGroup;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_atendido);

        chatImage = (ImageView) findViewById(R.id.chat_pedido_atendido);
        statusImage = (ImageView) findViewById(R.id.status_image);
        circleImgGroup = (CircleImageView) findViewById(R.id.circle_img_group);
        textNameGroup = (TextView) findViewById(R.id.text_name_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar_pedido_atendido);
        nomePedido = (TextView) findViewById(R.id.nome_pedido_atendido);
        descricao = (TextView) findViewById(R.id.descricao_pedido_atendido);
        tagsCategoria = (TagGroup) findViewById(R.id.tags_pedido_categoria_atendido);
        tagsGrupo = (TagGroup) findViewById(R.id.tags_pedido_grupo_atendido);
        cancelarButton = (Button) findViewById(R.id.finalizar_pedido_button);

        Preferences preferences = new Preferences(PedidoAtendidoActivity.this);

        final String username = preferences.getNome();

        final Bundle extra = getIntent().getExtras();
        if (extra != null) {

            pedido = new Pedido();

            pedido.setIdPedido(extra.getString("idPedido"));
            pedido.setTagsCategoria(extra.getStringArrayList("tagsCategoria"));
            pedido.setDescricao(extra.getString("descricao"));
            pedido.setTitulo(extra.getString("titulo"));
            pedido.setGroupId(extra.getString("groupId"));
            pedido.setGrupo(extra.getString("tagsGrupo"));
            pedido.setStatus(extra.getInt("status"));
            pedido.setTipo(extra.getString("tipo"));
            pedido.setCriadorId(extra.getString("criadorId"));
            pedido.setAtendenteId(extra.getString("atendenteId"));


        }

        if (pedido.getTipo().equals("Doacao")) {
             donationType = extra.getInt("donationType");
            pedido.setDonationType(donationType);

            if(donationType == 0) {
                cancelarButton.setText("Confirmar Doação");
            }

            else if (donationType == 1){
                cancelarButton.setText("Cancelar Doação");
            }

        }
                chatImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference dbConversas = FirebaseConfig.getFireBase().child("conversas").child(userKey);
                dbConversas.child(pedido.getIdPedido()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Conversa conversa = dataSnapshot.getValue(Conversa.class);
                        Intent intent = new Intent(PedidoAtendidoActivity.this, ChatActivity.class);
                        intent.putExtra("nome", conversa.getNome());
                        String email = Base64Decoder.decoderBase64(conversa.getIdUsuario());
                        intent.putExtra("email", email);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        });


        if (pedido.getStatus() != 0) {
            int status = pedido.getStatus();
            System.out.println("status pedido " + pedido.getTitulo() + ": " + status);
            if (status == 0) {
                Glide.with(PedidoAtendidoActivity.this).load(R.drawable.tag_aberto).override(274, 274).into(statusImage);
            } else if (status == 1) {
                Glide.with(PedidoAtendidoActivity.this).load(R.drawable.tag_emandamento).override(274, 274).into(statusImage);
            } else if (status == 2) {
                Glide.with(PedidoAtendidoActivity.this).load(R.drawable.tag_finalizado).override(274, 274).into(statusImage);
            } else if (status == 3) {
                Glide.with(PedidoAtendidoActivity.this).load(R.drawable.tag_cancelado).override(274, 274).into(statusImage);
            }
        }

   /*     final NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);

        mBuilder.setSmallIcon(R.drawable.logo_pb);
        mBuilder.setContentTitle("Pedido Cancelado");
        mBuilder.setContentText("O Usuario " + username + " cancelou o pedido  " + pedido.getTitulo());

        Intent resultIntent = new Intent(this, PedidosActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(PedidosActivity.class);

        stackBuilder.addNextIntent(resultIntent);
        final PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, mBuilder.build());*/

        String title;
        try {
            title = pedido.getTitulo().substring(1, 14) + "...";
        } catch (Exception e) {
            title = pedido.getTitulo().toString();
        }

        String toolbarTitle = "Detalhe do Pedido";
        if(pedido.getTipo().equals("Doacao")){
            toolbarTitle = "Detalhe da Doação";
        }
        toolbar.setTitle(toolbarTitle);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        nomePedido.setText(pedido.getTitulo());

        descricao.setText(pedido.getDescricao());
        if (pedido.getTagsCategoria() != null) {
            tagsCategoria.setTags(pedido.getTagsCategoria());
        }
        if (pedido.getGrupo() != null) {
            insertGroupData(pedido.getGrupo(), pedido.getGroupId());
        }


        cancelarButton.setOnClickListener(new View.OnClickListener() {


                                              @Override
                                              public void onClick(View v) {
                                                  if (pedido.getTipo().equals("Doacao") && donationType == 0) {
                                                      AlertDialog.Builder confirmDialog = new AlertDialog.Builder(PedidoAtendidoActivity.this);
                                                      confirmDialog.setTitle("Confirmar Doação");
                                                      confirmDialog.setMessage("Você recebeu a doação?");

                                                      confirmDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              startStars(true);


                                                          }
                                                      }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              enviaEmailSuporte("Por favor, nos conte o que ocorreu: ", "DENUNCIA");
                                                          }
                                                      }).create().show();

                                                  } else {
                                                      AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoAtendidoActivity.this);

                                                      alertDialog.setTitle("Desistir do Pedido");
                                                      alertDialog.setMessage("Deseja desistir deste pedido de ajuda? você não receberá créditos ou pontos por ele");
                                                      alertDialog.setCancelable(false);

                                                      alertDialog.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                          }

                                                      });

                                                      alertDialog.setPositiveButton("Desistir", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {

                                                              pedido.setStatus(3);
                                                              pedido.setAtendenteId("");
                                                              pedido.save();

                                                              //apaga pedido do usuario logado
                                                              DatabaseReference dbUser = FirebaseConfig.getFireBase();
                                                              dbUser.child("usuarios").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                  @Override
                                                                  public void onDataChange(DataSnapshot dataSnapshot) {
                                                                      User user = dataSnapshot.getValue(User.class);
                                                                      final ArrayList<String> pedidosAtendidos = new ArrayList<>();

                                                                      pedidosAtendidos.addAll(user.getPedidosAtendidos());

                                                                      int targetPedidoToRemove = pedidosAtendidos.indexOf(pedido.getIdPedido());

                                                                      pedidosAtendidos.remove(targetPedidoToRemove);
                                                                      user.setChatNotificationCount(0);
                                                                      user.setPedidosAtendidos(pedidosAtendidos);
                                                                      user.setId(Base64Decoder.encoderBase64(user.getEmail()));
                                                                      user.save();

                                                                      //renova pedido do usuario criador
                                                                      DatabaseReference dbUser = FirebaseConfig.getFireBase();
                                                                      dbUser.child("usuarios").child(pedido.getCriadorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                                          @Override
                                                                          public void onDataChange(DataSnapshot dataSnapshot) {
                                                                              User user = dataSnapshot.getValue(User.class);
                                                                              user.setId(Base64Decoder.encoderBase64(user.getEmail()));
                                                                              user.setMessageNotification("O usuario " + username + " cancelou o pedido de ajuda de seu pedido '" + pedido.getTitulo() + "' voce pode alterar o status para aberto e procurar um novo ajudante");
                                                                              user.setChatNotificationCount(0);
                                                                              user.save();
                                                                              //change pedido status
                                                                              dbPedidos.child(pedido.getIdPedido()).child("status").setValue(3);
                                                                              //REMOVING CHAT FIELD
                                                                              dbConversa.child(userKey).child(pedido.getIdPedido()).removeValue();
                                                                              dbConversa.child(pedido.getCriadorId()).child(pedido.getIdPedido()).removeValue();


                                                                              Notification notifs = new Notification();
                                                                              notifs.setUsername(username);
                                                                              notifs.setEmail(Base64Decoder.decoderBase64(userKey));

                                                                              FirebaseConfig.getNotificationRef().child(user.getNotificationToken()).setValue(notifs);

                                                                              //FirebaseMessaging.getInstance().subscribeToTopic("Notifications");

                                                                          }

                                                                          @Override
                                                                          public void onCancelled(DatabaseError databaseError) {

                                                                          }
                                                                      });

                                                                  }

                                                                  @Override
                                                                  public void onCancelled(DatabaseError databaseError) {

                                                                  }
                                                              });

                                                              Toast.makeText(PedidoAtendidoActivity.this, "Voce Desistiu do pedido", Toast.LENGTH_LONG).show();
                                                              finish();

                                                          }
                                                      }).create().show();

                                                /*  FirebaseMessaging fm = FirebaseMessaging.getInstance();
                                                  fm.send(new RemoteMessage.Builder(userKey + "https://ajudaqui-d58a0.firebaseio.com/").setMessageId(Integer.toString(msgId.incrementAndGet()))
                                                          .addData("my_message","O Usuario " + username + " cancelou o pedido  " + pedido.getTitulo() )
                                                          .addData("my_action", resultPendingIntent)
                                                          .build());*/


                                                  }
                                              }
                                          }


        );

    }

    private void enviaEmailSuporte(String titulo, final String tipo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoAtendidoActivity.this);

        alertDialog.setTitle(titulo);
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(PedidoAtendidoActivity.this);
        alertDialog.setView(editText);

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        alertDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String mensagemSolicitacao = editText.getText().toString();

                //Validate message to contact
                if (mensagemSolicitacao.isEmpty()) {
                    Toast.makeText(PedidoAtendidoActivity.this, "Preencha o campo de mensagem", Toast.LENGTH_LONG).show();
                } else {
                    Preferences preferences = new Preferences(PedidoAtendidoActivity.this);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{SUPPORT_EMAIL});
                    i.putExtra(Intent.EXTRA_SUBJECT, "AJUDAQUI: "+tipo+": USUARIO: " + preferences.getNome());
                    i.putExtra(Intent.EXTRA_TEXT,
                            " " + Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail()) + "\n" +
                                    "username: " + preferences.getNome() + "\n" +
                                    "Order ID:" + pedido.getIdPedido() +"\n" +
                                    "user Creator ID" + pedido.getCriadorId() + "\n" +
                                    "usermail: " + FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail()+ "\n" +
                                    "\n" + mensagemSolicitacao);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                        Toast.makeText(PedidoAtendidoActivity.this, "Obrigado pela mensagem, entraremos em contato o mais breve possível", Toast.LENGTH_SHORT).show();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(PedidoAtendidoActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).create().show();

    }

    private void startStars(boolean trigger) {

        if (trigger) {

            final DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");
            final AlertDialog.Builder builder = new AlertDialog.Builder(PedidoAtendidoActivity.this);
            final AlertDialog alertDialog = builder.create();
            View title = ((LayoutInflater) PedidoAtendidoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rating_title, null);
            View root = ((LayoutInflater) PedidoAtendidoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rating, null);
            alertDialog.setCustomTitle(title);
            final RatingBar ratingBar = (RatingBar) root.findViewById(R.id.ratingbar);
            final Button enviar = (Button) root.findViewById(R.id.buttonEnviar);

            enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference atendenteUser = FirebaseConfig.getFireBase().child("usuarios");
                    final int rating = (int) (ratingBar.getRating());
                    atendenteUser.child(pedido.getCriadorId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.setPontos(user.getPontos() + rating);
                            //Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_SHORT).show();
                            user.setCreditos(user.getCreditos() + 1);
                            user.setId(pedido.getCriadorId());
                            user.save();

                            apagaPedido(pedido);

                            finish();
                            startActivity(new Intent(PedidoAtendidoActivity.this, PedidosActivity.class));
                            Toast.makeText(getApplicationContext(), "Pedido finalizado com sucesso", Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                    alertDialog.cancel();

                }
            });
            alertDialog.setView(root);
            alertDialog.show();
            // alertDialog.setIcon(R.drawable.logo_big);


        }

    }

    private void apagaPedido(Pedido pedido) {
        DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");
        DatabaseReference dbPedidos = FirebaseConfig.getFireBase().child("Pedidos");
        dbPedidos.child(pedido.getIdPedido()).child("status").setValue(2);
       /* dbConversa.child(userKey).child(pedido.getIdPedido()).removeValue();
        dbConversa.child(pedido.getAtendenteId()).child(pedido.getIdPedido()).removeValue();*/
        finish();
    }
    private void insertGroupData(String grupo, String groupId) {

        StorageReference storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
        storage.child(groupId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
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

}
