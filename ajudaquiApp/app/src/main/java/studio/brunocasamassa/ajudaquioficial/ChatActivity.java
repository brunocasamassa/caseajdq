package studio.brunocasamassa.ajudaquioficial;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import studio.brunocasamassa.ajudaquioficial.adapters.MensagemAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.Conversa;
import studio.brunocasamassa.ajudaquioficial.helper.FileModel;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Mensagem;
import studio.brunocasamassa.ajudaquioficial.helper.Notification;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;

public class ChatActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText editMensagem;
    private ImageButton btMensagem;
    private ImageButton btFoto;
    private DatabaseReference firebase;
    private DatabaseReference dbUserDestinatario;
    private DatabaseReference dbUserRemetente;
    private DatabaseReference dbUser;
    private ListView listView;
    private ArrayList<Mensagem> mensagens;
    private ArrayAdapter<Mensagem> adapter;
    private ValueEventListener valueEventListenerMensagem;

    // dados do destinatário
    private String idPedido;
    private String idUsuarioDestinatario;
    private String nomePedido;


    // dados do rementente
    private String userKey;
    private String nomeUsuarioRemetente;
    private int chatCount = 0;
    private boolean trigger = true;
    static final String TAG = MainActivity.class.getSimpleName();
    private int IMAGE_GALLERY_REQUEST = 1;

    @Override
    protected void onStart() {
        super.onStart();


    }

    @Override
    protected void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerMensagem);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = (Toolbar) findViewById(R.id.tb_conversa);
        editMensagem = (EditText) findViewById(R.id.edit_mensagem);
        btMensagem = (ImageButton) findViewById(R.id.bt_enviar);
        btFoto = (ImageButton) findViewById(R.id.bt_enviarImagem);
        listView = (ListView) findViewById(R.id.lv_conversas);

        // dados do usuário logado
        Preferences preferencias = new Preferences(ChatActivity.this);
        userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
        nomeUsuarioRemetente = preferencias.getNome();

        Bundle extra = getIntent().getExtras();

        if (extra != null) {

            idPedido = Base64Decoder.encoderBase64(extra.getString("nome"));
            String emailDestinatario = extra.getString("email");
            idUsuarioDestinatario = Base64Decoder.encoderBase64(emailDestinatario);
            nomePedido = extra.getString("nome");

        }

        // Configura toolbar
        toolbar.setTitle(nomePedido);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, ConversasActivity.class));
            }
        });

        //VERIFICA CONTAGEM DO CHAT E ZERA AO ENTRAR

        dbUserRemetente = FirebaseConfig.getFireBase().child("conversas");
        FirebaseConfig.getFireBase().child("usuarios").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                dbUserRemetente.child(userKey /* <- userKey*/).child(idPedido).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Conversa conversa = dataSnapshot.getValue(Conversa.class);
                        try {
                            user.setChatNotificationCount(user.getChatNotificationCount() - conversa.getChatCount());
                            conversa.setChatCount(0);
                        } catch (Exception e){
                            e.printStackTrace();
                        }

                        DatabaseReference saveDb = FirebaseConfig.getFireBase().child("conversas").child(userKey);
                        saveDb.child(idPedido).setValue(conversa);
                       // System.out.println("Chat activity update chat count no userKey (null == zerou(precisa conversa.save))" + conversa.getMensagem());

                        //caso algum erro diminua o valor do count para <0
                        if (user.getChatNotificationCount() < 0) {
                            user.setChatNotificationCount(0);
                        }

                        user.save();
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

        // Monta listview e adapter
        mensagens = new ArrayList<>();
        adapter = new MensagemAdapter(ChatActivity.this, mensagens);

        listView.setDivider(null);
        listView.setAdapter(adapter);

        // Recuperar mensagens do Firebase
        firebase = FirebaseConfig.getFireBase()
                .child("mensagens")
                .child(userKey)
                .child(idPedido);

        // Cria listener para mensagens
        valueEventListenerMensagem = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Limpar mensagens
                mensagens.clear();

                // Recupera mensagens
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    Mensagem mensagem = dados.getValue(Mensagem.class);
                    mensagens.add(mensagem);
                    adapter.notifyDataSetChanged();

                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        int friendlyMessageCount = adapter.getCount();
        int lastVisiblePosition = listView.getLastVisiblePosition();
        try {
            listView.scrollTo(listView.getScrollX(), lastVisiblePosition);
        } catch (Exception e){
            Log.w("TAG", "failed scroll chat by positionX "+ e.toString());
            try{
                listView.scrollTo(friendlyMessageCount, lastVisiblePosition);
            } catch (Exception d){
                Log.w("TAG", "failed scroll chat by MESAGECOUNT " + d.toString());
            }
        }

        firebase.addValueEventListener(valueEventListenerMensagem);
        // Enviar mensagem
        btMensagem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String textoMensagem = editMensagem.getText().toString();

                if (textoMensagem.isEmpty()) {
                    Toast.makeText(ChatActivity.this, "Digite uma mensagem para enviar!", Toast.LENGTH_LONG).show();

                } else {
                    sendMessage(textoMensagem, null);
                    editMensagem.setText("");

                }

            }
        });

        btFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoGalleryIntent();

            }
        });


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Mensagem mensagem1 = mensagens.get(position);
                if (mensagem1.getFile() != null ){
                    Intent intent = new Intent(ChatActivity.this,FullScreenImageActivity.class);
                    intent.putExtra("nameUser",nomeUsuarioRemetente);
                    intent.putExtra("urlPhotoUser",mensagem1.getFile().getUrl_file());
                    intent.putExtra("urlPhotoClick",mensagem1.getFile().getUrl_file());
                    startActivity(intent);



                }

            }
        });

    }

    private void sendMessage(String textoMensagem, FileModel file) {
        chatCount++;
        Mensagem mensagem = new Mensagem();
        mensagem.setIdUsuario(userKey);
        mensagem.setMensagem(textoMensagem);
        if (file != null) {
            mensagem.setFile(file);
        }

        // salvando mensagem para o remetente
        Boolean retornoMensagemRemetente = salvarMensagem(userKey, idUsuarioDestinatario, mensagem);
        if (!retornoMensagemRemetente) {
            Toast.makeText(
                    ChatActivity.this,
                    "Problema ao salvar mensagem, tente novamente!",
                    Toast.LENGTH_LONG
            ).show();

        } else {

            //salvando mensagem para o destinatario
            Boolean retornoMensagemDestinatario = salvarMensagem(idUsuarioDestinatario, userKey, mensagem);
            if (!retornoMensagemDestinatario) {
                Toast.makeText(
                        ChatActivity.this,
                        "Problema ao enviar mensagem para o destinatário, tente novamente!",
                        Toast.LENGTH_LONG
                ).show();
            } else {
                if (trigger) {
                    sendNotification(idUsuarioDestinatario, mensagem, nomePedido);
                    trigger = false;
                }
            }


        }

        // salvamos Conversa para o remetente
        DateFormat formatter = new SimpleDateFormat("HH:mm");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
        String currentTime = formatter.format(new Date());
        System.out.println("formatter: " + currentTime);

        Conversa conversa = new Conversa();
        conversa.setIdUsuario(idUsuarioDestinatario);
        conversa.setNome(nomePedido);
        conversa.setMensagem(textoMensagem);
        conversa.setTime(currentTime);
        Boolean retornoConversaRemetente = salvarConversa(userKey, idUsuarioDestinatario, conversa);
        if (!retornoConversaRemetente) {
            Toast.makeText(
                    ChatActivity.this,
                    "Problema ao salvar conversa, tente novamente!",
                    Toast.LENGTH_LONG
            ).show();
        } else {

            // salvamos Conversa para o Destinatario

            conversa = new Conversa();
            conversa.setIdUsuario(userKey);
            conversa.setNome(nomePedido);
            conversa.setChatCount(chatCount);
            conversa.setMensagem(textoMensagem);
            conversa.setTime(currentTime);

            Boolean retornoConversaDestinatario = salvarConversa(idUsuarioDestinatario, userKey, conversa);
            if (!retornoConversaDestinatario) {
                Toast.makeText(
                        ChatActivity.this,
                        "Problema ao salvar conversa para o destinatário, tente novamente!",
                        Toast.LENGTH_LONG
                ).show();
            }

        }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_exit:
                //logoutUser();
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, MainActivity.class));
                return true;


            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void sendNotification(String idUsuarioDestinatario, final Mensagem mensagem, final String nomePedido) {

        DatabaseReference dbDestinatario = FirebaseConfig.getFireBase().child("usuarios");
        dbDestinatario.child(idUsuarioDestinatario).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                String userToken = user.getNotificationToken();
                String userId = user.getId();

                Notification notifs = new Notification();
                notifs.setToken(userToken);
                notifs.setUid(userId);
                notifs.setMessage(mensagem.getMensagem());
                notifs.setCommand("chat");
                notifs.setTitle("AJUDAQUI - " + nomePedido);

                FirebaseConfig.getNotificationRef().child(userId).setValue(notifs);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private boolean salvarMensagem(String idRemetente, final String idDestinatario, Mensagem mensagem) {

        try {

            firebase = FirebaseConfig.getFireBase().child("mensagens");

            firebase.child(idRemetente)
                    .child(idPedido)
                    .push()
                    .setValue(mensagem);

            dbUserDestinatario = FirebaseConfig.getFireBase().child("usuarios");

            dbUserDestinatario.child(idUsuarioDestinatario).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    user.setId(idUsuarioDestinatario);
                    user.setChatNotificationCount(user.getChatNotificationCount() + 1);
                    user.save();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    private boolean salvarConversa(String idRemetente, String idDestinatario, Conversa conversa) {
        try {
            firebase = FirebaseConfig.getFireBase().child("conversas");
            firebase.child(idRemetente)
                    .child(idPedido)
                    .setValue(conversa);

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void refresh() {
        Intent intent = getIntent();
        finish();
        System.out.println("REFRESHED");
        startActivity(intent);
    }

    /**
     * Enviar foto pela galeria
     */
    private void photoGalleryIntent() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, getString(R.string.select_picture_title)), IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        StorageReference storageRef = FirebaseConfig.getFirebaseStorage().child("fileImages");

        if (requestCode == IMAGE_GALLERY_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    sendFileFirebase(storageRef, selectedImageUri);
                } else {
                    //URI IS NULL
                }
            }
        }
    }

    private void sendFileFirebase(StorageReference storageReference, final Uri file) {
        if (storageReference != null) {
            final String name = android.text.format.DateFormat.format("yyyy-MM-dd_hhmmss", new Date()).toString();
            StorageReference imageGalleryRef = storageReference.child(name + "_gallery");
            UploadTask uploadTask = imageGalleryRef.putFile(file);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "onFailure sendFileFirebase " + e.getMessage());
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Log.i(TAG, "onSuccess sendFileFirebase");
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    FileModel file = new FileModel("img", downloadUrl.toString(), name, "");
                    //Mensagem mensagem = new Mensagem(userModel,"", Calendar.getInstance().getTime().getTime()+"",fileModel);

                    sendMessage("", file);
                    Toast.makeText(getApplicationContext(),"Imagem Enviada", Toast.LENGTH_SHORT).show();
                    refresh();
                }
            });
        } else {
            //IS NULL
        }

    }

}
