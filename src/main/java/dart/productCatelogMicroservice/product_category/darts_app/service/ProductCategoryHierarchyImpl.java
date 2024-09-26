package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryHierarchyModel;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ErrorHandler;
import dart.productCatelogMicroservice.product_category.utilities.RunTimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

/**
 * Service class responsible for managing product category hierarchies.
 * This service communicates with the Redis cache service to retrieve
 * product categories, validate requests, and handle errors.
 */
@Service
public class ProductCategoryHierarchyImpl {

    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryHierarchyImpl.class);
    private final RedisCacheService redisCacheService;

    /**
     * Constructs a ProductCategoryHierarchyImpl instance with the provided
     * Redis cache service for product category management.
     *
     * @param redisCacheService The Redis cache service used to manage product categories.
     */
    public ProductCategoryHierarchyImpl(RedisCacheService redisCacheService) {
        this.redisCacheService = redisCacheService;
    }

    /**
     * Fetches all root (parent) product categories.
     * Communicates with Redis to build and return the parent category hierarchy.
     *
     * @return A ResponseEntity containing the ProductCategoryHierarchyModel
     *         representing all parent categories, or an error if none are found.
     * @throws RunTimeException if an error occurs during the category retrieval process.
     */
    public ResponseEntity<ProductCategoryHierarchyModel> fetchOnlyParentProductCategory() {
        ProductCategoryHierarchyModel cacheListener = redisCacheService.buildParentCategoryHierarchy();

        if (!cacheListener.getStatus()) {
            throw new RunTimeException(
                    new ErrorHandler(false, cacheListener.getMessage()),
                    HttpStatus.CONFLICT
            );
        }

        if (cacheListener.getCategory().isEmpty()) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Unable to fetch product category by ID. No product category is associated with this ID."),
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                new ProductCategoryHierarchyModel(
                        true,
                        cacheListener.getMessage(),
                        cacheListener.getCategory()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Fetches a product category and its associated parent and child categories by a specific ID.
     *
     * @param categoryId The ID of the product category to retrieve.
     * @return A ResponseEntity containing the ProductCategoryHierarchyModel
     *         for the specified category, including parent and child categories.
     * @throws RunTimeException if the category ID is null or an error occurs during retrieval.
     */
    public ResponseEntity<ProductCategoryHierarchyModel> fetchProductCategoryByCategoryId(Integer categoryId) {
        validateRequest(categoryId);

        ProductCategoryHierarchyModel cacheListener = redisCacheService.buildParentChildCategoryHierarchy(categoryId);

        if (!cacheListener.getStatus()) {
            throw new RunTimeException(
                    new ErrorHandler(false, cacheListener.getMessage()),
                    HttpStatus.CONFLICT
            );
        }

        if (cacheListener.getCategory().isEmpty()) {
            throw new RunTimeException(
                    new ErrorHandler(false, "No base hierarchy can be fetched, nor is the root product category found."),
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                new ProductCategoryHierarchyModel(
                        true,
                        cacheListener.getMessage(),
                        cacheListener.getCategory()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Fetches the entire product category hierarchy, including all root, parent, and child categories.
     *
     * @return A ResponseEntity containing the complete ProductCategoryHierarchyModel.
     * @throws RunTimeException if an error occurs during retrieval.
     */
    public ResponseEntity<ProductCategoryHierarchyModel> fetchAllProductCategory() {
        ProductCategoryHierarchyModel cacheListener = redisCacheService.buildParentChildCategoryHierarchy(0);

        if (!cacheListener.getStatus()) {
            throw new RunTimeException(
                    new ErrorHandler(false, cacheListener.getMessage()),
                    HttpStatus.CONFLICT
            );
        }

        if (cacheListener.getCategory().isEmpty()) {
            throw new RunTimeException(
                    new ErrorHandler(false, "No base hierarchy can be fetched, nor is the root product category found."),
                    HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
                new ProductCategoryHierarchyModel(
                        true,
                        cacheListener.getMessage(),
                        cacheListener.getCategory()
                ),
                HttpStatus.OK
        );
    }

    /**
     * Validates the category ID for the request, ensuring it is not null.
     *
     * @param categoryId The ID of the product category to validate.
     * @throws RunTimeException if the category ID is null.
     */
    private void validateRequest(Integer categoryId) {
        if (categoryId == null) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Category ID cannot be null."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }
}
