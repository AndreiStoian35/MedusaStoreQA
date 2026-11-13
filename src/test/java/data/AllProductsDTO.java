package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class AllProductsDTO {

    @JsonProperty("products")
    public List<Product> products;




    @JsonIgnoreProperties(ignoreUnknown=true)
    public static class Product{

        @JsonProperty("id")
        public String id;
    }
}
