package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)

public class CheckoutDTO {

    @JsonProperty("type")
    public String type;

    @JsonProperty("order")
    public Order order;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Order{

        @JsonProperty("id")
        public String id;

        @JsonProperty("status")
        public String status;

        @JsonProperty("region_id")
        public String regionId;


        @JsonProperty("items")
        public List<Items> items;

        @JsonProperty("shipping_methods")
        public List<ShippingMethods> shippingMethods;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ShippingMethods{

        @JsonProperty("id")
        public String orderId;

        @JsonProperty("shipping_option_id")
        public String shippingOptionId;
    }


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Items{

        @JsonProperty("variant_id")
        public String variantId;
    }
}
