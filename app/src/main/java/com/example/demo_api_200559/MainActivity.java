package com.example.demo_api_200559;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private Button btnGuardar;
    private Button btnBuscar;
    private Button btnEliminar;
    private Button btnActualizar;
    private EditText etCodigoBarras;
    private EditText etDescripcion;
    private EditText etMarca;
    private EditText etPrecioC;
    private EditText etPrecioV;
    private EditText etExistencias;
    private ListView lvProducto;
    private RequestQueue colaPeticiones;
    private JsonArrayRequest jsonArrayRequest;
    private ArrayList<String> origenDatos = new ArrayList<String>();
    private ArrayAdapter<String> adapter;
    //private String url = "http://10.10.62.5:3300";
    private String url = "http://192.168.1.70:3300";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnGuardar = findViewById(R.id.btnGuardar);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnEliminar = findViewById(R.id.btnEliminar);
        btnActualizar = findViewById(R.id.btnActualizar);
        etCodigoBarras = findViewById(R.id.etCodigoBarras);
        etDescripcion = findViewById(R.id.etDescripcion);
        etMarca = findViewById(R.id.etMarca);
        etPrecioC = findViewById(R.id.etPrecioC);
        etPrecioV = findViewById(R.id.etPrecioV);
        etExistencias = findViewById(R.id.etExistencias);
        colaPeticiones = Volley.newRequestQueue(this);
        lvProducto = findViewById(R.id.lvProducto);
        listarProductos();


        btnEliminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etCodigoBarras.getText().toString().isEmpty()) {
                    Toast.makeText(MainActivity.this, "Ingrese el código de barras", Toast.LENGTH_SHORT).show();
                } else {
                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                            Request.Method.DELETE,
                            url + "/borrar/" + etCodigoBarras.getText().toString(),
                            null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        if (response.getString("status").equals("Producto Eliminado")) {
                                            Toast.makeText(MainActivity.this, "Producto Eliminado", Toast.LENGTH_SHORT).show();
                                            etCodigoBarras.setText("");
                                            etDescripcion.setText("");
                                            etMarca.setText("");
                                            etPrecioC.setText("");
                                            etPrecioV.setText("");
                                            etExistencias.setText("");
                                            adapter.clear();
                                            lvProducto.setAdapter(adapter);
                                            listarProductos();
                                        } else if (response.getString("status").equals("Not Found")) {
                                            Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                        }

                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                    );
                    colaPeticiones.add(jsonObjectRequest);
                }
            }
        });


        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JsonObjectRequest peticion = new JsonObjectRequest(
                        Request.Method.GET,
                        url + "/" + etCodigoBarras.getText().toString(),
                        null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                if (response.has("status"))
                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                else {
                                    try {
                                        etDescripcion.setText(response.getString("descripcion"));
                                        etMarca.setText(response.getString("marca"));
                                        etPrecioC.setText(String.valueOf(response.getInt("preciocompra")));
                                        etPrecioV.setText(String.valueOf(response.getInt("precioventa")));
                                        etExistencias.setText(String.valueOf(response.getInt("existencias")));
                                    } catch (JSONException e) {
                                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(peticion);
            }
        });


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JSONObject producto = new JSONObject();
                try {
                    producto.put("codigobarras", etCodigoBarras.getText().toString());
                    producto.put("descripcion", etDescripcion.getText().toString());
                    producto.put("marca", etMarca.getText().toString());
                    producto.put("preciocompra", Float.parseFloat(etPrecioC.getText().toString()));
                    producto.put("precioventa", Float.parseFloat(etPrecioV.getText().toString()));
                    producto.put("existencias", Float.parseFloat(etExistencias.getText().toString()));
                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                        Request.Method.POST,
                        url + "/insert",
                        producto,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    if (response.getString("status").equals("Producto insertado")) {
                                        Toast.makeText(MainActivity.this, "Producto insertado", Toast.LENGTH_SHORT).show();
                                        etCodigoBarras.setText("");
                                        etDescripcion.setText("");
                                        etMarca.setText("");
                                        etPrecioC.setText("");
                                        etPrecioV.setText("");
                                        etExistencias.setText("");
                                        adapter.clear();
                                        lvProducto.setAdapter(adapter);
                                        listarProductos();
                                    }
                                } catch (JSONException e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
                colaPeticiones.add(jsonObjectRequest);
            }
        });


        btnActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String codigoBarras = etCodigoBarras.getText().toString();

                if (codigoBarras.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Escribe el código", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        JSONObject productos = new JSONObject();
                        productos.put("codigobarras", codigoBarras);

                        if (!etDescripcion.getText().toString().isEmpty()) {
                            productos.put("descripcion", etDescripcion.getText().toString());
                        }

                        if (!etMarca.getText().toString().isEmpty()) {
                            productos.put("marca", etMarca.getText().toString());
                        }

                        if (!etPrecioC.getText().toString().isEmpty()) {
                            productos.put("preciocompra", Float.parseFloat(etPrecioC.getText().toString()));
                        }

                        if (!etPrecioV.getText().toString().isEmpty()) {
                            productos.put("precioventa", Float.parseFloat(etPrecioV.getText().toString()));
                        }

                        if (!etExistencias.getText().toString().isEmpty()) {
                            productos.put("existencias", Float.parseFloat(etExistencias.getText().toString()));
                        }

                        JsonObjectRequest actualizar = new JsonObjectRequest(
                                Request.Method.PUT,
                                url + "/actualizar/" + codigoBarras,
                                productos,
                                new Response.Listener<JSONObject>() {
                                    @Override
                                    public void onResponse(JSONObject response) {
                                        try {
                                            if (response.has("status")) {
                                                String status = response.getString("status");
                                                if (status.equals("Producto Eliminado")) {
                                                    if (productos.length() > 1) {
                                                        Toast.makeText(MainActivity.this, "Producto actualizado", Toast.LENGTH_SHORT).show();
                                                        etCodigoBarras.setText("");
                                                        etDescripcion.setText("");
                                                        etMarca.setText("");
                                                        etPrecioC.setText("");
                                                        etPrecioV.setText("");
                                                        etExistencias.setText("");
                                                        adapter.clear();
                                                        lvProducto.setAdapter(adapter);
                                                        listarProductos();
                                                    } else {
                                                        Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                                    }
                                                } else if (status.equals("Not Found")) {
                                                    Toast.makeText(MainActivity.this, "Producto no encontrado", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } catch (JSONException e) {
                                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                },
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(VolleyError error) {
                                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                        colaPeticiones.add(actualizar);
                    } catch (JSONException e) {
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void listarProductos(){
        jsonArrayRequest = new JsonArrayRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        for(int i = 0 ; i<response.length();i++){
                            try {
                                String codigobarras = response.getJSONObject(i).getString("codigobarras");
                                String descripcion = response.getJSONObject(i).getString("descripcion");
                                String marca = response.getJSONObject(i).getString("marca");
                                origenDatos.add(codigobarras+": "+descripcion+": "+marca);
                            } catch (JSONException e) {

                            }
                        }
                        adapter = new ArrayAdapter<>(MainActivity.this, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item, origenDatos);
                        lvProducto.setAdapter(adapter);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
        );
        colaPeticiones.add(jsonArrayRequest);
    }
}