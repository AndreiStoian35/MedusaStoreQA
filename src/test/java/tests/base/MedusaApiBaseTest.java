package tests.base;

import api.MedusaStoreApi;
import api.TestSteps;
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
    protected TestSteps testSteps;

    @BeforeEach
    public void setUp(){

        playwright = Playwright.create();
        apiRequest = playwright.request();

        Map<String,String> headers = new HashMap<>();
        headers.put("x-publishable-api-key",ConfigReader.getApiKey());
        headers.put("Content-Type", "application/json");


        apiRequestContext = apiRequest.newContext(
                new APIRequest.NewContextOptions()
                        .setBaseURL(ConfigReader.apiBaseUrl())
                        .setExtraHTTPHeaders(headers)
        );

        medusaStoreApi = new MedusaStoreApi(apiRequestContext);
        testContext = new TestContext();
        apiUtils = new ApiUtils(medusaStoreApi);
        testSteps = new TestSteps(testContext,apiUtils,medusaStoreApi);
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
            testContext = null;

        }
    }


}
