package studio.brunocasamassa.ajudaquioficial;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import co.lujun.androidtagview.TagContainerLayout;
import co.lujun.androidtagview.TagView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 22/05/2017.
 */

public class CriaPedidoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText pedidoName;
    private EditText descricao;
    private Button createButton;
    private String tagCaptured;
    ArrayList<String> tagsCaptured = new ArrayList<String>();
    private TagContainerLayout categorias;
    private TagContainerLayout grupos;
    private TextView add_grupos;
    private Double latitude;
    private Double longitude;
    private String pedidoGroup;
    private TextView add_tags;
    private Pedido pedido;
    private int creditos;
    private View constraintLayout;
    private int premium;
    private ImageButton addTagButton;
    private ImageButton addGroupButton;
    private String tipoPedido;   //doacao, servico, troca, emprestimo
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private String groupCaptured;
    private String idGroupSelected;
    private String groupCapturedId;
    private CardView cardview;
    private String groupConcat;
    private int donationType;

    @Override
    protected void onResume() {
        super.onResume();
        Bundle extras = getIntent().getExtras();
        idGroupSelected = (extras.getString("groupId"));
        pedidoGroup = (extras.getString("groupName"));

        if (pedidoGroup != null) {

            System.out.println("pedido grupo no resume" + pedidoGroup);
            System.out.println("pedido grupo no resume" + idGroupSelected);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        System.out.println("vamos ver " + tagCaptured);
        if (tagsCaptured.size() > 3) {
            Toast.makeText(getApplicationContext(), "Apenas 3 categorias podem ser selecionadas", Toast.LENGTH_SHORT).show();
            tagsCaptured.remove(tagCaptured);
        } else categorias.setTags(tagsCaptured);

        if (pedidoGroup != null) {
            if (pedidoGroup.length() > 13) {
                groupConcat = pedidoGroup.substring(0, 13);
                grupos.setTags(groupConcat);
            } else grupos.setTags(pedidoGroup);

        } else if (groupCaptured != null) {
            grupos.setTags(groupConcat);
            pedidoGroup = groupCaptured;
            idGroupSelected = groupCapturedId;
        }

        System.out.println("vamos ver " + groupCaptured);
        System.out.println("vamos ver " + idGroupSelected);

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_pedido);


        final Bundle extras = getIntent().getExtras();
        premium = (extras.getInt("premium"));
        creditos = (extras.getInt("creditos"));
        System.out.println("PREMIUM RECEBE" + premium);

        latitude = (extras.getDouble("latitude"));
        longitude = (extras.getDouble("longitude"));
        pedidoGroup = (extras.getString("groupName"));
        if (extras.getString("idGroupSelected") != null) {
            idGroupSelected = (extras.getString("idGroupSelected"));
        } else if (extras.getString("groupId") != null) {
            idGroupSelected = (extras.getString("groupId"));
        }

        System.out.println("FUCK latitude  " + latitude);
        System.out.println("FUCK longitude " + longitude);

        cardview = (CardView) findViewById(R.id.cardview_create_doacao);
        toolbar = (Toolbar) findViewById(R.id.toolbar_create_group);
        toolbar.setTitle("Criar Pedido");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        categorias = (TagContainerLayout) findViewById(R.id.tagGroupCategoria);
        grupos = (TagContainerLayout) findViewById(R.id.tagGroupGrupos);
      /*  grupos.setBackgroundColor(Color.TRANSPARENT);
        grupos.setBackgroundResource(R.drawable.logo);*/

        add_grupos = (TextView) findViewById(R.id.textView5);
        add_tags = (TextView) findViewById(R.id.word_add_tags);
        pedidoName = (EditText) findViewById(R.id.create_pedido_name);
        descricao = (EditText) findViewById(R.id.create_pedido_description);
        createButton = (Button) findViewById(R.id.create_pedido_button);
        addTagButton = (ImageButton) findViewById((R.id.add_tag_button));
        addGroupButton = (ImageButton) findViewById((R.id.addGroup_tag_button));


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        // Toast.makeText(getApplicationContext(), "width "+ width + " height "+ height , Toast.LENGTH_SHORT).show();

        final int currentapiVersion = android.os.Build.VERSION.SDK_INT;

        if (currentapiVersion <= 19) {

            TextView descr = (TextView) findViewById(R.id.textView6);
            ImageView v = (ImageView) findViewById(R.id.imageView10);
            ImageView w = (ImageView) findViewById(R.id.imageView11);
            ImageView x = (ImageView) findViewById(R.id.imageView12);
            cardview.setTranslationY(Float.valueOf(-58));
            categorias.setTagTextSize(15);
            grupos.setTagTextSize(30);
            descricao.setTranslationY(Float.valueOf(-55));
            add_grupos.setTranslationY(Float.valueOf(-55));
            add_tags.setTranslationY(Float.valueOf(-55));
            addGroupButton.setTranslationY(Float.valueOf(-55));
            addTagButton.setTranslationY(Float.valueOf(-55));
            x.setTranslationY(Float.valueOf(-55));
            w.setTranslationY(Float.valueOf(-55));
            v.setTranslationY(Float.valueOf(-55));
            descr.setTranslationY(Float.valueOf(-55));
            grupos.setTranslationY(Float.valueOf(-55));
            categorias.setTranslationY(Float.valueOf(-55));

        } else {


        }

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        grupos.setBackgroundColor(Color.TRANSPARENT);
        grupos.setBorderColor(Color.TRANSPARENT);
        grupos.setOnTagClickListener(new TagView.OnTagClickListener() {
            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                grupos.removeTag(position);
                idGroupSelected = null;
                pedidoGroup = null;


            }
        });
        categorias.setBorderColor(Color.TRANSPARENT);
        categorias.setBackgroundColor(Color.TRANSPARENT);
        categorias.setOnTagClickListener(new TagView.OnTagClickListener() {

            @Override
            public void onTagClick(int position, String text) {

            }

            @Override
            public void onTagLongClick(int position, String text) {

            }

            @Override
            public void onTagCrossClick(int position) {
                categorias.removeTag(position);
                tagsCaptured.remove(position);

            }
        });

        add_grupos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (premium == 0) {
                    Toast.makeText(getApplicationContext(), "Conteudo exclusivo para usuarios Premium, adquira para utilizar o recurso de grupos", Toast.LENGTH_LONG).show();
                } else
                    startActivityForResult(new Intent(CriaPedidoActivity.this, PedidoAddGroupsList.class), 2);

            }
        });

        addGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (premium == 0) {
                    Toast.makeText(getApplicationContext(), "Conteudo exclusivo para usuarios Premium, adquira para utilizar o recurso de grupos", Toast.LENGTH_LONG).show();
                } else
                    startActivityForResult(new Intent(CriaPedidoActivity.this, PedidoAddGroupsList.class), 2);
            }
        });

        add_tags.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CriaPedidoActivity.this, TagsList.class), 1);

            }
        });

        addTagButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(CriaPedidoActivity.this, TagsList.class), 1);

            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {

               /* if(creditos <=0){
                System.out.println("user creditos "+creditos);
                    Toast.makeText(getApplicationContext(), "Você não possui creditos suficientes para criar um pedido", Toast.LENGTH_LONG).show();
                    return;
                }
                else*/
                if (pedidoName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira um Titulo para o pedido", Toast.LENGTH_LONG).show();
                    return;
                } else if (descricao.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira uma descricao para o pedido", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CriaPedidoActivity.this);

                    alertDialog.setTitle("Selecione o tipo de Pedido que deseja efetuar");
                    alertDialog.setCancelable(false);

                    alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    alertDialog.setItems(new CharSequence[]
                                    {"Serviços", "Emprestimos", "Trocas", "Doações"},
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // The 'which' argument contains the index position
                                    // of the selected item
                                    switch (which) {
                                        case 0:
                                            // Toast.makeText(getApplicationContext(), "Servicos", Toast.LENGTH_SHORT).show();
                                            tipoPedido = "Servicos";
                                            createPedido();
                                            break;
                                        case 1:
                                            // Toast.makeText(getApplicationContext(), "Emprestimos", Toast.LENGTH_SHORT).show();
                                            tipoPedido = "Emprestimos";
                                            createPedido();
                                            break;
                                        case 2:
                                            //  Toast.makeText(getApplicationContext(), "Troca", Toast.LENGTH_SHORT).show();
                                            tipoPedido = "Troca";
                                            createPedido();
                                            break;
                                        case 3:
                                            // Toast.makeText(getApplicationContext(), "finalizando processo de backend, selecione outra opção", Toast.LENGTH_SHORT).show();
                                            tipoPedido = "Doacao";
                                            final AlertDialog.Builder donationAlert = new AlertDialog.Builder(CriaPedidoActivity.this);
                                            donationAlert.setTitle("Você esta pedindo ou oferecendo esta doação?");
                                            donationAlert.setItems(new CharSequence[]
                                                    {"Oferecendo", "Pedindo"}, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    switch (which) {
                                                        case 0:
                                                            donationType = 0;
                                                            createPedido();
                                                            break;
                                                        case 1:
                                                            donationType = 1;
                                                            createPedido();
                                                            break;
                                                    }
                                                }
                                            }).create().show();
                                            break;
                                    }
                                }
                            });

                    alertDialog.create();
                    alertDialog.show();

                }
            }

        });

    }

    private void createPedido() {
        pedido = new Pedido();
        if (tipoPedido.equals("Doacao")) {
            pedido.setDonationType(donationType);

        }

        Random randomizer = new Random();
        int r = randomizer.nextInt(1000);

        System.out.println("tipo de pedido: " + tipoPedido);
        pedido.setTagsCategoria(tagsCaptured);
        pedido.setTipo(tipoPedido);

        pedido.setDescricao(descricao.getText().toString());
        pedido.setTitulo(pedidoName.getText().toString());
        pedido.setIdPedido(Base64Decoder.encoderBase64(pedido.getTitulo()));
        pedido.setCriadorId(userKey);

        if (latitude == null) {
            pedido.setLatitude(0.0);
        } else {
            pedido.setLatitude(latitude);
        }
        if (longitude == null) {
            pedido.setLongitude(0.0);
        } else {
            pedido.setLongitude(longitude);
        }
        if (pedidoGroup != null) {
            pedido.setGrupo(pedidoGroup);
        } else if (groupCaptured != null) {
            pedido.setGrupo(groupCaptured);
        }
        if (idGroupSelected != null) {
            pedido.setGroupId(idGroupSelected);
            savePedidoIntoGroup(pedido);
        } else if (groupCapturedId != null) {
            pedido.setGroupId(groupCapturedId);
            savePedidoIntoGroup(pedido);
        }

        System.out.println("user id key " + userKey);

        pedido.save();
        pedidoSaveIntoUser(true);
        Toast.makeText(getApplicationContext(), "Pedido criado com sucesso", Toast.LENGTH_LONG).show();
        refresh();

    }

    private void savePedidoIntoGroup(final Pedido pedido) {
        final String tipoPedido = pedido.getTipo();
        final ArrayList<String> pedidosList = new ArrayList<>();
        DatabaseReference dbGroup = FirebaseConfig.getFireBase().child("grupos").child(pedido.getGroupId());
        dbGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                if (tipoPedido.equals("Emprestimos")) {
                    if (grupo.getEmprestimos() != null) {
                        pedidosList.addAll(grupo.getEmprestimos());
                        pedidosList.add(pedidosList.size(), pedido.getIdPedido());
                        grupo.setEmprestimos(pedidosList);
                    } else {
                        pedidosList.add(0, pedido.getIdPedido());
                        grupo.setEmprestimos(pedidosList);
                    }

                }
                if (tipoPedido.equals("Troca")) {
                    if (grupo.getTrocas() != null) {
                        pedidosList.addAll(grupo.getTrocas());
                        pedidosList.add(pedidosList.size(), pedido.getIdPedido());
                        grupo.setTrocas(pedidosList);
                    } else {
                        pedidosList.add(0, pedido.getIdPedido());
                        grupo.setTrocas(pedidosList);
                    }

                }

                if (tipoPedido.equals("Servicos")) {
                    if (grupo.getServicos() != null) {
                        pedidosList.addAll(grupo.getServicos());
                        pedidosList.add(pedidosList.size(), pedido.getIdPedido());
                        grupo.setServicos(pedidosList);
                    } else {
                        pedidosList.add(0, pedido.getIdPedido());
                        grupo.setServicos(pedidosList);
                    }

                }
                if (tipoPedido.equals("Doacoes")) {
                    if (grupo.getDoacoes() != null) {
                        pedidosList.addAll(grupo.getDoacoes());
                        pedidosList.add(pedidosList.size(), pedido.getIdPedido());
                        grupo.setDoacoes(pedidosList);
                    } else {
                        pedidosList.add(0, pedido.getIdPedido());
                        grupo.setDoacoes(pedidosList);
                    }

                }

                grupo.save();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("erro database " + databaseError);

            }
        });

    }

    ;

    private void pedidoSaveIntoUser(boolean b) {
        if (b) {
            DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
            databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                    user.setCreditos(user.getCreditos() - 1);
                    ArrayList<String> totalPedidos = new ArrayList<String>();
                    if (user.getPedidosFeitos() != null) {
                        totalPedidos.addAll(user.getPedidosFeitos());
                        totalPedidos.add(totalPedidos.size(), pedido.getIdPedido());
                        user.setPedidosFeitos(totalPedidos);
                    } else {
                        totalPedidos.add(0, pedido.getIdPedido());
                        user.setPedidosFeitos(totalPedidos);
                    }

                    user.save();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                tagCaptured = data.getStringExtra("result");
                System.out.println("CRIA PEDIDO tag capturada " + tagCaptured);

                if (!tagsCaptured.contains(tagCaptured)) {
                    tagsCaptured.add(tagCaptured);
                } else if (tagsCaptured.contains(tagCaptured)) {
                    Toast.makeText(getBaseContext(), "Tag já selecionada", Toast.LENGTH_SHORT).show();
                } else if (tagsCaptured.size() == 3) {
                    Toast.makeText(getBaseContext(), "Apenas 3 tags podem ser escolhidas", Toast.LENGTH_SHORT).show();
                }

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                groupCaptured = data.getStringExtra("groupSelected");
                if (groupCaptured != null) {
                    if (groupCaptured.length() > 13) {
                        groupConcat = groupCaptured.substring(0, 10) + "...";
                    } else groupConcat = groupCaptured;
                }
                groupCapturedId = data.getStringExtra("idGroupSelected");
                System.out.println("CRIA PEDIDO grupo capturada " + groupCaptured);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    private void refresh() {
        Intent intent = new Intent(CriaPedidoActivity.this, PedidosActivity.class);
        finish();
        startActivity(intent);
    }

    ;
}





