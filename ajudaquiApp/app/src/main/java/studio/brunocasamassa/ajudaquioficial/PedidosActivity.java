package studio.brunocasamassa.ajudaquioficial;

import android.Manifest;
import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import im.delight.android.location.SimpleLocation;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.ModelLatLong;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.PedidosTabAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Permissao;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;
import studio.brunocasamassa.ajudaquioficial.helper.User;

//import com.google.android.gms.location.FusedLocationProviderClient;

/**
 * Created by bruno on 24/04/2017.
 */

public class PedidosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private Toolbar toolbar;
    private ListView listview_nomes;
    private ViewPager viewPager;
    private ArrayAdapter<Pedido> arrayAdapter;
    private ArrayAdapter<Pedido> arrayEscolhidosAdapter;
    private ArrayAdapter<Pedido> arrayMeusPedidosAdapter;
    private SlidingTabLayout slidingTabLayout;
    private AlertDialog.Builder alertDialog;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private SimpleLocation localizacao;
    private String[] permissoesNecessarias = new String[]{
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
    };

    private String versionFromDb;
    private Double latitude;
    //private FusedLocationProviderClient mFusedLocationClient;
<<<<<<< HEAD
    private Double longitude = 0.0;
=======
    private Double longitude;
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
    private static NavigationDrawer navigator = new NavigationDrawer();
    private FloatingActionButton fab;
    private DatabaseReference getLocalization;


    @Override
    protected void onStart() {
        super.onStart();
        //getLocalization.addValueEventListener(localizationListener);

    }

    @Override
    protected void onResume() {
        super.onResume();


    /*


        localizacao.setListener(new SimpleLocation.Listener() {

            public void onPositionChanged() {
                // new location data has been received and can be accessed
                latitude = localizacao.getLatitude();
                longitude = localizacao.getLongitude();
                System.out.println("ENTREI RESUME "+ latitude + "  " + longitude);
            }

        });
*/
    }

    @Override
    protected void onPause() {
        // stop location updates (saves battery)
        // ...
        //localizacao.endUpdates();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localizacao.endUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //getLocalization.removeEventListener(localizationListener);
        //localizacao.endUpdates();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);

        freeMemory();

/*        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        Toast.makeText(getApplicationContext(),width +"  :  "+height,Toast.LENGTH_SHORT  ).show();
*/
<<<<<<< HEAD
=======
        fab = (FloatingActionButton) findViewById(R.id.fab);

        final Intent intent = new Intent(PedidosActivity.this, CriaPedidoActivity.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("latitude "+latitude);
                if (latitude == null) {
                    Toast.makeText(getApplicationContext(), "Você deve permitir o uso de geolocalização", Toast.LENGTH_SHORT).show();
                } else {

                    // System.out.println("PREMIUM PASSA" + premiumUser);
                    intent.putExtra("premium", 1);
                    intent.putExtra("creditos", 1);
                    intent.putExtra("latitude", latitude);
                    intent.putExtra("longitude", longitude);
                    startActivity(intent);

                }

            }
        });
