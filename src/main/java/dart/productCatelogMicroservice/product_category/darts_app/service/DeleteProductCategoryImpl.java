package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.grpc.GrpcManager;
import dart.productCatelogMicroservice.product_category.darts_app.helper.BuilderManager;
import dart.productCatelogMicroservice.product_category.darts_app.repository.DeleteUpdateProductCategoryRepo;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ErrorHandler;
import dart.productCatelogMicroservice.product_category.darts_app.kafka.MessageBrokerManager;
import dart.productCatelogMicroservice.product_category.utilities.ResponseHandler;
import dart.productCatelogMicroservice.product_category.utilities.RunTimeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class DeleteProductCategoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(DeleteProductCategoryImpl.class);
    public final DeleteUpdateProductCategoryRepo productCategoryRepo;
    public GrpcManager grpcManager;
    private final BuilderManager builderManager;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;

    public DeleteProductCategoryImpl(GrpcManager grpcManager, DeleteUpdateProductCategoryRepo productCategoryRepo, BuilderManager builderManager, RedisCacheService redisCacheService, MessageBrokerManager messageBrokerManager) {
        this.grpcManager = grpcManager;
        this.productCategoryRepo = productCategoryRepo;
        this.builderManager = builderManager;
        this.redisCacheService = redisCacheService;
        this.messageBrokerManager = messageBrokerManager;
    }

    /**
     * Deletes a product category by changing its active status to false.
     * Validates the category ID and checks for associated products before deletion.
     *
     * @param categoryId the ID of the product category to be deleted
     * @return a ResponseEntity containing a ResponseHandler with the deletion status
     * @throws RunTimeException if the category ID is invalid, or if the category is inactive or has associated products
     */
    public ResponseEntity<ResponseHandler> deleteProductCategory(Integer categoryId) {
        validateRequest(categoryId);

        Optional<ProductCategoryDbModel> dbListener = productCategoryRepo.findById(categoryId);

        if (dbListener.isPresent()) {
            // gRPC call to ProductMicroService confirming if any active product is associated with the product category id
            Boolean grpcChannel = grpcManager.checkCategoryAssociation(categoryId);

            if (!grpcChannel) {
                throw new RunTimeException(new ErrorHandler(false, "Cannot delete product category with associated products."), HttpStatus.CONFLICT);
            }

            if (!dbListener.get().getIsactive()) {
                String errorMessage = "Cannot delete. The product category is inactive.";
                throw new RunTimeException(new ErrorHandler(false, errorMessage), HttpStatus.CONFLICT);
            }

            ProductCategoryDbModel recordFetchFromDb = dbListener.get();

            ProductCategoryDbModel productCategoryBuilder = builderManager.dbBuilder(
                    recordFetchFromDb.getId(),
                    recordFetchFromDb.getName(),
                    recordFetchFromDb.getDescription(),
                    recordFetchFromDb.getParentid(),
                    false,
                    recordFetchFromDb.getCreatedat(),
                    LocalDateTime.now()
            );

            ProductCategoryDbModel onDeleteDbRecord = deleteProductCategory(productCategoryBuilder);

            boolean onDeleteRecordInCache = redisCacheService.deleteProductCategoryFromCacheMemory(builderManager.CacheModelBuilder(onDeleteDbRecord));

            // Send message to message broker if Redis cache service fails
            if (!onDeleteRecordInCache) {
                messageBrokerManager.PushTopicToMessageBroker("delete", onDeleteDbRecord);
            }

            return new ResponseEntity<>(new ResponseHandler(true, "Product category deleted successfully."), HttpStatus.OK);
        }

        throw new RunTimeException(new ErrorHandler(false, "No product category found with the given ID."), HttpStatus.CONFLICT);
    }

    /**
     * Validates the incoming category ID for deletion.
     *
     * @param request the category ID to validate
     * @throws RunTimeException if the category ID is null
     */
    private void validateRequest(Integer request) {
        if (request == null) {
            String errorMessage = "Category ID cannot be null.";
            logger.error("Validation Failed: {}", errorMessage);
            throw new RunTimeException(new ErrorHandler(false, errorMessage), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Updates the product category's status to inactive in the database.
     *
     * @param productCategoryId the product category model to update
     * @return the updated ProductCategoryDbModel
     * @throws RunTimeException if an error occurs while saving to the database
     */
    private ProductCategoryDbModel deleteProductCategory(ProductCategoryDbModel productCategoryId) {
        try {
            return productCategoryRepo.save(productCategoryId);
        } catch (Exception e) {
            logger.error("DeleteProductCategoryImpl deleteProductCategory - Error deleting Product Category: {}", e.getMessage());
            throw new RunTimeException(
                    new ErrorHandler(false, "Unable to delete your record at this time."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
