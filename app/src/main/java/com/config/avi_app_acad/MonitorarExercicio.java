package com.config.avi_app_acad;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MonitorarExercicio extends AppCompatActivity implements OnMapReadyCallback {

    private Chronometer chronometer;
    private TextView velocidadeTextView;
    private TextView distanciaTextView;
    private TextView caloriasTextView;
    private Location ultimaLocalizacao;
    private long tempoUltimaAtualizacao;
    private boolean exercicioIniciado = false;
    private long tempoInicioExercicio;
    private double distanciaTotal = 0;
    private double caloriasGastas = 0;

    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private LocationRequest locationRequest;

    private GoogleMap googleMap;

    {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    Location location = locationResult.getLastLocation();
                    if (ultimaLocalizacao != null) {
                        float distancia = location.distanceTo(ultimaLocalizacao);
                        long tempoDecorrido = location.getTime() - tempoUltimaAtualizacao;
                        double velocidade = distancia / tempoDecorrido;

                        atualizarDadosReais(velocidade, distancia, tempoDecorrido);
                    }
                    ultimaLocalizacao = location;
                    tempoUltimaAtualizacao = location.getTime();
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitorar_exercicio);

        chronometer = findViewById(R.id.chronometer);
        velocidadeTextView = findViewById(R.id.velocidadeTextView);
        distanciaTextView = findViewById(R.id.distanciaTextView);
        caloriasTextView = findViewById(R.id.caloriasTextView);

        chronometer.start();
        iniciarAtualizacao();
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        Button startExerciseButton = findViewById(R.id.startExerciseButton);
        startExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!exercicioIniciado) {
                    iniciarExercicio();
                } else {
                    pararExercicio();
                }
            }
        });

        Button stopExerciseButton = findViewById(R.id.stopExerciseButton);
        stopExerciseButton.setVisibility(View.INVISIBLE);
        stopExerciseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pararExercicio();
            }
        });
    }

    private void iniciarExercicio() {
        exercicioIniciado = true;
        tempoInicioExercicio = SystemClock.elapsedRealtime();

        chronometer.setBase(tempoInicioExercicio);
        chronometer.start();

        iniciarAtualizacao();

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            iniciarAtualizacaoLocalizacao();
        }
        Button stopExerciseButton = findViewById(R.id.stopExerciseButton);
        stopExerciseButton.setVisibility(View.VISIBLE);
    }

    private void atualizarDadosReais(double velocidade, double distancia, long tempoDecorrido) {
        String unidadeVelocidade = "m/s";
        velocidadeTextView.setText(String.format("%.2f %s", velocidade, unidadeVelocidade));

        distanciaTotal += distancia / 1000;
        distanciaTextView.setText(String.format("%.2f km", distanciaTotal));

        double gastoCalorico = calcularGastoCalorico(velocidade, tempoDecorrido);
        caloriasGastas += gastoCalorico;
        caloriasTextView.setText(String.format("%.2f kcal", caloriasGastas));
    }

    private void pararExercicio() {
        exercicioIniciado = false;

        chronometer.stop();
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

        long tempoDecorrido = SystemClock.elapsedRealtime() - tempoInicioExercicio;

        double velocidadeMedia = distanciaTotal / (tempoDecorrido / 1000.0);
        double gastoCalorico = calcularGastoCalorico(velocidadeMedia, tempoDecorrido);
        caloriasGastas += gastoCalorico;
        caloriasTextView.setText(String.format("%.2f kcal", caloriasGastas));

        Button stopExerciseButton = findViewById(R.id.stopExerciseButton);
        stopExerciseButton.setVisibility(View.INVISIBLE);  // Tornar o botão de parar invisível após parar o exercício

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            iniciarAtualizacaoLocalizacao();
        }
    }

    private void iniciarAtualizacao() {
        // Atualização com dados reais da localização
        if (!exercicioIniciado) {
            return;
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (exercicioIniciado) {
                    long tempoDecorrido = SystemClock.elapsedRealtime() - tempoInicioExercicio;
                    // Atualizar a posição no mapa
                    // Simulação de velocidade em m/s
                    double velocidadeInstantanea = distanciaTotal/tempoDecorrido;
                    // Unidade de velocidade definida na tela de configuração
                    String unidadeVelocidade = "m/s";
                    velocidadeTextView.setText(String.format("%.2f %s", velocidadeInstantanea, unidadeVelocidade));

                    // Atualizar o cronômetro
                    chronometer.setBase(tempoInicioExercicio - tempoDecorrido);
                    chronometer.start();
                }
            }
        });


    }

    private void iniciarAtualizacaoLocalizacao() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }

    private double calcularGastoCalorico(double velocidade, long tempoDecorrido) {
        double constanteMetabolica = 3.5;
        double fatorPeso = 2.2;
        double pesoUsuario = 70.0;  // Peso do usuário em kg

        double velocidadeMetrosSegundo = velocidade;

        double gastoCalorico = (constanteMetabolica * pesoUsuario * fatorPeso * tempoDecorrido / 3600) / 200;
        return gastoCalorico;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }



        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng minhaLocalizacao = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaLocalizacao, 15));
                    googleMap.addMarker(new MarkerOptions().position(minhaLocalizacao).title("Minha Localização"));
                }
            }
        });
    }
}
