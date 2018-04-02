package studio.brunocasamassa.ajudaquioficial.helper;

import android.os.Bundle;

import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide;

import studio.brunocasamassa.ajudaquioficial.R;

/**
 * Created by bruno on 18/09/2017.
 */

public class MainIntroActivity extends IntroActivity {

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        setFullscreen(true);
        setButtonCtaVisible(false);
        setButtonBackVisible(false);

        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.

        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro01)
                .build());

        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro02)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro03)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro04)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro05)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro06)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_07)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_08)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_09)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_10)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_11)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_12)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_13)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_14)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_15)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_16)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_17)
                .build());
        addSlide(new SimpleSlide.Builder()
                .background(R.color.colorBackground)
                .image(R.drawable.intro_18)
                .build());

    }
}