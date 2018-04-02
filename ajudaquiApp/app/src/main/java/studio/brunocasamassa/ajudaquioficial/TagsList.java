package studio.brunocasamassa.ajudaquioficial;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;

/**
 * Created by bruno on 24/05/2017.
 */


public class TagsList extends AppCompatActivity implements SearchView.OnQueryTextListener {
    private ListView tagView;
    private ArrayAdapter tagAdapter;
    private ArrayList<String> tags;
    private DatabaseReference tagsRefs;
    public String selectedTag;
    private ProgressDialog dialog = null;
    private TextView title;
    private Toolbar toolbar;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_taglist);
        tags = new ArrayList<>();
        tagView = (ListView) findViewById(R.id.tagsList);

        toolbar = (Toolbar) findViewById(R.id.toolbar_taglist);
        toolbar.setTitle("Selecione uma Categoria");

        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tagsRefs = FirebaseConfig.getFireBase();
        tagsRefs.child("tags").child("categorias");
        //dialog.show(TagsList.this, "Por favor aguarde", "Recebendo Tags...", true);
        tagsRefs.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                tags.clear();

                for (final DataSnapshot dados : dataSnapshot.child("tags").getChildren()) {
                    System.out.println("TAG ADAPTER " + tagAdapter);

                    System.out.println("tag EXTRAIDA NO taglist " + dados.getValue());
                    dados.getValue();
                    ArrayList tagss = (ArrayList) dados.getValue();
                    tags.addAll(tagss);
                    tagAdapter = new ArrayAdapter(getBaseContext(), android.R.layout.simple_list_item_2,
                            android.R.id.text1,
                            tags);

                    Collections.sort(tags, new Comparator<String>() {
                        @Override
                        public int compare(String s1, String s2) {
                            return s1.compareToIgnoreCase(s2);
                        }
                    });

                    tagView.setAdapter(tagAdapter);

                    tagView.setDivider(null);

                    System.out.println("TAG VIEW " + tagView);

                    tagView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            selectedTag = (String) tagView.getItemAtPosition(position);

                            System.out.println("seleceted tag " + selectedTag);
                            Intent intent = new Intent(TagsList.this, CriaPedidoActivity.class);
                            intent.putExtra("result", selectedTag);

                            setResult(Activity.RESULT_OK, intent);
                            finish();

                        }
                    });

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }


        });

    }


    public String getSelectedTag() {
        return selectedTag;
    }

    public void setSelectedTag(String selectedTag) {
        this.selectedTag = selectedTag;
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
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        TagsList.this.tagAdapter.getFilter().filter(newText);
        return true;
    }
}
