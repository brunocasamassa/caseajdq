package studio.brunocasamassa.ajudaquioficial;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.facebook.login.LoginManager;
import com.github.clans.fab.FloatingActionMenu;
import com.google.firebase.auth.FirebaseAuth;

import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.GruposTabAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.NavigationDrawer;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.SlidingTabLayout;

/**
 * Created by bruno on 24/04/2017.
 */

public class GruposActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    private Toolbar toolbar;
    private ListView listview_nomes;
    private ViewPager viewPager;
    private SlidingTabLayout slidingTabLayout;
    private int posicao;
    private ArrayAdapter<Grupo> arrayAdapterAllGroups;
    private ArrayAdapter<Grupo> arrayAdapterMyGroups;

    private android.support.design.widget.FloatingActionButton fab;
    private Button donation;
    private FloatingActionMenu fabMenu;
    private com.github.clans.fab.FloatingActionButton fab2;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello_grupos);


        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);
        toolbar.setTitle(getResources().getString(R.string.menu_grupos));
        //toolbar.setTitleTextColor(getResources().getColor(R.color.colorPrimaryDark));
        setSupportActionBar(toolbar);

        //fabMenu = (FloatingActionMenu) findViewById(R.id.fab_open_menu);
    /*    fab2 = (com.github.clans.fab.FloatingActionButton) findViewById(R.id.fab2);

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GruposActivity.this, CabineFarturaActivity.class);
                startActivity(intent);
            }
        });*/

        fab = (android.support.design.widget.FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GruposActivity.this, CriaGrupoActivity.class);
                startActivity(intent);
            }
        });

        listview_nomes = (ListView) findViewById(R.id.ListContatos);
        viewPager = (ViewPager) findViewById(R.id.vp_pagina);

        slidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        slidingTabLayout.setDistributeEvenly(true);
        slidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.colorAccent));

        GruposTabAdapter gruposTabAdapter = new GruposTabAdapter(getSupportFragmentManager());
        viewPager.setAdapter(gruposTabAdapter);

        slidingTabLayout.setViewPager(viewPager);

        NavigationDrawer navigator = new NavigationDrawer();
        navigator.createDrawer(GruposActivity.this, toolbar, 5);

        //@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)

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
                Preferences preferences = new Preferences(GruposActivity.this);
                preferences.clearSession();
                LoginManager.getInstance().logOut();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(GruposActivity.this, MainActivity.class));
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

    private void refresh() {
        Intent intent = new Intent(GruposActivity.this, GruposActivity.class);
        finish();
        System.out.println("REFRESHED");
        startActivity(intent);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        refresh();
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {

        arrayAdapterAllGroups= getArrayAdapterAllGroups();
        arrayAdapterAllGroups.getFilter().filter(newText);
        arrayAdapterAllGroups.notifyDataSetChanged();

        arrayAdapterMyGroups = getArrayAdapterMyGroups();
        if (arrayAdapterMyGroups != null) { //fica null quando inicia a activity
            arrayAdapterMyGroups.getFilter().filter(newText);
            arrayAdapterMyGroups.notifyDataSetChanged();
        }

        return true;
    }


    public ArrayAdapter<Grupo> getArrayAdapterAllGroups() {
        return arrayAdapterAllGroups;
    }

    public void setArrayAdapterAllGroups(ArrayAdapter<Grupo> arrayAdapterAllGroups) {
        this.arrayAdapterAllGroups = arrayAdapterAllGroups;
    }

    public ArrayAdapter<Grupo> getArrayAdapterMyGroups() {
        return arrayAdapterMyGroups;
    }

    public void setArrayAdapterMyGroups(ArrayAdapter<Grupo> arrayAdapterMyGroups) {
        this.arrayAdapterMyGroups = arrayAdapterMyGroups;
    }
}
