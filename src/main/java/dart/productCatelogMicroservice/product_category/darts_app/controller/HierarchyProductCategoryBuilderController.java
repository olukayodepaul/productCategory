package dart.productCatelogMicroservice.product_category.darts_app.controller;



import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryHierarchyModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.HierarchyProductCategoryBuilderImpl;
import dart.productCatelogMicroservice.product_category.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HierarchyProductCategoryBuilderController {

    private final HierarchyProductCategoryBuilderImpl hierarchyProductCategoryBuilder;

    public HierarchyProductCategoryBuilderController(HierarchyProductCategoryBuilderImpl hierarchyProductCategoryBuilder) {
        this.hierarchyProductCategoryBuilder = hierarchyProductCategoryBuilder;
    }

    @PostMapping("/product/categories")
    public ResponseEntity<ProductCategoryHierarchyModel> buildProductCategory() {
        return hierarchyProductCategoryBuilder.buildProductCategory();
    }
    
}
