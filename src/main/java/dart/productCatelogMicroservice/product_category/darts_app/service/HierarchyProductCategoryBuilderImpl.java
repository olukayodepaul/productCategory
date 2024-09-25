package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class HierarchyProductCategoryBuilderImpl {

    private final RedisCacheService redisCacheService;

    public HierarchyProductCategoryBuilderImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    public ResponseEntity<ResponseHandler> buildProductCategory() {
        System.out.println(redisCacheService.getAllCachedRecords());
        return null;
    }
}
