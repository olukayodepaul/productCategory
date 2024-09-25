package dart.productCatelogMicroservice.product_category.darts_app.helper;

import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.utilities.UtilityManager;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BuilderManager {

    private final UtilityManager utilityManager;

    public BuilderManager(UtilityManager utilityManager) {
        this.utilityManager = utilityManager;
    }

    public CacheModel CacheModelBuilder(ProductCategoryDbModel savedProductCategory) {
        return CacheModel.builder()
                .id(savedProductCategory.getId())
                .name(savedProductCategory.getName())
                .description(savedProductCategory.getDescription())
                .parentid(savedProductCategory.getParentid())
                .isactive(savedProductCategory.getIsactive())
                .createdat(utilityManager.DateToStringDate(savedProductCategory.getCreatedat()))
                .updatedat(utilityManager.DateToStringDate(savedProductCategory.getUpdatedat()))
                .build();
    }

    public ProductCategoryDbModel dbBuilder(
            Integer id,
            String name,
            String description,
            String parentId,
            Boolean active,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
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
    }


}
