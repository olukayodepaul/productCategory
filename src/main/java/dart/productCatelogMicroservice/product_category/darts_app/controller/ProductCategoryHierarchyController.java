package dart.productCatelogMicroservice.product_category.darts_app.controller;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryHierarchyModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.ProductCategoryHierarchyImpl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing product category hierarchy requests.
 * This controller provides endpoints for retrieving various product
 * categories, including root categories, full category hierarchies,
 * and specific categories by their ID.
 */
@RestController
@RequestMapping("/api")
public class ProductCategoryHierarchyController {

    private final ProductCategoryHierarchyImpl productCategoryHierarchy;

    /**
     * Constructs a ProductCategoryHierarchyController with the given
     * ProductCategoryHierarchyImpl instance.
     *
     * @param productCategoryHierarchy an instance of ProductCategoryHierarchyImpl
     */
    public ProductCategoryHierarchyController(ProductCategoryHierarchyImpl productCategoryHierarchy) {
        this.productCategoryHierarchy = productCategoryHierarchy;
    }

    /**
     * Retrieves all root (parent) product categories.
     *
     * Endpoint: GET /categories/parent
     *
     * @return a ResponseEntity containing the ProductCategoryHierarchyModel,
     *         which includes all root product categories.
     */
    @GetMapping("/categories/parent")
    public ResponseEntity<ProductCategoryHierarchyModel> fetchOnlyParentProductCategory() {
        return productCategoryHierarchy.fetchOnlyParentProductCategory();
    }

    /**
     * Retrieves a specific product category by its ID.
     *
     * Endpoint: GET /categories/{categoryId}/details
     *
     * @param categoryId the ID of the product category to retrieve
     * @return a ResponseEntity containing the ProductCategoryHierarchyModel
     *         for the specified category ID, or an error response if not found.
     */
    @GetMapping("/categories/{categoryId}/details")
    public ResponseEntity<ProductCategoryHierarchyModel> fetchProductCategoryByCategoryId(@PathVariable Integer categoryId) {
        return productCategoryHierarchy.fetchProductCategoryByCategoryId(categoryId);
    }

    /**
     * Retrieves the entire product category hierarchy, including all root, parent,
     * and child categories.
     *
     * Endpoint: GET /categories/all
     *
     * @return a ResponseEntity containing the ProductCategoryHierarchyModel,
     *         which includes the full product category hierarchy.
     */
    @GetMapping("/categories/all")
    public ResponseEntity<ProductCategoryHierarchyModel> fetchAllProductCategory() {
        return productCategoryHierarchy.fetchAllProductCategory();
    }

}
