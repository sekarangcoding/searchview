package com.example.searchview;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    ArrayAdapter<Get_data> adapter;
    ArrayList<Get_data>list_data;
    ListView listView;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = (ListView) findViewById(R.id.list);
        searchView = (SearchView) findViewById(R.id.search);

        ambil_data();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                ArrayList<Get_data> newList = new ArrayList<>();
                for(Get_data person : newList)
                {
                    String judul = person.getJudul();
                    String penulis = person.getPenulis();
                    String penerbit = person.getPenerbit().toLowerCase();
                    String tahun = person.getTahun();
                    String isbn = person.getIsbn();
                    String file = person.getFile();
                    if(judul.contains(newText)){
                        newList.add(person);
                    }
                }
                adapter.setFilter(newList);

                return true;
            }
        });
    }


    void ambil_data()
    {
        final String link = "http://192.168.0.102/buku/api/buku.php";
        StringRequest respon = new StringRequest(Request.Method.POST, link, new Response.Listener<String>()
        {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("hasil");
                            list_data = new ArrayList<>();
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject hasil = jsonArray.getJSONObject(i);
                                String judul = hasil.getString("judul");
                                String penulis = hasil.getString("penulis");
                                String penerbit = hasil.getString("penerbit");
                                String tahun = hasil.getString("tahun_terbit");
                                String isbn = hasil.getString("isbn");
                                String file = hasil.getString("file_buku");
                                list_data.add(new Get_data(
                                        judul,
                                        penulis,
                                        penerbit,
                                        tahun,
                                        isbn,
                                        file
                                ));
                            }
                            ListView listView = findViewById(R.id.list);
                            final Custom_adapter adapter= new Custom_adapter(MainActivity.this, list_data);
                            listView.setAdapter(adapter);

                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    Get_data get_data = (Get_data) adapter.getItem(position);
                                    //Toast.makeText(getApplicationContext(),get_data.getJudul(),Toast.LENGTH_SHORT).show();
                                    String judul = get_data.getJudul();

                                    Intent intent =new Intent(MainActivity.this, Home.class);
                                    intent.putExtra("judul", judul);
                                    startActivity(intent);
                                    finish();
                                }
                            });
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this,error.getMessage() ,Toast.LENGTH_SHORT).show();
                    }
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(respon);

    }



    class Get_data
    {
        String judul="", penulis="", penerbit="", tahun="", isbn="", file="";
        Get_data(String judul, String penulis, String penerbit, String tahun, String isbn,String file){
            this.judul = judul;
            this.penulis = penulis;
            this.penerbit = penerbit;
            this.tahun = tahun;
            this.isbn = isbn;
            this.file = file;
        }

        public String getJudul() {
            return judul;
        }

        public String getPenulis() {
            return penulis;
        }

        public String getPenerbit() {
            return penerbit;
        }

        public String getTahun() {
            return tahun;
        }

        public String getIsbn() {
            return isbn;
        }

        public String getFile() {
            return file;
        }
    }

    class Custom_adapter extends BaseAdapter{

        Context context;
        LayoutInflater layoutInflater;
        ArrayList<Get_data> model;

        Custom_adapter(Context context, ArrayList<Get_data> model){
            layoutInflater=LayoutInflater.from(context);
            this.context = context;
            this.model = model;
        }

        @Override
        public int getCount() {
            return model.size();
        }

        @Override
        public Object getItem(int position) {
            return model.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = layoutInflater.inflate(R.layout.list,null);

            //deklarasi text yang ada di list layout
            TextView judul, penulis, penerbit, tahun, isbn, file;
            judul = view.findViewById(R.id.Judul);
            penulis = view.findViewById(R.id.Penulis);
            penerbit = view.findViewById(R.id.Penerbit);
            tahun = view.findViewById(R.id.Tahun);
            isbn = view.findViewById(R.id.Isbn);
            file = view.findViewById(R.id.file);

            //menampilkan data model data ke textview
            judul.setText(model.get(position).getJudul());
            penulis.setText(model.get(position).getPenulis());
            penerbit.setText(model.get(position).getPenerbit());
            tahun.setText(model.get(position).getTahun());
            isbn.setText(model.get(position).getIsbn());
            file.setText(model.get(position).getFile());

            return view;
        }


        public void setFilter(ArrayList<Get_data> newList) {
            list_data= new ArrayList<>();
            list_data.addAll(newList);
            adapter.notifyDataSetChanged();
        }
    }
}
