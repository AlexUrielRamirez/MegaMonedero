package arc.plus.megamonedero;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import arc.plus.megamonedero.Buscador.Buscador;
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

    @Override
    public void onBackPressed() {
        if(fragmentManager.findFragmentByTag("Feed") != null && fragmentManager.findFragmentByTag("Feed").isVisible())
            super.onBackPressed();
        else if(fragmentManager.findFragmentByTag("Servicios") != null && fragmentManager.findFragmentByTag("Servicios").isVisible()){
            if(Buscador.ContenedorZtop.getVisibility() == View.VISIBLE){
                Buscador.ContenedorZtop.setAnimation(Buscador.bottom_out_down);
                Buscador.bottom_out_down.start();
                Buscador.ContenedorZtop.setVisibility(View.GONE);
            }else if(Buscador.Card.getVisibility() == View.VISIBLE)
                Methods.EsconderTarjeta(Principal.this,Buscador.Card);
            else
                fragmentManager.beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).replace(R.id.SuperContenedor,new Feed(),"Feed").commit();
        }
    }
}