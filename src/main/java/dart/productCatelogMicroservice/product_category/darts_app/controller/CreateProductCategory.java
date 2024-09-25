package dart.productCatelogMicroservice.product_category.darts_app.controller;


import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryResModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.CreateProductCategoryImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class CreateProductCategory {

    private final CreateProductCategoryImpl productCategoryImpl;

    public CreateProductCategory(CreateProductCategoryImpl productCategoryImpl) {
        this.productCategoryImpl = productCategoryImpl;
    }

    @PostMapping("/categories")
    public ResponseEntity<ProductCategoryResModel> createProductCategory(@RequestBody ProductCategoryReqModel bodyRequest) {
        return productCategoryImpl.createProductCategory(bodyRequest);
    }

}
