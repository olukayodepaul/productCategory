package dart.productCatelogMicroservice.product_category.darts_app.helper;

import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class BuilderManager {

    public List<ProductCategoryDbModel> buildHierarchy(List<ProductCategoryDbModel> categories) {
        Map<String, List<ProductCategoryDbModel>> groupedByParent = categories.stream()
                .collect(Collectors.groupingBy(category ->
                        category.getParentid() == null ? "root" : category.getParentid()));
        return buildCategoryTree(groupedByParent, "root");
    }

    private List<ProductCategoryDbModel> buildCategoryTree(Map<String, List<ProductCategoryDbModel>> groupedByParent, String parentId) {
        List<ProductCategoryDbModel> subCategories = groupedByParent.getOrDefault(parentId, new ArrayList<>());
        for (ProductCategoryDbModel category : subCategories) {
            category.setSubCategories(buildCategoryTree(groupedByParent, String.valueOf(category.getId())));
        }
        return subCategories;
    }

    public CacheModel SingleCacheModelBuilder(ProductCategoryDbModel savedProductCategory) {
        return CacheModel.builder()
                .id(savedProductCategory.getId())
                .name(savedProductCategory.getName())
                .description(savedProductCategory.getDescription())
                .parentid(savedProductCategory.getParentid())
                .isactive(savedProductCategory.getIsactive())
                .createdat(savedProductCategory.getCreatedat())
                .updatedat(savedProductCategory.getUpdatedat())
                .build();
    }

    public ProductCategoryDbModel dbBuilder(String name, String description, String parentid){
        return ProductCategoryDbModel
                .builder()
                .name(name)
                .description(description)
                .parentid(parentid)
                .isactive(true)
                .createdat(LocalDateTime.now())
                .updatedat(LocalDateTime.now())
                .build();
    }


}
