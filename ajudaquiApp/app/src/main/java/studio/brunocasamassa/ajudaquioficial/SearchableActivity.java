package studio.brunocasamassa.ajudaquioficial;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.helper.Pedido;

/**
 * Created by bruno on 12/07/2017.
 */

public class SearchableActivity extends AppCompatActivity{
    private Toolbar toolbar ;
    private ListView listView;
    private ArrayList<Pedido> listaPedidos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);

        toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        toolbar.setTitle("Pesquisa pedidos");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }

        });

        listaPedidos = new ArrayList<>();

        listView = (ListView) findViewById(R.id.search_listview);

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //doMySearch(query);
            Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
        }

    }

}
