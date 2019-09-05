package com.example.maps;


import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, LocationListener, GoogleMap.OnMapLongClickListener ,PopWindow.FragmentListener{

    private GoogleMap mMap;
    private Marker MiUbicacion;
    private Polygon icesiArea;
    private TextView sitioTV;
    private ArrayList<Marker> markers;
    private Button marcadores;
    private boolean enable;
    private PopWindow window;
    private String respuesta;
    private double distancia;
    private LatLng var;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    enable=false;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        marcadores= findViewById(R.id.marcador_btn);
        marcadores.setOnClickListener(new View.OnClickListener() {
                                          @Override
                                          public void onClick(View view) {
             enable=true;
             markers= new ArrayList<>();
                                          }
                                      }
        );
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        ActivityCompat.requestPermissions(this, new String[]{
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,

        },11 );
        sitioTV=findViewById(R.id.sitioTV);
        window= new PopWindow();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);
icesiArea=mMap.addPolygon(new PolygonOptions().add(
        new LatLng(3.342960,-76.530695),
        new LatLng(3.343153,-76.529135),
        new LatLng(3.339945,-76.529355),
        new LatLng(3.339816,-76.531097)
        ));
        // Add a marker in Sydney and move the camera
        LatLng icesi = new LatLng(3.341797, -76.530178);
        MiUbicacion =        mMap.addMarker(new MarkerOptions().position(icesi).title("Marker Icesi"));
     //   MiUbicacion.setSnippet();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(icesi,15));
        //solicitud de ubicacion
        LocationManager manager=  (LocationManager) getSystemService(LOCATION_SERVICE);
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0,this);
    }

    @Override
    public void onLocationChanged(Location location) {
        LatLng pos = new LatLng(location.getLatitude(), location.getLongitude());
        MiUbicacion.setPosition(pos);
        MiUbicacion.setSnippet(Direccion(pos));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
        boolean isIcesi =PolyUtil.containsLocation(pos, icesiArea.getPoints(), true);
        if(isIcesi){
            sitioTV.setVisibility(View.VISIBLE);
        }else{
            sitioTV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }
    @Override
    public void ApplyText(String name) {
        respuesta=name;
        Marker marker = mMap.addMarker(new MarkerOptions().position(var));
        markers.add(marker);
        double dis=0;

        dis = Math.sqrt(Math.pow(marker.getPosition().latitude - MiUbicacion.getPosition().latitude, 2) + Math.pow(marker.getPosition().longitude - MiUbicacion.getPosition().longitude,2));

        dis = dis * 111.12 * 1000;
        marker.setSnippet("Distancia: "+dis);



        marker.setTitle("nombre : "+respuesta);
        if(markers.size() == 1){
            Marker a = markers.get(markers.size() - 1);
            distancia = Math.sqrt(Math.pow(a.getPosition().latitude - MiUbicacion.getPosition().latitude, 2) + Math.pow(a.getPosition().longitude - MiUbicacion.getPosition().longitude,2));
            distancia = distancia * 111.12 * 1000;

            sitioTV.setText("Marcador mas cercano " + a.getTitle());
        }else if(markers.size() > 1){
            Marker a = markers.get(markers.size() - 1);
            double nuevo = Math.sqrt(Math.pow(a.getPosition().latitude - MiUbicacion.getPosition().latitude, 2) + Math.pow(a.getPosition().longitude - MiUbicacion.getPosition().longitude,2));
            nuevo = nuevo * 111.12 * 1000;

            if(nuevo < distancia){
                distancia = nuevo;
                sitioTV.setText("Marcador mas cercano " + a.getTitle());
            }
            if(distancia<50){
                sitioTV.setText("El usuario ya se encuentra en el lugar marcado");
            }

        }
    }
    @Override
    public void onMapLongClick(LatLng latLng) {

        if(enable==true) {
            var = latLng;
            PopWindow s = new PopWindow();
            s.show(getSupportFragmentManager(),"notice");

            }

    }


    public void openDialog(){

    }



    public String Direccion(LatLng location){
        String direccion="";
        Geocoder geocoder= new Geocoder(MapsActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1);
            for (int i=0; i<addresses.size();i++) {
                String adreses = addresses.get(0).getAddressLine(i);
                direccion = adreses;
            }
        }catch (Exception e){

        }
        return direccion;
    }
}
