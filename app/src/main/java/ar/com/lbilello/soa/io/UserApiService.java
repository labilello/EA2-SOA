package ar.com.lbilello.soa.io;

import ar.com.lbilello.soa.io.response.EventResponse;
import ar.com.lbilello.soa.io.response.UserResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;

public interface UserApiService {

    @POST("register")
    @FormUrlEncoded
    Call<UserResponse> doRegister(
            @Field("env") String env,
            @Field("name") String name,
            @Field("lastname") String lastname,
            @Field("dni") long dni,
            @Field("email") String email,
            @Field("password") String password,
            @Field("commission") int commission
    );

    @POST("login")
    @FormUrlEncoded
    Call<UserResponse> doLogin(
            @Field("email") String email,
            @Field("password") String password
    );

    @PUT("refresh")
    Call<UserResponse> refreshToken(@Header("Authorization") String auth);

    @POST("event")
    @FormUrlEncoded
    Call<EventResponse> logEvent(
            @Header("Authorization") String auth,
            @Field("env") String env,
            @Field("type_events") String type_events,
            @Field("description") String description
    );

}
