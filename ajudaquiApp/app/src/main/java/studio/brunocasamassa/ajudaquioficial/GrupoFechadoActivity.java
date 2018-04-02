package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 24/04/2017.
 */

public class GrupoFechadoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private TextView qtdMembros;
    private TextView groupName;
    private TextView descricao;
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private Grupo grupo;
    private Bundle itens;
    private Button botaoSolicitar;
    private DatabaseReference firebase;
    private ValueEventListener valueEventSolicita;
    private CircleImageView groupImg;
    private DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios");
    private ValueEventListener valueEventListenerUser;
    private User user = new User();
    private String idAdmin;
    private ArrayList<String> solicitacoesUser = new ArrayList<>();
    private ArrayList<String> gruposUser = new ArrayList<>();
    private String userName = new String();
    private TextView groupNotification;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onStart() {
        super.onStart();
        //userData.addListenerForSingleValueEvent(valueEventListenerUser);
        //dbGroups.addListenerForSingleValueEvent(valueEventListenerAllGroups);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        //firebase.removeEventListener(valueEventListenerUser);
        //dbGroups.removeEventListener(valueEventListenerAllGroups);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        qtdMembros = (TextView) findViewById(R.id.qtdMembros);
        groupName = (TextView) findViewById(R.id.groupName);
        descricao = (TextView) findViewById(R.id.grupoDescricao);
        groupImg = (CircleImageView) findViewById(R.id.groupImg);
        botaoSolicitar = (Button) findViewById(R.id.botaoSolicitar);
        groupNotification = (TextView) findViewById(R.id.textView3);

        grupo = new Grupo();

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            userName = extra.getString("userName");

            System.out.println("userName " + userName);

            grupo.setOpened(extra.getBoolean("isOpened"));
            grupo.setIdAdms(extra.getStringArrayList("idAdmins"));
            grupo.setDescricao(extra.getString("descricao"));
            grupo.setNome(extra.getString("nome"));
            grupo.setId(extra.getString("groupId"));
            grupo.setQtdMembros(Integer.valueOf(extra.getString("qtdmembros")));

        }

        DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");
        dbUser.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getGruposSolicitados() != null) {
                    solicitacoesUser.addAll(user.getGruposSolicitados());
                }
                if (user.getGrupos() != null) {
                    gruposUser.addAll(user.getGrupos());
                }
                if (grupo.isOpened()) {
                    botaoSolicitar.setText("PARTICIPAR");
                    groupNotification.setText("Este é um grupo aberto, ao clicar em participar voce fará parte dele");
                    botaoSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("Solicitacao user " + solicitacoesUser);
                            if (!gruposUser.contains(grupo.getId())) {
                                AlertDialog.Builder alertDialog2 = new AlertDialog.Builder(GrupoFechadoActivity.this);

                                alertDialog2.setTitle("Participar do Grupo");
                                alertDialog2.setMessage("Deseja participar deste grupo?");
                                alertDialog2.setCancelable(false);

                                alertDialog2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }

                                });
                                alertDialog2.setPositiveButton("Entrar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        userInGroup();
                                        finish();
                                    }
                                }).create().show();

                            } else {
                                Toast.makeText(GrupoFechadoActivity.this, "Voce já participa deste grupo", Toast.LENGTH_LONG).show();
                                finish();
                            }
                        }
                    });
                } else {
                    botaoSolicitar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println("Solicitacao user " + solicitacoesUser);
                            if (solicitacoesUser.contains(grupo.getId())) {
                                Toast.makeText(GrupoFechadoActivity.this, "Pedido de solicitação para este grupo já enviado, aguarde resposta dos administradores", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (gruposUser.contains(grupo.getId())) {
                                Toast.makeText(GrupoFechadoActivity.this, "Voce já participa deste grupo", Toast.LENGTH_LONG).show();
                                finish();
                            } else if (!solicitacoesUser.contains(grupo.getId())) {
                                geraSolicitacao();

                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        final File[] imgFile = new File[1];
        StorageReference storage = FirebaseConfig.getFirebaseStorage().child("groupImage");
        Task<Uri> uri2 = storage.child(grupo.getNome() + ".jpg").getDownloadUrl();/*.addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //imgFile[0] = new File(uri.toString());
                grupo.setGrupoImg(uri.toString());
                System.out.println("my groups lets seee2"+ uri);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });*/

        groupName.setText(grupo.getNome());
        qtdMembros.setText(String.valueOf(grupo.getQtdMembros()));
        // groupImg.setImageURI();
        storage = FirebaseConfig.getFirebaseStorage().child("groupImages");

        storage.child(grupo.getId() + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                try {
                    //Glide.with(GrupoFechadoActivity.this).load(uri).override(68, 68).into(groupImg);
                    Picasso.with(getApplicationContext()).load(uri).resize(groupImg.getWidth(), groupImg.getHeight()).into(groupImg);
                } catch (Exception e) {
                    groupImg.setImageURI(uri);
                }
                System.out.println("group image chat " + uri);
            }
        });


        System.out.println("group URI " + grupo.getGrupoImg());
        descricao.setText(grupo.getDescricao());
        //grupo.save();

        //grupo.setGrupoImg(groupImg);

        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        toolbar.setTitle(R.string.menu_grupos);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));


        NavigationDrawer navigator = new NavigationDrawer();
        navigator.createDrawer(GrupoFechadoActivity.this, toolbar, 5);

    }

    private void userInGroup() {

        //ADD USER INTO GROUP
        DatabaseReference dbGroups = FirebaseConfig.getFireBase().child("grupos").child(grupo.getId());
        dbGroups.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                System.out.println("grupo " + grupo.getNome());
                ArrayList<String> idMembros = new ArrayList<String>();
                int qtdMembros = grupo.getQtdMembros() + 1;
                grupo.setQtdMembros(qtdMembros);
                if (grupo.getIdMembros() != null) {
                    idMembros.addAll(grupo.getIdMembros());
                    idMembros.add(idMembros.size(), userKey);
                } else idMembros.add(0, userKey);

                grupo.setIdMembros(idMembros);

                grupo.save();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("SOLICITATION ERROR: " + databaseError);
            }
        });

        //ADD GROUP INTO USER
        databaseUsers.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                ArrayList<String> gruposUserLogado = new ArrayList<String>();
                if (user.getGrupos() != null) {  //populate groups
                    gruposUserLogado.addAll(user.getGrupos());
                    System.out.println("grupos user logado qtd " + gruposUserLogado.size());
                    gruposUserLogado.add(gruposUserLogado.size(), grupo.getId());
                    System.out.println("grupos caraio " + gruposUserLogado);
                } else {
                    gruposUserLogado.add(0, grupo.getId());
                }
                System.out.println("caraio Email user " + user.getEmail() + "nOME USER: " + user.getName());
                user.setGrupos(gruposUserLogado);
                System.out.println("refs2 usuario " + user.getGrupos());
                user.setId(Base64Decoder.encoderBase64(user.getEmail()));
                System.out.println("grupos do usuario que irão ser salvos" + user.getGrupos());
                user.save();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

        Toast.makeText(getApplicationContext(), "Parabéns, você entrou no grupo " + grupo.getNome(), Toast.LENGTH_SHORT).show();
    }


    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    ;

    private void geraSolicitacao() {

        Log.i("Gera Solicitação", "entrei");
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(GrupoFechadoActivity.this);

        alertDialog.setTitle("Solicitar Participação");
        alertDialog.setMessage("Escreva uma mensagem para os administradores");
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(GrupoFechadoActivity.this);
        alertDialog.setView(editText);

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        alertDialog.setPositiveButton("Solicitar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String mensagemSolicitacao = editText.getText().toString();
                String grupoNome = groupName.getText().toString();

                //MESSAGE TO ADMINS

                if (mensagemSolicitacao.isEmpty()) {
                    Toast.makeText(GrupoFechadoActivity.this, "Preencha o campo de mensagem", Toast.LENGTH_LONG).show();
                } else {
                    //insert solicitacao into user(para nao solicitar de novo)
                    DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
                    dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User user = dataSnapshot.getValue(User.class);
                            ArrayList<String> gruposSolicitados = new ArrayList<String>();
                            user.setGruposSolicitados(user.getGruposSolicitados());
                            if (user.getGruposSolicitados() != null) {
                                gruposSolicitados.addAll(user.getGruposSolicitados());
                                gruposSolicitados.add(gruposSolicitados.size(), grupo.getId());
                                user.setGruposSolicitados(gruposSolicitados);
                            } else {
                                gruposSolicitados.add(0, grupo.getId());
                                user.setGruposSolicitados(gruposSolicitados);
                            }
                            user.setId(userKey);
                            user.save();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                    DatabaseReference dbGroups = FirebaseConfig.getFireBase().child("grupos");
                    dbGroups.child(grupo.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            final Grupo group = dataSnapshot.getValue(Grupo.class);
                            ArrayList<String> arrayAdmins = group.getIdAdms();

                            for (int i = 0; i < arrayAdmins.size(); i++) {
                                DatabaseReference userData = FirebaseConfig.getFireBase().child("usuarios").child(arrayAdmins.get(i));
                                userData.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        User dataUser = dataSnapshot.getValue(User.class);
                                        ArrayList<String> msgSolicitacoes = new ArrayList();
                                        if (dataUser.getMsgSolicitacoes() != null) {
                                            System.out.println("mensagens solicitação usuario: " + dataUser.getMsgSolicitacoes());
                                            System.out.println("mensagens solicitação usuario: " + user.getMsgSolicitacoes());
                                            msgSolicitacoes.addAll(dataUser.getMsgSolicitacoes());
                                            //padrao de mensagem na db
                                            dataUser.setProfileNotificationCount(dataUser.getProfileNotificationCount() + 1);

                                            msgSolicitacoes.add(msgSolicitacoes.size(), "GRUPO:" + grupo.getNome() + ":USUARIO:" + userName + " :MENSAGEM: " + mensagemSolicitacao + ":USERKEY:" + userKey + ":SOLICITATIONKEY:" + userKey + grupo.getId() + ":GROUPKEY:" + grupo.getId());
                                        } else {
                                            //padrao de mensagem na db
                                            msgSolicitacoes.add(msgSolicitacoes.size(), "GRUPO:" + grupo.getNome() + ":USUARIO:" + userName + " :MENSAGEM: " + mensagemSolicitacao + ":USERKEY:" + userKey + ":SOLICITATIONKEY:" + userKey + grupo.getId() + ":GROUPKEY:" + grupo.getId());
                                        }

                                        dataUser.setMsgSolicitacoes(msgSolicitacoes);

                                        dataUser.save();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                        Toast.makeText(getApplicationContext(), "FAILED TO SEND " + databaseError, Toast.LENGTH_SHORT).show();
                                    }
                                });

                            }
                            Toast.makeText(getApplicationContext(), "Solicitação enviada", Toast.LENGTH_SHORT).show();
                            finish();

                        }

                        ;

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                            Toast.makeText(getApplicationContext(), "FAILED TO SEND " + databaseError, Toast.LENGTH_SHORT).show();
                        }

                    });
                }

            }


        }).create().show();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exit:
                //logoutUser();
                Preferences preferences = new Preferences(GrupoFechadoActivity.this);
                preferences.clearSession();
                DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                dbUser.child("notificationToken").removeValue();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(GrupoFechadoActivity.this, MainActivity.class));
                return true;
            case R.id.action_settings:
                finish();
                startActivity(new Intent(GrupoFechadoActivity.this, ConfiguracoesActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

