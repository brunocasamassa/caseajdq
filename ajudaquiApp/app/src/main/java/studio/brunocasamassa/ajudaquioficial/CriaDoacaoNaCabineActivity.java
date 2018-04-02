package studio.brunocasamassa.ajudaquioficial;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import co.lujun.androidtagview.TagContainerLayout;
import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 22/05/2017.
 */

public class CriaDoacaoNaCabineActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private EditText pedidoName;
    private int PICK_IMAGE_REQUEST = 1;
    private EditText descricao;
    private Button createButton;
    private String tagCaptured;
    ArrayList<String> tagsCaptured = new ArrayList<String>();
    private TagContainerLayout categorias;
    private TagContainerLayout grupos;
    private TextView add_grupos;
    private TextView add_tags;
    private Pedido pedido;
    private CardView cardView;
    private int premium;
    private ImageButton addTagButton;
    private ImageButton addGroupButton;
    private String tipoPedido;   //doacao, servico, troca, emprestimo
    private String userKey = Base64Decoder.encoderBase64(FirebaseAuth.getInstance().getCurrentUser().getEmail());
    private String groupKey;
    private SeekBar doacaoQtd;
    private int qtd;
    private int naCabine = 0;
    private StorageReference storage;
    private Double latitude;
    private Double longitude;
    private TextView seekValue;
    private CircleImageView img;
    private String groupCaptured;
    private String idGroupSelected;
    private String pedidoGroup;
    private EditText donationContact;
    private EditText endereco;
    private boolean HAS_IMAGE;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onStart() {
        super.onStart();
    /*    if (pedidoGroup != null) {
            grupos.setTags(pedidoGroup);
        } else if (groupCaptured != null) {
            grupos.setTags(groupCaptured);
        }*/

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_doacao);
        final Bundle extras = getIntent().getExtras();


        latitude = (extras.getDouble("latitude"));
        longitude = (extras.getDouble("longitude"));

        storage = FirebaseConfig.getFirebaseStorage().child("donationImages");
        if (latitude == null) {
            latitude = 0.0;
            longitude = 0.0;
        }

        pedidoGroup = (extras.getString("groupName"));

        if (extras.getString("idGroupSelected") != null) {
            idGroupSelected = (extras.getString("idGroupSelected"));
        }
        System.out.println("CRIA DOACAO" + idGroupSelected);

        toolbar = (Toolbar) findViewById(R.id.toolbar_create_group);
        toolbar.setTitle("Criar doação");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        /*grupos = (TagContainerLayout) findViewById(R.id.tagGroupGruposDoacao);
        grupos.setBackgroundColor(Color.TRANSPARENT);*/

        seekValue = (TextView) findViewById(R.id.seekbar_value);
        doacaoQtd = (SeekBar) findViewById(R.id.seekBar);
        donationContact = (EditText) findViewById(R.id.donation_contact);
        endereco = (EditText) findViewById(R.id.endereco);
        add_tags = (TextView) findViewById(R.id.word_add_tags);
        pedidoName = (EditText) findViewById(R.id.create_pedido_name);
        descricao = (EditText) findViewById(R.id.create_pedido_description);
        createButton = (Button) findViewById(R.id.create_pedido_button);
        addTagButton = (ImageButton) findViewById((R.id.add_tag_button));
        cardView = (CardView) findViewById(R.id.cardview_create_doacao);
        /*add_grupos = (TextView) findViewById(R.id.word_add_groups);
        addGroupButton = (ImageButton) findViewById((R.id.addGroup_tag_button));*/
        img = (CircleImageView) findViewById(R.id.import_donation_img);

        fitLayoutForSmallDevices();

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            }
        });

        //System.out.println("taggroup " + grupos.getTags().size());
        doacaoQtd.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                qtd = progress + 10;
                seekValue.setText(String.valueOf(progress + 10));
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
                if (qtd < 10) {
                    Toast.makeText(getApplicationContext(), "Insira um valor minimo de 10", Toast.LENGTH_LONG).show();
                }
               else if (pedidoName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira um Titulo para o pedido", Toast.LENGTH_LONG).show();
                    //return;
                }
                else if (descricao.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Insira uma descricao para o pedido", Toast.LENGTH_LONG).show();
                    //return;
                }
                else if (!HAS_IMAGE){
                    Toast.makeText(getApplicationContext(), "Insira uma Imagem para o pedido", Toast.LENGTH_LONG).show();
                   // return;
                }
                else if(endereco.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Insira um endereço para as doações", Toast.LENGTH_LONG).show();
                }

                else if(donationContact.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Quem será o responsável pela entrega?", Toast.LENGTH_LONG).show();
                }
                else {
                    naCabine = 1;
                    createPedido();
                }

            }
        });
    }

    private void uploadImageDonation() {


            StorageReference imgRef = storage.child(pedido.getIdPedido() + ".png");
            //download img source
            img.setDrawingCacheEnabled(true);
            img.buildDrawingCache();
            Bitmap bitmap = img.getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = imgRef.putBytes(data);
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                    System.out.println("huehuebrjava " + downloadUrl);
                }
            });

            DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
            dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User usuario = dataSnapshot.getValue(User.class);
                    usuario.setCreditos(usuario.getCreditos() + 5);
                    ArrayList<String> totalPedidos = new ArrayList<String>();
                    if (usuario.getPedidosFeitos() != null) {
                        totalPedidos.addAll(usuario.getPedidosFeitos());
                        totalPedidos.add(totalPedidos.size(), pedido.getIdPedido());
                        usuario.setPedidosFeitos(totalPedidos);
                    } else {
                        totalPedidos.add(0, pedido.getIdPedido());
                        usuario.setPedidosFeitos(totalPedidos);
                    }
                    usuario.save();

                    Toast.makeText(getApplicationContext(), "Doação gerada com sucesso", Toast.LENGTH_LONG).show();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        refresh();


    }

    private void fitLayoutForSmallDevices() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        //Toast.makeText(getApplicationContext(), "width "+ width + " height "+ height , Toast.LENGTH_SHORT).show();

        int currentapiVersion = Build.VERSION.SDK_INT;

        //manually fits layout in small devices --- i am really sorry for that

        if (currentapiVersion <= 19 && width <= 790) {
            System.out.println("entrei huehuebr");
            ImageView h = (ImageView) findViewById(R.id.imageView11);
            ImageView i = (ImageView) findViewById(R.id.imageView10);
            ImageView j = (ImageView) findViewById(R.id.imageView9);
            ImageView k = (ImageView) findViewById(R.id.imageView);
            TextView v = (TextView) findViewById(R.id.textView6);
            TextView w = (TextView) findViewById(R.id.textView5);
            TextView x = (TextView) findViewById(R.id.textView18);
            TextView y = (TextView) findViewById(R.id.textView20);
            TextView z = (TextView) findViewById(R.id.textView4);
            h.setTranslationY(Float.valueOf(-105));
            i.setTranslationY(Float.valueOf(-105));
            j.setTranslationY(Float.valueOf(-105));
            k.setTranslationY(Float.valueOf(-105));
            v.setTranslationY(Float.valueOf(-105));
            w.setTranslationY(Float.valueOf(-105));
            x.setTranslationY(Float.valueOf(-105));
            y.setTranslationY(Float.valueOf(-105));
            z.setTranslationY(Float.valueOf(-105));
            doacaoQtd.setTranslationY(Float.valueOf(-105));
            donationContact.setTranslationY(Float.valueOf(-105));
            endereco.setTranslationY(Float.valueOf(-105));
            seekValue.setTranslationY(Float.valueOf(-105));
            descricao.setTranslationY(Float.valueOf(-105));
            img.setTranslationY(Float.valueOf(-85));
            img.setBorderColor(Color.TRANSPARENT);
            pedidoName.setTranslationX(pedidoName.getTranslationX() - 50);
            cardView.setTranslationY(Float.valueOf(-25));

        } else if (currentapiVersion <= 19) {
            cardView.setTranslationY(Float.valueOf(-35));

        }
    }

    private void createPedido() {
        AlertDialog.Builder alert = new AlertDialog.Builder(CriaDoacaoNaCabineActivity.this);
        alert.setTitle("Dados para contato:");
        alert.setMessage("O seu numero de telefone é um campo obrigatório para que as pessoas possam entrar em contato a fim de receber mais detalhes sobre a doação: ");
        final EditText edit = new EditText(CriaDoacaoNaCabineActivity.this);
        edit.setHint("0000-0000");
        alert.setView(edit);
        alert.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(edit.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(), "Você deve especificar um número para contato", Toast.LENGTH_SHORT).show();
                    return;
                }
                else{
                try {
                    pedido = new Pedido();

                    System.out.println("tipo de pedido: " + tipoPedido);
                    pedido.setTagsCategoria(tagsCaptured);
                    pedido.setTipo("Doacoes");

                    pedido.setDadosDoador(String.valueOf(edit.getText()));
                    pedido.setNaCabine(naCabine);
                    pedido.setLongitude(longitude);
                    pedido.setLatitude(latitude);
                    pedido.setQtdDoado(qtd);
                    pedido.setQtdAtual(qtd);

                    pedido.setStatus(5);
                    pedido.setDonationContact(donationContact.getText().toString());
                    pedido.setEndereco(endereco.getText().toString());
                    pedido.setDescricao(descricao.getText().toString());
                    pedido.setTitulo(pedidoName.getText().toString());
                    pedido.setIdPedido(Base64Decoder.encoderBase64(pedido.getTitulo()));
                    pedido.setCriadorId(userKey);

                    /*pedido.setGroupId(idGroupSelected);
                    pedido.setGrupo(groupCaptured);*/


                    System.out.println("user id key " + userKey);

                    uploadImageDonation();

                    pedidoSaveIntoUser(true);

                    pedido.save();

                } catch (Exception e) {
                    System.out.println("exception CRIADOACAONACABINE " + e);
                }}


            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();

    }

    private void savePedidoIntoGroup(Pedido pedido) {
        final String groupKey = pedido.getGroupId();
        final String pedidoId = pedido.getIdPedido();
        System.out.println("group id cabine " + groupKey);
        final DatabaseReference dbGroup = FirebaseConfig.getFireBase().child("grupos");
        dbGroup.child(groupKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Grupo grupo = dataSnapshot.getValue(Grupo.class);
                System.out.println("groupo " + grupo.getNome());
                ArrayList<String> pedidosList = new ArrayList<>();
                if (grupo.getDoacoes() != null) {
                    pedidosList.addAll(grupo.getDoacoes());
                    pedidosList.add(pedidosList.size(), pedidoId);
                    grupo.setDoacoes(pedidosList);
                } else {
                    pedidosList.add(0, pedidoId);
                }
                dbGroup.child(groupKey).setValue(grupo);
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
                    ArrayList<String> doacoes = new ArrayList<String>();
                    if (user.getItensDoados() != null) {
                        doacoes.addAll(user.getItensDoados());
                        doacoes.add(doacoes.size(), pedido.getIdPedido());
                    } else doacoes.add(0, pedido.getIdPedido());

                    user.setItensDoados(doacoes);
                    user.save();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

        }
    }

    private void refresh() {
        Intent intent = new Intent(CriaDoacaoNaCabineActivity.this, PedidosActivity.class);
        finish();
        startActivity(intent);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2) {
            if (resultCode == Activity.RESULT_OK) {
                groupCaptured = data.getStringExtra("groupSelected");
                idGroupSelected = data.getStringExtra("idGroupSelected");
                System.out.println("CRIA DOACAO grupo capturada " + idGroupSelected);

            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("image", String.valueOf(bitmap));

                img.setImageBitmap(bitmap);

                HAS_IMAGE = true;
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error in get image ", e.toString());
            }
        }

    }


}



