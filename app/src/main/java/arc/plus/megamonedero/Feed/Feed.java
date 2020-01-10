package arc.plus.megamonedero.Feed;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import arc.plus.megamonedero.Buscador.Buscador;
import arc.plus.megamonedero.Constant;
import arc.plus.megamonedero.R;

public class Feed extends Fragment {
    private LinearLayout Transporte, Servicios;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        Transporte = view.findViewById(R.id.Opcion_transporte);
        Servicios = view.findViewById(R.id.Opcion_servicios);

        Transporte.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).replace(R.id.SuperContenedor,new Buscador(Constant.TRANSPORTES),"Transportes").commit();
            }
        });

        Servicios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((FragmentActivity)getContext()).getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout).replace(R.id.SuperContenedor,new Buscador(Constant.SRVICIOS),"Servicios").commit();
            }
        });

        return view;
    }
}
