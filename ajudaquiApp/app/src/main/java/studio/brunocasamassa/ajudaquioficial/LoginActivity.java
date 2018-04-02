package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.mail.AuthenticationFailedException;
import javax.mail.MessagingException;

import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Mail;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;
import studio.brunocasamassa.ajudaquioficial.payment.TermosActivity;

/**
 * Created by bruno on 24/04/   2017.
 */

public class LoginActivity extends AppCompatActivity {

    private Button entrar;
    private TextView lostPassword;
    private User usuario;
    private EditText email;
    private EditText senha;
    private MyFirebaseInstanceIdService md = new MyFirebaseInstanceIdService();
    private FirebaseAuth autenticacao;
    private boolean termosAceitos = true;
    private DatabaseReference firebase;
    private DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");
    public static String idUser;
    private ValueEventListener valueEventListenerUsuario;
    private ImageButton backButton;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (autenticacao != null) {
            autenticacao.signOut();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final String ajudaquimail = Base64Decoder.encoderBase64("ajudaquisuporte@gmail.com");
        final String ajudaquipass = Base64Decoder.encoderBase64("ajudaqui931931931");

        backButton = (ImageButton) findViewById(R.id.backButton);
        email = (EditText) findViewById(R.id.email);
        senha = (EditText) findViewById(R.id.senha);
        entrar = (Button) findViewById(R.id.entrar);
        lostPassword = (TextView) findViewById(R.id.lostPassword);

        entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                usuario = new User();
                usuario.setEmail(email.getText().toString());
                usuario.setSenha(senha.getText().toString());
                validarLogin();
            }

        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (autenticacao != null) {
                    autenticacao.signOut();
                }
                finish();
            }
        });

        lostPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);

                alertDialog.setTitle("Esqueci minha senha");
                alertDialog.setMessage("Digite seu email abaixo, enviaremos sua senha para resgate");
                alertDialog.setCancelable(false);
                final EditText editText = new EditText(LoginActivity.this);
                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbUser.child(Base64Decoder.encoderBase64(editText.getText().toString())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                try {
                                    if (user.getSenha() != null) {
                                        String password = Base64Decoder.decoderBase64(user.getSenha());
                                        String[] recipients = {editText.getText().toString()};
                                        SendEmailAsyncTask email = new SendEmailAsyncTask();
                                        email.m = new Mail(Base64Decoder.decoderBase64(ajudaquimail), Base64Decoder.decoderBase64(ajudaquipass));
                                        email.m.set_from("ajudaquisuporte@gmail.com");
                                        email.m.setBody("Voce recebeu este e-mail porque foi requisitado um resgate de senha para o AJUDAQUIAPP, SUA SENHA É: " + password + "\n acesse o app para validar seu login... \n\n Um forte abraço da equipe Ajudaqui e por favor, nos mantenha atualizado com sua satisfação" + "\n\n Cordialmente,\n Equipe Ajudaqui ");
                                        email.m.set_to(recipients);
                                        email.m.set_subject("AJUDAQUI - RESGATE DE SENHA");
                                        email.execute();
                                        Toast.makeText(getApplicationContext(), "Mensagem enviada", Toast.LENGTH_SHORT).show();
                                    } else
                                        Toast.makeText(getApplicationContext(), "Falha ao enviar mensagem, Verifique o email digitado", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    System.out.println("exception " + e);
                                    Toast.makeText(getApplicationContext(), "Erro ao enviar mensagem", Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });

                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();

            }
        });


    }


    private void validarLogin() {

        System.out.println("USER EMAIL: " + usuario.getEmail() + " USER SENHA: " + usuario.getSenha());
        try {
            autenticacao = FirebaseConfig.getFirebaseAuthentication();
            autenticacao.signInWithEmailAndPassword(
                    usuario.getEmail(),
                    usuario.getSenha()
            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {


                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //System.out.println("FIA EMAIL VERIFICADO " + autenticacao.getCurrentUser().isEmailVerified());
                    if (task.isSuccessful()) {
                        if (autenticacao.getCurrentUser().isEmailVerified()) {

                            Toast.makeText(LoginActivity.this, "Sucesso ao fazer login!", Toast.LENGTH_LONG).show();

                            System.out.println("111USER EMAIL: " + usuario.getEmail() + "USER SENHA: " + usuario.getSenha());

                            idUser = Base64Decoder.encoderBase64(usuario.getEmail());

                            System.out.println("LA: decoder 64 " + idUser);
                            System.out.println("LA: FIREBASE " + firebase);

                            firebase = FirebaseConfig.getFireBase()
                                    .child("usuarios");

                            firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child(idUser).exists()) {
                                        setLoggedUser();
                                        Toast.makeText(LoginActivity.this, "Sucesso ao fazer login!", Toast.LENGTH_LONG).show();
                                    } else {
                                        createUserInDB();
                                        System.out.println("CRIAUSUARIO");

                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });


                        } else if (!autenticacao.getCurrentUser().isEmailVerified()) {
                            Toast.makeText(getApplicationContext(), "Email ainda nao verificado, favor confirmá-lo para entrar no Ajudaqui", Toast.LENGTH_LONG).show();
                            showAnotherVerifyMessage();
                        }

                    } else {
                        Toast.makeText(LoginActivity.this, "Erro ao fazer login!", Toast.LENGTH_LONG).show();
                        System.out.println("FIA TASK EXCEPTION" + task.getException());

                    }
                }
            });

        } catch (Exception e) {
            System.out.println("FIA EXCEPTION " + e);
        }
    }

    private void createUserInDB() {
        android.support.v7.app.AlertDialog.Builder alertDialog = new android.support.v7.app.AlertDialog.Builder(LoginActivity.this);

        alertDialog.setTitle("Nome do Usuario:");
        alertDialog.setMessage("Verificamos que esta é sua primeira entrada no Ajudaqui, como gostaria de ser chamado?");
        alertDialog.setCancelable(false);
        final EditText name = new EditText(LoginActivity.this);
        alertDialog.setView(name);

        alertDialog.setPositiveButton("ENTRAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProgressDialog progress = new ProgressDialog(LoginActivity.this);
                progress.show(LoginActivity.this, "Aguarde...",
                        "Analisando dados do usuario", true);
                usuario.setName(name.getText().toString());
                usuario.setPremiumUser(1);
                usuario.setMaxDistance(30);
                usuario.setTermosAceitos(false);
                usuario.setSenha(Base64Decoder.encoderBase64(usuario.getSenha()));
                usuario.setEmail(usuario.getEmail());
                usuario.setId(idUser);
                ArrayList<Integer> badgesList = new ArrayList<Integer>();
                usuario.setMedalhas(badgesList);
                System.out.println("user name1 " + usuario.getName());
                Preferences preferences = new Preferences(LoginActivity.this);
                preferences.saveData(usuario.getId(), usuario.getName());

                ArrayList<String> entradas = new ArrayList<String>();
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
                format.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
                String currentTime = format.format(new Date());
                //Toast.makeText(getApplicationContext(), currentTime, Toast.LENGTH_SHORT).show();

                if (currentTime != null) {
                    if (usuario.getEntradas() != null) {
                        entradas.addAll(usuario.getEntradas());
                        entradas.add(entradas.size(), currentTime);
                        usuario.setEntradas(entradas);
                    } else {
                        entradas.add(0, currentTime);
                        usuario.setEntradas(entradas);
                    }

                }
                md.onTokenRefresh();

                usuario.save();

                abrirTelaPrincipal(usuario.isTermosAceitos());
                Toast.makeText(LoginActivity.this, "Sucesso ao fazer login!", Toast.LENGTH_LONG).show();
            }

        }).create().show();


    }

    public void setLoggedUser() {
        //IF EXISTS
        DatabaseReference dbLogin = FirebaseConfig.getFireBase()
                .child("usuarios");

        dbLogin.child(idUser).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User usuarioRecuperado = dataSnapshot.getValue(User.class);
                termosAceitos = usuarioRecuperado.isTermosAceitos();
                usuarioRecuperado.setMaxDistance(30);
                System.out.println("LA: Data Changed " + usuarioRecuperado + "termos aceitos: "+ termosAceitos );
                Preferences preferencias = new Preferences(LoginActivity.this);
                preferencias.saveData(idUser, usuarioRecuperado.getName());

                ArrayList<String> entradas = new ArrayList<String>();
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy hh:mm:ss Z");
                format.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
                String currentTime = format.format(new Date());
                //Toast.makeText(getApplicationContext(), currentTime, Toast.LENGTH_SHORT).show();

                if (currentTime != null) {
                    if (usuarioRecuperado.getEntradas() != null) {
                        entradas.addAll(usuarioRecuperado.getEntradas());
                        entradas.add(entradas.size(), currentTime);
                        usuarioRecuperado.setEntradas(entradas);
                    } else {
                        entradas.add(0, currentTime);
                        usuarioRecuperado.setEntradas(entradas);
                    }

                }
                md.onTokenRefresh();
                usuarioRecuperado.save();

                abrirTelaPrincipal(termosAceitos);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("CANCELADO " + databaseError);
            }
        });
    }

    private void showAnotherVerifyMessage() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(LoginActivity.this);
        alertDialog.setTitle("Reenviar Email");
        alertDialog.setMessage("Verificamos que você já tentou se cadastrar com a gente, mas ainda nao verificou seu e-mail, gostaria de reenviá-lo?");
        alertDialog.setCancelable(false);


        alertDialog.setPositiveButton("ENVIAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autenticacao.getCurrentUser().sendEmailVerification();
            }
        }).setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                autenticacao.signOut();
                finish();

            }
        }).create().show();
    }


    private void abrirTelaPrincipal(boolean termosAceitos) {
        Intent intent = new Intent(LoginActivity.this, PedidosActivity.class);
        Intent termos = new Intent(LoginActivity.this, TermosActivity.class);
        termos.putExtra("cameFrom", 0);

        System.out.println("termos abrir tela principal "+ termosAceitos);
        Preferences preferences = new Preferences(LoginActivity.this);
        preferences.saveLogin(email.getText().toString(), senha.getText().toString());
        try {
            if (termosAceitos) {
                startActivity(intent);
            } else if (!termosAceitos) {
                startActivity(termos);
            }
        } catch (Exception e) {
            startActivity(intent);
        }

        finish();
    }


    public void displayMessage(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


}

class SendEmailAsyncTask extends AsyncTask<Void, Void, Boolean> {
    Mail m;
    LoginActivity activity;

    public SendEmailAsyncTask() {
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        try {
            if (m.send()) {
                // activity.displayMessage("Email sent.");
            } else {
                // activity.displayMessage("Email failed to send.");
            }

            return true;
        } catch (AuthenticationFailedException e) {
            System.out.println(SendEmailAsyncTask.class.getName() + "Bad account details");
            e.printStackTrace();
            // activity.displayMessage("Authentication failed.");
            return false;
        } catch (MessagingException e) {
            System.out.println(SendEmailAsyncTask.class.getName() + "Email failed");
            e.printStackTrace();
            //  activity.displayMessage("Email failed to send.");
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            // activity.displayMessage("Unexpected error occured.");
            return false;
        }
    }
}