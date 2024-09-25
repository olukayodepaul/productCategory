package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryHierarchyModel;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class HierarchyProductCategoryBuilderImpl {

    private final RedisCacheService redisCacheService;

    public HierarchyProductCategoryBuilderImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    public ResponseEntity<ProductCategoryHierarchyModel> buildProductCategory() {
        redisCacheService.getAllCachedRecords();
        return new ResponseEntity<>(new ProductCategoryHierarchyModel(true, "Category successfully created", redisCacheService.getCachedRecords()), HttpStatus.CREATED);
    }
}
