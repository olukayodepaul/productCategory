package dart.productCatelogMicroservice.product_category.darts_app.helper;


import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RedisResHelper {
    private Boolean isListener;
    private ProductCategoryDbModel isCacheProduct;
}

