package studio.brunocasamassa.ajudaquioficial.adapters;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.afinal.simplecache.ACache;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.helper.Conversa;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.User;

public class ConversaAdapter extends ArrayAdapter<Conversa> {

    private DatabaseReference firebase;
    private ArrayList<Conversa> conversas;
    private Context context;
    private StorageReference storage;
    private ACache mCache;

    public ConversaAdapter(Context c, ArrayList<Conversa> objects) {
        super(c, 0, objects);
        this.context = c;
        this.conversas = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        mCache = ACache.get(getContext());

        // Verifica se a lista está preenchida
        if (conversas != null) {

            // inicializar objeto para montagem da view
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            try {
                // Monta view a partir do xml
                view = inflater.inflate(R.layout.model_chat, parent, false);
            } catch (Exception e) {
                System.out.println("exception " + e);
            }

            // recupera elemento para exibição
            final CircleImageView img = (CircleImageView) view.findViewById(R.id.img_chat_contact);
            TextView nome = (TextView) view.findViewById(R.id.name_chat_contact);
            TextView ultimaMensagem = (TextView) view.findViewById(R.id.last_message);
            TextView chatCount = (TextView) view.findViewById(R.id.notification_count);
            TextView time = (TextView) view.findViewById(R.id.time_chat);

            DateFormat formatter = new SimpleDateFormat("HH:mm");
            formatter.setTimeZone(TimeZone.getTimeZone("GMT-3:00"));
            String currentTime = formatter.format(new Date());
            System.out.println("formatter: " + currentTime);

            Conversa conversa = conversas.get(position);

            try {
                nome.setText(String.valueOf(conversa.getNome().substring(0, 30)) + "...");
                System.out.println("DADOS PEDIDO NO ADAPTER: " + conversa.getNome());
            } catch (Exception e) {
                nome.setText(conversa.getNome());
            }

            ultimaMensagem.setText(conversa.getMensagem());

            if (conversa.getChatCount() > 0) {
                time.setTextColor(Color.argb(255, 255, 64, 128));
                chatCount.setBackgroundResource(R.drawable.layout_bg);
                chatCount.setTextColor(Color.WHITE);
                chatCount.setText(String.valueOf(conversa.getChatCount()));
            }


            final String idUser = conversa.getIdUsuario();
            final String imgKey = "imgKey: " + idUser;

            if (conversa.getTime() != null) {
                time.setText(conversa.getTime());
            } else time.setText(currentTime);
            firebase = FirebaseConfig.getFireBase().child("usuarios").child(idUser);

            storage = FirebaseConfig.getFirebaseStorage().child("userImages");
            String cacheData = mCache.getAsString(imgKey);
            if (cacheData != null) {
                Picasso.with(getContext()).load(cacheData).resize(680, 680).into(img);
            } else {
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            User user = dataSnapshot.getValue(User.class);
                            final String facebookUri = user.getProfileImg();

                            storage.child(idUser + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    try {
                                        //Glide.with(getContext()).load(uri).override(68, 68).into(img);
                                        Picasso.with(getContext()).load(uri).resize(680, 680).into(img);
                                        mCache.put(imgKey, uri.toString(), 2 * ACache.TIME_DAY);
                                        System.out.println("user image chat " + uri);

                                    } catch (Exception e) {
                                        mCache.put(imgKey, uri.toString(), 2 * ACache.TIME_DAY);
                                        img.setImageURI(uri);
                                        System.out.println("exception error" + e);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                    storage.child(idUser + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            try {
                                                //Glide.with(getContext()).load(uri).override(68, 68).into(img);
                                                Picasso.with(getContext()).load(uri).resize(68, 68).into(img);
                                                mCache.put(imgKey, uri.toString(), 2 * ACache.TIME_DAY);
                                                System.out.println("user image chat " + uri);

                                            } catch (Exception e) {
                                                Picasso.with(getContext()).load(facebookUri).resize(68, 68).into(img);
                                                mCache.put(imgKey, uri.toString(), 2 * ACache.TIME_DAY);
                                                System.out.println("exception error" + e);
                                            }

                                        }
                                    });

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if (facebookUri != null) {
                                        Picasso.with(getContext()).load(facebookUri).resize(68, 68).into(img);
                                        mCache.put(imgKey, facebookUri.toString(), 2 * ACache.TIME_DAY);
                                        System.out.println("exception error" + e);
                                    }
                                }
                            });

                        } catch (Exception e) {
                            System.out.println("Exception getting user chat image " + e);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
        return view;
    }
}
