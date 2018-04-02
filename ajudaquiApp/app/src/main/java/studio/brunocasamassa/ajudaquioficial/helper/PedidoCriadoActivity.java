package studio.brunocasamassa.ajudaquioficial.helper;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
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

import de.hdodenhof.circleimageview.CircleImageView;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.ChatActivity;
import studio.brunocasamassa.ajudaquioficial.ConversasActivity;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.R;

/**
 * Created by bruno on 04/06/2017.
 */

public class PedidoCriadoActivity extends AppCompatActivity {

    private int statusInt;
    private Toolbar toolbar;
    private ConstraintLayout my_layout;
    private TextView nomePedido;
    private TextView descricao;
    private ImageView pedidoChat;
    private TagGroup tagsCategoria;
    private int donationType;
    private TagGroup tagsGrupo;
    private ImageView statusImage;
    private Button finalizarPedido;
    private Pedido pedido;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private User user = new User();
    private String keyAtendente;
    private boolean trigger = true;
    private CircleImageView circleImgGroup;
    private TextView textNameGroup;

    @Override
    protected void onStart() {
        super.onStart();

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido_criado);

        my_layout = (ConstraintLayout) findViewById(R.id.layout_my_pedido);
        pedidoChat = (ImageView) findViewById(R.id.chat_pedido);
        statusImage = (ImageView) findViewById(R.id.status_image_criado);
        circleImgGroup = (CircleImageView) findViewById(R.id.circle_img_group);
        textNameGroup = (TextView) findViewById(R.id.text_name_group);
        toolbar = (Toolbar) findViewById(R.id.toolbar_pedido_criado);
        nomePedido = (TextView) findViewById(R.id.nome_pedido_criado);
        descricao = (TextView) findViewById(R.id.descricao_pedido_criado);
        tagsCategoria = (TagGroup) findViewById(R.id.tags_pedido_categoria_criado);
        tagsGrupo = (TagGroup) findViewById(R.id.tags_pedido_grupo_criado);
        finalizarPedido = (Button) findViewById(R.id.finalizar_pedido_criado);

        final Bundle extra = getIntent().getExtras();
        if (extra != null) {

            pedido = new Pedido();
            pedido.setIdPedido(extra.getString("idPedido"));
            pedido.setTagsCategoria(extra.getStringArrayList("tagsCategoria"));
            pedido.setDescricao(extra.getString("descricao"));
            pedido.setTitulo(extra.getString("titulo"));
            pedido.setGrupo(extra.getString("tagsGrupo"));
            pedido.setGroupId(extra.getString("groupId"));
            pedido.setStatus(extra.getInt("status"));
            pedido.setTipo(extra.getString("tipo"));
            pedido.setCriadorId(extra.getString("criadorId"));
            pedido.setAtendenteId(extra.getString("atendenteId"));
            if(pedido.getTipo().equals("Doacao")){
                donationType = extra.getInt("donationType");
                pedido.setDonationType(donationType);
            }

        }



        pedidoChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference dbConversas = FirebaseConfig.getFireBase().child("conversas").child(userKey);
                dbConversas.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //TO CHAT ACTIVITY
                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                            Conversa conversa = post.getValue(Conversa.class);
                            if (Base64Decoder.encoderBase64(conversa.getNome()).equals(pedido.getIdPedido())) {
                                Intent intent = new Intent(PedidoCriadoActivity.this, ChatActivity.class);
                                intent.putExtra("nome", conversa.getNome());
                                String email = Base64Decoder.decoderBase64(conversa.getIdUsuario());
                                intent.putExtra("email", email);
                                startActivity(intent);
                                finish();
                            } else {
                                //TO CONVERSAS ACTIVITY
                                startActivity(new Intent(PedidoCriadoActivity.this, ConversasActivity.class));
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

        descricao.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoCriadoActivity.this);

                alertDialog.setTitle("Descrição do pedido");
                alertDialog.setMessage("Escreva abaixo a nova descrição");
                alertDialog.setCancelable(false);

                final EditText editText = new EditText(PedidoCriadoActivity.this);
                alertDialog.setView(editText);

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setPositiveButton("Alterar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        pedido.setDescricao(editText.getText().toString());
                        pedido.save();
                        Toast.makeText(getApplicationContext(), "Descrição alterada com sucesso", Toast.LENGTH_SHORT).show();
                    }
                }).create().show();
                return false;
            }
        });

