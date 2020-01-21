package arc.plus.megamonedero.CenserInfo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;

import arc.plus.megamonedero.R;

public class InformacionCenser extends Fragment {

    private String IdCenser;
    private ImageView parralax_image;

    public InformacionCenser(String IdCenser){
        this.IdCenser = IdCenser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion_censer, container, false);

        parralax_image = view.findViewById(R.id.parallax_image);

        Glide.with(getContext()).load("http://192.168.100.215/test/assets/Censers/"+this.IdCenser+"/Banner.jpg").centerCrop().override(360).into(this.parralax_image);

        return view;
    }

}
