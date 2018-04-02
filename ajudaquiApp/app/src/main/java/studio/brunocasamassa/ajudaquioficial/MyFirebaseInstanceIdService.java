package studio.brunocasamassa.ajudaquioficial;

/**
 * Created by bruno on 06/07/2017.
 */

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;


public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    private String refreshedToken;

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */

    // [START refresh_token]

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.

        System.out.println("INSTANCE ID  " + FirebaseInstanceId.getInstance().getId());
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        System.out.println(TAG + " Refreshed token: " + refreshedToken);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
/*      Preferences preferencias = new Preferences(MyFirebaseInstanceIdService.this);
        preferencias.saveToken(refreshedToken);*/

        if (refreshedToken != null) {
            sendRegistrationToServer(refreshedToken);
        }
    }

    // [END refresh_token]

    /**
     * Persist token to third-party servers.
     * <p>
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */

    public void sendRegistrationToServer(String token) {
        try {
            if (FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail() != null) {
                String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
                DatabaseReference dbUser = FirebaseConfig.getFireBase().child("usuarios");
                dbUser.child(userKey).child("notificationToken").setValue(token);

                FirebaseConfig.getNotificationRef().setValue(token);
            }

        } catch (Exception e) {
            System.out.println("Exception " + e);
        }
    }
}
