package studio.brunocasamassa.ajudaquioficial.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.R;


/**
 * A simple {@link Fragment} subclass.
 */


public class ContatosFragment extends Fragment {

    private ListView listview_nomes;
    private ArrayList<String> arraylist_nomes = new ArrayList<>();
    private ArrayAdapter<String> adapter_nomes;
    private DatabaseReference firebaseDatabase;
    private String contactName ;


    public ContatosFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View v = inflater.inflate(R.layout.activity_conversas, container, false);


        listview_nomes = (ListView) v.findViewById(R.id.ListContatos);




        // Inflate the layout for this fragment
        return v;

    }


    public void insertContact(ArrayAdapter<String> array_adapter) {

        listview_nomes.setAdapter(array_adapter);


    }
}

