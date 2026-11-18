package tests.apiTests;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import data.*;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import tests.base.MedusaApiBaseTest;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import utils.FileParseUtils;


public class MedusaStoreApiTests extends MedusaApiBaseTest {

    ObjectMapper mapper = new ObjectMapper();

    @Test
    public void shouldReturnAllProducts(){

        APIResponse response = medusaStoreApi.getAllProducts();
        Allure.step("Verify the response contains products", () -> {
            Assertions.assertEquals(200, response.status());

            JsonNode responseJson = mapper.readTree(response.text());
            JsonNode productsJson = responseJson.get("products");

            Assertions.assertNotNull(productsJson);
            Assertions.assertTrue(productsJson.isArray());
            Assertions.assertFalse(productsJson.isEmpty());
        });
    }

    @Test
    public void shouldReturnProduct(){

        String pId = Allure.step("Select a product ID", () -> apiUtils.extractProductId());
        APIResponse response = medusaStoreApi.getProduct(pId);

        Allure.step("Verify the product details are present", () -> {
            Assertions.assertEquals(200, response.status());

            JsonNode responseJson = mapper.readTree(response.text());
            JsonNode productJson = responseJson.get("product");

            Assertions.assertNotNull(productJson);
            Assertions.assertTrue(productJson.isObject());
            Assertions.assertTrue(productJson.hasNonNull("id"));
            Assertions.assertTrue(productJson.hasNonNull("title"));
            Assertions.assertTrue(productJson.hasNonNull("description"));
            Assertions.assertTrue(productJson.hasNonNull("variants"));
        });
    }

    @Test
    public void shouldReturnAllProductVariants(){
        APIResponse response = medusaStoreApi.getAllProductVariants();

        Allure.step("Verify the response contains product variants", () -> {
            Assertions.assertEquals(200, response.status());

            JsonNode responseJson = mapper.readTree(response.text());
            JsonNode variantsJson = responseJson.get("variants");

            Assertions.assertNotNull(variantsJson);
            Assertions.assertTrue(variantsJson.isArray());
            Assertions.assertFalse(variantsJson.isEmpty());
        });
    }

    @Test
    public void shouldReturnAllRegions(){
        APIResponse response = medusaStoreApi.getAllRegions();
        Allure.step("Verify the response contains regions", () -> {
            Assertions.assertEquals(200, response.status());

            JsonNode responseJson = mapper.readTree(response.text());
            JsonNode regionsJson = responseJson.get("regions");
            Assertions.assertNotNull(regionsJson);
            Assertions.assertTrue(regionsJson.isArray());
            Assertions.assertFalse(regionsJson.isEmpty());

            JsonNode region = regionsJson.get(0);
            Assertions.assertTrue(region.hasNonNull("id"));
            Assertions.assertTrue(region.hasNonNull("name"));
            Assertions.assertTrue(region.hasNonNull("currency_code"));
        });
    }

    @Test
    public void shouldReturnRegionById(){
        String regionId = Allure.step("Select a region ID", () -> apiUtils.extractRegionId());
        APIResponse response = medusaStoreApi.getRegionById(regionId);
        Allure.step("Verify the region details are present", () -> {
            Assertions.assertEquals(200, response.status());

            JsonNode responseJson = mapper.readTree(response.text());
            JsonNode region = responseJson.get("region");

            Assertions.assertNotNull(region);
            Assertions.assertFalse(region.isEmpty());
            Assertions.assertTrue(region.isObject());
            Assertions.assertTrue(region.hasNonNull("id"));
            Assertions.assertTrue(region.hasNonNull("name"));
            Assertions.assertTrue(region.hasNonNull("currency_code"));
        });
    }

