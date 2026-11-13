package data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ShippingOptionsDTO {

    @JsonProperty("shipping_options")
    public List<ShippingOptions> shippingOptionsList;



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingOptions{

        @JsonProperty("id")
        public String id;

        public String name;
    }
}
