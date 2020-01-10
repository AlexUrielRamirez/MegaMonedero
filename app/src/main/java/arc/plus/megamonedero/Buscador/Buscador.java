package arc.plus.megamonedero.Buscador;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Normalizer;
import java.util.ArrayList;

import arc.plus.megamonedero.R;

public class Buscador extends Fragment implements SearchView.OnQueryTextListener {

    public Buscador(Integer TIPO_BUSQUEDA) {
        this.TIPO_BUSQUEDA = TIPO_BUSQUEDA;
    }

    private Integer TIPO_BUSQUEDA;
    private SearchView Buscador;
    private RecyclerView Sugerencias;
    private ArrayList<Entidades> arrayList = new ArrayList<>(), FilterList = new ArrayList<>();
    private class FillList extends AsyncTask<Void,Void,Void>{
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_buscador, container, false);

        new FillList().execute();

        Buscador = view.findViewById(R.id.Buscador);
        Buscador.setOnQueryTextListener(this);

        Sugerencias = view.findViewById(R.id.RecyclerSugerencias);
        Sugerencias.setLayoutManager(new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,false));
        Sugerencias.setHasFixedSize(true);

        Sugerencias.setAdapter(new AdapterSugerencias(arrayList));

        return view;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        FilterList.clear();
        for(int i = 0; i<arrayList.size();i++){
            if(arrayList.get(i).getTitulo().toLowerCase().contains(removeDiacriticalMarks(newText.toLowerCase()))){
                Entidades entidades = new Entidades();
                entidades.setTitulo(arrayList.get(i).getTitulo());
                entidades.setSubtitulo(arrayList.get(i).getSubtitulo());
                FilterList.add(entidades);
            }
        }

        Sugerencias.setAdapter(new AdapterSugerencias(FilterList));
        Sugerencias.getAdapter().notifyDataSetChanged();
        return false;
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

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

}