/*        statusImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoCriadoActivity.this);

                alertDialog.setTitle("Editar Pedido");
                alertDialog.setMessage("Deseja alterar o status deste pedido?");
                alertDialog.setCancelable(false);

                alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (pedido.getStatus() == 0) {
                            Toast.makeText(getApplication(), "Pedidos abertos nao podem ser alterados", Toast.LENGTH_SHORT).show();
                        } else {
                            AlertDialog.Builder selectStatus = new AlertDialog.Builder(PedidoCriadoActivity.this);
                            selectStatus.setTitle("Selecione o novo status do pedido");
                            selectStatus.setNegativeButton("Cancelar:", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            selectStatus.setItems(new CharSequence[]
                                            {"Aberto", "Finalizado"},
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // The 'which' argument contains the index position
                                            // of the selected item
                                            switch (which) {
                                                case 0:
                                                    pedido.setStatus(0);
                                                    Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_aberto).into(statusImage);
                                                    pedido.save();
                                                    break;
                                                case 1:
                                                    pedido.setStatus(2);
                                                    Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_finalizado).into(statusImage);
                                                    pedido.save();
                                                    break;
                                            }
                                        }
                                    }).create().show();

                            selectStatus.setCancelable(false);
                        }
                    }

                }).create().show();

                return false;
            }
        });*/

        if (pedido.getStatus() != 0) {
            int status = pedido.getStatus();
            System.out.println("status pedido " + pedido.getTitulo() + ": " + status);
            if (status == 0) {
                Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_aberto).override(274, 274).into(statusImage);
            } else if (status == 1) {
                Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_emandamento).override(274, 274).into(statusImage);
            } else if (status == 2) {
                Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_finalizado).override(274, 274).into(statusImage);
            } else if (status == 3) {
                Glide.with(PedidoCriadoActivity.this).load(R.drawable.tag_cancelado).override(274, 274).into(statusImage);
            }
        }

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

        if (pedido.getStatus() == 3) {
            finalizarPedido.setText("LIBERAR PEDIDO");
            finalizarPedido.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoCriadoActivity.this);

                    alertDialog.setTitle("Liberar Pedido");
                    alertDialog.setMessage("Deseja tornar este pedido aberto novamente?");
                    alertDialog.setCancelable(false);

                    alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openPedidoStatus();
                            finish();
                            startActivity(new Intent(PedidoCriadoActivity.this, PedidosActivity.class));

                        }
                    }).create().show();
                }
            });

        } else {
            final String finalDialogTitle = "Finalizar Pedido";
            finalizarPedido.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    if (pedido.getStatus() == 0) {
                        Toast.makeText(getApplicationContext(), "Voce nao pode finalizar um pedido aberto", Toast.LENGTH_SHORT).show();

                    } else {

                        AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidoCriadoActivity.this);
                        alertDialog.setTitle(finalDialogTitle);
                        alertDialog.setMessage("Deseja finalizar este pedido?");
                        alertDialog.setCancelable(false);

                        alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });

                        alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (pedido.getTipo().equals("Doacao") && donationType==0) {

                                Toast.makeText(getApplicationContext(), "Você nao pode finalizar uma doação sua, esta ação é feita " +
                                        "apenas por quem a receberá! ",Toast.LENGTH_LONG).show();

                                }
                                else {startStars(trigger);}


                            }
                        }).create().show();


/*
                Toast.makeText(PedidoCriadoActivity.this, "Em processo", Toast.LENGTH_LONG).show();
*/
                    }
                }
            });

        }
    }

    private void openPedidoStatus() {
        DatabaseReference dbPedido = FirebaseConfig.getFireBase().child("Pedidos");
        dbPedido.child(pedido.getIdPedido()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Pedido pedido = dataSnapshot.getValue(Pedido.class);
                pedido.setStatus(0);
                pedido.save();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void startStars(boolean trigger) {

        if (trigger) {

            final DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");
            final AlertDialog.Builder builder = new AlertDialog.Builder(PedidoCriadoActivity.this);
            final AlertDialog alertDialog = builder.create();
            View title = ((LayoutInflater) PedidoCriadoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rating_title, null);
            View root = ((LayoutInflater) PedidoCriadoActivity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.rating, null);
            alertDialog.setCustomTitle(title);
            final RatingBar ratingBar = (RatingBar) root.findViewById(R.id.ratingbar);
            final Button enviar = (Button) root.findViewById(R.id.buttonEnviar);

            enviar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference atendenteUser = FirebaseConfig.getFireBase().child("usuarios");
                    final int rating = (int) (ratingBar.getRating());
                    atendenteUser.child(pedido.getAtendenteId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            user.setPontos(user.getPontos() + rating);
                            //Toast.makeText(getApplicationContext(), String.valueOf(rating), Toast.LENGTH_SHORT).show();
                            user.setCreditos(user.getCreditos() + 1);
                            user.setId(pedido.getAtendenteId());
                            user.save();

                            pedido.setStatus(2);
                            pedido.save();
                            finish();
                            startActivity(new Intent(PedidoCriadoActivity.this, PedidosActivity.class));
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

    private void insertGroupData(String grupo, final String groupId) {

        StorageReference storage = FirebaseConfig.getFirebaseStorage().child("groupImages");
        storage.child(groupId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri).resize(68, 68).into(circleImgGroup);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //Toast.makeText(getApplicationContext(), groupId + e.toString(),Toast.LENGTH_LONG).show();
                Picasso.with(getApplicationContext()).load(R.drawable.add_group_logo).resize(68, 68).into(circleImgGroup);
            }
        });

        textNameGroup.setText(grupo.toString());

    }
}

