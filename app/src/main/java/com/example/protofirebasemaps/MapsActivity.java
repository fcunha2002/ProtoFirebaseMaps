package com.example.protofirebasemaps;

import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    //getReference() busca o nó raiz
    private DatabaseReference reference = FirebaseDatabase.getInstance().getReference();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        loginToFirebase();
    }

    private void loginToFirebase() {
        //Autentica no Firebase, usando email e senha//
        String email = getString(R.string.test_email);
        String password = getString(R.string.test_password);

        //Chama OnCompleteListener quando o usuário é autenticado corretamente//
        FirebaseAuth.getInstance().signInWithEmailAndPassword(
                email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                //Se o usuário foi autenticado...//
                if (task.isSuccessful()) {
                    DatabaseReference drLocal = reference.child("location");

                    //Adiciona um listener no nodo "usuário" e toda vez que ocorre alguma alteração
                    //o listener é acionado
                    drLocal.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Log.i("LOCALIZA", dataSnapshot.child("latitude").getValue().toString());
                            double latitude = (double) dataSnapshot.child("latitude").getValue();
                            double longitude = (double) dataSnapshot.child("longitude").getValue();
                            desenharMarcador(latitude, longitude);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });

                    // Add a marker in Sydney and move the camera
                    LatLng local = new LatLng(-30, -51);
                    mMap.addMarker(new MarkerOptions().position(local).title("Marker in Sydney"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
                } else {
                    //Se autenticação falhou, loga o erro//
                    Log.d("FALHA", "Firebase authentication failed");
                }
            }
        });
    }


    //Método que desenha o marcador
    private void desenharMarcador(double latitude, double longitude){
        mMap.clear();
        LatLng local = new LatLng(latitude, longitude);
        mMap.addMarker(
                new MarkerOptions().position(local)
                        .title("Caminhando...")
                        .icon(
                                BitmapDescriptorFactory.fromResource(R.drawable.walk_32_color)
                        )
        );
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(local, 18));
    }



}






