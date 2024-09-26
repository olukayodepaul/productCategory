package dart.productCatelogMicroservice.product_category.darts_app.helper;

import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.UtilityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BuilderManager {

    private final UtilityManager utilityManager;
    private static final Logger logger = LoggerFactory.getLogger(BuilderManager.class);

    public BuilderManager(UtilityManager utilityManager) {
        this.utilityManager = utilityManager;
    }

    public List<CacheModel> buildHierarchy(List<CacheModel> categories, Integer parentId) {
        try {
            // Group categories by their parentId
            Map<Integer, List<CacheModel>> groupedByParent = categories.stream()
                    .collect(Collectors.groupingBy(CacheModel::getParentid));

            // Build the category tree starting from the given parentId
            return buildCategoryTree(groupedByParent, parentId);
        } catch (Exception e) {
            // Log and handle the error
            logger.error("Error building hierarchy for parent ID {}: {}", parentId, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<CacheModel> buildCategoryTree(Map<Integer, List<CacheModel>> groupedByParent, Integer parentId) {
        try {
            // Fetch subcategories for the given parentId
            List<CacheModel> subCategories = groupedByParent.getOrDefault(parentId, new ArrayList<>());

            // Sort the subcategories by their ID
            subCategories.sort(Comparator.comparing(CacheModel::getId));

            // Recursively build the children of each subcategory
            for (CacheModel category : subCategories) {
                category.setChildren(buildCategoryTree(groupedByParent, category.getId()));
            }

            return subCategories;
        } catch (Exception e) {
            // Log the error if the tree-building process fails
            logger.error("Error building category tree for parent ID {}: {}", parentId, e.getMessage());
            return Collections.emptyList();
        }
    }

    public CacheModel CacheModelBuilder(ProductCategoryDbModel savedProductCategory) {
        try {
            // Build and return a CacheModel object using values from the savedProductCategory
            return CacheModel.builder()
                    .id(savedProductCategory.getId())
                    .name(savedProductCategory.getName())
                    .description(savedProductCategory.getDescription())
                    .parentid(savedProductCategory.getParentid())
                    .isactive(savedProductCategory.getIsactive())
                    .createdat(utilityManager.DateToStringDate(savedProductCategory.getCreatedat()))
                    .updatedat(utilityManager.DateToStringDate(savedProductCategory.getUpdatedat()))
                    .build();
        } catch (Exception e) {
            // Log the error if the object construction fails
            logger.error("Error building CacheModel for product category with ID {}: {}", savedProductCategory.getId(), e.getMessage());
            return null;
        }
    }

    public ProductCategoryDbModel dbBuilder(
            Integer id,
            String name,
            String description,
            Integer parentId,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        try {
            // Build and return a ProductCategoryDbModel object
            return ProductCategoryDbModel
                    .builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .parentid(parentId)
                    .isactive(active)
                    .createdat(createdAt)
                    .updatedat(updatedAt)
                    .build();
        } catch (Exception e) {
            // Log and handle any error that occurs during the building process
            logger.error("Error building ProductCategoryDbModel with ID {}: {}", id, e.getMessage());
            return null;
        }
    }



}
