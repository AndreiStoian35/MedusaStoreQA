package api;

import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.RequestOptions;
import utils.ConfigReader;

public class MedusaStoreApi {


    public APIRequestContext apiRequestContext;

    public MedusaStoreApi(APIRequestContext apiRequestContext) {
        this.apiRequestContext = apiRequestContext;
    }

    public APIResponse getAllProducts(){

        return apiRequestContext.get("./products");
    }

    public APIResponse getAllProductVariants(){
        return apiRequestContext.get("./product-variants");
    }

    public APIResponse getProduct(String prodId){

        return apiRequestContext.get("./products/" + prodId);
    }

    public APIResponse getAllRegions(){
        return apiRequestContext.get("./regions");
    }

    public APIResponse getRegionById(String regionId){
        return apiRequestContext.get("./regions/" + regionId);
    }

    public APIResponse createCart(RequestOptions requestOptions){
        return apiRequestContext.post("./carts", requestOptions);
    }

    public APIResponse addLineItemToCart(String cartId, RequestOptions requestOptions){
        return apiRequestContext.post("./carts/" + cartId + "/line-items", requestOptions);
    }

    public APIResponse getShippingOptionsForCart(String cartId){
        return apiRequestContext.get("./shipping-options?cart_id=" + cartId);
    }

    public APIResponse selectShippingOptionForCart(String cartId,RequestOptions requestOptions){
        return apiRequestContext.post("./carts/" + cartId + "/shipping-methods",requestOptions);
    }
    public APIResponse createPaymentCollection(RequestOptions requestOptions){
        return apiRequestContext.post("./payment-collections", requestOptions);
    }

    public APIResponse initializePaymentCollection(String paymentCollectionId, RequestOptions requestOptions){
        return apiRequestContext.post("./payment-collections/"+ paymentCollectionId + "/payment-sessions",requestOptions);
    }

    public APIResponse getPaymentProvider(String regionId){
        return apiRequestContext.get("./payment-providers?region_id=" + regionId);
    }

    public APIResponse completeCheckoutForCart(String cartId,RequestOptions requestOptions){
        return apiRequestContext.post("./carts/"+cartId + "/complete", requestOptions);
    }
}
