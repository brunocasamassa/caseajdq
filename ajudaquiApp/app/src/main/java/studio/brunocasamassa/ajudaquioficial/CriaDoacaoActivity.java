package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import me.gujun.android.taggroup.TagGroup;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 22/05/2017.
 */

public class CriaDoacaoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText pedidoName;
    private EditText descricao;
    private Button createButton;
    private String tagCaptured;
    ArrayList<String> tagsCaptured = new ArrayList<String>();
    private TagContainerLayout categorias;
    private TagGroup grupos;
    private TextView add_grupos;
    private TextView add_tags;
    private Pedido pedido;
    private int premium;
    private ImageButton addTagButton;
    private ImageButton addGroupButton;
    private String tipoPedido;   //doacao, servico, troca, emprestimo
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private String groupKey;
    private SeekBar doacaoQtd;
    private int qtd;
    private int naCabine = 0;
    private Double latitude = 0.0;
    private Double longitude = 0.0;
    private TextView seekValue;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_doacao);
        final Bundle extras = getIntent().getExtras();

        latitude = (extras.getDouble("latitude"));
        longitude = (extras.getDouble("longitude"));

        groupKey = extras.getString("groupKey");

        System.out.println("group key " + groupKey);

        toolbar = (Toolbar) findViewById(R.id.toolbar_create_group);
        toolbar.setTitle("Criar doacao");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //grupos = (TagGroup) findViewById(R.id.tagGroupGruposDoacao);
        //grupos.setTags(Base64Decoder.decoderBase64(groupKey));

        seekValue = (TextView) findViewById(R.id.seekbar_value);
        doacaoQtd = (SeekBar) findViewById(R.id.seekBar);
        add_grupos = (TextView) findViewById(R.id.textView5);
        add_tags = (TextView) findViewById(R.id.word_add_tags);
        pedidoName = (EditText) findViewById(R.id.create_pedido_name);
        descricao = (EditText) findViewById(R.id.create_pedido_description);
        createButton = (Button) findViewById(R.id.create_pedido_button);
        addTagButton = (ImageButton) findViewById((R.id.add_tag_button));
        addGroupButton = (ImageButton) findViewById((R.id.addGroup_tag_button));

        System.out.println("taggroup " + grupos.getTags().length);
        doacaoQtd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                                                 @Override
                                                 public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                                     qtd = progress;
                                                     seekValue.setText(String.valueOf(progress));
                                                 }

                                                 @Override
                                                 public void onStartTrackingTouch(SeekBar seekBar) {

                                                 }

                                                 @Override
                                                 public void onStopTrackingTouch(SeekBar seekBar) {

                                                 }
                                             });

                createButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (qtd == 0){
                            Toast.makeText(getApplicationContext(), "Insira um valor diferente de 0", Toast.LENGTH_LONG).show();
                        }
                        if (pedidoName.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Insira um Titulo para o pedido", Toast.LENGTH_LONG).show();
                            return;
                        } else if (descricao.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Insira uma descricao para o pedido", Toast.LENGTH_LONG).show();
                            return;
                        } else if (qtd >= 10) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CriaDoacaoActivity.this);

                            alertDialog.setTitle("Cabine da fartura?");
                            alertDialog.setMessage("Mais de 10 quantidades? deseja envia-las para a a cabine da fartura?");
                            alertDialog.setCancelable(false);

                            alertDialog.setNeutralButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    naCabine = 0;
                                    createPedido();
                                }
                            });
                            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    naCabine = 1;
                                    createPedido();
                                    DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
                                    dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            User usuario = dataSnapshot.getValue(User.class);
                                            usuario.setCreditos(usuario.getCreditos() + 5);
                                            usuario.save();

                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });

                                }
                            }).create().show();

                        }

                        else if (qtd < 10) {
                            final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CriaDoacaoActivity.this);

                            alertDialog.setTitle("Fazer doação?");
                            alertDialog.setMessage("Deseja gerar esta doação?");
                            alertDialog.setCancelable(false);

                            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    naCabine = 0;
                                    createPedido();

                                }
                            });

                            alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            }).create().show();
                        }
                    }

                });

    }

    private void createPedido() {

        for (int i = 1; i <= qtd; i++) {

            pedido = new Pedido();
            System.out.println("tipo de pedido: " + tipoPedido);
            pedido.setTagsCategoria(tagsCaptured);
            pedido.setTipo("Doacoes");

            pedido.setNaCabine(naCabine);
            pedido.setGroupId(groupKey);
            pedido.setLongitude(longitude);
            pedido.setLatitude(latitude);

            pedido.setDescricao(descricao.getText().toString());
            pedido.setTitulo(pedidoName.getText().toString() + " Nº" + i + " ");
            pedido.setIdPedido(Base64Decoder.encoderBase64(pedido.getTitulo()));
            pedido.setCriadorId(userKey);
            pedido.setGrupo(Base64Decoder.decoderBase64(groupKey));

            if (pedido.getGrupo() != null) {
                savePedidoIntoGroup(pedido);
            }

            System.out.println("user id key " + userKey);

            pedido.save();
            pedidoSaveIntoUser(true);
            Toast.makeText(getApplicationContext(), "Doação gerada com sucesso", Toast.LENGTH_LONG).show();
            refresh();
        }

    }

    private void savePedidoIntoGroup(final Pedido pedido) {
        final String tipoPedido = pedido.getTipo();
        final ArrayList<String> pedidosList = new ArrayList<>();
        DatabaseReference dbGroup = FirebaseConfig.getFireBase().child("grupos").child(groupKey);
        dbGroup.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo grupo = dataSnapshot.getValue(Grupo.class);

                if (tipoPedido.equals("Doacoes")) {
                    if (grupo.getDoacoes() != null) {
                        pedidosList.addAll(grupo.getDoacoes());
                        pedidosList.add(pedidosList.size(), pedido.getIdPedido());
                        grupo.setDoacoes(pedidosList);

                    } else {

                        pedidosList.add(0, pedido.getIdPedido());

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

    private void refresh() {
        Intent intent = new Intent(CriaDoacaoActivity.this, PedidosActivity.class);
        finish();
        startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    ;
}





