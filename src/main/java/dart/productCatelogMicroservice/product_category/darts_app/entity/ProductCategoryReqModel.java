package dart.productCatelogMicroservice.product_category.darts_app.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCategoryReqModel {
    private String name;
    private String description;
    private Integer parentid;
    private String isactive;
}