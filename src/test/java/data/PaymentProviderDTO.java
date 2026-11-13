package data;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown=true)
public class PaymentProviderDTO {

    @JsonProperty("payment_providers")
    public List<PaymentProvider> paymentProviderList;


    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentProvider{

        @JsonProperty("id")
        public String id;
    }
}
