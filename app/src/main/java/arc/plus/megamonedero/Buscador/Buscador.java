package arc.plus.megamonedero.Buscador;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;

import arc.plus.megamonedero.Constant;
import arc.plus.megamonedero.Methods;
import arc.plus.megamonedero.Principal;
import arc.plus.megamonedero.R;

import static android.content.Context.LOCATION_SERVICE;

public class Buscador extends Fragment implements OnMapReadyCallback, TextWatcher {

    public Buscador(Integer TIPO_BUSQUEDA) {
        this.TIPO_BUSQUEDA = TIPO_BUSQUEDA;
    }

    private Integer TIPO_BUSQUEDA;
    private EditText Buscador;
    private RecyclerView Sugerencias;
    private RelativeLayout HolderSearchBar;
    private ArrayList<Entidades> arrayList = new ArrayList<>(), FilterList = new ArrayList<>();
    private ImageView IconoBuscadorComprimido;
    private LayoutTransition lt;
    private GoogleMap Mapa;
    public static RelativeLayout Card;
    public LocationManager mLocationManager;
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    private ImageView Card_Image;

    public JSONArray JsonArray_LocalCenser = new JSONArray();
    public JSONObject
            LocalJson1 = new JSONObject(),
            LocalJson2 = new JSONObject(),
            LocalJson3 = new JSONObject(),
            LocalJson4 = new JSONObject(),
            LocalJson5 = new JSONObject(),
            LocalJson6 = new JSONObject();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscador, container, false);

        new FillLocalCenser().execute();
        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Card_Image = view.findViewById(R.id.card_img);
        Glide.with(getContext()).load("https://www.beautymarket.es/estetica/foros/anuncio1547300315-12.jpg").centerCrop().override(240).into(Card_Image);

        new FillList().execute();
        lt = new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.CHANGING);

        HolderSearchBar = view.findViewById(R.id.HolderSearchBar);
        HolderSearchBar.setLayoutTransition(lt);

        IconoBuscadorComprimido = view.findViewById(R.id.buscador_comprimido_ico);
        IconoBuscadorComprimido.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.ExpandirBuscador(getContext(),HolderSearchBar,Buscador,IconoBuscadorComprimido,Sugerencias);
            }
        });

        Buscador = view.findViewById(R.id.Buscador);
        Buscador.addTextChangedListener(this);
        Buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    BuscarCenser(0,"");
                    Methods.ContraerBuscador(getContext(),HolderSearchBar,Buscador,IconoBuscadorComprimido,Sugerencias);
                    Methods.hideKeyboard(getActivity());
                    return true;
                }
                return false;
            }
        });

        Sugerencias = view.findViewById(R.id.RecyclerSugerencias);
        Sugerencias.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        Sugerencias.setHasFixedSize(true);

        Sugerencias.setAdapter(new AdapterSugerencias(arrayList));

        Card = view.findViewById(R.id.card);

        /*LayoutTransition layoutTransition = new LayoutTransition();
        layoutTransition.enableTransitionType(LayoutTransition.CHANGING);
        Card.setLayoutTransition(layoutTransition);*/

        return view;
    }

    @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}

    @Override public void afterTextChanged(Editable s) {
        FilterList.clear();
        for (int i = 0; i < arrayList.size(); i++) {
            if (QuitarAcentos(arrayList.get(i).getTitulo().toLowerCase()).contains(QuitarAcentos(s.toString().toLowerCase()))) {
                Entidades entidades = new Entidades();
                entidades.setTitulo(arrayList.get(i).getTitulo());
                entidades.setSubtitulo(arrayList.get(i).getSubtitulo());
                FilterList.add(entidades);
            }
        }
        Sugerencias.setAdapter(new AdapterSugerencias(FilterList));
        Sugerencias.getAdapter().notifyDataSetChanged();
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        Mapa = googleMap;
        MarcarPosicionActual(googleMap);
    }

    public void MarcarPosicionActual(GoogleMap googleMap){
        @SuppressLint("MissingPermission")
        Location location = ObtenerUltimaPosicionConocida();
        if(location != null){
            LatLng UserLocation = new LatLng(location.getLatitude(), location.getLongitude());

            Marker marker = Mapa.addMarker(new MarkerOptions()
                    .position(UserLocation)
                    .title("Posicion Actual")
                    .icon(BitmapDescriptorFactory.fromBitmap(Methods.drawableToBitmap(getContext().getResources().getDrawable(R.drawable.current_user_location)))));
            marker.setTag(-1);

            float zoom = 16.0f; //Hasta 21
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(UserLocation, zoom));
        }else
            Toast.makeText(getContext(), "ERROR_LOCATION_MANAGER_NULL", Toast.LENGTH_SHORT).show();
    }



    private void BuscarCenser(Integer Entrada, String Parametro){
        try {
            for(int i = 0; i < JsonArray_LocalCenser.length();i++){
                JSONObject json = JsonArray_LocalCenser.getJSONObject(i);
                LatLng UserLocation = new LatLng(Double.parseDouble(json.getString("Lat")), Double.parseDouble(json.getString("Lang")));

                Marker marker = Mapa.addMarker(new MarkerOptions().position(UserLocation).title(json.getString("Nombre")));
                marker.setTag(i);

                Mapa.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        //int position = (int)(marker.getTag());
                        // Toast.makeText(getContext(), "Tag: "+marker.getTag(), Toast.LENGTH_SHORT).show();
                        if(Card.getVisibility() != View.VISIBLE)
                            Methods.MostrarTarjeta(getContext(),Card);
                        else
                            Methods.EsconderTarjeta(getContext(),Card);
                        return false;
                    }
                });
            }
        }catch (JSONException e){
            Log.e("JSONEX","Error pintando locations->"+e);
        }
    }

    private Location ObtenerUltimaPosicionConocida() {
        mLocationManager = (LocationManager)getContext().getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            @SuppressLint("MissingPermission") Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    public static String QuitarAcentos(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            return false;
        } else {
            return true;
        }
    }

    private class Entidades{
        public String getTitulo() {
            return Titulo;
        }

        public void setTitulo(String titulo) {
            Titulo = titulo;
        }

        public String getSubtitulo() {
            return Subtitulo;
        }

        public void setSubtitulo(String subtitulo) {
            Subtitulo = subtitulo;
        }

        private String Titulo;
        private String Subtitulo;
    }

    public class AdapterSugerencias extends RecyclerView.Adapter<AdapterSugerencias.ViewHolderSol> implements View.OnClickListener {
        ArrayList<Entidades> lista;
        private View.OnClickListener listener;

        Context context;

        public AdapterSugerencias(ArrayList<Entidades> lista) {
            this.lista = lista;
        }
        @NonNull
        @Override

        public ViewHolderSol onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sugerencias,null,false);
            view.setOnClickListener(this);
            context = parent.getContext();
            return new ViewHolderSol(view);
        }

        @SuppressLint("StaticFieldLeak")
        @Override
        public void onBindViewHolder(@NonNull ViewHolderSol holder, int position) {
            holder.Titulo.setText(lista.get(position).Titulo);
            holder.Subtitulo.setText(lista.get(position).Subtitulo);
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
            TextView Titulo, Subtitulo;
            public ViewHolderSol(View itemView) {
                super(itemView);
                Titulo = itemView.findViewById(R.id.Titulo);
                Subtitulo = itemView.findViewById(R.id.Subtitulo);
            }
        }
    }

    private class FillList extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            Entidades entidades1 = new Entidades();
            entidades1.setTitulo("Mecánico");
            entidades1.setSubtitulo("Mecánico");
            arrayList.add(entidades1);

            Entidades entidades2 = new Entidades();
            entidades2.setTitulo("Doctor");
            entidades2.setSubtitulo("Doctor");
            arrayList.add(entidades2);

            Entidades entidades3 = new Entidades();
            entidades3.setTitulo("Abarrotes");
            entidades3.setSubtitulo("Abarrotes");
            arrayList.add(entidades3);

            Entidades entidades4 = new Entidades();
            entidades4.setTitulo("Restaurant");
            entidades4.setSubtitulo("Restaurant");
            arrayList.add(entidades4);

            Entidades entidades5 = new Entidades();
            entidades5.setTitulo("Estética");
            entidades5.setSubtitulo("Estética");
            arrayList.add(entidades5);

            Entidades entidades6 = new Entidades();
            entidades6.setTitulo("Jardinería");
            entidades6.setSubtitulo("Jardinería");
            arrayList.add(entidades6);

            Entidades entidades7 = new Entidades();
            entidades7.setTitulo("Paquetería");
            entidades7.setSubtitulo("Paquetería");
            arrayList.add(entidades7);

            Entidades entidades8 = new Entidades();
            entidades8.setTitulo("AutoServicio");
            entidades8.setSubtitulo("AutoServicio");
            arrayList.add(entidades8);

            Entidades entidades9 = new Entidades();
            entidades9.setTitulo("Gasolineras");
            entidades9.setSubtitulo("Gasolineras");
            arrayList.add(entidades9);

            Entidades entidades10 = new Entidades();
            entidades10.setTitulo("Bares");
            entidades10.setSubtitulo("Bares");
            arrayList.add(entidades10);

            return null;
        }
    }

    private class FillLocalCenser extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {

            try {
                LocalJson1.put("Lat","19.687669");
                LocalJson1.put("Lang","-99.161189");
                LocalJson1.put("Nombre", "Censer 1");

                LocalJson2.put("Lat","19.688972");
                LocalJson2.put("Lang","99.165631");
                LocalJson2.put("Nombre", "Censer 2");

                LocalJson3.put("Lat","19.684912");
                LocalJson3.put("Lang","99.158354");
                LocalJson3.put("Nombre", "Censer 3");

                JsonArray_LocalCenser.put(LocalJson1);
                JsonArray_LocalCenser.put(LocalJson2);
                JsonArray_LocalCenser.put(LocalJson3);

            } catch (JSONException e) {
                Log.e("JSONEX","Error llenando locations->"+e);
            }

            return null;
        }
    }

}