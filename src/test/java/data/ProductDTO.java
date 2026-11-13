package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown=true)
public class ProductDTO {

    @JsonProperty("product")
    public Product product;




    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Product{

        @JsonProperty("id")
        public String id;
    }
}
