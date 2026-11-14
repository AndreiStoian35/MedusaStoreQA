package utils;

import api.MedusaStoreApi;
import com.microsoft.playwright.APIResponse;
import data.AllProductsDTO;
import data.AllRegionsDTO;
import tools.jackson.databind.ObjectMapper;

public class ApiUtils{

    ObjectMapper mapper  = new ObjectMapper();

    public MedusaStoreApi medusaStoreApi;

    public ApiUtils(MedusaStoreApi medusaStoreApi){
        this.medusaStoreApi = medusaStoreApi;
    }

    public String extractProductId(){
        APIResponse response = medusaStoreApi.getAllProducts();

        String responseBody = response.text();

        AllProductsDTO allProductObjects  = mapper.readValue(responseBody,
                AllProductsDTO.class);

        return allProductObjects.products.getFirst().id;
    }

    public String extractRegionId(){
        APIResponse response = medusaStoreApi.getAllRegions();
        AllRegionsDTO allRegions = mapper.readValue(response.text(), AllRegionsDTO.class);

        return allRegions.regions.getFirst().id;
    }
}
