package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 24/04/2017.
 */

public class CadastroActivity extends AppCompatActivity {
    private Button cadastrar;
    private ImageButton backButton;
    private EditText email;
    private int PICK_IMAGE_REQUEST = 1;
    private EditText nome;
    private EditText senha;
    private EditText senhaConfirm;
    private FirebaseAuth autenticacao;
    private DatabaseReference firebaseDatabase;
    public User usuario;
    private StorageReference storage;
    public static String idUser;
    protected ProgressDialog progress ;


    private Base64Decoder decoder;
    private ArrayList<Integer> badgesList = new ArrayList<>();
    private CircleImageView userImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        //badgesList.add(0,2);

        LoginManager.getInstance().logOut();
        storage = FirebaseConfig.getFirebaseStorage().child("userImages");

        backButton = (ImageButton) findViewById(R.id.backButton);
        nome = (EditText) findViewById(R.id.cadastro_nome);
        email = (EditText) findViewById(R.id.cadastro_email);
        senha = (EditText) findViewById(R.id.cadastro_senha);
        senhaConfirm = (EditText) findViewById(R.id.cadastro_senhaConfirm);
        userImg = (CircleImageView) findViewById(R.id.cadastro_img);

        userImg.setOnClickListener(new View.OnClickListener() {
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

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(CadastroActivity.this, MainActivity.class));
            }
        });

        cadastrar = (Button) findViewById(R.id.buttonValidarCadstro);

        cadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CadastroActivity.this);

                alertDialog.setTitle("Este é um cadastro de empresa?");

                alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        AlertDialog.Builder putCNPJ = new AlertDialog.Builder(CadastroActivity.this);
                        putCNPJ.setMessage("Digite seu CNPJ");
                        final EditText editText = new EditText(CadastroActivity.this);
                        putCNPJ.setView(editText);


                        putCNPJ.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });

                        putCNPJ.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                usuario = new User();
                                usuario.setName(nome.getText().toString());
                                usuario.setEmail(email.getText().toString());
                                usuario.setSenha(senha.getText().toString());
                                idUser = Base64Decoder.encoderBase64(usuario.getEmail());
                                System.out.println("BASE64 ENCODER: " + idUser);
                                usuario.setId(idUser);
                                usuario.setMedalhas(badgesList);
                                String cpf = editText.getText().toString();
                                usuario.setCpf_cnpj(cpf);
                                cadastrarUsuario();
                            }
                        }).create().show();
                    }

                });

                alertDialog.setNegativeButton("NAO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (nome.getText() == null || email.getText() == null || senha.getText() == null) {
                            Toast.makeText(getApplicationContext(), "Preencha todos os campos antes do cadastro", Toast.LENGTH_SHORT).show();
                        } else {
                            usuario = new User();
                            usuario.setName(nome.getText().toString());
                            usuario.setEmail(email.getText().toString().toLowerCase());
                            usuario.setSenha(senha.getText().toString());
                            idUser = Base64Decoder.encoderBase64(usuario.getEmail());
                            System.out.println("BASE64 ENCODER: " + idUser);
                            usuario.setId(idUser);
                            usuario.setMedalhas(badgesList);
                            cadastrarUsuario();
                        }
                    }
                }).create().show();

                if (senha.getText().toString().equals(senhaConfirm.getText().toString())) {

                } else
                    Toast.makeText(getApplicationContext(), "Senha e confirmação devem ser iguais", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void cadastrarUsuario() {
        progress = new ProgressDialog(CadastroActivity.this);
        //timerDelayRemoveDialog(4, progress);
        autenticacao = FirebaseConfig.getFirebaseAuthentication();
        autenticacao.createUserWithEmailAndPassword(usuario.getEmail(), usuario.getSenha())
                .addOnCompleteListener(CadastroActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    try {
                                        progress.show(CadastroActivity.this, "Aguarde...",
                                                "Cadastrando usuario", true);
                                        Preferences preferences = new Preferences(CadastroActivity.this);
                                        preferences.saveData(idUser, usuario.getName());
                                        preferences.saveLogin(usuario.getEmail(), usuario.getSenha());

                                        usuario.setPremiumUser(1);

                                        // FirebaseUser usuarioFireBase = task.getResult().getUser();
                                        //ENCRYPT USER PASSWORD INTO THE SERVER

                                        usuario.setSenha(Base64Decoder.encoderBase64(usuario.getSenha()));

                                        autenticacao.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                //Log.d("CadastroActivity", aVoid.toString());
                                                Toast.makeText(getApplicationContext(), "Cadastro realizado, uma mensagem foi enviada para sua caixa de email. Por favor, acesse o link enviado", Toast.LENGTH_LONG).show();
                                                //usuario.save();
                                                if (progress.isShowing()) {
                                                    progress.dismiss();
                                                }
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getApplicationContext(), "Falha no envio de email, verifique se o email digitado é um email valido e tente novamente ", Toast.LENGTH_LONG).show();
                                                if (progress.isShowing()) {
                                                    progress.dismiss();
                                                }

                                            }
                                        });

                                        uploadImages();

                                    } catch (Exception e) {
                                        Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário: Tente novamente mais tarde", Toast.LENGTH_LONG).show();
                                        if (progress.isShowing()) {
                                            progress.dismiss();
                                        }
                                        System.out.println("error in task suceesful cadastrar usuario: " + e);
                                    }
                                } else {

                                    String erro = "";

                                    try {
                                        throw task.getException();
                                    } catch (FirebaseAuthWeakPasswordException e) {
                                        erro = "Escolha uma senha que contenha, letras e números.";

                                    } catch (FirebaseAuthInvalidCredentialsException e) {
                                        erro = "Email indicado não é válido.";

                                    } catch (FirebaseAuthUserCollisionException e) {
                                        erro = "Já existe uma conta com esse e-mail.";

                                    } catch (Exception e) {
                                        e.printStackTrace();

                                    }

                                    Toast.makeText(CadastroActivity.this, "Erro ao cadastrar usuário: " + erro, Toast.LENGTH_LONG).show();

                                }

                            }
                        }


                );

    }

    private void openProfieUser() {
        String nomeUser = usuario.getName().toString();
        Toast.makeText(CadastroActivity.this, "Olá " + nomeUser + ", bem vindo ao app Ajudaqui ", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(CadastroActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void uploadImages() {
        StorageReference imgRef = storage.child(idUser + ".png");
        System.out.println("lei lei " + idUser);
        //download img source
        userImg.setDrawingCacheEnabled(true);
        userImg.buildDrawingCache();
        Bitmap bitmap = userImg.getDrawingCache();
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

        openProfieUser();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("image", String.valueOf(bitmap));

                userImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error in get image ", e.toString());
            }
        }
    }

    public void timerDelayRemoveDialog(long time, final ProgressDialog d) {
        new Handler().postDelayed(new Runnable() {
            public void run() {
                d.dismiss();
            }
        }, time);
    }

}
