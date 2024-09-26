package dart.productCatelogMicroservice.product_category.darts_app.controller;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryResModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.CreateProductCategoryImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for managing product category creation requests.
 * This controller provides an endpoint for creating new product categories.
 */
@RestController
@RequestMapping("/api")
public class CreateProductCategory {

    private final CreateProductCategoryImpl productCategoryImpl;

    /**
     * Constructs a CreateProductCategory controller with the given
     * CreateProductCategoryImpl instance.
     *
     * @param productCategoryImpl an instance of CreateProductCategoryImpl
     */
    public CreateProductCategory(CreateProductCategoryImpl productCategoryImpl) {
        this.productCategoryImpl = productCategoryImpl;
    }

    /**
     * Creates a new product category.
     *
     * @param bodyRequest the request body containing details of the product category to be created
     * @return a ResponseEntity containing the response model for the created product category
     *         along with the appropriate HTTP status code.
     */
    @PostMapping("/categories")
    public ResponseEntity<ProductCategoryResModel> createProductCategory(@RequestBody ProductCategoryReqModel bodyRequest) {
        return productCategoryImpl.createProductCategory(bodyRequest);
    }

}
