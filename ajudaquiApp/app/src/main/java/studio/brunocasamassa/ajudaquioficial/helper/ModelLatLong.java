package studio.brunocasamassa.ajudaquioficial.helper;

/**
 * Created by bruno on 26/04/2017.
 */

public class ModelLatLong {

    public ModelLatLong() {

    }

    private Double latitude;

    private String email ;
    private Double longitude;

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}