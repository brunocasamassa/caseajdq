package studio.brunocasamassa.ajudaquioficial;

import com.google.firebase.database.DatabaseReference;

import java.util.ArrayList;

import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;

/**
 * Created by bruno on 22/05/2017.
 */

class Categoria {
    public ArrayList<String> getCategorias() {
        return tags;
    }

    private ArrayList<String> tags;


    public void setCategorias(ArrayList<String> categorias) {
        this.tags = categorias;
    }


    public void save() {
        DatabaseReference referenciaFirebase = FirebaseConfig.getFireBase();
        referenciaFirebase.child("tags").setValue(this);
    }


}
