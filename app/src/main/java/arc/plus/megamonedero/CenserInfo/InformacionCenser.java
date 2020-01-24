package arc.plus.megamonedero.CenserInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.icu.util.DateInterval;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import arc.plus.megamonedero.Buscador.ViewPager.SliderAdapter;
import arc.plus.megamonedero.Constant;
import arc.plus.megamonedero.Entidades.EntidadesOpiniones;
import arc.plus.megamonedero.Methods;
import arc.plus.megamonedero.R;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class InformacionCenser extends Fragment {

    private String IdCenser;
    private ImageView parralax_image;
    private TextView Distancia;
    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest, jsonObjectRequestOpiniones;

    private LinearLayout horizontal_gallery;

    private TextView Nombre, Descripcion, Rating_txt, Direccion, Horario;
    private RatingBar Rating_bar;

    private RecyclerView RecyclerOpiniones;
    private ArrayList<EntidadesOpiniones> list_opiniones = new ArrayList<>();

    public static RelativeLayout HolderSlider;
    private ViewPager Slider;
    private SliderAdapter sliderAdapter;
    private int CurrentItem = 0;

    public InformacionCenser(String IdCenser){
        this.IdCenser = IdCenser;
    }

    private interface api_network_detalles_censer{
        @FormUrlEncoded
        @POST("/traer_detalles.php")
        void setData(
                @Field("IdCenser") String IdCenser,
                Callback<Response> callback
        );
    }

    public interface TraerFecha {
        @FormUrlEncoded
        @POST("/traer_fecha.php")
        public void PasarParametros(
                @Field("fecha") String Fecha,
                Callback<Response> callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_informacion_censer, container, false);

        parralax_image = view.findViewById(R.id.parallax_image);
        horizontal_gallery = view.findViewById(R.id.horizontal_gallery);
        Distancia = view.findViewById(R.id.Distancia);

        Nombre = view.findViewById(R.id.nombre_censer);
        Descripcion = view.findViewById(R.id.descripcion_censer);
        Rating_txt = view.findViewById(R.id.rating_txt);
        Rating_bar = view.findViewById(R.id.rating_bar);
        Direccion = view.findViewById(R.id.direccion_censer);
        Horario = view.findViewById(R.id.horario_censer);

        RecyclerOpiniones = view.findViewById(R.id.RecyclerOpiniones);
        RecyclerOpiniones.setLayoutManager(new GridLayoutManager(getContext(),1));

        HolderSlider = view.findViewById(R.id.holder_slider);
        Slider = view.findViewById(R.id.slider);
        view.findViewById(R.id.close_slider).setOnClickListener(v->HolderSlider.setVisibility(View.GONE));

        new RestAdapter.Builder().setEndpoint("http://192.168.100.215/test/").build().create(api_network_detalles_censer.class).setData(this.IdCenser, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                try {
                    new ProcesarInformacion().execute(new BufferedReader(new InputStreamReader(response.getBody().in())).readLine());
                } catch (IOException e){}
            }
            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(getContext(), "Algo salió mal, intente nuevamente", Toast.LENGTH_SHORT).show();
            }
        });

        Glide.with(getContext()).load("http://192.168.100.215/test/assets/Censers/"+this.IdCenser+"/Banner.jpg").centerCrop().override(360).into(this.parralax_image);

        String distancia = "A "+ Methods.getDistanceMeters(Constant.PosicionUsuario.latitude, Constant.PosicionUsuario.longitude, Constant.PosicionCenser.latitude, Constant.PosicionCenser.longitude) +"m de distancia";
        Distancia.setText(distancia);

        request = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.100.215/test/traer_fotos_censer.php?IdCenser="+this.IdCenser, null, response -> {
            JSONArray json = response.optJSONArray("Fotos");
            ArrayList<String> list = new ArrayList<>();
            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);

                    list.add("http://192.168.100.215/test/"+jsonObject.optString("Ruta"));

                    ImageView iv = new ImageView(getContext());
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Constant.dpToPx(75), Constant.dpToPx(75));
                    params.topMargin = Constant.dpToPx(1);
                    params.bottomMargin = Constant.dpToPx(1);
                    params.leftMargin = Constant.dpToPx(1);
                    params.rightMargin = Constant.dpToPx(1);

                    iv.setLayoutParams(params);

                    Glide.with(getContext()).load("http://192.168.100.215/test/"+jsonObject.optString("Ruta")).centerCrop().override(144).into(iv);

                    iv.setOnClickListener(v-> HolderSlider.setVisibility(View.VISIBLE));

                    horizontal_gallery.addView(iv);
                }
                sliderAdapter = new SliderAdapter(getContext(), list);
                Slider.setAdapter(sliderAdapter);
            } catch (JSONException e) {
                Log.e("BuscarCenser","Error->"+e);
            }
        }, error -> Log.e("BuscarCenser","request Error->"+error));
        request.add(jsonObjectRequest);

        jsonObjectRequestOpiniones = new JsonObjectRequest(Request.Method.GET, "http://192.168.100.215/test/traer_opiniones.php?IdCenser="+this.IdCenser, null, response -> {
            JSONArray json = response.optJSONArray("Opiniones");
            try {
                for (int i = 0; i < json.length(); i++) {
                    JSONObject jsonObject = json.getJSONObject(i);
                    EntidadesOpiniones entidades = new EntidadesOpiniones();
                    entidades.setId(jsonObject.optString("Id"));
                    entidades.setNombre(jsonObject.optString("Nombre"));
                    entidades.setPuntuacion(jsonObject.optString("Puntuacion"));
                    entidades.setComentario(jsonObject.optString("Comentario"));
                    entidades.setFecha(jsonObject.optString("Fecha"));
                    list_opiniones.add(entidades);
                }

                RecyclerOpiniones.setAdapter(new AdapterOpiniones(list_opiniones));

            } catch (JSONException e) {
                Log.e("BuscarCenser","Error->"+e);
            }

        }, error -> Log.e("BuscarCenser","request Error->"+error));
        request.add(jsonObjectRequestOpiniones);



        return view;
    }

    public class AdapterOpiniones extends RecyclerView.Adapter<AdapterOpiniones.ViewHolderSol> implements View.OnClickListener {
        ArrayList<EntidadesOpiniones> lista;
        private View.OnClickListener listener;

        Context context;

        public AdapterOpiniones(ArrayList<EntidadesOpiniones> lista) {
            this.lista = lista;
        }
        @NonNull
        @Override

        public ViewHolderSol onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_opiniones,parent,false);
            view.setOnClickListener(this);
            context = parent.getContext();
            return new ViewHolderSol(view);
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBindViewHolder(@NonNull ViewHolderSol holder, int position) {
            holder.nombre.setText(list_opiniones.get(position).getNombre());
            holder.rating_txt.setText(list_opiniones.get(position).getPuntuacion());
            holder.rating_bar.setRating(Float.parseFloat(list_opiniones.get(position).getPuntuacion()));
            holder.comentario.setText(list_opiniones.get(position).getComentario());

            new RestAdapter.Builder().setEndpoint("http://192.168.100.215/test/").build().create(TraerFecha.class).PasarParametros(list_opiniones.get(position).getFecha(), new Callback<retrofit.client.Response>() {
                @Override
                public void success(retrofit.client.Response result, retrofit.client.Response response) {
                    BufferedReader reader;
                    String fechafinal;
                    try {
                        reader = new BufferedReader(new InputStreamReader(result.getBody().in()));
                        fechafinal = reader.readLine();
                        holder.fecha.setText(fechafinal);
                    }catch (IOException e){}
                }
                @Override public void failure(RetrofitError error){}
            });

        }

        @Override
        public int getItemCount() {
            return lista.size();
        }

        public void setOnClickListener(View.OnClickListener listener){
            this.listener=listener;
        }

        @Override
        public void onClick(View view) { if(listener!=null){ listener.onClick(view); }}

        public class ViewHolderSol extends RecyclerView.ViewHolder {
            TextView nombre, rating_txt, fecha, comentario;
            RatingBar rating_bar;
            public ViewHolderSol(View itemView) {
                super(itemView);
                nombre = itemView.findViewById(R.id.nombre_usuario);
                rating_txt = itemView.findViewById(R.id.rating_user_txt);
                rating_bar = itemView.findViewById(R.id.rating_user_bar);
                fecha = itemView.findViewById(R.id.fecha_opinion_usuario);
                comentario = itemView.findViewById(R.id.comentario_user);
            }
        }
    }

    private class ProcesarInformacion extends AsyncTask<String, String, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            try { return new JSONObject(strings[0]); } catch (JSONException e) { e.printStackTrace(); }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject s) {
            super.onPostExecute(s);
            try {
                InformacionCenser.this.Nombre.setText(s.getString("Nombre"));
                InformacionCenser.this.Descripcion.setText(s.getString("Descripcion"));
                InformacionCenser.this.Rating_txt.setText(s.getString("Puntuacion"));
                InformacionCenser.this.Rating_bar.setRating(Float.parseFloat(s.getString("Puntuacion")));
                InformacionCenser.this.Direccion.setText(s.getString("Direccion"));
                InformacionCenser.this.Horario.setText("Abierto de "+s.getString("H_Apertura")+" a "+s.getString("H_Cierre"));
            } catch (JSONException e) {}
        }
    }

}
