package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import studio.brunocasamassa.ajudaquioficial.adapters.SobreAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;

/**
 * Created by bruno on 24/04/2017.
 */

public class SobreActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ListView listview_nomes;
    private ViewPager viewPager;
    private ArrayAdapter arrayAdapterSobre;
    private SlidingTabLayout slidingTabLayout;
    public int posicao;
    private String SUPPORT_EMAIL = "contato@ajudaqui.com.br";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activvity_sobre);

        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_sobre);
        toolbar.setTitle(getResources().getString(R.string.menu_sobre));
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        listview_nomes = (ListView) findViewById(R.id.sobre_lista);
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        final NavigationDrawer navigator = new NavigationDrawer();
        navigator.createDrawer(SobreActivity.this, toolbar, 9);

        String[] vector = {"Duvidas", "Fale Conosco", "Denuncie", "Curta no Facebook", "Acesse o site" /*, "Termos de Uso"*/};
        //ArrayList<String> listaSobre = new ArrayList<>();

        arrayAdapterSobre = new SobreAdapter(getApplicationContext(), vector);

        listview_nomes.setDivider(null);

        listview_nomes.setAdapter(arrayAdapterSobre);

        listview_nomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> parent, View view, int position, long id) {
                if (position == 0){
                    startActivity(new Intent(SobreActivity.this, FaqActivity.class));
                }
                if (position == 1) {
                    //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    enviaEmailSuporte("Suporte ao Usuario", "Escreva uma mensagem para nós, adoraremos te conhecer", "FALE CONOSCO");
                }
                if (position == 2){
                    enviaEmailSuporte("Denuncia", "Por favor, nos conte o que ocorreu: ", "DENUNCIA");
                }

                if(position == 3){
                    //newFacebookIntent(getPackageManager(),"https://www.facebook.com/ajudaquiapp/");
                    Uri uri = Uri.parse("https://www.facebook.com/ajudaquiapp/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);

                }

                if (position ==4){
                    Uri uri = Uri.parse("http://www.ajudaqui.com.br/");
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);

                    startActivity(intent);
                }

               /* if (position ==5){
                    Intent intent = new Intent(SobreActivity.this, TermosActivity.class);
                    intent.putExtra("cameFrom" , 0);
                    finish();
                    startActivity(intent);
                }*/



            }
        });
    }

    public static Intent newFacebookIntent(PackageManager pm, String url) {
        Uri uri = Uri.parse(url);
        try {
            ApplicationInfo applicationInfo = pm.getApplicationInfo("com.facebook.katana", 0);
            if (applicationInfo.enabled) {
                // http://stackoverflow.com/a/24547437/1048340
                uri = Uri.parse("fb://facewebmodal/f?href=" + url);
            }
        } catch (PackageManager.NameNotFoundException ignored) {
            System.out.println("sobre error "+ignored);
        } catch (Exception e ){
            System.out.println("sobre error "+e);
        }
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    private void enviaEmailSuporte(String titulo, String mensagem, final String tipo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SobreActivity.this);

        alertDialog.setTitle(titulo);
        alertDialog.setMessage(mensagem);
        alertDialog.setCancelable(false);

        final EditText editText = new EditText(SobreActivity.this);
        alertDialog.setView(editText);

        alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }

        });

        alertDialog.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                final String mensagemSolicitacao = editText.getText().toString();

                //Validate message to contact
                if (mensagemSolicitacao.isEmpty()) {
                    Toast.makeText(SobreActivity.this, "Preencha o campo de mensagem", Toast.LENGTH_LONG).show();
                } else {
                    Preferences preferences = new Preferences(SobreActivity.this);
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{SUPPORT_EMAIL});
                    i.putExtra(Intent.EXTRA_SUBJECT, "AJUDAQUI: "+tipo+": USUARIO: " + preferences.getNome());
                    i.putExtra(Intent.EXTRA_TEXT,
                            " " + Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail()) + "\n" +
                                    "USUARIO NOME: " + preferences.getNome() + "\n" +
                                    "USUARIO E-MAIL: " + FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail()+ "\n" +
                                    "\n" + mensagemSolicitacao);
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                        Toast.makeText(SobreActivity.this, "Obrigado pela mensagem, entraremos em contato o mais breve possível", Toast.LENGTH_SHORT).show();
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(SobreActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).create().show();

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
                Preferences preferences = new Preferences(SobreActivity.this);
                preferences.clearSession();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(SobreActivity.this, MainActivity.class));
                //logoutUser();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(SobreActivity.this, ConfiguracoesActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}

