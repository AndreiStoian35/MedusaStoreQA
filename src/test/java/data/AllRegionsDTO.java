package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AllRegionsDTO {

    @JsonProperty("regions")
    public List<Regions> regions;



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Regions{

        @JsonProperty("id")
        public String id;

        public String name;
        public String currencyCode;
    }
}
