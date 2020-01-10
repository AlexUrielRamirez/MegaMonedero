package arc.plus.megamonedero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.RelativeLayout;

import arc.plus.megamonedero.Feed.Feed;

public class Principal extends AppCompatActivity {

    public static RelativeLayout Splash;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal_layout);

        fragmentManager = getSupportFragmentManager();
        Splash = findViewById(R.id.Splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Splash.setVisibility(View.GONE);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        findViewById(R.id.SuperContenedor).setVisibility(View.VISIBLE);
                        fragmentManager.beginTransaction().add(R.id.SuperContenedor,new Feed(),"Feed").commit();
                    }
                }, 800);
            }
        }, 2000);
    }
}