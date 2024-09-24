package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.grpc.GrpcManager;
import dart.productCatelogMicroservice.product_category.darts_app.helper.BuilderManager;
import dart.productCatelogMicroservice.product_category.darts_app.repository.DeleteUpdateProductCategoryRepo;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ErrorHandler;
import dart.productCatelogMicroservice.product_category.utilities.MessageBrokerManager;
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

            //todo: gRPC call to ProductsMicroService to check if any active product is associated with the category id.
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

            ProductCategoryDbModel dbResponse = dbListener.get();

            ProductCategoryDbModel productCategoryBuilder = builderManager.dbBuilder(
                    dbResponse.getId(),
                    dbResponse.getName(),
                    dbResponse.getDescription(),
                    dbResponse.getParentid(),
                    false,
                    dbResponse.getCreatedat(),
                    LocalDateTime.now()
            );

            ProductCategoryDbModel updateCategory = saveAndUpdateCategory(productCategoryBuilder);

            boolean cacheStatus = redisCacheService.deleteProductCategoryFromCacheMemory(builderManager.SingleCacheModelBuilder(updateCategory));

            if (!cacheStatus) {
                messageBrokerManager.PushTopicToMessageBroker("delete", updateCategory);
            }
            return new ResponseEntity<>(new ResponseHandler(true, "Category successfully created"), HttpStatus.CREATED);
        }

        throw new RunTimeException(
                new ErrorHandler(false, "No product category is associated with the if"),
                HttpStatus.CONFLICT
        );
    }

    private void validateRequest(Integer request) {
        if (request == null) {
            logger.error("DeleteProductCategoryImpl: Request is null");
            throw new RunTimeException(
                    new ErrorHandler(false, "Description is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public ProductCategoryDbModel saveAndUpdateCategory(ProductCategoryDbModel productCategoryId) {
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
