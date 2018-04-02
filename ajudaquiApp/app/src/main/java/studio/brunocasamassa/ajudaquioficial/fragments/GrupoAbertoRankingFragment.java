package studio.brunocasamassa.ajudaquioficial.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

import studio.brunocasamassa.ajudaquioficial.PerfilGruposActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.RankingAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.User;


/**
 * A simple {@link Fragment} subclass.
 */


public class GrupoAbertoRankingFragment extends Fragment {

    private ListView listview_nomes;
    private ArrayList<User> arraylist_nomes = new ArrayList<>();
    private ArrayAdapter<User> adapter_nomes;
    private DatabaseReference firebaseDatabase;
    private String contactName;
    private String idGroup;
    private String nomeGroup;
    private int userPlace = 0;
    private int qtdMembros;
    private ValueEventListener valueEventListenerRankingGroup;
    private Query query;
    private ArrayList<String> adminsList;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());

    @Override
    public void onStart() {
        super.onStart();
        query.addListenerForSingleValueEvent(valueEventListenerRankingGroup);
    }

    public GrupoAbertoRankingFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             final Bundle savedInstanceState) {
        Bundle extra = getActivity().getIntent().getExtras();

        idGroup = extra.getString("groupId");

        nomeGroup = extra.getString("nome");

        qtdMembros = extra.getInt("qtdmembros");

        adminsList = extra.getStringArrayList("adminsList");
        View v = inflater.inflate(R.layout.fragment_grupos_ranking, container, false);

        listview_nomes = (ListView) v.findViewById(R.id.ranking_list);

        adapter_nomes = new RankingAdapter(getContext(), arraylist_nomes, userPlace);

        listview_nomes.setDivider(null);

        listview_nomes.setAdapter(adapter_nomes);

        listview_nomes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), PerfilGruposActivity.class);
                intent.putExtra("rankedUserId", arraylist_nomes.get(position).getId());
                System.out.println("target id no ranking " + arraylist_nomes.get(position).getId());
                intent.putExtra("rankedUserName", arraylist_nomes.get(position).getName());
                intent.putExtra("rankedUserEmail", arraylist_nomes.get(position).getEmail());
                intent.putExtra("rankedUserPontos", arraylist_nomes.get(position).getPontos());
                intent.putExtra("groupId", idGroup);
                intent.putExtra("rankedUserImg", arraylist_nomes.get(position).getProfileImg());
                if (arraylist_nomes.get(position).getPedidosAtendidos() == null) {
                    intent.putExtra("rankedUserPedidosAtendidos", 0);
                } else
                    intent.putExtra("rankedUserPedidosAtendidos", arraylist_nomes.get(position).getPedidosAtendidos().size());
                if (arraylist_nomes.get(position).getPedidosFeitos() == null) {
                    intent.putExtra("rankedUserPedidosFeitos", 0);
                } else
                    intent.putExtra("rankedUserPedidosFeitos", arraylist_nomes.get(position).getPedidosFeitos().size());

                intent.putExtra("posicao", position);
                System.out.println("usr img no ranking " + arraylist_nomes.get(position).getProfileImageURL());
                startActivity(intent);
            }
        });

        firebaseDatabase = FirebaseConfig.getFireBase().child("usuarios");

        query = firebaseDatabase.orderByChild("pontos");

        valueEventListenerRankingGroup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                arraylist_nomes.clear();
                if (adminsList.contains(userKey)) { //admin ranking list
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        try{
                        User rankedUser = postSnapshot.getValue(User.class);
                        System.out.println("idGROUP " + idGroup);

                        if (rankedUser.getGrupos() != null && rankedUser.getGrupos().contains(idGroup)) {
                            System.out.println("user no ranking " + rankedUser.getName());
                            arraylist_nomes.add(rankedUser);

                        }}
                     catch (Exception e ){
                         System.out.println("exception rank "+e);
                     }
                    }

                    Collections.reverse(arraylist_nomes);
                    adapter_nomes.notifyDataSetChanged();

                } else {  //non admin ranking list
                    int cont = 1;
                    ArrayList<User> userList = new ArrayList<>();
                    for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                        try{
                        userList.add(userList.size(), postSnapshot.getValue(User.class));
                        System.out.println("user add " + postSnapshot.getValue(User.class).getName());
                    }catch (Exception e){
                            System.out.println("exception rank "+e);
                    }
                    }
                    Collections.reverse(userList);
                    System.out.println("user add reversal : " + userList.get(0).getName());
                    for (int i = 0; i < userList.size(); i++) {
                        if (userList.get(i).getGrupos() != null && userList.get(i).getGrupos().contains(idGroup) && userList.get(i).getId().equals(userKey)) { //verify user in ranking
                            arraylist_nomes.add(userList.get(i));
                            userPlace = cont;
                            cont++;
                        } else if (userList.get(i).getGrupos() != null && userList.get(i).getGrupos().contains(idGroup) && cont <= 3) { //populate top 3
                            System.out.println("user no ranking non admin" + userList.get(i).getName());
                            arraylist_nomes.add(userList.get(i));
                            cont++;
                        } else if (userList.get(i).getGrupos() != null && userList.get(i).getGrupos().contains(idGroup)) {  //count position in ranking
                            cont++;
                        }
                    }

                    adapter_nomes.notifyDataSetChanged();


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };


        // Inflate the layout for this fragment
        return v;

    }


}

