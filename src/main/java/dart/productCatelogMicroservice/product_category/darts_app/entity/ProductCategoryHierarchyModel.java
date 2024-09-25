package dart.productCatelogMicroservice.product_category.darts_app.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryHierarchyModel {
    private Boolean status;
    private String message;
    private List<CacheModel> category;
}
