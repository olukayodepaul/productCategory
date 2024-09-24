package dart.productCatelogMicroservice.product_category.darts_app.controller;


import dart.productCatelogMicroservice.product_category.darts_app.service.FetchAllProductCategoryImpl;
import dart.productCatelogMicroservice.product_category.darts_app.service.FetchProductCategoryByIdImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class GetProductCategory {

    private final FetchAllProductCategoryImpl fetchAllProductCategoryImpl;
    private final FetchProductCategoryByIdImpl fetchProductCategoryByIdImpl;


    public GetProductCategory(
            FetchAllProductCategoryImpl fetchAllProductCategoryImpl,
            FetchProductCategoryByIdImpl fetchProductCategoryByIdImpl
    ) {
        this.fetchAllProductCategoryImpl = fetchAllProductCategoryImpl;
        this.fetchProductCategoryByIdImpl = fetchProductCategoryByIdImpl;
    }

//    @GetMapping("categories")
//    public ResponseEntity<List<ProductCategoryResDataModel>> fetchAllProductCategory() {
//        return fetchAllProductCategoryImpl.fetchAllProductCategory();
//    }
//
//    @GetMapping("categories/{id}")
//    public ResponseEntity<ProductCategoryResDataModel> fetchProductCategoryById(@PathVariable("id") Integer id) {
//        return fetchProductCategoryByIdImpl.fetchProductCategoryById(id);
//    }

}
