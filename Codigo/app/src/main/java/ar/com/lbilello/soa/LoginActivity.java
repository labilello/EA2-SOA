package ar.com.lbilello.soa;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

import ar.com.lbilello.soa.io.UserApiAdapter;
import ar.com.lbilello.soa.io.response.UserResponse;
import ar.com.lbilello.soa.models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    private View loginFormView;
    private ProgressBar loginProgressView;

    private AutoCompleteTextView emailTextView;
    private EditText passwordTextView;

    private Intent nextActivity;

    private User user = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nextActivity = new Intent(this, Main2Activity.class);
        emailTextView = findViewById(R.id.email);
        passwordTextView = findViewById(R.id.password);
        loginFormView = findViewById(R.id.email_login_form);
        loginProgressView = findViewById(R.id.login_progress);
        Button loginButton = findViewById(R.id.email_sign_in_button);
        TextView sign_up = findViewById(R.id.sign_up);

        sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), RegisterActivity.class);
                v.getContext().startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doLogin();
            }
        });
        showProgress( false );
    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyConnection();

        if( user != null) {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        }
    }

    public void verifyLogin() {

        final Call<UserResponse> call = UserApiAdapter.getApiService().doLogin(emailTextView.getText().toString(), passwordTextView.getText().toString());
        call.enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) { verifyResponse(response); }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                if(t!=null)
                {
                    t.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Error al conectarse al servidor!", Toast.LENGTH_LONG ).show();
                    showProgress( false );
                    verifyConnection();
                }
            }
        });
    }

    private void verifyResponse( Response<UserResponse> response ) {

        if (response.code() == 200)
        {
            nextActivity.putExtra("auth", new User(
                    response.body().getToken(),
                    response.body().getToken_refresh()
            ));
            startActivity(nextActivity);
            Utils.logEvent(getApplicationContext(), response.body().getToken(), "Login", "Usuario logueado");
            finish();

        } else if ( response.code() == 400 ) {
            user = null;
            Toast.makeText(this,  "Datos incorrectos!", Toast.LENGTH_LONG ).show();
            showProgress( false );

        } else if ( response.code() > 400 ) {
            user = null;
            Toast.makeText(this,    "Error al verificar los datos!", Toast.LENGTH_LONG ).show();
            showProgress( false );
        }
    }

    private boolean verifyInputs() {
        boolean isOk = true;

        if ( !isEmailValid( emailTextView.getText().toString() ) ) {
            emailTextView.setError( getString(R.string.invalid_email) );
            isOk = false;
        }

        if ( !isPasswordValid( passwordTextView.getText().toString() ) ) {
            passwordTextView.setError( getString(R.string.invalid_password) );
            isOk = false;
        }

        return isOk;
    }

    private boolean isEmailValid(String email) {
        //add your own logic
        return ( !TextUtils.isEmpty( email ) && email.contains("@") );
    }

    private boolean isPasswordValid(String password) {
        //add your own logic
        return ( !TextUtils.isEmpty( password ) && password.length() > 7 );
    }

    private void doLogin() {

        showProgress( true );

        if( !verifyInputs() ) {
            showProgress( false );
            return;
        }

        Toast.makeText(this,"Veficando datos en el servidor...", Toast.LENGTH_SHORT).show();
        verifyLogin();
    }

    private void showProgress(boolean show) {
        if(show) {
            loginFormView.setVisibility(View.GONE);
            loginProgressView.setVisibility(View.VISIBLE);
        } else {
            loginProgressView.setVisibility(View.GONE);
            loginFormView.setVisibility(View.VISIBLE);
        }
    }

    private void verifyConnection() {
        TextView status = findViewById(R.id.conection_status);
        Button button = findViewById(R.id.email_sign_in_button);

        Utils.verifyConnection(this, status, button);
    }
}
