package api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import context.TestContext;
import data.CartDTO;
import data.PaymentCollectionDTO;
import utils.ApiUtils;
import utils.FileParseUtils;

public class TestSteps {

    private final TestContext testContext;
    private final ApiUtils apiUtils;
    private final MedusaStoreApi medusaStoreApi;

    public TestSteps(TestContext testContext, ApiUtils apiUtils, MedusaStoreApi medusaStoreApi){
        this.testContext = testContext;
        this.apiUtils = apiUtils;
        this.medusaStoreApi = medusaStoreApi;
    }

    public APIResponse createCart(){
        String createCartPayload = FileParseUtils.parseFileToStream("payloads/create-cart.json");

        testContext.regionId = apiUtils.extractRegionId();
        String updatedCartPayload =  createCartPayload.replace("{regionId}",testContext.regionId);

        return medusaStoreApi.createCart(RequestOptions.create()
                .setData(updatedCartPayload));
    }
    public APIResponse addLineItemsToCart(String variantId, String cartId){
        String addLineItemPayload = FileParseUtils.parseFileToStream("payloads/add-line-item.json");
        String addLineItemJson =  addLineItemPayload.replace("{productVariant}", variantId );

        return medusaStoreApi.addLineItemToCart( cartId,
                RequestOptions.create()
                        .setData(addLineItemJson));

    }

    public APIResponse createPaymentCollection(String cartId){
        String paymentCollectionPayload = FileParseUtils.parseFileToStream("payloads/create-payment-collection.json");

        String paymentCollectionJson =  paymentCollectionPayload.replace("{cartId}", cartId);

        return medusaStoreApi.createPaymentCollection(
                RequestOptions.create()
                        .setData(paymentCollectionJson));
    }

    public APIResponse initializePaymentSession(String paymentProviderId, String paymentCollectionId){
        String initialPmtSessionJson = FileParseUtils.parseFileToStream("payloads/init-payment-session.json");
        String actualPmtSessionJson = initialPmtSessionJson.replace("{providerId}", paymentProviderId);

        return medusaStoreApi.initializePaymentCollection(paymentCollectionId
                ,RequestOptions.create()
                        .setData(actualPmtSessionJson));
    }
}
