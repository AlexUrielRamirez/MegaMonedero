package arc.plus.megamonedero.Buscador;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import arc.plus.megamonedero.CenserInfo.InformacionCenser;
import arc.plus.megamonedero.Constant;
import arc.plus.megamonedero.Entidades.EntidadesCensers;
import arc.plus.megamonedero.HerramientasMaps.DirectionsJSONParser;
import arc.plus.megamonedero.Methods;
import arc.plus.megamonedero.R;

import static android.content.Context.LOCATION_SERVICE;
import static arc.plus.megamonedero.Constant.IdCenser;
import static arc.plus.megamonedero.Methods.QuitarAcentos;

public class Buscador extends Fragment implements OnMapReadyCallback {

    public Buscador(Integer TIPO_BUSQUEDA) {
        this.TIPO_BUSQUEDA = TIPO_BUSQUEDA;
    }

    private Polyline mPolyline;

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

    private TextView btn_ver_mas,Card_nombre, Card_descripcion;

    private RequestQueue request;
    private JsonObjectRequest jsonObjectRequest;

    public static RelativeLayout ContenedorZtop;
    public static Animation abajo_arriba, bottom_out_down;

    public ArrayList<EntidadesCensers> lista_censers = new ArrayList<>();

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscador, container, false);

        abajo_arriba = AnimationUtils.loadAnimation(getContext(),R.anim.abajo_arriba);
        bottom_out_down = AnimationUtils.loadAnimation(getContext(),R.anim.bottom_out_down);

        checkLocationPermission();

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ContenedorZtop = view.findViewById(R.id.Contenedor_Ztop);

        Card_Image = view.findViewById(R.id.card_img);
        Card_nombre = view.findViewById(R.id.card_nombre);
        Card_descripcion = view.findViewById(R.id.card_descripcion);

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
        btn_ver_mas = view.findViewById(R.id.btn_ver_mas);
        btn_ver_mas.setOnClickListener(v -> {
            if(ContenedorZtop.getVisibility() == View.GONE){

                getChildFragmentManager().beginTransaction().replace(ContenedorZtop.getId(),new InformacionCenser(IdCenser),"InformacionCenser").commit();

                Animation animation = AnimationUtils.loadAnimation(getContext(),R.anim.abajo_arriba);
                ContenedorZtop.setAnimation(animation);
                animation.start();
                ContenedorZtop.setVisibility(View.VISIBLE);
            }
        });
        view.findViewById(R.id.Como_llegar).setOnClickListener(v->{

            String url = getDirectionsUrl(Constant.PosicionUsuario, Constant.PosicionCenser);
            Toast.makeText(getContext(), url, Toast.LENGTH_SHORT).show();
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);
        });

        Buscador = view.findViewById(R.id.Buscador);
        Buscador.addTextChangedListener(new TextWatcher() {
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
        });
        Buscador.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    BuscarCenser(v.getText().toString());
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

        return view;
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

            Constant.PosicionUsuario = UserLocation;

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

    private void BuscarCenser(String key){
        request = Volley.newRequestQueue(getContext());
        jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, "http://192.168.100.215/test/traer_censer.php?key="+key, null, response -> {
            JSONArray json = response.optJSONArray("CENSERS");

            ArrayList<EntidadesCensers> list = new ArrayList<>();
            try {
                for (int i = 0; i < json.length(); i++) {
                    EntidadesCensers entidades = new EntidadesCensers();
                    JSONObject jsonObject = json.getJSONObject(i);
                    entidades.setIdCenser(jsonObject.optString("Id"));
                    entidades.setNombre(jsonObject.optString("Nombre"));
                    entidades.setDescripcion(jsonObject.optString("Descripcion"));
                    entidades.setLat(jsonObject.optString("Lat"));
                    entidades.setLang(jsonObject.optString("Lang"));
                    list.add(entidades);

                    LatLng sydney = new LatLng(Double.parseDouble(entidades.getLat()),Double.parseDouble( entidades.getLang()));
                    /*Mapa.addMarker(
                            new MarkerOptions()
                                    .position(sydney)
                                    .title(entidades.getNombre()));*/
                    MarkerOptions options = new MarkerOptions();
                    options.position(sydney);

                    Marker marker =  Mapa.addMarker(options);
                    marker.setTitle(entidades.getNombre());
                    marker.setTag(i);

                    Mapa.setOnMarkerClickListener(marker1 -> {
                        int position = Integer.parseInt(marker1.getTag().toString());

                        Constant.PosicionCenser = new LatLng(Double.parseDouble(list.get(position).getLat()),Double.parseDouble(list.get(position).getLang()));

                        Card.setAnimation(abajo_arriba);
                        abajo_arriba.start();
                        Card.setVisibility(View.VISIBLE);

                        IdCenser = list.get(position).getIdCenser();

                        Glide.with(getContext()).load("http://192.168.100.215/test/assets/Censers/"+list.get(position).getIdCenser()+"/1.jpg").centerCrop().override(240).into(Card_Image);
                        Card_nombre.setText(list.get(position).getNombre());
                        Card_descripcion.setText(list.get(position).getDescripcion());

                        return false;
                    });

                }
            } catch (JSONException e) {
                Log.e("BuscarCenser","Error->"+e);
            }
        }, error -> Log.e("BuscarCenser","request Error->"+error));
        request.add(jsonObjectRequest);
    }


    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Key
        String key = "key=AIzaSyCVEAIE78U4OG_ENKwVIxHMNAg47WRDpX4";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+key;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }

    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception on download", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /** A class to download data from Google Directions URL */
    private class DownloadTask extends AsyncTask<String, Void, String> {

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
                Log.d("DownloadTask","DownloadTask : " + data);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Directions in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
                if(mPolyline != null){
                    mPolyline.remove();
                }
                mPolyline = Mapa.addPolyline(lineOptions);

            }
        }
    }

}