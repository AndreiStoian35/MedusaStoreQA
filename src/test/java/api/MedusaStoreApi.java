package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.RequestOptions;
import io.qameta.allure.Step;
import utils.ConfigReader;

public class MedusaStoreApi {


    public APIRequestContext apiRequestContext;

    public MedusaStoreApi(APIRequestContext apiRequestContext) {
        this.apiRequestContext = apiRequestContext;
    }


    @Step("API call: GET all products")
    public APIResponse getAllProducts(){

        return apiRequestContext.get("./products");
    }

    @Step("API call: GET all product variants")
    public APIResponse getAllProductVariants(){
        return apiRequestContext.get("./product-variants");
    }

    @Step("API call: GET product by ID")
    public APIResponse getProduct(String prodId){

        return apiRequestContext.get("./products/" + prodId);
    }

    @Step("API call: GET all regions")
    public APIResponse getAllRegions(){
        return apiRequestContext.get("./regions");
    }

    @Step("API call: GET region by ID")
    public APIResponse getRegionById(String regionId){
        return apiRequestContext.get("./regions/" + regionId);
    }

    @Step("API call: POST create cart")
    public APIResponse createCart(RequestOptions requestOptions){
        return apiRequestContext.post("./carts", requestOptions);
    }

    @Step("API call: POST add line item to cart")
    public APIResponse addLineItemToCart(String cartId, RequestOptions requestOptions){
        return apiRequestContext.post("./carts/" + cartId + "/line-items", requestOptions);
    }

    @Step("API call: GET shipping options for cart")
    public APIResponse getShippingOptionsForCart(String cartId){
        return apiRequestContext.get("./shipping-options?cart_id=" + cartId);
    }

    @Step("API call: POST select shipping option for cart")
    public APIResponse selectShippingOptionForCart(String cartId,RequestOptions requestOptions){
        return apiRequestContext.post("./carts/" + cartId + "/shipping-methods",requestOptions);
    }

    @Step("API call: POST create payment collection")
    public APIResponse createPaymentCollection(RequestOptions requestOptions){
        return apiRequestContext.post("./payment-collections", requestOptions);
    }

    @Step("API call: POST initialize payment collection")
    public APIResponse initializePaymentCollection(String paymentCollectionId, RequestOptions requestOptions){
        return apiRequestContext.post("./payment-collections/"+ paymentCollectionId + "/payment-sessions",requestOptions);
    }

    @Step("API call: GET payment provider for region")
    public APIResponse getPaymentProvider(String regionId){
        return apiRequestContext.get("./payment-providers?region_id=" + regionId);
    }

    @Step("API call: POST complete checkout for cart")
    public APIResponse completeCheckoutForCart(String cartId){
        return apiRequestContext.post("./carts/"+cartId + "/complete");
    }
}
