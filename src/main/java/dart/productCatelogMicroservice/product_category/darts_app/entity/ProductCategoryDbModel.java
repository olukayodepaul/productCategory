package dart.productCatelogMicroservice.product_category.darts_app.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "categories")
public class ProductCategoryDbModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private Boolean isactive;
    private Integer parentid = 0;
    private LocalDateTime createdat;
    private  LocalDateTime updatedat;

    @Transient
    public List<ProductCategoryDbModel> children = new ArrayList<>();





}
