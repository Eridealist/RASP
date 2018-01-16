package genericDashButton;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "__metadata",
    "Raspid",
    "Custid",
    "Funcid",
    "Funspc",
    "Rasnam",
    "Rasdes"
})
public class D {

    @JsonProperty("__metadata")
    private Metadata metadata;
    @JsonProperty("Raspid")
    private String raspid;
    @JsonProperty("Custid")
    private String custid;
    @JsonProperty("Funcid")
    private String funcid;
    @JsonProperty("Funspc")
    private String funspc;
    @JsonProperty("Rasnam")
    private String rasnam;
    @JsonProperty("Rasdes")
    private String rasdes;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("__metadata")
    public Metadata getMetadata() {
        return metadata;
    }

    @JsonProperty("__metadata")
    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @JsonProperty("Raspid")
    public String getRaspid() {
        return raspid;
    }

    @JsonProperty("Raspid")
    public void setRaspid(String raspid) {
        this.raspid = raspid;
    }

    @JsonProperty("Custid")
    public String getCustid() {
        return custid;
    }

    @JsonProperty("Custid")
    public void setCustid(String custid) {
        this.custid = custid;
    }

    @JsonProperty("Funcid")
    public String getFuncid() {
        return funcid;
    }

    @JsonProperty("Funcid")
    public void setFuncid(String funcid) {
        this.funcid = funcid;
    }

    @JsonProperty("Funspc")
    public String getFunspc() {
        return funspc;
    }

    @JsonProperty("Funspc")
    public void setFunspc(String funspc) {
        this.funspc = funspc;
    }

    @JsonProperty("Rasnam")
    public String getRasnam() {
        return rasnam;
    }

    @JsonProperty("Rasnam")
    public void setRasnam(String rasnam) {
        this.rasnam = rasnam;
    }

    @JsonProperty("Rasdes")
    public String getRasdes() {
        return rasdes;
    }

    @JsonProperty("Rasdes")
    public void setRasdes(String rasdes) {
        this.rasdes = rasdes;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
