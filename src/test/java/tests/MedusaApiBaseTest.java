package tests;

import api.MedusaStoreApi;
import com.microsoft.playwright.*;
import context.TestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import utils.ApiUtils;
import utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

public class MedusaApiBaseTest {
    protected Playwright playwright;
    protected APIRequestContext apiRequestContext;
    protected MedusaStoreApi medusaStoreApi;
    protected APIRequest apiRequest;
    protected TestContext testContext;
    protected ApiUtils apiUtils;

    @BeforeEach
    public void setUp(){

        playwright = Playwright.create();
        apiRequest = playwright.request();

        Map<String,String> headers = new HashMap<>();
        headers.put("x-publishable-api-key",ConfigReader.getApiKey());

        apiRequestContext = apiRequest.newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigReader.apiBaseUrl())
                        .setExtraHTTPHeaders(headers)
        );

        medusaStoreApi = new MedusaStoreApi(apiRequestContext);
        testContext = new TestContext();
        apiUtils = new ApiUtils(medusaStoreApi);
    }

    @AfterEach
    public void tearDown(){
        try{
            if( playwright != null){
                playwright.close();
            }
        }
        finally {
            playwright = null;
            apiRequest = null;
        }
    }


}
