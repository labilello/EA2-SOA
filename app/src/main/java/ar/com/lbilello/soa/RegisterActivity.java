package ar.com.lbilello.soa;

import android.annotation.SuppressLint;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.lbilello.soa.io.UserApiAdapter;
import ar.com.lbilello.soa.io.response.UserResponse;
import ar.com.lbilello.soa.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private Intent nextActivity;

    private EditText name;
    private EditText lastname;
    private EditText dni;
    private EditText email;
    private EditText password;
    private EditText commission;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nextActivity = new Intent(this, Main2Activity.class);
        setListeners();

        name        = findViewById(R.id.name);
        lastname    = findViewById(R.id.lastname);
        dni         = findViewById(R.id.dni);
        email       = findViewById(R.id.email);
        password    = findViewById(R.id.password);
        commission  = findViewById(R.id.commission);
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyConnection();
    }

    private void verifyConnection() {
        TextView status = findViewById(R.id.connection_status);
        Button button = findViewById(R.id.register_button);

        Utils.verifyConnection(this, status, button);
    }

    private void setListeners() {
        findViewById(R.id.register_button).setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyInputs();
                doRegister();
            }
        });
    }

    private void doRegister() {
        final Call<UserResponse> call = UserApiAdapter.getApiService().doRegister(
                getString( R.string.app_env ),
                name.getText().toString(),
                lastname.getText().toString(),
                Long.parseLong( dni.getText().toString() ),
                email.getText().toString(),
                password.getText().toString(),
                Integer.parseInt( commission.getText().toString() )
        );

        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.code() == 200)
                {
                    User user = new User(
                            response.body().getToken(),
                            response.body().getToken_refresh()
                    );
                    nextActivity.putExtra("auth", user);

                    Utils.logEvent(getApplicationContext(), user.getToken(), "Register", "Nuevo usuario registrado");
                    startActivity(nextActivity);
                    finish();

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
                    verifyConnection();
                }
            }
        });
    }

    private boolean verifyInputs() {
        boolean isOk = true;

        if ( !isEmailValid( email.getText().toString() ) ) {
            email.setError( getString(R.string.invalid_email) );
            isOk = false;
        }

        if ( !isPasswordValid( password.getText().toString() ) ) {
            password.setError( getString(R.string.invalid_password) );
            isOk = false;
        }

        if ( !isDniValid( dni.getText().toString() ) ) {
            dni.setError( getString(R.string.invalid_dni) );
            isOk = false;
        }

        if ( !isCommissionValid( commission.getText().toString() ) ) {
            commission.setError( getString(R.string.field_required) );
            isOk = false;
        }

        return isOk;
    }

    private boolean isEmailValid(String email) {
        return ( !TextUtils.isEmpty( email ) && email.contains("@") );
    }

    private boolean isPasswordValid(String password) {
        return ( !TextUtils.isEmpty( password ) && password.length() > 7 );
    }

    private boolean isDniValid(String dni) {
        return ( !TextUtils.isEmpty( dni ) && dni.length() > 7 );
    }

    private boolean isCommissionValid(String commission) {
        return ( !TextUtils.isEmpty( commission ) && commission.length() > 1 );
    }


}
