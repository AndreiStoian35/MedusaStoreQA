package tests.apiTests;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import data.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.base.MedusaApiBaseTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.FileParseUtils;
import java.util.List;


public class MedusaStoreApiTests extends MedusaApiBaseTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnAllProducts(){

        APIResponse response = medusaStoreApi.getAllProducts();

        String responseBody = response.text();

        Assertions.assertEquals(200, response.status());

        JsonNode responseJson = mapper.readTree(responseBody);
        JsonNode productsJson = responseJson.get("products");

        Assertions.assertNotNull(productsJson);
        Assertions.assertFalse(productsJson.isEmpty());
        Assertions.assertTrue(productsJson.isArray());


    }

    @Test
    public void shouldReturnProduct(){

        String pId =  apiUtils.extractProductId();
        APIResponse response = medusaStoreApi.getProduct(pId);

        Assertions.assertEquals(200, response.status());

        JsonNode responseJson = mapper.readTree(response.text());
        JsonNode productJson = responseJson.get("product");

        Assertions.assertNotNull(productJson);
        Assertions.assertTrue(productJson.isObject());
        Assertions.assertTrue(productJson.hasNonNull("id"));
        Assertions.assertTrue(productJson.hasNonNull("title"));
        Assertions.assertTrue(productJson.hasNonNull("description"));
        Assertions.assertTrue(productJson.hasNonNull("variants"));

    }

    @Test
    public void shouldReturnAllProductVariants(){
        APIResponse response = medusaStoreApi.getAllProductVariants();

        Assertions.assertEquals(200, response.status());


        JsonNode responseJson = mapper.readTree(response.text());
        JsonNode variantsJson = responseJson.get("variants");

        Assertions.assertNotNull(variantsJson);
        Assertions.assertTrue(variantsJson.isArray());
        Assertions.assertFalse(variantsJson.isEmpty());

        JsonNode variant = variantsJson.get(0);
        Assertions.assertTrue(variant.hasNonNull("id"));
        Assertions.assertTrue(variant.hasNonNull("title"));
        Assertions.assertTrue(variant.hasNonNull("product_id"));

    }

    @Test
    public void shouldReturnAllRegions(){
        APIResponse response = medusaStoreApi.getAllRegions();
        Assertions.assertEquals(200, response.status());

        JsonNode responseJson = mapper.readTree(response.text());
        JsonNode regionsJson = responseJson.get("regions");

        Assertions.assertNotNull(regionsJson);
        Assertions.assertFalse(regionsJson.isEmpty());
        Assertions.assertTrue(regionsJson.isArray());

        JsonNode region = regionsJson.get(0);
        Assertions.assertTrue(region.hasNonNull("id"));
        Assertions.assertTrue(region.hasNonNull("name"));
        Assertions.assertTrue(region.hasNonNull("currency_code"));

    }

    @Test
    public void shouldReturnRegionById(){
        APIResponse response = medusaStoreApi.getRegionById(apiUtils.extractRegionId());
        Assertions.assertEquals(200, response.status());

        JsonNode responseJson = mapper.readTree(response.text());
        JsonNode region = responseJson.get("region");

        Assertions.assertNotNull(region);
        Assertions.assertFalse(region.isEmpty());
        Assertions.assertTrue(region.isObject());


        Assertions.assertTrue(region.hasNonNull("id"));
        Assertions.assertTrue(region.hasNonNull("name"));
        Assertions.assertTrue(region.hasNonNull("currency_code"));
    }

    @Test
    public void shouldCompleteCheckout(){

        System.out.println("create cart");

        APIResponse createCartResponse = testSteps.createCart();
        Assertions.assertEquals(200, createCartResponse.status());
        CartDTO createdCartObject = mapper.readValue(createCartResponse.text(),CartDTO.class);
        testContext.cartId = createdCartObject.cart.id;


        System.out.println("get product variants and add line item to cart");

        APIResponse productVariants = medusaStoreApi.getAllProductVariants();
        VariantsDTO variantsList = mapper.readValue(productVariants.text(),VariantsDTO.class);
        testContext.variantId = variantsList.variants.getFirst().id;


        System.out.println("add line item to cart");

        APIResponse addLineItemResponse = testSteps.addLineItemsToCart();
        Assertions.assertEquals(200, addLineItemResponse.status());


        //verify cart after item addition
        System.out.println("verify cart after item addition");

        JsonNode cartAfterLineItem = mapper.readTree(addLineItemResponse.text());
        JsonNode cartWithItemsNode = cartAfterLineItem.get("cart").get("items");
        String variantId = cartWithItemsNode.get(0).get("variant_id").asString();

        Assertions.assertFalse(cartWithItemsNode.isEmpty());
        Assertions.assertEquals(testContext.variantId, variantId);

        //get all shipping options
        System.out.println("get all shipping options");

        APIResponse shippingOptionsResponse = medusaStoreApi.getShippingOptionsForCart(testContext.cartId);
        Assertions.assertEquals(200, shippingOptionsResponse.status());

        ShippingOptionsDTO shippingOptions = mapper.readValue(shippingOptionsResponse.text(), ShippingOptionsDTO.class);

        testContext.shippingOptionId = shippingOptions.shippingOptionsList.getFirst().id;


        //todo continue creating separate methods where applicable

        //create shipping options selection payload
        System.out.println("create shipping options selection payload");

        String initialShipOptionsPayload = FileParseUtils.parseFileToStream("payloads/shipping-options.json");

        String optionsFinalJson = initialShipOptionsPayload.replace("{shippingOption}", testContext.shippingOptionId);

        APIResponse selectShippingOptResp = medusaStoreApi.selectShippingOptionForCart(testContext.cartId,
                RequestOptions.create()
                        .setData(optionsFinalJson));

        Assertions.assertEquals(200,selectShippingOptResp.status());

        //create payment collection for cart
        System.out.println("create payment collection for cart");

        String paymentCollectionPayload = FileParseUtils.parseFileToStream("payloads/create-payment-collection.json");

        String paymentCollectionJson =  paymentCollectionPayload.replace("{cartId}", testContext.cartId);

        APIResponse createPmtCollectionResp = medusaStoreApi.createPaymentCollection(
                RequestOptions.create()
                        .setData(paymentCollectionJson));

        Assertions.assertEquals(200,createPmtCollectionResp.status());
        PaymentCollectionDTO paymentCollectionDTO = mapper.readValue(createPmtCollectionResp.text(),PaymentCollectionDTO.class);


        //get payment provider for cart region
        System.out.println("get payment provider for cart region");

        APIResponse getPmtProvider = medusaStoreApi.getPaymentProvider(testContext.regionId);
        Assertions.assertEquals(200,getPmtProvider.status());

        PaymentProviderDTO paymentProviderDTO = mapper.readValue(getPmtProvider.text(), PaymentProviderDTO.class);

        //initialize payment session
        System.out.println("initialize payment session");

        String initialPmtSessionJson = FileParseUtils.parseFileToStream("payloads/init-payment-session.json");
        String actualPmtSessionJson = initialPmtSessionJson.replace("{providerId}", paymentProviderDTO.paymentProviderList.getFirst().id);

        APIResponse initPaymentSession = medusaStoreApi.initializePaymentCollection(paymentCollectionDTO.paymentCollectionList.id
                ,RequestOptions.create()
                        .setData(actualPmtSessionJson));

        Assertions.assertEquals(200,initPaymentSession.status());



        //complete checkout for cart
        System.out.println("complete checkout for cart");
        APIResponse checkoutResponse = medusaStoreApi.completeCheckoutForCart(testContext.cartId);

        Assertions.assertEquals(200,checkoutResponse.status());

        CheckoutDTO checkoutObjectDTO = mapper.readValue(checkoutResponse.text(),CheckoutDTO.class);


        Assertions.assertEquals("order",checkoutObjectDTO.type);
        Assertions.assertEquals("pending",checkoutObjectDTO.order.status);
        Assertions.assertEquals(testContext.regionId,checkoutObjectDTO.order.regionId);


       List<String> items = checkoutObjectDTO.order.items.stream().map(i -> i.variantId).toList();

        Assertions.assertTrue(items.stream().anyMatch(i -> i.equals(testContext.variantId)));
        Assertions.assertTrue(checkoutObjectDTO.order.shippingMethods.stream().map(i->i.shippingOptionId)
                .anyMatch(i -> i.equals(testContext.shippingOptionId)));



    }

}