    @Test
    public void shouldCompleteCheckout(){

        APIResponse createCartResponse = Allure.step("Create a cart for the selected region", testSteps::createCart);
        Assertions.assertEquals(200, createCartResponse.status());
        CartDTO createdCartObject = mapper.readValue(createCartResponse.text(), CartDTO.class);
        testContext.cartId = createdCartObject.cart.id;

        APIResponse productVariants = medusaStoreApi.getAllProductVariants();
        VariantsDTO variantsList = mapper.readValue(productVariants.text(), VariantsDTO.class);
        testContext.variantId = variantsList.variants.getFirst().id;
        APIResponse addLineItemResponse = Allure.step("Add a product variant to the cart",
                () -> testSteps.addLineItemsToCart(testContext.variantId, testContext.cartId));

        Allure.step("Verify the cart contains the selected product", () -> {
            Assertions.assertEquals(200, addLineItemResponse.status());
            JsonNode cartAfterLineItem = mapper.readTree(addLineItemResponse.text());
            JsonNode cartItems = cartAfterLineItem.get("cart").get("items");

            Assertions.assertFalse(cartItems.isEmpty());
            Assertions.assertEquals(testContext.variantId, cartItems.get(0).get("variant_id").asString());
        });

        APIResponse shippingOptionsResponse = medusaStoreApi.getShippingOptionsForCart(testContext.cartId);
        Assertions.assertEquals(200, shippingOptionsResponse.status());
        JsonNode shippingOptions = mapper.readTree(shippingOptionsResponse.text());
        testContext.shippingOptionId = shippingOptions.get("shipping_options").get(0).get("id").asString();

        String initialShipOptionsPayload = FileParseUtils.parseFileToStream("payloads/shipping-options.json");
        String optionsFinalJson = initialShipOptionsPayload.replace("{shippingOption}", testContext.shippingOptionId);
        APIResponse selectShippingOptResp = medusaStoreApi.selectShippingOptionForCart(testContext.cartId,
                RequestOptions.create().setData(optionsFinalJson));
        Assertions.assertEquals(200, selectShippingOptResp.status());

        APIResponse createPmtCollectionResp = Allure.step("Create a payment collection for the cart",
                () -> testSteps.createPaymentCollection(testContext.cartId));
        Assertions.assertEquals(200, createPmtCollectionResp.status());
        PaymentCollectionDTO paymentCollectionDTO = mapper.readValue(createPmtCollectionResp.text(),
                PaymentCollectionDTO.class);
        testContext.paymentCollectionId = paymentCollectionDTO.paymentCollectionList.id;

        APIResponse getPmtProvider = medusaStoreApi.getPaymentProvider(testContext.regionId);
        Assertions.assertEquals(200, getPmtProvider.status());
        PaymentProviderDTO paymentProviderDTO = mapper.readValue(getPmtProvider.text(), PaymentProviderDTO.class);
        String pmtProviderId = paymentProviderDTO.paymentProviderList.getFirst().id;
        APIResponse initPaymentSession = Allure.step("Initialize a payment session",
                () -> testSteps.initializePaymentSession(pmtProviderId, testContext.paymentCollectionId));
        Assertions.assertEquals(200, initPaymentSession.status());

        APIResponse checkoutResponse = medusaStoreApi.completeCheckoutForCart(testContext.cartId);

        Allure.step("Verify checkout created an order with the expected items and shipping", () -> {
            Assertions.assertEquals(200, checkoutResponse.status());
            CheckoutDTO checkoutObjectDTO = mapper.readValue(checkoutResponse.text(), CheckoutDTO.class);

            Assertions.assertEquals("order", checkoutObjectDTO.type);
            Assertions.assertEquals("pending", checkoutObjectDTO.order.status);
            Assertions.assertEquals(testContext.regionId, checkoutObjectDTO.order.regionId);
            Assertions.assertTrue(checkoutObjectDTO.order.items.stream().map(i -> i.variantId)
                    .anyMatch(testContext.variantId::equals));
            Assertions.assertTrue(checkoutObjectDTO.order.shippingMethods.stream().map(i -> i.shippingOptionId)
                    .anyMatch(testContext.shippingOptionId::equals));
        });
    }

}



