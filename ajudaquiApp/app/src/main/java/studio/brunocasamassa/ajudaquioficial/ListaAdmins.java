package studio.brunocasamassa.ajudaquioficial;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 20/06/2017.
 */
public class ListaAdmins extends AppCompatActivity {
    private ListView viewAdmins;
    private ArrayAdapter listaAdapter;
    private ArrayList<String> listaAdmins;
    private DatabaseReference firebase;
    public String selectedTag;
    private ProgressDialog dialog = null;
    private User user = new User();
    private TextView title;


    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_admins);

        Preferences preferences = new Preferences(ListaAdmins.this);

        final String userName = preferences.getNome();
        final String userId = preferences.getIdentificador();


        viewAdmins = (ListView) findViewById(R.id.adminsList);

        Bundle bundle = getIntent().getExtras();

        final String mensagemSolicitacao = bundle.getString("MESSAGE");
        String groupId = bundle.getString("GROUP ID");

        System.out.println("MESSAGE "+ mensagemSolicitacao);


        firebase = FirebaseConfig.getFireBase().child("grupos").child(groupId);
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final Grupo grupo = dataSnapshot.getValue(Grupo.class);
                ArrayList<String> adminsId = new ArrayList<String>();
                adminsId.addAll(grupo.getIdAdms());
                ArrayList<String> adminsName = new ArrayList<String>();

                for (int i = 0; i < adminsId.size(); i++) {
                    adminsName.add(i, Base64Decoder.decoderBase64(adminsId.get(i)));
                }

                listaAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2,
                        android.R.id.text1,
                        adminsName);

                viewAdmins.setAdapter(listaAdapter);

                viewAdmins.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        final String adminSelected = (String) viewAdmins.getItemAtPosition(position);

                        DatabaseReference userData = FirebaseConfig.getFireBase().child("usuarios").child(Base64Decoder.encoderBase64(adminSelected));
                        userData.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User dataUser = dataSnapshot.getValue(User.class);
                                ArrayList<String> msgSolicitacoes = new ArrayList();
                                if (dataUser.getMsgSolicitacoes() != null) {
                                    System.out.println("mensagens solicitação usuario: " + dataUser.getMsgSolicitacoes());
                                    user.setMsgSolicitacoes(dataUser.getMsgSolicitacoes());
                                    System.out.println("mensagens solicitação usuario: " + user.getMsgSolicitacoes());
                                    msgSolicitacoes.addAll(user.getMsgSolicitacoes());
                                    //padrao de mensagem na db
                                    msgSolicitacoes.add(msgSolicitacoes.size(), "GRUPO:" + grupo.getNome() + ":USUARIO:" + userName + " :MENSAGEM: " + mensagemSolicitacao + ":USERKEY:" + userId);

                                } else {
                                    //padrao de mensagem na db
                                    msgSolicitacoes.add(msgSolicitacoes.size(), "GRUPO:" + grupo.getNome() + ":USUARIO:" + userName + " :MENSAGEM: " + mensagemSolicitacao + ":USERKEY:" + userId);
                                }

                                /*if (dataUser.getMessageNotification() != null) {  //todo mudou user, altera aqui
                                    user.setMessageNotification(dataUser.getMessageNotification());
                                }
                                if (dataUser.getMedalhas() != null) {
                                    user.setMedalhas(dataUser.getMedalhas());
                                }
                                dataUser.setMsgSolicitacoes(msgSolicitacoes);
                                if (dataUser.getGrupos() != null) {
                                    user.setGrupos(dataUser.getGrupos());
                                }
                                user.setCreditos(dataUser.getCreditos());
                                if (dataUser.getEmail() != null) {
                                    user.setEmail(dataUser.getEmail());
                                }
                                if (dataUser.getName() != null) {
                                    user.setName(dataUser.getName());
                                }
                                user.setPremiumUser(dataUser.getPremiumUser());
                                if (dataUser.getProfileImageURL() != null) {
                                    user.setProfileImageURL(dataUser.getProfileImageURL());
                                }
                                if (dataUser.getProfileImg() != null) {
                                    user.setProfileImg(dataUser.getProfileImg());
                                }
                                if (dataUser.getPedidosFeitos() != null) {
                                    user.setPedidosFeitos(dataUser.getPedidosFeitos());
                                }
                                if (dataUser.getPedidosAtendidos() != null) {
                                    user.setPedidosAtendidos(dataUser.getPedidosAtendidos());
                                }

                                    user.setPontos(dataUser.getPontos());

                                System.out.println("userName " + adminSelected);
                                user.setId(Base64Decoder.encoderBase64(user.getEmail()));
*/
                                dataUser.save();

                                Toast.makeText(ListaAdmins.this, "Solicitação enviada", Toast.LENGTH_LONG).show();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        finish();
                    }

                });

            };

            @Override
            public void onCancelled (DatabaseError databaseError){

            }
        });

    };


    private void refresh() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    ;
}