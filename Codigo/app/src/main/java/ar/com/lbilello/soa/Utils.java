package ar.com.lbilello.soa;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import ar.com.lbilello.soa.io.UserApiAdapter;
import ar.com.lbilello.soa.io.response.EventResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Utils {


    public static boolean hasConnection( Context context ) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void verifyConnection(Context context, TextView conStatus, Button btnSubmit) {

        if ( !hasConnection( context ) ) {
            conStatus.setTextColor( context.getResources().getColor( R.color.error_text ));
            conStatus.setText( context.getString( R.string.con_fail ));

            btnSubmit.setEnabled( false );
            btnSubmit.setTextColor( context.getResources().getColor( R.color.darker_gray ));
        } else {
            conStatus.setTextColor( context.getResources().getColor( R.color.colorPrimary ));
            conStatus.setText(  context.getString( R.string.con_ok ) );

            btnSubmit.setTextColor( context.getResources().getColor( R.color.accent ));
            btnSubmit.setEnabled( true );
        }
    }

    public static void logEvent(final Context context, String token, String type_events, String description) {
        final Call<EventResponse> call = UserApiAdapter.getApiService().logEvent(
                context.getString(R.string.api_auth) + " " + token,
                context.getString(R.string.app_env),
                type_events,
                description
        );
        call.enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                if (response.code() == 200)
                {
                    Toast.makeText(context, response.body().getEvent().toString(), Toast.LENGTH_LONG);

                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                if (t != null) {
                    t.printStackTrace();
                    Toast.makeText(context, "Error al enviar evento al sevidor!", Toast.LENGTH_LONG);
                }
            }
        });
    }
}
