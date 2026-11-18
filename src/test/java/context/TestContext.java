package context;

import data.AllProductsDTO;
import data.ProductDTO;

public class TestContext {

   public ProductDTO product = new ProductDTO();
   public AllProductsDTO allProducts = new AllProductsDTO();

   public String productId;

   public String regionId;

   public String variantId;

   public String cartId;

   public String shippingOptionId;

   public String paymentCollectionId;
}
