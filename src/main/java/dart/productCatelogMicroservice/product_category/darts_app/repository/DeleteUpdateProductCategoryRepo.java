package dart.productCatelogMicroservice.product_category.darts_app.repository;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface DeleteUpdateProductCategoryRepo extends JpaRepository<ProductCategoryDbModel,Long> {
    Optional<ProductCategoryDbModel> findById(Integer Id);
}
