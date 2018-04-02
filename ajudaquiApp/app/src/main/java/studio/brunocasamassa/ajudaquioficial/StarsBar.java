package studio.brunocasamassa.ajudaquioficial;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;

import com.google.firebase.database.DatabaseReference;

import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;

public class StarsBar extends AppCompatActivity {
    private RatingBar ratingbar1;
    Button button;
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private DatabaseReference dbConversa = FirebaseConfig.getFireBase().child("conversas");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rating_bar);
        ratingbar1 = (RatingBar) findViewById(R.id.ratingBar1);
        button = (Button) findViewById(R.id.button1);

        Bundle extras = getIntent().getExtras();
        final String atendenteKey = extras.getString("keyAtendente");
        final String pedidoId = extras.getString("pedidoId");
        System.out.println("key nas stars " + atendenteKey);

        //Performing action on Button Click
        Drawable progress = ratingbar1.getProgressDrawable();
        //DrawableCompat.setTint(progress, Color.BLUE);

        button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //Getting the rating and displaying it on the toast
                String rating = String.valueOf(ratingbar1.getRating());
                final int ratingPoints = (int) (ratingbar1.getRating() * 2);
                /*Intent intent = new Intent(StarsBar.this, PedidoCriadoActivity.class);
                intent.putExtra("groupSelected", rating);
                setResult(Activity.RESULT_OK, intent);
                */



            }

        });


    }
}