package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.User;

public class PerfilGruposActivity extends AppCompatActivity {

    private int premiumUser;
    private String targetUserId;
    private String targetUserName;
    private String targetUserEmail;
    private int targetUserPontos;
    private String targetUserImg;
    private int targetUserPedidosAtendidos;
    private int targetUserPedidosFeitos;
    private int targetUserPosicao;
    private TextView rankedUserPedidosFeitos;
    private TextView rankedUserPedidosAtendidos;
    private TextView rankedUserPoints;
    private TextView rankedUserPosition;
    private CircleImageView img;
    private TextView rankedUserName;
    private DatabaseReference groupDb;
    private DatabaseReference userDb;
    private StorageReference dbImageUsers;
    private TextView becomeAdminButton;
    private String groupId;
    private TextView removeUserButton;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_perfil_grupos);

        Bundle extra = getIntent().getExtras();

        groupId = extra.getString("groupId");
        targetUserId = extra.getString("rankedUserId");
        targetUserName = extra.getString("rankedUserName");
        targetUserEmail = extra.getString("rankedUserEmail");
        targetUserImg = extra.getString("rankedUserImg");
        targetUserPontos = extra.getInt("rankedUserPontos");
        targetUserPosicao = extra.getInt("posicao");
        targetUserPedidosAtendidos = extra.getInt("rankedUserPedidosAtendidos");
        targetUserPedidosFeitos = extra.getInt("rankedUserPedidosFeitos");

        System.out.println("position " + targetUserPosicao);
        System.out.println("target id " + targetUserId);
        img = (CircleImageView) findViewById(R.id.rankedProfileImg);
        rankedUserName = (TextView) findViewById(R.id.rankedProfileName);
        rankedUserPedidosAtendidos = (TextView) findViewById(R.id.rankedPedidosAtendidos);
        rankedUserPedidosFeitos = (TextView) findViewById(R.id.rankedPedidosFeitos);
        rankedUserPoints = (TextView) findViewById(R.id.rankedUserPontosConquistados);
        rankedUserPosition = (TextView) findViewById(R.id.rankingPosition);
        becomeAdminButton = (TextView) findViewById(R.id.adminButton);
        removeUserButton = (TextView) findViewById(R.id.removeButton);

        rankedUserName.setText(targetUserName);
        rankedUserPedidosAtendidos.setText(String.valueOf(targetUserPedidosAtendidos));
        rankedUserPedidosFeitos.setText(String.valueOf(targetUserPedidosFeitos));
        rankedUserPoints.setText(String.valueOf(targetUserPontos));
        rankedUserPosition.setText(String.valueOf(targetUserPosicao + 1) + "º");

        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        toolbar.setTitle(targetUserName);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        userDb = FirebaseConfig.getFireBase().child("usuarios");
        userDb.child(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User userDb = dataSnapshot.getValue(User.class);
                    dbImageUsers = FirebaseConfig.getFirebaseStorage().child("userImages");

                    try {

                        dbImageUsers.child(targetUserId + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Glide.with(getApplication()).load(uri).override(68, 68).into(img);
                                Picasso.with(getApplication()).load(uri).resize(1000, 1000).into(img);
                                System.out.println("user image chat " + uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {


                                dbImageUsers.child(targetUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        //Glide.with(getApplication()).load(uri).override(68, 68).into(img);
                                        Picasso.with(getApplication()).load(uri).resize(1000, 1000).into(img);
                                        System.out.println("user image chat " + uri);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                        if (userDb.getProfileImg() != null ) { //todo bug manual register or facebook register
                                            //Glide.with(ConfiguracoesActivity.this).load(user.getProfileImg()).override(68, 68).into(circleImageView);
                                            img.setBackgroundColor(Color.TRANSPARENT);
                                            Picasso.with(PerfilGruposActivity.this).load(userDb.getProfileImg()).resize(1000, 1000).onlyScaleDown().into(img);
                                        }
                                        //Glide.with(getApplication()).load(targetUserImg).into(img);

                                    }
                                });
                            }
                        });
                    } catch (Exception e){

                        dbImageUsers.child(targetUserId + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                //Glide.with(getApplication()).load(uri).override(68, 68).into(img);
                                Picasso.with(getApplication()).load(uri).resize(1000, 1000).into(img);
                                System.out.println("user image chat " + uri);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                if (userDb.getProfileImg() != null ) { //todo bug manual register or facebook register
                                    //Glide.with(ConfiguracoesActivity.this).load(user.getProfileImg()).override(68, 68).into(circleImageView);
                                    img.setBackgroundColor(Color.TRANSPARENT);
                                    Picasso.with(PerfilGruposActivity.this).load(userDb.getProfileImg()).resize(1000, 1000).onlyScaleDown().into(img);
                                }
                                //Glide.with(getApplication()).load(targetUserImg).into(img);

                            }
                        });



                    }

                }




            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        groupDb = FirebaseConfig.getFireBase().child("grupos");

        groupDb.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Grupo group = dataSnapshot.getValue(Grupo.class);
                final ArrayList<String> idAdmins = group.getIdAdms();
                final ArrayList<String> idMembros = group.getIdMembros();

                if (idAdmins.contains(userKey) && userKey != targetUserId) {
                    becomeAdminButton.setText("TORNAR ADMINISTRADOR DO GRUPO");
                    removeUserButton.setText("REMOVER USUARIO DO GRUPO");

                    becomeAdminButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilGruposActivity.this);
                            alertDialog.setTitle("Tornar Administrador");
                            alertDialog.setMessage("Deseja que este usuario se torne um administrador?");
                            alertDialog.setCancelable(false);

                            alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (idAdmins.contains(targetUserId)) {
                                        Toast.makeText(getApplicationContext(), "Usuario já é um administrador", Toast.LENGTH_SHORT).show();
                                    } else {
                                        idAdmins.add(targetUserId);
                                        idMembros.remove(targetUserId);
                                        group.setIdMembros(idMembros);
                                        group.setIdAdms(idAdmins);
                                        group.save();
                                        Toast.makeText(getApplicationContext(), targetUserName + " agora é um administrador", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }

                                }
                            }).create().show();

                        }
                    });

                    removeUserButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PerfilGruposActivity.this);
                            alertDialog.setTitle("Remover usuario");
                            alertDialog.setMessage("Deseja que este usuario seja removido do grupo?");
                            alertDialog.setCancelable(false);

                            alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (idMembros != null) {
                                        if (idMembros.contains(targetUserId)) {
                                            idMembros.remove(targetUserId);
                                            DatabaseReference dbTargetUser = FirebaseConfig.getFireBase().child("usuarios");
                                            dbTargetUser.child(targetUserId).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    User user = dataSnapshot.getValue(User.class);
                                                    ArrayList<String> groupsId = user.getGrupos();
                                                    if (groupsId != null) {
                                                        groupsId.remove(groupId);
                                                    }
                                                    user.save();
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                            group.setIdMembros(idMembros);
                                            group.setQtdMembros(group.getQtdMembros() - 1);
                                            group.save();
                                            Toast.makeText(getApplicationContext(), "Removido com sucesso", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    } else if (idAdmins.contains(targetUserId)) {
                                        Toast.makeText(getApplicationContext(), "Nao é possivel remover outro administrador", Toast.LENGTH_SHORT).show();
                                    }
                                    else {
                                        Toast.makeText(getApplicationContext(), "Nao é possivel este usuario", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }).create().show();


                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
}
