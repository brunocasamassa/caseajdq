package studio.brunocasamassa.ajudaquioficial.helper;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.holder.BadgeStyle;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import studio.brunocasamassa.ajudaquioficial.ConfiguracoesActivity;
import studio.brunocasamassa.ajudaquioficial.ConversasActivity;
import studio.brunocasamassa.ajudaquioficial.GruposActivity;
import studio.brunocasamassa.ajudaquioficial.PedidosActivity;
import studio.brunocasamassa.ajudaquioficial.PerfilActivity;
import studio.brunocasamassa.ajudaquioficial.R;
import studio.brunocasamassa.ajudaquioficial.SobreActivity;
import studio.brunocasamassa.ajudaquioficial.payment.PaymentActivity3;

/**
 * Created by bruno on 24/04/2017.
 */

public class NavigationDrawer {

    //NAVIGATION DRAWER
    private DatabaseReference firebaseData;
    private DatabaseReference dbConversa;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    //private static Activity setClasse = new Activity();
    //private static MainActivity main;
    private static User usuario = new User();
    public int pivotPosition;
    public Activity pivotClass;
    private int premium;
    public String nomeUser;
    private static String idUser;
    private StorageReference storage;


    public void createDrawer(final Activity classe, final Toolbar toolbar, final int posicao) {

        storage = FirebaseConfig.getFirebaseStorage().child("userImages");

        FirebaseUser authentication = FirebaseConfig.getFirebaseAuthentication().getCurrentUser();
        System.out.println("usuario no drawer: " + authentication);

        final String emailUser = authentication.getEmail();
        System.out.println("email user " + emailUser);
        idUser = Base64Decoder.encoderBase64(emailUser);

        try {
            firebaseData = FirebaseConfig.getFireBase().child("usuarios").child(idUser);
            firebaseData.addValueEventListener(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    int chatNotification;
                    int groupNotification;
                    int pedidosNotification;
                    int profileNotification;


                    System.out.println("DATASNAPSHOT " + dataSnapshot);
                    User user = dataSnapshot.getValue(User.class);
                    System.out.println("MAIL " + user.getEmail());
                    System.out.println("NAME " + user.getName());
                    nomeUser = user.getName().toString();
                    premium = user.getPremiumUser();
                    System.out.println("Premium user drawer in response" + premium);
                    usuario = user;

                    chatNotification = user.getChatNotificationCount();
                    pedidosNotification = user.getPedidosNotificationCount();
                    profileNotification = user.getProfileNotificationCount();

                    PrimaryDrawerItem item1;
                    PrimaryDrawerItem item2;
                    PrimaryDrawerItem item3;
                    PrimaryDrawerItem item4;

                    if (pedidosNotification > 0) {
                        item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.menu_pedidos).withIcon(R.drawable.pedidos_icon).withBadge(String.valueOf(pedidosNotification)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorAccent));
                    } else
                        item1 = new PrimaryDrawerItem().withIdentifier(1).withName(R.string.menu_pedidos).withIcon(R.drawable.pedidos_icon);
                    if (chatNotification > 0) {
                        item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.menu_chats).withIcon(R.drawable.chat_icon).withBadge(String.valueOf(chatNotification)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorAccent));
                    } else
                        item2 = new PrimaryDrawerItem().withIdentifier(2).withName(R.string.menu_chats).withIcon(R.drawable.chat_icon);

                    item3 = new PrimaryDrawerItem().withIdentifier(3).withName(R.string.menu_grupos).withIcon(R.drawable.groups_icon);

                    if(profileNotification > 0){
                    item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.menu_perfil).withIcon(R.drawable.profile_icon).withBadge(String.valueOf(profileNotification)).withBadgeStyle(new BadgeStyle().withTextColor(Color.WHITE).withColorRes(R.color.colorAccent));
                    } else item4 = new PrimaryDrawerItem().withIdentifier(4).withName(R.string.menu_perfil).withIcon(R.drawable.profile_icon);

                    PrimaryDrawerItem item5 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.menu_sobre).withIcon(R.drawable.sobre_icon);
                    PrimaryDrawerItem item6 = new PrimaryDrawerItem().withIdentifier(5).withName(R.string.menu_configuracoes).withIcon(R.drawable.config_icon);
                    // Create the Navigation Drawer AccountHeader
                    PrimaryDrawerItem item7 = new PrimaryDrawerItem().withIdentifier(5).withName("Passo-à-Passo").withIcon(R.drawable.ic_passoapasso);

                    AccountHeader headerResult = new AccountHeaderBuilder()
                            .withActivity(classe)
                            .withHeaderBackground(R.drawable.background_navigation_drawer)
                            .addProfiles(
                                    new ProfileDrawerItem().withName(nomeUser).withEmail(emailUser).withIcon(android.R.color.transparent)
                            )

                            .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                                @Override
                                public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                                    return false;
                                }
                            })
                            .withSelectionListEnabled(false)
                            .build();

                    //Definition Drawer
                    Drawer drawer = new DrawerBuilder()
                            .withSliderBackgroundColor(Color.WHITE)
                            .withActivity(classe)
                            .withToolbar(toolbar)
                            .withAccountHeader(headerResult)
                            .addDrawerItems(
                                    item1,
                                    new DividerDrawerItem(),//Divisor
                                    item2,
                                    new DividerDrawerItem(),//Divisor
                        /*DIVISAO COM MENSAGEM new SectionDrawerItem().withName(R.string.section),//Seção*/
                                    item3,
                                    new DividerDrawerItem(),//Divisor
                                    item4,
                                    new DividerDrawerItem(),//Divisor
                                    item5,
                                    new DividerDrawerItem(),//Divisor
                                    item6,
                                    new DividerDrawerItem(),//Divisor
                                    item7
                                    //Divisor
                            )
                            .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                                @Override
                                public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                                    pivotClass = classe;
                                    pivotPosition = position;
                                    verifyActivity(pivotClass, pivotPosition);
                                    return false;
                                }
                            }).withSelectedItemByPosition(posicao)
                            .build();


                }


                @Override
                public void onCancelled(DatabaseError databaseError) {

                }


            });
        } catch (Exception e) {

            System.out.println("exception " + e);
            FirebaseAuth.getInstance().signOut();

        }


    }

    //nao me julgue
    private void verifyActivity(Activity classe, int position) {
        if (position == 1) {
            Intent intent = new Intent(classe, PedidosActivity.class);
            final Preferences filterPreferences = new Preferences(classe);
            filterPreferences.saveFilterPedido(null);
            classe.startActivity(intent);
        }
        if (position == 3) {
            classe.startActivity(new Intent(classe, ConversasActivity.class));
        }
        if (position == 5) {
            if (premium != 1) {

                classe.startActivity(new Intent(classe, PaymentActivity3.class));

            } else {
                classe.startActivity(new Intent(classe, GruposActivity.class));

            }

        }
        if (position == 7) {
            classe.startActivity(new Intent(classe, PerfilActivity.class));
        }
        if (position == 9) {

            Intent sobreIntent = new Intent(classe, SobreActivity.class);
            sobreIntent.putExtra("NOME", nomeUser);
            sobreIntent.putExtra("EMAIL", FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
            classe.startActivity(sobreIntent);
        }
        if (position == 11) {
            Intent sobreIntent = new Intent(classe, ConfiguracoesActivity.class);
            classe.startActivity(sobreIntent);

        }
        if (position == 13) {
            Intent sobreIntent = new Intent(classe, MainIntroActivity.class);
            classe.startActivity(sobreIntent);

        }
    }



}
