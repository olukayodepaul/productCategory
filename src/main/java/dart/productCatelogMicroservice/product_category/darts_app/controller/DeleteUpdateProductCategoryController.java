package dart.productCatelogMicroservice.product_category.darts_app.controller;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.DeleteProductCategoryImpl;
import dart.productCatelogMicroservice.product_category.darts_app.service.UpdateProductCategoryImpl;
import dart.productCatelogMicroservice.product_category.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class DeleteUpdateProductCategoryController {

    private final DeleteProductCategoryImpl deleteImpl;
    private final UpdateProductCategoryImpl updateImpl;

    public DeleteUpdateProductCategoryController(DeleteProductCategoryImpl deleteImpl, UpdateProductCategoryImpl updateImpl) {
        this.deleteImpl = deleteImpl;
        this.updateImpl = updateImpl;
    }

    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ResponseHandler> deleteProductCategory(@PathVariable Integer categoryId ) {
        return deleteImpl.deleteProductCategory(categoryId);
    }

    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ResponseHandler> updateProductCategory(@RequestBody ProductCategoryReqModel request, @PathVariable Integer categoryId ) {
        return updateImpl.updateProductCategory(request, categoryId);
    }
    
}
