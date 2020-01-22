package arc.plus.megamonedero.CenserInfo;

import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import arc.plus.megamonedero.Constant;
import arc.plus.megamonedero.Methods;
import arc.plus.megamonedero.R;

public class InformacionCenser extends Fragment {

    private String IdCenser;
    private ImageView parralax_image;
    private TextView Distancia;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    private LinearLayout horizontal_gallery;

    public InformacionCenser(String IdCenser){
        this.IdCenser = IdCenser;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion_censer, container, false);

        parralax_image = view.findViewById(R.id.parallax_image);

        horizontal_gallery = view.findViewById(R.id.horizontal_gallery);

        Glide.with(getContext()).load("http://192.168.100.215/test/assets/Censers/"+this.IdCenser+"/Banner.jpg").centerCrop().override(360).into(this.parralax_image);

        Distancia = view.findViewById(R.id.Distancia);

        String distancia = "A "+ Methods.getDistanceMeters(Constant.PosicionUsuario.latitude, Constant.PosicionUsuario.longitude, Constant.PosicionCenser.latitude, Constant.PosicionCenser.longitude) +"m de distancia";
        Distancia.setText(distancia);

        request = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.100.215/test/traer_fotos_censer.php?IdCenser="+this.IdCenser, null, response -> {
            JSONArray json = response.optJSONArray("Fotos");
            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    ImageView iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constant.dpToPx(65), Constant.dpToPx(65));
                    params.topMargin = Constant.dpToPx(1);
                    params.bottomMargin = Constant.dpToPx(1);
                    params.leftMargin = Constant.dpToPx(1);
                    params.rightMargin = Constant.dpToPx(1);

                    iv.setLayoutParams(params);

                    Glide.with(getContext()).load("http://192.168.100.215/test/"+jsonObject.optString("Ruta")).centerCrop().override(144).into(iv);

                    horizontal_gallery.addView(iv);
                }
            } catch (JSONException e) {
                Log.e("BuscarCenser","Error->"+e);
            }
        }, error -> Log.e("BuscarCenser","request Error->"+error));
        request.add(jsonObjectRequest);

        return view;
    }

}
