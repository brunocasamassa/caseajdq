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

import studio.brunocasamassa.ajudaquioficial.CabineFarturaActivity;
import studio.brunocasamassa.ajudaquioficial.GrupoFechadoActivity;
import studio.brunocasamassa.ajudaquioficial.GruposActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.adapters.AllGroupsAdapter;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Grupo;
import studio.brunocasamassa.ajudaquioficial.helper.Pedido;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;


/**
 * A simple {@link Fragment} subclass.
 */


public class GruposTodosgruposFragment extends Fragment {
    private GridView listView;
    private ArrayAdapter adapter;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private ArrayList<Grupo> grupos;
    private DatabaseReference firebase;
    private ValueEventListener valueEventListenerAllGroups;
    private FloatingActionButton fab;
    private User usuario = new User();
    private String userName = new String();
    private ArrayList<String> gruposSolicitadosUser = new ArrayList<>();
    private SwipeRefreshLayout refresh;
    private AllGroupsAdapter groupsAdapter;
    private GruposActivity ga;
    private DatabaseReference dbGroups;


    public GruposTodosgruposFragment() {

        // Required empty public constructor
    }

    @Override
    public void onStart() {
        if (ga.getArrayAdapterAllGroups() != null) {
            adapter = ga.getArrayAdapterAllGroups();
        }
        super.onStart();
        firebase.addValueEventListener(valueEventListenerAllGroups);
    }

    @Override
    public void onStop() {
        super.onStop();
        firebase.removeEventListener(valueEventListenerAllGroups);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_allgroups, container, false);
        ga = (GruposActivity) getActivity();

        grupos = new ArrayList<>();

        refresh = (SwipeRefreshLayout) view.findViewById(R.id.swiperefresh);
        /*usuario.setGrupos(GruposMeusgruposFragment.usuario.getGrupos());
        usuario.setName(GruposMeusgruposFragment.usuario.getName());
        */System.out.println("grupos do usuario: " + usuario.getGrupos());
        System.out.println("Nome do usuario 2: " + usuario.getName());
        Preferences preferencias = new Preferences(getActivity());
        userName = preferencias.getNome();

        listView = (GridView) view.findViewById(R.id.allgroups_list);

        groupsAdapter= new AllGroupsAdapter(getContext(), grupos);

        if (ga.getArrayAdapterAllGroups() != null) {
            adapter = ga.getArrayAdapterAllGroups();
        } else adapter = groupsAdapter;

        ga.setArrayAdapterAllGroups(adapter);

        listView.setAdapter(adapter);

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refresh();

            }
        });

        firebase = FirebaseConfig.getFireBase()
                .child("grupos");


        dbGroups = FirebaseConfig.getFireBase().child("usuarios").child(userKey);
<<<<<<< HEAD
        dbGroups.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                usuario.setGrupos(user.getGrupos());
                usuario.setName(user.getName());
                //premium = user.getPremiumUser();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
=======
>>>>>>> c143454de42efeeca8bf8931f097315860f35714


        //Listener para recuperar contatos
        valueEventListenerAllGroups = new ValueEventListener() {

            @Override
            public void onDataChange(final DataSnapshot groupsSnapshot) {


                dbGroups.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final User user = dataSnapshot.getValue(User.class);

                        //instantiate 'cabine da fartura' position

<<<<<<< HEAD
                //instantiate 'cabine da fartura' position

=======
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                Grupo cabine = new Grupo();

                //Limpar lista
                grupos.clear();

                //Cabine da fartura
                grupos.add(0,cabine);
                adapter.notifyDataSetChanged();

                //Listar grupos
                for (DataSnapshot dados : groupsSnapshot.getChildren()) {
                    System.out.println("get children allgroups " + dados);
                    try {
                        Grupo grupo = dados.getValue(Grupo.class);
                        System.out.println("grupo " + grupo.getNome());
                        //remove user groups
                        if (user.getGrupos()!= null) {

<<<<<<< HEAD
                    Grupo grupo = dados.getValue(Grupo.class);
                    System.out.println("grupo " + grupo.getNome());
                    //remove user groups
                    try {
                        if (!GruposMeusgruposFragment.usuario.getGrupos().contains(grupo.getId())) {

=======
                            if(!user.getGrupos().contains(grupo.getId())){
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                            if (grupo.getId() != null) {
                                grupos.add(grupo);
                                System.out.println("schnawxzer" + grupo.getNome());
                                adapter.notifyDataSetChanged();

<<<<<<< HEAD
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("exception eee " + e.getLocalizedMessage());
                        System.out.println("exception eee33 " + e.getCause());
=======
                            }}
                        } else{
                            grupos.add(grupo);
                            System.out.println("schnawxzer" + grupo.getNome());
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("exception eee " + e);
>>>>>>> c143454de42efeeca8bf8931f097315860f35714
                    }
                    }



                }


<<<<<<< HEAD
=======

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

                }
>>>>>>> c143454de42efeeca8bf8931f097315860f35714

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final Grupo target = groupsAdapter.getGruposFiltrado().get(position);
                final String groupId = target.getId();

                if(target.getId()!=null){
                if (userKey.equals("bHVjaWFuYS4zMDIwMDlAaG90bWFpbC5jb20=") || userKey.equals("YnJ1bm9fMTk5NEBob3RtYWlsLmNvbQ==") || userKey.equals("YnJ1bm9jYXNhbWFzc2FAaG90bWFpbC5jb20=")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Apagar Grupo");
                    alert.setMessage("Deseja apagar o grupo " + target.getNome());
                    alert.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final ProgressDialog progress = ProgressDialog.show(getActivity(), "Aguarde...",
                                    "Apagando Grupo ", true);
                            try {

                                FirebaseConfig.getFireBase().child("Pedidos").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (DataSnapshot post : dataSnapshot.getChildren()) {
                                            Pedido pedidoComGrupo = post.getValue(Pedido.class);
                                            if (pedidoComGrupo.getGroupId() != null) {
                                                if (pedidoComGrupo.getGroupId().equals(groupId)) {
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
            }

                return false;
            }
        });



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // recupera dados a serem passados

                if (position == 0) {
                    startActivity(new Intent(getActivity(), CabineFarturaActivity.class));
                }
                // enviando dados para grupo activity
                else {
                    Grupo grupo = groupsAdapter.getGruposFiltrado().get(position);
                    try {
                        Intent intent = new Intent(getActivity(), GrupoFechadoActivity.class);
                        if (grupo.getIdAdms() != null) {
                            intent.putExtra("idAdmins", grupo.getIdAdms());
                        }

                        intent.putExtra("isOpened", grupo.isOpened());
                        intent.putExtra("userName", usuario.getName());
                        intent.putExtra("nome", grupo.getNome());
                        intent.putExtra("groupId", grupo.getId());
                        intent.putExtra("qtdmembros", String.valueOf(grupo.getQtdMembros()));
                        intent.putExtra("descricao", grupo.getDescricao());


                        startActivity(intent);

                    } catch (Exception e) {
                        System.out.println("Exception grupos " + e);
                    }

                }
            }
        });

        return view;


    }


    private void refresh() {
        Intent intent = new Intent(getActivity(), GruposActivity.class);
        getActivity().finish();
        startActivity(intent);
    }


    //class ExlcudeGroup extends AsyncTask<String,>

}
