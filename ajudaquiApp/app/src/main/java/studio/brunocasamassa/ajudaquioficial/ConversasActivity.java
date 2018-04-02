package studio.brunocasamassa.ajudaquioficial;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.adapters.ConversaAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.Conversa;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 24/04/2017.
 */

public class ConversasActivity extends AppCompatActivity {

    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerConversas;
    private ArrayList<Conversa> conversas;
    private Toolbar toolbar;
    private ListView listview_nomes;
    private ArrayAdapter adapter;
    private ViewPager viewPager;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private SlidingTabLayout slidingTabLayout;
    private int posicao;
    private int chatCount = 0;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    public void onStart() {
        super.onStart();
        firebase.addValueEventListener(valueEventListenerConversas);

    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerConversas);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversas);

        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        toolbar.setTitle(getResources().getString(R.string.menu_chats));
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);

        NavigationDrawer navigator = new NavigationDrawer();
        navigator.createDrawer(ConversasActivity.this, toolbar, 3);

        conversas = new ArrayList<>();
        listview_nomes = (ListView) findViewById(R.id.ListContatos);

        adapter = new ConversaAdapter(this, conversas);

        listview_nomes.setDivider(null);
        listview_nomes.setAdapter(adapter);

        // Recuperar dados do usuário
        Preferences preferencias = new Preferences(getApplication());
        String idUsuarioLogado = preferencias.getIdentificador();
        String nameUsuarioLogado = preferencias.getNome();
        System.out.println("preferencias " + nameUsuarioLogado);

        // Recuperar conversas do Firebase
        firebase = FirebaseConfig.getFireBase()
                .child("conversas")
                .child(userKey);

        valueEventListenerConversas = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                System.out.println("entrei laco conversas ativas do user");
                conversas.clear();
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Conversa conversa = dados.getValue(Conversa.class);
                    conversas.add(conversa);
                    chatCount+=conversa.getChatCount();
                }


                adapter.notifyDataSetChanged();

                verifyUserChatCount(chatCount);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        //Adicionar evento de clique na lista
        listview_nomes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Conversa conversa = conversas.get(position);
                AlertDialog.Builder alert = new AlertDialog.Builder(ConversasActivity.this);
                alert.setTitle("Apagar Conversa");
                alert.setMessage("Deseja Apagar esta conversa?");
                alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseConfig.getFireBase().child("conversas").child(userKey).child(Base64Decoder.encoderBase64(conversa.getNome())).removeValue();
                    }
                }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                    }
                }).create().show();

                return false;
            }
        });

        listview_nomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("MANU ENTROU");
                Conversa conversa = conversas.get(position);
                Intent intent = new Intent(ConversasActivity.this, ChatActivity.class);
                intent.putExtra("nome", conversa.getNome());
                String email = Base64Decoder.decoderBase64(conversa.getIdUsuario());
                intent.putExtra("email", email);

                startActivity(intent);

            }
        });

    }

    private void verifyUserChatCount(final int chatCount) {

        DatabaseReference userDb =  FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        userDb.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                user.setChatNotificationCount(chatCount);
                user.save();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_sobre, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exit:
                //logoutUser();
                Preferences preferences = new Preferences(ConversasActivity.this);
                preferences.clearSession();
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ConversasActivity.this, MainActivity.class));
                return true;
            case R.id.action_settings:
                    startActivity(new Intent(ConversasActivity.this, ConfiguracoesActivity.class));
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }

    }

}
