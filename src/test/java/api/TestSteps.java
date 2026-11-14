package api;

import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;
import context.TestContext;
import data.CartDTO;
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
    public APIResponse addLineItemsToCart(){
        String addLineItemPayload = FileParseUtils.parseFileToStream("payloads/add-line-item.json");
        String addLineItemJson =  addLineItemPayload.replace("{productVariant}", testContext.variantId );

        return medusaStoreApi.addLineItemToCart( testContext.cartId,
                RequestOptions.create()
                        .setData(addLineItemJson));

    }
}
