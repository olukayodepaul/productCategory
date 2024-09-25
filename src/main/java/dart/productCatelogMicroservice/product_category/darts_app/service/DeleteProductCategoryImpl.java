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

    public ResponseEntity<ResponseHandler> deleteProductCategory(Integer categoryId) {

        validateRequest(categoryId);

        Optional<ProductCategoryDbModel> dbListener = productCategoryRepo.findById(categoryId);

        if(dbListener.isPresent()) {
            //todo: gRPC call to ProductMicroService confirming if any active product is associated with the category id.
            //note cant delete a product category with associated product.
            Boolean grpcChannel = grpcManager.checkCategoryAssociation(categoryId);

            if (!grpcChannel) {
                throw new RunTimeException(
                        new ErrorHandler(false, "Cannot delete product category with associated products."),
                        HttpStatus.CONFLICT
                );
            }

            if(!dbListener.get().getIsactive()) {
                throw new RunTimeException(
                        new ErrorHandler(false, "The product category is inactive"),
                        HttpStatus.CONFLICT
                );
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

            if (!onDeleteRecordInCache) {
                messageBrokerManager.PushTopicToMessageBroker("delete", onDeleteDbRecord);
            }
            return new ResponseEntity<>(new ResponseHandler(true, "Category successfully deleted"), HttpStatus.CREATED);
        }

        throw new RunTimeException(
                new ErrorHandler(false, "No product category is associated with the "),
                HttpStatus.CONFLICT
        );
    }

    private void validateRequest(Integer request) {
        if (request == null) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Description is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    //note that the application is not deleting any record but change the status to false
    private ProductCategoryDbModel deleteProductCategory(ProductCategoryDbModel productCategoryId) {
        try {
            return productCategoryRepo.save(productCategoryId);
        } catch (Exception e) {
            logger.error("Error saving Product Category: {}", e.getMessage());
            throw new RunTimeException(
                    new ErrorHandler(false, "Unable to save your record at this time."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

}
