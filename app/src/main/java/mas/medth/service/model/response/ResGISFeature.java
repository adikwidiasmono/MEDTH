package mas.medth.service.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by adikwidiasmono on 10/02/18.
 */

public class ResGISFeature {
    @SerializedName("attributes")
    @Expose
    private ResGISAttributes attributes;
    @SerializedName("geometry")
    @Expose
    private ResGISGeometry geometry;

    public ResGISAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(ResGISAttributes attributes) {
        this.attributes = attributes;
    }

    public ResGISGeometry getGeometry() {
        return geometry;
    }

    public void setGeometry(ResGISGeometry geometry) {
        this.geometry = geometry;
    }
}
