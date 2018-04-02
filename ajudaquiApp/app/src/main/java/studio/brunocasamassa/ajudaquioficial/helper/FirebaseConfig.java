package studio.brunocasamassa.ajudaquioficial.helper;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
//static for no instantiation

public class FirebaseConfig {

    private static DatabaseReference referenciaFirebase;
    private static FirebaseAuth autenticacao;
    private static StorageReference storage;

    public static DatabaseReference getFireBase() {

        if (referenciaFirebase == null) {
            referenciaFirebase = FirebaseDatabase.getInstance().getReference();
        }

        return referenciaFirebase;
    }

    //jBgW8yG8MjfJsOtlmSY1Q7nntcz2

    public static FirebaseAuth getFirebaseAuthentication(){
        if(autenticacao == null){
            autenticacao  = FirebaseAuth.getInstance();

        }
        return autenticacao;
    }

    public static StorageReference getFirebaseStorage(){

        if(storage == null){
            storage = FirebaseStorage.getInstance().getReferenceFromUrl("gs://ajudaqui-f345a.appspot.com/");

        }
        return storage;
    }

    public  static  DatabaseReference getNotificationRef(){
        return FirebaseDatabase.getInstance().getReference("Notifications");
    }


}
