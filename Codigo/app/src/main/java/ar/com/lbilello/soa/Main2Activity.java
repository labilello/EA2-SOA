package ar.com.lbilello.soa;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.BatteryManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.ArrayList;

import ar.com.lbilello.soa.io.UserApiAdapter;
import ar.com.lbilello.soa.io.response.UserResponse;
import ar.com.lbilello.soa.models.TokenLive;
import ar.com.lbilello.soa.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Main2Activity extends AppCompatActivity implements SensorEventListener {

    private User user;
    // =================== VIEW ==========================
    private TextView time_token;
    private ArrayList<TextView> girolist  = new ArrayList<TextView>();
    private ProgressBar proximity;


    // =================== TOKEN ==========================
    private TokenLive tokenLive;
    private static Handler timeTokenHandler;
    private Thread countdown = null;
    private boolean appblock = false;

    // =================== BATERIA ==========================
    IntentFilter ifilter;
    Intent batteryStatus;

    // =================== SENSORES ==========================
    private SensorManager sensorManager;
    DecimalFormat dosdecimales = new DecimalFormat("###.###");


    public static Handler getHandler() {
        return timeTokenHandler;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getBatteryStatus(view);
            }
        });

        user = (User) getIntent().getExtras().get("auth");

        // =================== TOKEN =======================
        time_token = findViewById(R.id.time_token);
        countdown = new Thread( new TokenLive( user.getTimeToken() ) );
        countdown.setName("COUNTDOWN");
        timeTokenHandler = handler_token();

        countdown.start();

        // =================== SENSORES =======================
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        girolist.add((TextView) findViewById(R.id.giro_x));
        girolist.add((TextView) findViewById(R.id.giro_y));
        girolist.add((TextView) findViewById(R.id.giro_z));
        proximity = findViewById(R.id.proximity);

        Sensor mProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        proximity.setMax( Math.round( mProximity.getMaximumRange() ) );
    }

    @Override
    protected void onResume() {

        if( !appblock ) {
            sensorsRegister();
        }
        super.onResume();
    }

    @Override
    protected void onPause() {

        if( !appblock ) {
            countdown.interrupt();
        }
        super.onPause();
    }

    private Handler handler_token() {
        return new Handler() {
            @Override
            public void handleMessage(@NonNull Message msg) {
                updateTimeToken( msg.getData().getLong("H"), msg.getData().getLong("M") );
            }
        };
    }

    private synchronized void updateTimeToken (long sec, long min) {
        this.time_token.setText( min + " minutos y " + sec + " segundos" );

        if( min < 1 && sec < 1) {
            appblock = true;
            sensorsUnregister();
            setContentView(R.layout.content_token);

            findViewById(R.id.update_token).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /// UPDATE TOKEN
                    final Call<UserResponse> call = UserApiAdapter.getApiService().refreshToken(getString(R.string.api_auth) + " " + user.getToken_refresh() );

                    call.enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.code() == 200)
                            {
                                updateUser( response.body().getToken(), response.body().getToken_refresh() );
                            } else if ( response.code() == 400 ) {
                                Toast.makeText(getApplicationContext(),  "Datos incorrectos!", Toast.LENGTH_LONG ).show();
                            } else if ( response.code() > 400 ) {
                                Toast.makeText(getApplicationContext(),  "Error al verificar los datos!", Toast.LENGTH_LONG ).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            if(t!=null)
                            {
                                t.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Error al conectarse al servidor!", Toast.LENGTH_LONG ).show();
                            }
                        }
                    });
                }
            });

            findViewById(R.id.log_out).setOnClickListener( new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    logout();
                }
            });
        }

    }

    private void updateUser(String token, String token_refresh) {
        Intent nextActivity = new Intent(this, Main2Activity.class);
        nextActivity.putExtra("auth", new User( token, token_refresh ) );
        startActivity(nextActivity);
        appblock = false;
        finish();
    }

    private void logout() {
        Intent nextActivity = new Intent(this, LoginActivity.class);
        startActivity(nextActivity);
        finish();
    }

    private void getBatteryStatus(View view) {

        String formatOutput = "";

        ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        batteryStatus = getApplicationContext().registerReceiver(null, ifilter);

        // Are we charging / charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);

        if ( status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL ) {
            formatOutput = "Bateria cargandose - ";
        } else
            formatOutput = "Bateria descargandose - ";

        // Battery Level
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        int batteryPct = Math.round( level * 100 / (float)scale );

        formatOutput += "Porcentaje de carga: " + batteryPct + "%";

        Snackbar.make(view, formatOutput, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        synchronized (this)
        {
            switch(event.sensor.getType())
            {
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i = 0; i < 3; i++) {
                        girolist.get( i ).setText( dosdecimales.format(event.values[ i ]) + " m/s2" );
                    }

                    if ((event.values[0] > 25) || (event.values[1] > 25) || (event.values[2] > 25))
                    {
                        Utils.logEvent(this, user.getToken(), "Sensor", "Vibracion detectada");
                    }

                    break;

                case Sensor.TYPE_PROXIMITY :
                    proximity.setProgress( Math.round( event.values[ 0 ] ) );

                    if( event.values[0] == 0 )
                    {
                        Utils.logEvent(this, user.getToken(), "Sensor", "Objecto a menos de " + proximity.getMax() + "cm");
                    }

                    break;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void sensorsRegister () {
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY), SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void sensorsUnregister() {
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY));
        sensorManager.unregisterListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
    }
}


