package ar.com.lbilello.soa.io.response;

import ar.com.lbilello.soa.models.Event;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EventResponse {

    @SerializedName("success")
    @Expose
    private boolean success;
    @SerializedName("env")
    @Expose
    private String env;
    @SerializedName("event")
    @Expose
    private Event event;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public EventResponse withSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public String getEnv() {
        return env;
    }

    public void setEnv(String env) {
        this.env = env;
    }

    public EventResponse withEnv(String env) {
        this.env = env;
        return this;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public EventResponse withEvent(Event event) {
        this.event = event;
        return this;
    }

}