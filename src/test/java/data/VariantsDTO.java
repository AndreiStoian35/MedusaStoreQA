package data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class VariantsDTO {

    @JsonProperty("variants")
    public List<Variants> variants;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Variants{

        @JsonProperty("id")
        public String id;
    }
}
