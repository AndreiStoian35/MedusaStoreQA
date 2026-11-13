package data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;


@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentCollectionDTO {

    @JsonProperty("payment_collection")
   public PaymentCollection paymentCollectionList;



    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentCollection{

        @JsonProperty("id")
        public String id;

        @JsonProperty("payment_sessions")
        public List<PaymentSession> paymentSessionList;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentSession{

        @JsonProperty("id")
        public String paymentSessionId;

        @JsonProperty("provider_id")
        public String providerId;
    }
}
