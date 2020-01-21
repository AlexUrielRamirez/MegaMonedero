package arc.plus.megamonedero.CenserInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import arc.plus.megamonedero.R;

public class InformacionCenser extends Fragment {

    private String IdCenser;

    public InformacionCenser(String IdCenser){
        this.IdCenser = IdCenser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion_censer, container, false);

        return view;
    }

}
