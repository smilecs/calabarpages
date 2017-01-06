package ng.com.calabaryellowpages;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

public class intro extends AppIntro {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Note here that we DO NOT use setContentView();
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = preferences.edit();
        // Add your slide fragments here.
        // AppIntro will automatically generate the dots indicator and buttons.
        // Instead of fragments, you can also use our default slide
        // Just set a title, description, background and image. AppIntro will do the rest.
        addSlide(SampleSlide.newInstance(R.layout.splash1));
        addSlide(SampleSlide.newInstance(R.layout.splash2));

        // OPTIONAL METHODS
        // Override bar/separator color.
        setBarColor(Color.parseColor("#000000"));
        setSeparatorColor(Color.parseColor("#ffffff"));

        // Hide Skip/Done button.
        showSkipButton(true);
        setProgressButtonEnabled(true);


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        editor.putBoolean("isnotlogged", false);
        editor.commit();
        Intent i = new Intent(this, tabbed.class);
        startActivity(i);
        finish();

    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent i = new Intent(this, FacebookActivity.class);
        startActivity(i);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }

}
