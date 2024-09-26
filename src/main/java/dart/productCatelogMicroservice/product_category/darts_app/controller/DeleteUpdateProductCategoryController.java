package dart.productCatelogMicroservice.product_category.darts_app.controller;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.service.DeleteProductCategoryImpl;
import dart.productCatelogMicroservice.product_category.darts_app.service.UpdateProductCategoryImpl;
import dart.productCatelogMicroservice.product_category.utilities.ResponseHandler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for handling delete and update operations on product categories.
 */
@RestController
@RequestMapping("/api")
public class DeleteUpdateProductCategoryController {

    private final DeleteProductCategoryImpl deleteImpl;
    private final UpdateProductCategoryImpl updateImpl;

    /**
     * Constructs a new DeleteUpdateProductCategoryController.
     *
     * @param deleteImpl The service implementation for deleting product categories.
     * @param updateImpl The service implementation for updating product categories.
     */
    public DeleteUpdateProductCategoryController(DeleteProductCategoryImpl deleteImpl, UpdateProductCategoryImpl updateImpl) {
        this.deleteImpl = deleteImpl;
        this.updateImpl = updateImpl;
    }

    /**
     * Deletes a product category by its ID.
     *
     * @param categoryId The ID of the category to be deleted.
     * @return A ResponseEntity containing the response status and message.
     */
    @DeleteMapping("/categories/{categoryId}")
    public ResponseEntity<ResponseHandler> deleteProductCategory(@PathVariable Integer categoryId) {
        return deleteImpl.deleteProductCategory(categoryId);
    }

    /**
     * Updates a product category by its ID.
     *
     * @param request    The request model containing updated category information.
     * @param categoryId The ID of the category to be updated.
     * @return A ResponseEntity containing the response status and updated category information.
     */
    @PutMapping("/categories/{categoryId}")
    public ResponseEntity<ResponseHandler> updateProductCategory(@RequestBody ProductCategoryReqModel request, @PathVariable Integer categoryId) {
        return updateImpl.updateProductCategory(request, categoryId);
    }
}
