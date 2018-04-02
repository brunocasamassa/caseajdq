package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.facebook.login.LoginManager;
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
import es.dmoral.toasty.Toasty;
import studio.brunocasamassa.ajudaquioficial.adapters.MedalhasAdapter;
import studio.brunocasamassa.ajudaquioficial.adapters.NotificacoesAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 24/04/2017.
 */

public class PerfilActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int NUMBER_BADGES = 10;
    private ListView listview_nomes;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private int posicao;
    private CircleImageView profileImg;
    private TextView profileName;
    private RecyclerView badges;
    private StorageReference storage;
    private TextView pontosConquistados;
    private TextView userCredits;
    private TextView pedidosFeitos;
    private TextView pedidosAtendidos;
    private User user = new User();
    private MainActivity main;
    private CadastroActivity cdrst;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> listaNotificacoes;
    private ArrayList<String> listaMessages;
    private ArrayList<String> listaUserName;
    private ArrayList<String> listaGrupos;
    private ArrayList<String> listaKey;
    private String groupName;
    private String groupKey;
    private String userName;
    private String userKeySolicitante;
    private String message;
    private ImageView premiumTag;
    public static User usuarioPivot = new User();
    private ArrayList<Integer> badgesList = new ArrayList<>();
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private static int premium;
    private String encodedKeySolicitation;
    private ArrayList<String> listaSolicitationKey;
    private ArrayList<String> listaGruposKeys;
    private RecyclerView horizontal;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil);


        freeMemory();

        final ListView notificacoes = (ListView) findViewById(R.id.perfil_notificacoes);
        profileImg = (CircleImageView) findViewById(R.id.profileImg);
        profileName = (TextView) findViewById(R.id.profileName);
        premiumTag = (ImageView) findViewById(R.id.premiumTag);
        pedidosAtendidos = (TextView) findViewById(R.id.rankedPedidosAtendidos);
        pedidosFeitos = (TextView) findViewById(R.id.rankedPedidosFeitos);
        pontosConquistados = (TextView) findViewById(R.id.rankedUserPontosConquistados);
        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        userCredits = (TextView) findViewById(R.id.user_credits);
        horizontal = (RecyclerView) findViewById(R.id.horizontal_scroll);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.HORIZONTAL);

        if (profileName.getText().length() < 13) {
            userCredits.setTranslationX(Float.valueOf(-10));
        }

        listaSolicitationKey = new ArrayList<>();
        listaNotificacoes = new ArrayList();
        listaMessages = new ArrayList();
        listaGrupos = new ArrayList();
        listaGruposKeys = new ArrayList();
        listaUserName = new ArrayList();
        listaKey = new ArrayList();

        final String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());

        storage = FirebaseConfig.getFirebaseStorage().child("userImages");

        final DatabaseReference databaseUsers = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        databaseUsers.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final User usuario = dataSnapshot.getValue(User.class);
                System.out.println("recebe usuario NAME: " + usuario.getName());
                System.out.println("recebe usuario DATA: " + dataSnapshot.getValue());
                int respPremium = usuario.getPremiumUser();
                premium = respPremium;

                usuario.setProfileNotificationCount(0);
                usuario.save();

                if (premium == 1) {
                    Glide.with(PerfilActivity.this).load(R.drawable.premium_icon).into(premiumTag);
                }

                System.out.println("Premium user Perfil Activity response " + premium);

                if (usuario.getMsgSolicitacoes() != null) {
                    ArrayList<String> msgSolicita = usuario.getMsgSolicitacoes();
                    for (int i = 0; i < msgSolicita.size(); i++) {
                        String msgCompleta = "";
                        String[] msg = msgSolicita.get(i).split(":");

                        //GRUPO: vamos ver: usuario: dGVzdGVAdGVzdGUuY29t :mensagem: osmanu: hashkey"

                        //CONCATE NOTIFICATION STRING
                        for (String sentence : msg) {
                            if (sentence.equals("GRUPO")) {
                                groupName = msg[1];
                                //msgCompleta = groupName;
                                System.out.println("GROUP NAME CONCATENADO " + groupName);
                            }
                            if (sentence.equals("USUARIO")) {
                                userName = msg[3];
                                msgCompleta = "O usuario " + userName + " deseja entrar no grupo " + groupName;
                            }
                            if (sentence.equals("MENSAGEM")) {
                                message = msg[5];
                                //msgCompleta = msgCompleta + " (MENSAGEM: "+message+" )";
                            }
                            if (sentence.equals("USERKEY")) {
                                userKeySolicitante = msg[7];
                                System.out.println("USERKEY userkey " + userKeySolicitante);
                                //msgCompleta = msgCompleta + " (MENSAGEM: "+message+" )";
                            }

                            if (sentence.equals("SOLICITATIONKEY")) {
                                encodedKeySolicitation = msg[9];
                                System.out.println("USERKEY userkey " + encodedKeySolicitation);
                                //msgCompleta = msgCompleta + " (MENSAGEM: "+message+" )";
                            }

                            if (sentence.equals("GROUPKEY")) {
                                groupKey = msg[11];
                                System.out.println("GROUPKEY " + groupKey);
                                //msgCompleta = msgCompleta + " (MENSAGEM: "+message+" )";
                            }

                        }

                        //PUTTING WORDS INTO THE NOTIFICATION ARRAY
                        System.out.println("CONCATENADO TOTAL " + msgCompleta);
                        listaSolicitationKey.add(listaSolicitationKey.size(), encodedKeySolicitation);
                        listaKey.add(listaKey.size(), userKeySolicitante);
                        listaGrupos.add(listaGrupos.size(), groupName);
                        listaGruposKeys.add(listaGruposKeys.size(), groupKey);
                        listaUserName.add(listaUserName.size(), userName);
                        listaMessages.add(listaMessages.size(), message);
                        listaNotificacoes.add(listaNotificacoes.size(), msgCompleta);
                        adapter = new NotificacoesAdapter(getApplicationContext(), listaNotificacoes);

                        notificacoes.setDivider(null);
                        notificacoes.setAdapter(adapter);

                    }
                }


                //BADGES TREATMENT
                ArrayList<Integer> array = new ArrayList<Integer>();
                if (usuario.getMedalhas() != null) {
                    user.setMedalhas(usuario.getMedalhas());
                }
                array.addAll(verifyMedals());
                user.setMedalhas(array);
                horizontal.setLayoutManager(llm);
                horizontal.setAdapter(new MedalhasAdapter(array, PerfilActivity.this));


                //SETTING VALUES TO THE VIEW
                userCredits.setText("Creditos disponiveis: " + String.valueOf(usuario.getCreditos()));
                profileName.setText(usuario.getName());
                if (usuario.getPedidosAtendidos() != null) {
                    pedidosAtendidos.setText("" + usuario.getPedidosAtendidos().size());
                } else pedidosAtendidos.setText("" + 0);
                if (usuario.getPedidosFeitos() != null) {
                    pedidosFeitos.setText("" + usuario.getPedidosFeitos().size());
                } else pedidosFeitos.setText("" + 0);
                pontosConquistados.setText(String.valueOf(usuario.getPontos()));

                try {
                    storage.child(userKey + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImg.setBackgroundColor(Color.TRANSPARENT);
                            Picasso.with(PerfilActivity.this).load(uri).resize(1000, 1000).noFade().into(profileImg);
                            System.out.println("my groups lets seee2 " + uri);

                            //for better scale adjust

                       /* try {
                            InputStream imageStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);
                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                profileImg.setBackgroundColor(Color.TRANSPARENT);
                                profileImg.setImageBitmap(resized);
                            } catch (Exception e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("error in get image " + e.toString());
                        }*/
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            storage.child(userKey + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    profileImg.setBackgroundColor(Color.TRANSPARENT);
                                    Picasso.with(PerfilActivity.this).load(uri).resize(1000, 1000).noFade().into(profileImg);
                                    System.out.println("my groups lets seee2 " + uri);

                                    //for better scale adjust

                       /* try {
                            InputStream imageStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);
                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                profileImg.setBackgroundColor(Color.TRANSPARENT);
                                profileImg.setImageBitmap(resized);
                            } catch (Exception e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("error in get image " + e.toString());
                        }*/
                                }

                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    if (dataSnapshot.child("profileImg").exists()) { //todo bug manual register or facebook register
                                        Picasso.with(PerfilActivity.this).load(usuario.getProfileImg()).into(profileImg);
                                    }
                                }
                            });

                        }
                    });
                } catch (Exception e) {
                    storage.child(userKey + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            profileImg.setBackgroundColor(Color.TRANSPARENT);
                            Picasso.with(PerfilActivity.this).load(uri).resize(1000, 1000).noFade().into(profileImg);
                            System.out.println("my groups lets seee2 " + uri);

                            //for better scale adjust

                       /* try {
                            InputStream imageStream = getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(imageStream);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);
                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                profileImg.setBackgroundColor(Color.TRANSPARENT);
                                profileImg.setImageBitmap(resized);
                            } catch (Exception e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            System.out.println("error in get image " + e.toString());
                        }*/
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (dataSnapshot.child("profileImg").exists()) { //todo bug manual register or facebook register
                                Picasso.with(PerfilActivity.this).load(usuario.getProfileImg()).into(profileImg);
                            }
                        }
                    });


                }

                /*
                usuario.setId(userKey);
                usuario.save();*/
                return;
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(PerfilActivity.this, CriaPedidoActivity.class);
        intent.putExtra("premium", premium);
        System.out.println("Premium user Perfil Activity response fora" + premium);

        notificacoes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, final int position, final long id) {
                String mensagem = listaMessages.get(position);
                String usuarioSolicitante = listaUserName.get(position);
                final String nomeGrupoSolicitado = listaGrupos.get(position);
                final String grupoSolicitado = listaGruposKeys.get(position);
                final String userKeySolicitante = listaKey.get(position);
                final String encodedKeySolicitation = listaSolicitationKey.get(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilActivity.this);
                alertDialog.setTitle("Solicitação de Grupo");
                alertDialog.setTitle("O usuario " + usuarioSolicitante + " deseja entrar no grupo " + nomeGrupoSolicitado);
                alertDialog.setMessage(mensagem);
                alertDialog.setCancelable(false);


                alertDialog.setPositiveButton("ACEITAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(userKeySolicitante);
                        dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try {
                                    System.out.println("userkey '" + userKeySolicitante + "'");
                                    User user = dataSnapshot.getValue(User.class);
                                    System.out.println("username " + user.getName());  //ou caminho errado ou preciso declarar usuario como publico (GRUPOS MEUS GRUPOS FRAGMENT)
                                    addGroupIntoUser(user, grupoSolicitado, userKeySolicitante);
                                } catch (Exception e) {
                                    System.out.println("Exception " + e.getLocalizedMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("SOLICITATION ERROR: " + databaseError);

                            }
                        });

                        //ADD USER INTO GROUP
                        DatabaseReference dbGroups = FirebaseConfig.getFireBase().child("grupos").child(grupoSolicitado);
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
                                    idMembros.add(idMembros.size(), userKeySolicitante);
                                } else idMembros.add(0, userKeySolicitante);

                                grupo.setIdMembros(idMembros);
                                grupo.save();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                System.out.println("SOLICITATION ERROR: " + databaseError);
                            }
                        });

                        removeSolicitationMessage(grupoSolicitado, encodedKeySolicitation);

                        Toast.makeText(getApplicationContext(), "Solicitação Aceita", Toast.LENGTH_LONG).show();
                        finish();
                        /*ArrayAdapter newAdapter = new NotificacoesAdapter(getApplicationContext(), listaNotificacoes);
                        notificacoes.setDivider(null);
                        notificacoes.setAdapter(newAdapter);
*/
                    }
                });
                alertDialog.setNeutralButton("CANCELAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                alertDialog.setNegativeButton("RECUSAR", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Solicitação Recusada ", Toast.LENGTH_LONG).show();
                        removeSolicitationMessage(grupoSolicitado, encodedKeySolicitation);


                    }
                }).create().show();

            }
        });
        toolbar.setTitle(getResources().getString(R.string.menu_perfil));
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        setSupportActionBar(toolbar);
        NavigationDrawer navigator = new NavigationDrawer();

        navigator.createDrawer(PerfilActivity.this, toolbar, 7);

    }

    private ArrayList<Integer> verifyMedals() {
        final ArrayList<Integer> medals = new ArrayList<Integer>();
        try {  //get medals from user
            if (user.getMedalhas() != null) {
                medals.addAll(user.getMedalhas());

            } else { //populate array with ==0 (if arrayMedals equals null)  // 0 equals no medal achieved
                for (int i = 0; i < NUMBER_BADGES; i++) {
                    try {
                        medals.add(i, 0);
                        System.out.println("printing black in position " + i);

                    } catch (Exception e) {
                        System.out.println("error getting medals 1 " + e.getLocalizedMessage());
                    }
                }
            }

            DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");
            dbUser.child(userKey).child("medalhas").setValue(medals);
        } catch (Exception e) {
            System.out.println("error getting medals 2 " + e.getLocalizedMessage());
        }

        DatabaseReference db = FirebaseConfig.getFireBase().child("usuarios");
        db.child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                try {
                    try {
                        if (user.getPedidosAtendidos() != null && user.getPedidosAtendidos().size() >= 1) {
                            if (!user.getMedalhas().get(0).equals(1)) {
                                medals.set(0, 1); //primeiro pedido
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 10);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha1_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 1 " + e);
                    }

                    try {
                        if (user.getPedidosAtendidos() != null && user.getPedidosAtendidos().size() >= 3) {
                            if (!user.getMedalhas().get(1).equals(2)) {
                                medals.set(1, 2);   // 3 pedidos
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 50);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha2_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 2 " + e);
                    }

                    try {
                        if (user.getPedidosAtendidos() != null && user.getPedidosAtendidos().size() >= 10) {
                            if (!user.getMedalhas().get(2).equals(3)) {
                                medals.set(2, 3);   // 10 pedidos
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 100);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha3_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 3 " + e);
                    }
                    try {
                        if (user.getPedidosFeitos() != null && user.getPedidosFeitos().size() >= 1) {
                            if (!user.getMedalhas().get(3).equals(4)) {
                                medals.set(3, 4);   // primeiro pedido meu ajudado
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 10);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha4_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 4" + e);
                    }

                    try {
                        if (user.getPedidosFeitos() != null && user.getPedidosFeitos().size() >= 3) {
                            if (pedidosStatusVerify(user.getPedidosFeitos()) == 3)
                            //verifica se 3 pedidos do usuario estão em andamento
                            {
                                if (!user.getMedalhas().get(4).equals(5)) {
                                    medals.set(4, 5);   // 3 pedidos atendidos
                                    user.setMedalhas(medals);
                                    user.setPontos(user.getPontos() + 50);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        Toasty.custom(getApplicationContext(), getString(R.string.medalha5_message), getDrawable(R.drawable.logo),
                                                Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 5 " + e);
                    }

                    try {
                        if (user.getPedidosFeitos() != null && user.getPedidosFeitos().size() >= 10) {
                            if (pedidosStatusVerify(user.getPedidosFeitos()) == 10)
                            //verifica se 10 pedidos do usuario estão em andamento
                            {
                                if (!user.getMedalhas().get(5).equals(6)) {
                                    medals.set(5, 6);   // 10 pedidos atendidos
                                    user.setMedalhas(medals);
                                    user.setPontos(user.getPontos() + 100);
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                        Toasty.custom(getApplicationContext(), getString(R.string.medalha6_message), getDrawable(R.drawable.logo),
                                                Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 6 " + e);
                    }

                    try {
                        if (user.getGrupos() != null && user.getGrupos().size() >= 1) {
                            if (!user.getMedalhas().get(6).equals(7)) {
                                medals.set(6, 7);   // primeiro grupo
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 50);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha7_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 7 " + e);
                    }

                    try {
                        if (user.getGrupos() != null && user.getGrupos().size() >= 3) {
                            if (!user.getMedalhas().get(7).equals(8)) {
                                medals.set(7, 8);   // 3 grupos
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 200);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha8_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 8 " + e);
                    }
                    try {
                        if (user.getGrupos() != null && user.getGrupos().size() >= 10) {
                            if (!user.getMedalhas().get(8).equals(9)) {
                                medals.set(8, 9);   // primeiro grupo
                                user.setMedalhas(medals);
                                user.setPontos(user.getPontos() + 500);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Toasty.custom(getApplicationContext(), getString(R.string.medalha9_message), getDrawable(R.drawable.logo),
                                            Color.argb(255, 27, 77, 183), Toast.LENGTH_SHORT, true, true).show();
                                }
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("error get medal 9 " + e);
                    }

                    user.setMedalhas(medals);
                    user.save();
                } catch (Exception e) {
                    System.out.println("exception medals " + e);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return medals;
    }

    private int pedidosStatusVerify(ArrayList<String> pedidosFeitos) {
        final int[] cont = {0};
        for (int i = 0; i < pedidosFeitos.size(); i++) {
            DatabaseReference pedidosCall = FirebaseConfig.getFireBase().child("Pedidos").child(pedidosFeitos.get(i));
            pedidosCall.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Pedido pedido = dataSnapshot.getValue(Pedido.class);
                    try {
                        if (pedido.getStatus() == 1) {
                            cont[0]++;
                        }
                    } catch (Exception e) {
                        System.out.println("error verifying pedidos feitos medals " + e);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }

        return cont[0];
    }


    private void refresh() {
        Intent intent = getIntent();
        finish();
        System.out.println("REFRESHED");
        startActivity(intent);
    }

    ;

    private void removeSolicitationMessage(String grupoId, final String keySolicitacao) {

        DatabaseReference dbGroupSolicitaiton = FirebaseConfig.getFireBase().child("grupos").child(grupoId);
        dbGroupSolicitaiton.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo group = dataSnapshot.getValue(Grupo.class);
                ArrayList<String> groupAdmins = group.getIdAdms();
                //loop admins
                for (int i = 0; i < groupAdmins.size(); i++) {
                    System.out.println("admins grupo: " + groupAdmins.get(i));
                    DatabaseReference admins = FirebaseConfig.getFireBase().child("usuarios").child(groupAdmins.get(i));
                    admins.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User userAdmin = dataSnapshot.getValue(User.class);
                            ArrayList<String> userSolicitations = userAdmin.getMsgSolicitacoes();
                            //loop admin messsages
                            for (int j = 0; j < userSolicitations.size(); j++)
                                if (userSolicitations.get(j).contains(keySolicitacao)) {
                                    userSolicitations.remove(j);
                                }
                            userAdmin.setMsgSolicitacoes(userSolicitations);
                            userAdmin.save();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //don't judge me -- MANUAL DELAY FROM SERVER - TESTED AND NEEDED
        int timer = 2000;
        while (timer >= 0) {
            if (timer == 0) {
                finish();
            }
            timer--;
        }
    }

    public void freeMemory(){
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    private void addGroupIntoUser(User user, String grupoSolicitado, String userKeySolicitante) {

        System.out.println("USER SOLICITANTE: " + userKeySolicitante);
        ArrayList<String> solicitacoes = new ArrayList<>();
        if (user.getGruposSolicitados() != null) {
            System.out.println("USER SOLICITANTE SOLICITACOES: " + user.getGruposSolicitados());
            solicitacoes.addAll(user.getGruposSolicitados());
            System.out.println("USER SOLICITACOES: " + solicitacoes);
            solicitacoes.remove(grupoSolicitado);
            user.setGruposSolicitados(solicitacoes);
            System.out.println("USER SOLICITANTE SOLICITACOES: " + user.getGruposSolicitados());
            System.out.println("USER SOLICITACOES: " + solicitacoes);
        }
        ArrayList<String> grupos = new ArrayList<String>();
        if (user.getGrupos() != null) {
            grupos.addAll(user.getGrupos());
            grupos.add(grupos.size(), grupoSolicitado);
        } else
            grupos.add(0, grupoSolicitado);
        user.setGrupos(grupos);
        user.setId(userKeySolicitante);
        user.save();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exit:
                //logoutUser();
                Preferences preferences = new Preferences(PerfilActivity.this);
                preferences.clearSession();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                finish();
                startActivity(new Intent(PerfilActivity.this, MainActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(PerfilActivity.this, ConfiguracoesActivity.class));
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }


}