>>>>>>> c143454de42efeeca8bf8931f097315860f35714


        localizacao = new SimpleLocation(PedidosActivity.this);

        boolean permissao = Permissao.validaPermissoes(1, PedidosActivity.this, permissoesNecessarias);

        if (permissao) {
            // if we can't access the location yet
            if (!localizacao.hasLocationEnabled()) {
                // ask the user to enable location access
                SimpleLocation.openSettings(PedidosActivity.this);
                System.out.println("ENTREI NAO tem permissao");

                /*location = new Location();
                location.setLatitude(localizacao.getLatitude());
                location.setLongitude(localizacao.getLongitude());*/
                latitude = localizacao.getLatitude();
                longitude = localizacao.getLongitude();

                makeUseOfNewLocation(
<<<<<<< HEAD
                      latitude,
=======
                        latitude,
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                        longitude);

            } else if (localizacao.hasLocationEnabled()) {
                //SimpleLocation.openSettings(PedidosActivity.this);
                localizacao.beginUpdates();

                // SimpleLocation.openSettings(PedidosActivity.this);

                System.out.println(" ENTREI tem permissao");

                latitude = localizacao.getLatitude();
                longitude = localizacao.getLongitude();

                makeUseOfNewLocation(latitude, longitude);

<<<<<<< HEAD
                final Intent intent = new Intent(PedidosActivity.this, CriaPedidoActivity.class);

                fab = (FloatingActionButton) findViewById(R.id.fab);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // System.out.println("PREMIUM PASSA" + premiumUser);
                        intent.putExtra("premium", 1);
                        intent.putExtra("creditos", 1);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        startActivity(intent);
=======
//                final Intent intent = new Intent(PedidosActivity.this, CriaPedidoActivity.class);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    System.out.println("latitude "+latitude);
                        if (latitude == null) {
                            Toast.makeText(getApplicationContext(), "Você deve permitir o uso de geolocalização", Toast.LENGTH_SHORT).show();
                        } else {

                            // System.out.println("PREMIUM PASSA" + premiumUser);
                            intent.putExtra("premium", 1);
                            intent.putExtra("creditos", 1);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            startActivity(intent);

                        }

>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                    }
                });

            }
        }

<<<<<<< HEAD
=======
/*

        final Intent intent = new Intent(PedidosActivity.this, CriaPedidoActivity.class);


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // System.out.println("PREMIUM PASSA" + premiumUser);
                intent.putExtra("premium", 1);
                intent.putExtra("creditos", 1);
                intent.putExtra("latitude", latitude);
                intent.putExtra("longitude", longitude);

                if(latitude == null){
                    Toast.makeText(getBaseContext(), "Você deve permitir o uso de geolocalização", Toast.LENGTH_SHORT);
                } else{
                    startActivity(intent);
                }

            }
        });
*/

