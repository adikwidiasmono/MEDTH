package mas.medth.service.model.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by adikwidiasmono on 10/02/18.
 */

public class ResGISResponse {
    @SerializedName("objectIdFieldName")
    @Expose
    private String objectIdFieldName;
    @SerializedName("globalIdFieldName")
    @Expose
    private String globalIdFieldName;
    @SerializedName("geometryType")
    @Expose
    private String geometryType;
    @SerializedName("spatialReference")
    @Expose
    private ResGISSpatialReference spatialReference;
    @SerializedName("fields")
    @Expose
    private List<Object> fields;
    @SerializedName("features")
    @Expose
    private List<ResGISFeature> features;

    public String getObjectIdFieldName() {
        return objectIdFieldName;
    }

    public void setObjectIdFieldName(String objectIdFieldName) {
        this.objectIdFieldName = objectIdFieldName;
    }

    public String getGlobalIdFieldName() {
        return globalIdFieldName;
    }

    public void setGlobalIdFieldName(String globalIdFieldName) {
        this.globalIdFieldName = globalIdFieldName;
    }

    public String getGeometryType() {
        return geometryType;
    }

    public void setGeometryType(String geometryType) {
        this.geometryType = geometryType;
    }

    public ResGISSpatialReference getSpatialReference() {
        return spatialReference;
    }

    public void setSpatialReference(ResGISSpatialReference spatialReference) {
        this.spatialReference = spatialReference;
    }

    public List<Object> getFields() {
        return fields;
    }

    public void setFields(List<Object> fields) {
        this.fields = fields;
    }

    public List<ResGISFeature> getFeatures() {
        return features;
    }

    public void setFeatures(List<ResGISFeature> features) {
        this.features = features;
    }
}
