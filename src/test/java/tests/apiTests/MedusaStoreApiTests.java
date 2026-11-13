package tests.apiTests;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.BaseTest;
import tests.MedusaApiBaseTest;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import utils.FileParseUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class MedusaStoreApiTests extends MedusaApiBaseTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnAllProducts(){

        APIResponse response = medusaStoreApi.getAllProducts();

        String responseBody = response.text();

        Assertions.assertEquals(200, response.status());


    }

    @Test
    public void shouldReturnProduct(){

        String pId =  apiUtils.extractProductId();
        APIResponse response = medusaStoreApi.getProduct(pId);

        Assertions.assertEquals(200, response.status());

    }

    @Test
    public void shouldReturnAllProductVariants(){
        APIResponse response = medusaStoreApi.getAllProductVariants();

        Assertions.assertEquals(200, response.status());
    }

    @Test
    public void shouldReturnAllRegions(){
        APIResponse response = medusaStoreApi.getAllRegions();
        Assertions.assertEquals(200, response.status());
    }

    @Test
    public void shouldReturnRegionById(){
        APIResponse response = medusaStoreApi.getRegionById(apiUtils.extractRegionId());
        Assertions.assertEquals(200, response.status());
    }

    @Test
    public void shouldCompleteCheckout(){

        //create cart
        System.out.println("create cart");
        String createCartPayload = FileParseUtils.parseFileToStream("payloads/create-cart.json");
        Map<String, String> cartData = mapper.readValue(createCartPayload
                ,new TypeReference<Map<String, String>>() {}
        );
        cartData.replace("region_id", apiUtils.extractRegionId());
        String updatedCartPayload = mapper.writeValueAsString(cartData);
        APIResponse createCartResponse = medusaStoreApi.createCart(RequestOptions.create().setData(updatedCartPayload));
        Assertions.assertEquals(200, createCartResponse.status());


        //get product variants
        System.out.println("get product variants and add line item to cart");
        APIResponse productVariants = medusaStoreApi.getAllProductVariants();
        VariantsDTO variantsList = mapper.readValue(productVariants.text(),VariantsDTO.class);


        //add line item to cart
        String addLineItemPayload = FileParseUtils.parseFileToStream("payloads/add-line-item.json");
        Map<String,Object> lineItemData = mapper.readValue(addLineItemPayload,
                new TypeReference<Map<String,Object>>(){});
        lineItemData.replace("variant_id", variantsList.variants.getFirst().id);
        String addLineItemJson = mapper.writeValueAsString(lineItemData);

        CartDTO createdCartObject = mapper.readValue(createCartResponse.text(),CartDTO.class);
        APIResponse addLineItemResponse = medusaStoreApi.addLineItemToCart(createdCartObject.cart.id,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(addLineItemJson));

        Assertions.assertEquals(200, addLineItemResponse.status());



        //verify cart after item addition
        System.out.println("verify cart after item addition");

        CartDTO populatedCartObject = mapper.readValue(addLineItemResponse.text(),CartDTO.class);
        Assertions.assertFalse(populatedCartObject.cart.items.isEmpty());
        Assertions.assertEquals(variantsList.variants.getFirst().id,populatedCartObject.cart.items.getFirst().variantId);


        //get all shipping options
        System.out.println("get all shipping options");

        APIResponse shippingOptionsResponse = medusaStoreApi.getShippingOptionsForCart(populatedCartObject.cart.id);
        Assertions.assertEquals(200, shippingOptionsResponse.status());
        ShippingOptionsDTO shippingOptions = mapper.readValue(shippingOptionsResponse.text(), ShippingOptionsDTO.class);


        //create shipping options selection payload
        System.out.println("create shipping options selection payload");

        String initialShipOptionsPayload = FileParseUtils.parseFileToStream("payloads/shipping-options.json");
        Map<String,String> shippingPayloadData = mapper.readValue(initialShipOptionsPayload,
                new TypeReference<Map<String, String>>() {});
        shippingPayloadData.replace("option_id", shippingOptions.shippingOptionsList.getFirst().id);

        String optionsFinalJson = mapper.writeValueAsString(shippingPayloadData);
        APIResponse selectShippingOptResp = medusaStoreApi.selectShippingOptionForCart(populatedCartObject.cart.id,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(optionsFinalJson));

        Assertions.assertEquals(200,selectShippingOptResp.status());

        //create payment collection for cart
        System.out.println("create payment collection for cart");

        String paymentCollectionPayload = FileParseUtils.parseFileToStream("payloads/create-payment-collection.json");
        Map<String,String> paymentCollectionMap = mapper.readValue(paymentCollectionPayload,
                new TypeReference<Map<String, String>>() {});
        paymentCollectionMap.replace("cart_id", populatedCartObject.cart.id);

        String paymentCollectionJson= mapper.writeValueAsString(paymentCollectionMap);
        APIResponse createPmtCollectionResp = medusaStoreApi.createPaymentCollection(
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(paymentCollectionJson));

        Assertions.assertEquals(200,createPmtCollectionResp.status());
        PaymentCollectionDTO paymentCollectionDTO = mapper.readValue(createPmtCollectionResp.text(),PaymentCollectionDTO.class);


        //get payment provider for cart region
        System.out.println("get payment provider for cart region");

        APIResponse getPmtProvider = medusaStoreApi.getPaymentProvider(cartData.get("region_id"));
        Assertions.assertEquals(200,getPmtProvider.status());

        PaymentProviderDTO paymentProviderDTO = mapper.readValue(getPmtProvider.text(), PaymentProviderDTO.class);

        //initialize payment session
        System.out.println("initialize payment session");

        String initialPmtSessionJson = FileParseUtils.parseFileToStream("payloads/init-payment-session.json");
        String actualPmtSessionJson = initialPmtSessionJson.replace("{providerId}", paymentProviderDTO.paymentProviderList.getFirst().id);

        APIResponse initPaymentSession = medusaStoreApi.initializePaymentCollection(paymentCollectionDTO.paymentCollectionList.id
                ,RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(actualPmtSessionJson));

        Assertions.assertEquals(200,initPaymentSession.status());

        PaymentCollectionDTO paymentCollectionDTO1 = mapper.readValue(initPaymentSession.text(), PaymentCollectionDTO.class);


        //complete checkout for cart
        System.out.println("complete checkout for cart");
        APIResponse checkoutResponse = medusaStoreApi.completeCheckoutForCart(populatedCartObject.cart.id,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(optionsFinalJson));

        Assertions.assertEquals(200,checkoutResponse.status());

        CheckoutDTO checkoutObjectDTO = mapper.readValue(checkoutResponse.text(),CheckoutDTO.class);

        Assertions.assertEquals("pending",checkoutObjectDTO.order.status);
        Assertions.assertEquals(cartData.get("region_id"),checkoutObjectDTO.order.regionId);


       List<String> items = checkoutObjectDTO.order.items.stream().map(i -> i.variantId).toList();

        Assertions.assertTrue(items.stream().anyMatch(i -> i.equals(populatedCartObject.cart.items.getFirst().variantId)));
        Assertions.assertTrue(checkoutObjectDTO.order.shippingMethods.stream().map(i->i.shippingOptionId)
                .anyMatch(i -> i.equals(shippingOptions.shippingOptionsList.getFirst().id)));

        //todo increase shared value storage and reusability across steps

    }

}




