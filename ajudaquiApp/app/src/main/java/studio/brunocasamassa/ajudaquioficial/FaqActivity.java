package studio.brunocasamassa.ajudaquioficial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.adapters.FaqAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;

/**
 * Created by bruno on 19/06/2017.
 */

public class FaqActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listaFaq;
    private DatabaseReference databaseFaqPerguntas = FirebaseConfig.getFireBase().child("faq").child("Perguntas");
    private DatabaseReference databaseFaqRespostas = FirebaseConfig.getFireBase().child("faq").child("Respostas");
    private DatabaseReference databaseFaq = FirebaseConfig.getFireBase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        listaFaq = (ListView) findViewById(R.id.faq_lista);
        toolbar = (Toolbar) findViewById(R.id.toolbar_faq);
        toolbar.setTitle("Duvidas");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        databaseFaqPerguntas.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final ArrayList<String> getPerguntas= (ArrayList<String>) dataSnapshot.getValue();

                ArrayAdapter arrayAdapter = new FaqAdapter(getApplicationContext(), getPerguntas);

                listaFaq.setDivider(null);
                listaFaq.setAdapter(arrayAdapter);

                listaFaq.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                        databaseFaqRespostas.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {

                                ArrayList<String> getRespostas = (ArrayList) dataSnapshot.getValue();
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(FaqActivity.this);
                                alertDialog.setTitle("FAQ");

                                alertDialog.setMessage(getRespostas.get(position));

                                alertDialog.setPositiveButton("Fechar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                }).create().show();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }


}
