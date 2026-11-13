package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CartDTO {

    @JsonProperty("cart")
    public Cart cart;




    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Cart{

        @JsonProperty("id")
        public String id;

        @JsonProperty("items")
        public List<Items> items;

        @JsonProperty("region_id")
        public String regionId;

        public List<ShippingMethods> shippingMethodsList;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items{

        @JsonProperty("id")
        public String id;

        @JsonProperty("variant_id")
        public String variantId;

    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingMethods{

        @JsonProperty("shipping_option_id")
        public String shippingOptionId;

    }




}