>>>>>>> c143454de42efeeca8bf8931f097315860f35714

        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        toolbar.setTitle(getResources().getString(R.string.menu_pedidos));
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);
        Bundle extras = getIntent().getExtras();


        listview_nomes = (ListView) findViewById(R.id.ListContatos);

        viewPager = (ViewPager)

                findViewById(R.id.vp_pagina);

        slidingTabLayout = (SlidingTabLayout)

                findViewById(R.id.stl_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        PedidosTabAdapter pedidosTabAdapter = new PedidosTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pedidosTabAdapter);
        slidingTabLayout.setViewPager(viewPager);

        navigator.createDrawer(PedidosActivity.this, toolbar, 0);

        // checkVersion();
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
                final Preferences exitPreferences = new Preferences(PedidosActivity.this);
                exitPreferences.clearSession();
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                startActivity(new Intent(PedidosActivity.this, MainActivity.class));

                return true;

            case R.id.item_filter:

                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(PedidosActivity.this);

                final Preferences filterPreferences = new Preferences(PedidosActivity.this);
                alertDialog.setTitle("Filtrar: \nSelecione o tipo de pedido:");
                alertDialog.setCancelable(false);
                alertDialog.setNeutralButton("Limpar Filtro", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        filterPreferences.saveFilterPedido(null);
                        refresh();
                    }
                });
                alertDialog.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                alertDialog.setItems(new CharSequence[]
                                {"Serviços", "Emprestimos", "Trocas", "Doações"},
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // The 'which' argument contains the index position
                                // of the selected item
                                switch (which) {
                                    case 0:

                                        filterPreferences.saveFilterPedido("Servicos");
                                        refresh();
                                        // Toast.makeText(getApplicationContext(), "Servicos", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 1:
                                        filterPreferences.saveFilterPedido("Emprestimos");
                                        refresh();
                                        // Toast.makeText(getApplicationContext(), "Emprestimos", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 2:
                                        filterPreferences.saveFilterPedido("Trocas");
                                        refresh();
                                        //  Toast.makeText(getApplicationContext(), "Troca", Toast.LENGTH_SHORT).show();
                                        break;
                                    case 3:
                                        filterPreferences.saveFilterPedido("Doacao");
                                        refresh();
                                        // Toast.makeText(getApplicationContext(), "finalizando processo de backend, selecione outra opção", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        });
                alertDialog.create();
                alertDialog.show();
                return true;

            case R.id.item_search:
                SearchManager searchManager = (SearchManager)
                        getSystemService(Context.SEARCH_SERVICE);

                SearchView searchView = (SearchView) item.getActionView();

                searchView.setSearchableInfo(searchManager.
                        getSearchableInfo(getComponentName()));
                searchView.setSubmitButtonEnabled(true);
                searchView.setOnQueryTextListener(this);

                return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        refresh();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        arrayAdapter = getArrayAdapter();
        arrayAdapter.getFilter().filter(newText);
        arrayAdapter.notifyDataSetChanged();

        arrayEscolhidosAdapter = getArrayEscolhidosAdapter();
        arrayEscolhidosAdapter.getFilter().filter(newText);
        arrayEscolhidosAdapter.notifyDataSetChanged();

        arrayMeusPedidosAdapter = getArrayMeusPedidosAdapter();
        if (arrayMeusPedidosAdapter != null) { //fica null quando inicia a activity
            arrayMeusPedidosAdapter.getFilter().filter(newText);
            arrayMeusPedidosAdapter.notifyDataSetChanged();
        }
        return true;

    }

    public ArrayAdapter<Pedido> getArrayAdapter() {
        return arrayAdapter;
    }

    public void setArrayAdapter(ArrayAdapter<Pedido> arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
    }

    ;

    private void refresh() {
        Intent intent = new Intent(PedidosActivity.this, PedidosActivity.class);
        finish();
        startActivity(intent);
    }

    public ArrayAdapter<Pedido> getArrayEscolhidosAdapter() {
        return arrayEscolhidosAdapter;
    }

    public void setArrayEscolhidosAdapter(ArrayAdapter<Pedido> arrayEscolhidosAdapter) {
        this.arrayEscolhidosAdapter = arrayEscolhidosAdapter;
    }

    public ArrayAdapter<Pedido> getArrayMeusPedidosAdapter() {
        return arrayMeusPedidosAdapter;
    }

    public void setArrayMeusPedidosAdapter(ArrayAdapter<Pedido> arrayMeusPedidosAdapter) {
        this.arrayMeusPedidosAdapter = arrayMeusPedidosAdapter;
    }

    private boolean checkVersion() {
        //=----------------------------

        /*DatabaseReference version = FirebaseConfig.getFireBase().child("version");
        version.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Double versionDouble = dataSnapshot.getValue(Double.class);
                String versionFromDb = versionDouble.toString();*/
        VersionChecker versionChecker = new VersionChecker();


        try {
            versionFromDb = versionChecker.execute().get().toString();
            System.out.println("version from web: " + versionFromDb);
        } catch (InterruptedException e) {
            System.out.println("version from web: ERROR " + e);
            e.printStackTrace();
        } catch (ExecutionException e) {
            System.out.println("version from web: ERROR " + e);
            e.printStackTrace();
        }

        PackageInfo pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        String version = String.valueOf(pInfo.versionCode);
        System.out.println("version db " + versionFromDb + "  version app " + version);

        if (!version.equals(versionFromDb)) {
            alertDialog = new AlertDialog.Builder(PedidosActivity.this);

            alertDialog.setTitle("Atualização Disponivel");
            alertDialog.setMessage("Esta versao esta desatualizada, deseja ir para a pagina de atualização?");
            alertDialog.setCancelable(false);
            alertDialog.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });

            alertDialog.setNegativeButton("Nao", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Preferences preferences = new Preferences(PedidosActivity.this);
                    preferences.clearSession();
                    FirebaseAuth.getInstance().signOut();
                    LoginManager.getInstance().logOut();
                    startActivity(new Intent(PedidosActivity.this, MainActivity.class));
                }
            }).create().show();
        }

