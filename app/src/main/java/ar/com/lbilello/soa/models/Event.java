package ar.com.lbilello.soa.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Event {

    @SerializedName("type_events")
    @Expose
    private String typeEvents;
    @SerializedName("dni")
    @Expose
    private long dni;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("id")
    @Expose
    private int id;

    public String getTypeEvents() {
        return typeEvents;
    }

    public void setTypeEvents(String typeEvents) {
        this.typeEvents = typeEvents;
    }

    public Event withTypeEvents(String typeEvents) {
        this.typeEvents = typeEvents;
        return this;
    }

    public long getDni() {
        return dni;
    }

    public void setDni(long dni) {
        this.dni = dni;
    }

    public Event withDni(long dni) {
        this.dni = dni;
        return this;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Event withDescription(String description) {
        this.description = description;
        return this;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Event withId(int id) {
        this.id = id;
        return this;
    }

}
