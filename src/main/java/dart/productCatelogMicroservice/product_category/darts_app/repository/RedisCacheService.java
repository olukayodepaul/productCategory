package dart.productCatelogMicroservice.product_category.darts_app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryHierarchyModel;
import dart.productCatelogMicroservice.product_category.darts_app.helper.BuilderManager;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service class for managing product categories in Redis cache.
 */
@Service
public class RedisCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;
    private final BuilderManager builderManager;

    /**
     * Constructs a RedisCacheService instance.
     *
     * @param redisTemplate  The Redis template for performing cache operations.
     * @param objectMapper   The object mapper for converting objects to and from JSON.
     * @param builderManager  The builder manager for constructing category hierarchies.
     */
    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper, BuilderManager builderManager) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
        this.builderManager = builderManager;
    }

    /**
     * Saves or updates a product category in Redis cache.
     *
     * @param profile The CacheModel representing the product category to be saved or updated.
     * @return True if the operation was successful, false otherwise.
     */
    public Boolean saveUpdateProductCategoryInCacheMemory(CacheModel profile) {
        try {
            // Get the product category ID
            Integer productId = profile.getId();
            String key = "product:category";
            String subKey = productId.toString();

            // Save or update the product category in Redis cache
            redisTemplate.opsForHash().put(key, subKey, profile);

            // Log and return success
            logger.info("Successfully saved/updated product category with ID {} in cache.", productId);
            return true;
        } catch (Exception e) {
            // Log the error and return false
            logger.error("RedisCacheService: saveUpdateProductCategoryInCacheMemory - Error occurred while saving/updating category with ID {}: {}", profile.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Deletes a product category from Redis cache.
     *
     * @param productCategoryId The CacheModel representing the product category to be deleted.
     * @return True if the deletion was successful, false otherwise.
     */
    public Boolean deleteProductCategoryFromCacheMemory(CacheModel productCategoryId) {
        try {
            // Get the product category ID
            Integer productId = productCategoryId.getId();
            String key = "product:category";
            String subKey = productId.toString();

            // Delete the category from Redis cache
            redisTemplate.opsForHash().delete(key, subKey);

            // Log and return success
            logger.info("Successfully deleted product category with ID {} from cache.", productId);
            return true;
        } catch (Exception e) {
            // Log the error and return false
            logger.error("RedisCacheService: deleteProductCategoryFromCacheMemory - Error occurred while deleting category with ID {}: {}", productCategoryId.getId(), e.getMessage());
            return false;
        }
    }

    /**
     * Builds a hierarchy of parent and child categories based on a given category ID.
     *
     * @param categoryId The ID of the category for which to build the hierarchy.
     * @return A ProductCategoryHierarchyModel containing the hierarchy or an error message if an error occurs.
     */
    public ProductCategoryHierarchyModel buildParentChildCategoryHierarchy(Integer categoryId) {
        try {
            String pattern = "product:category";
            Set<String> keys = redisTemplate.keys(pattern);

            // Check if keys are empty or null
            if (keys == null || keys.isEmpty()) {
                return new ProductCategoryHierarchyModel(false, "No categories found: Empty key in Redis", Collections.emptyList());
            }

            // Retrieve hash entries for the given pattern
            Map<Object, Object> mapProductCategory = redisTemplate.opsForHash().entries(pattern);

            // Check if the map contains any product categories
            if (!mapProductCategory.isEmpty()) {

                // Convert map values to a list of CacheModel objects
                List<CacheModel> listOfCategory = mapProductCategory.values().stream()
                        .map(value -> objectMapper.convertValue(value, CacheModel.class))
                        .collect(Collectors.toList());


                // Build the hierarchy starting from the given categoryId and return the result
                return new ProductCategoryHierarchyModel(true, "Parent and child categories retrieved successfully", builderManager.buildHierarchy(listOfCategory, categoryId));
            }

            // If no categories found, return an empty list with a message
            return new ProductCategoryHierarchyModel(true, "No categories found for the given ID", Collections.emptyList());

        } catch (Exception e) {
            // Log the error and return a failure response with a user-friendly message
            logger.error("RedisCacheService: buildParentChildCategoryHierarchy - Error occurred: {}", e.getMessage());
            return new ProductCategoryHierarchyModel(false, "An error occurred while retrieving categories: " + e.getMessage(), Collections.emptyList());
        }
    }

    /**
     * Builds a hierarchy of parent categories.
     *
     * @return A ProductCategoryHierarchyModel containing the parent categories or an error message if an error occurs.
     */
    public ProductCategoryHierarchyModel buildParentCategoryHierarchy() {
        try {
            String pattern = "product:category";
            Set<String> keys = redisTemplate.keys(pattern);

            // Check if keys are empty or null
            if (keys == null || keys.isEmpty()) {
                return new ProductCategoryHierarchyModel(true, "No categories found: Empty key in Cache", Collections.emptyList());
            }

            // Retrieve hash entries for the given pattern
            Map<Object, Object> mapProductCategory = redisTemplate.opsForHash().entries(pattern);

            // Check if the map contains any product categories
            if (!mapProductCategory.isEmpty()) {

                // Convert and filter parent categories
                List<CacheModel> listOfCategory = mapProductCategory.values().stream()
                        .map(value -> objectMapper.convertValue(value, CacheModel.class))
                        .filter(filter -> filter.getParentid() == 0)  // Filter for parent categories
                        .sorted(Comparator.comparing(CacheModel::getId))  // Sort by ID
                        .collect(Collectors.toList());

                // Return success response with the list of parent categories
                return new ProductCategoryHierarchyModel(true, "Parent categories retrieved successfully", listOfCategory);
            }

            // If no categories found, return an empty list with a message
            return new ProductCategoryHierarchyModel(true, "No parent categories found", Collections.emptyList());

        } catch (Exception e) {
            // Log the error and return a failure response
            logger.error("RedisCacheService: buildParentCategoryHierarchy - Error occurred: {}", e.getMessage());
            return new ProductCategoryHierarchyModel(false, "Error: " + e.getMessage(), Collections.emptyList());
        }
    }
}