/*            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });*/
//------------
        return true;
    }

    private void makeUseOfNewLocation(final Double latitude, final Double longitude) {


        Preferences preferences = new Preferences(PedidosActivity.this);

        System.out.println("MY NAME IN LOCATION " + preferences.getNome().toString());
        ModelLatLong coord = new ModelLatLong();
        coord.setLatitude(latitude);
        coord.setLongitude(longitude);
        coord.setEmail(Base64Decoder.decoderBase64(userKey));
        DatabaseReference insertCoordValues = FirebaseConfig.getFireBase().child("coordenadas");
        insertCoordValues.child(preferences.getNome()).setValue(coord);

        getLocalization = FirebaseConfig.getFireBase().child("usuarios").child(userKey);

        getLocalization.addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user.getPedidosFeitos() != null) {
                    ArrayList<String> userGuests = user.getPedidosFeitos();
                    updatePedidosUsuarioCoordenadas(userGuests);
                }

                //localizacao.beginUpdates();
                //*preferencias.saveMyCoordinates(latitude,longitude);*//*

                System.out.println("ENTREI loxc");
                System.out.println("ENTREI loxc" + latitude + "  " + longitude);
                user.setLatitude(latitude);
                user.setLongitude(longitude);
                user.save();

                    /*System.out.println("FIZ UPDATE " + gps.getLatitude());*/

                if (user.getPedidosNotificationCount() != 0) {

                    Toast.makeText(getApplicationContext(), "Parabens, voce possui um pedido atendido", Toast.LENGTH_LONG).show();

                }
                final int premiumUser = user.getPremiumUser();
                final int creditos = user.getCreditos();
                final Intent intent = new Intent(PedidosActivity.this, CriaPedidoActivity.class);

                final Intent intent2 = new Intent(PedidosActivity.this, CriaDoacaoActivity.class);
                intent2.putExtra("latitude", latitude);
                intent2.putExtra("longitude", longitude);

                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
<<<<<<< HEAD
                        System.out.println("PREMIUM PASSA" + premiumUser);
                        intent.putExtra("premium", premiumUser);
                        intent.putExtra("creditos", creditos);
                        intent.putExtra("latitude", latitude);
                        intent.putExtra("longitude", longitude);
                        startActivity(intent);
=======
                        System.out.println("latitude 2" + latitude);
                        if (latitude == null) {
                            Toast.makeText(getApplicationContext(), "Você deve permitir o uso de geolocalização", Toast.LENGTH_SHORT).show();
                        } else {
                            intent.putExtra("premium", premiumUser);
                            intent.putExtra("creditos", creditos);
                            intent.putExtra("latitude", latitude);
                            intent.putExtra("longitude", longitude);
                            startActivity(intent);
                        }

>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                    }
                });


                System.out.println("LATITUDE> " + localizacao.getLatitude() + " LONGITUDE> " + longitude);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });
    }
<<<<<<< HEAD
    public void freeMemory(){
=======

    public void freeMemory() {
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }
<<<<<<< HEAD
=======

>>>>>>> c143454de42efeeca8bf8931f097315860f35714
    private void updatePedidosUsuarioCoordenadas(final ArrayList<String> userGuests) {

//START UPDATE USER GUESTS COORDINATES
        final DatabaseReference userPedidos = FirebaseConfig.getFireBase().child("Pedidos");
        final ArrayList<String> pedidosAll = new ArrayList<>();
        userPedidos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //implementa lista dos pedidos
                for (DataSnapshot post : dataSnapshot.getChildren()) {
                    Pedido pedido = post.getValue(Pedido.class);
                    pedidosAll.add(pedidosAll.size(), pedido.getIdPedido());
                }
                System.out.println("shuma " + pedidosAll.size());
                for (int i = 0; i < userGuests.size(); i++) {
                    if (pedidosAll.contains(userGuests.get(i))) {//pedido exists
                        try {
                            int index = pedidosAll.indexOf(userGuests.get(i));
                            userPedidos.child(userGuests.get(i)).child("latitude").setValue(latitude);
                            userPedidos.child(userGuests.get(i)).child("longitude").setValue(longitude);
                            Log.i("TAG", "populated " + userGuests.get(i).toString());
                        } catch (Exception e) {
                            Log.e("TAG", "failed to update User guest coordinates " + e);
                        }
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    //-----END UPDATE

}







