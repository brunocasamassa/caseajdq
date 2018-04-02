package studio.brunocasamassa.ajudaquioficial.fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.GrupoAbertoActivity;
import studio.brunocasamassa.ajudaquioficial.GruposActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.MyGroupsAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * A simple {@link Fragment} subclass.
 */

public class GruposMeusgruposFragment extends Fragment {
    private FloatingActionButton fab;
    private DatabaseReference firebase;
    private DatabaseReference dbGroups;
    private ValueEventListener valueEventListenerAll;
    private ArrayAdapter adapter;
    private ArrayList<Grupo> grupos;
    private GridView listView;
    private ValueEventListener valueEventListenerAllGroups;
    private MyGroupsAdapter groupsAdapter;
    private GruposActivity ga;


    public static User usuario = new User();
    private int premium;
    private SwipeRefreshLayout refresh;

    public GruposMeusgruposFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        if (ga.getArrayAdapterMyGroups() != null) {
            adapter = ga.getArrayAdapterMyGroups();
        }
        super.onStart();
        firebase.addListenerForSingleValueEvent(valueEventListenerAll);
//       dbGroups.addListenerForSingleValueEvent(valueEventListenerAllGroups);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerAll);
        //dbGroups.removeEventListener(valueEventListenerAllGroups);
    }


    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_mygroups, container, false);
        ga = (GruposActivity) getActivity();

        grupos = new ArrayList<>();
        listView = (GridView) view.findViewById(R.id.mygroups_list);
        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);

        groupsAdapter = new MyGroupsAdapter(getContext(), grupos);

        if (ga.getArrayAdapterMyGroups() != null) {
            adapter = ga.getArrayAdapterMyGroups();
        } else adapter = groupsAdapter;

        ga.setArrayAdapterMyGroups(adapter);


        //adapter = new MyGroupsAdapter(getActivity(), grupos);
        listView.setAdapter(adapter);

       /* fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), CriaGrupoActivity.class));
            }
        });


*/

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();
            }
        });


        final String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail()).toString();

        dbGroups = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
        dbGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usuario.setGrupos(user.getGrupos());
                usuario.setName(user.getName());
                premium = user.getPremiumUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        firebase = FirebaseConfig.getFireBase()
                .child("grupos");

        //Listener para recuperar grupos do usuario
        valueEventListenerAll = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                grupos.clear();

                //Listar contatos
                for (DataSnapshot dados : dataSnapshot.getChildren()) {
                    System.out.println("get children allgroups " + dados);

                    Grupo grupo = dados.getValue(Grupo.class);
                    System.out.println("grupo " + grupo.getNome());
                    if (usuario.getGrupos() != null) {
                        if (!grupos.contains(grupo) && usuario.getGrupos().contains(grupo.getId())) {
                            grupos.add(grupo);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                grupos.clear();

            }
        };

        System.out.println("grupos usuario fora caraio " + usuario.getGrupos());

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Grupo target = groupsAdapter.getGruposFiltrado().get(position);
                final String groupId = target.getId();


                    if (userKey.equals("bHVjaWFuYS4zMDIwMDlAaG90bWFpbC5jb20=") || userKey.equals("YnJ1bm9fMTk5NEBob3RtYWlsLmNvbQ==") || userKey.equals("YnJ1bm9jYXNhbWFzc2FAaG90bWFpbC5jb20=")) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                        alert.setTitle("Apagar Grupo");
                        alert.setMessage("Deseja apagar o grupo " + target.getNome());
                        alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog progress = ProgressDialog.show(getActivity(), "Aguarde...",
                                        "Apagando Grupo ", true); try {

                                    FirebaseConfig.getFireBase().child("Pedidos").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot post : dataSnapshot.getChildren()){
                                                Pedido pedidoComGrupo = post.getValue(Pedido.class);
                                                if(pedidoComGrupo.getGroupId()!= null){
                                                    if(pedidoComGrupo.getGroupId().equals(groupId)){
                                                        pedidoComGrupo.setGroupId(null);
                                                        pedidoComGrupo.setGrupo(null);
                                                        pedidoComGrupo.save();
                                                    }
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    });
                                    Toast.makeText(getActivity(), "Grupo removido com sucesso", Toast.LENGTH_SHORT).show();
                                    FirebaseConfig.getFireBase().child("grupos").child(target.getId()).removeValue();
                                    FirebaseConfig.getFirebaseStorage().child("groupImages").child(target.getId() + ".jpg").delete();
                                    refresh();

                                } catch (Exception e) {
                                    Toast.makeText(getActivity(), "Não foi possivel apagar o grupo, por favor tente novamente mais tarde", Toast.LENGTH_SHORT).show();
                                    progress.dismiss();
                                }
                            }
                        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).create().show();
                    }


                return false;
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), GrupoAbertoActivity.class);

                // recupera dados a serem passados
                Grupo grupo = groupsAdapter.getGruposFiltrado().get(position);

                // enviando dados para grupo activity
                intent.putExtra("premium", premium);
                intent.putExtra("uri", grupo.getGrupoImg());
                intent.putExtra("nome", grupo.getNome());
                intent.putExtra("adminsList", grupo.getIdAdms());
                intent.putExtra("groupId", grupo.getId());
                intent.putExtra("qtdmembros", String.valueOf(grupo.getQtdMembros()));
                intent.putExtra("descricao", grupo.getDescricao());
                intent.putExtra("isOpened", grupo.isOpened());
                startActivity(intent);

            }
        });

        return view;


    }

    private void refresh() {
        Intent intent = new Intent(getActivity(), GruposActivity.class);
        getActivity().finish();
        startActivity(intent);
    }
}
