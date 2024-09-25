package dart.productCatelogMicroservice.product_category.darts_app.service;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
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
public class UpdateProductCategoryImpl {

    private static final Logger logger = LoggerFactory.getLogger(DeleteProductCategoryImpl.class);
    public final DeleteUpdateProductCategoryRepo productCategoryRepo;
    private final BuilderManager builderManager;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;

    public UpdateProductCategoryImpl(
            DeleteUpdateProductCategoryRepo productCategoryRepo,
            BuilderManager builderManager,
            RedisCacheService redisCacheService,
            MessageBrokerManager messageBrokerManager)
    {
        this.productCategoryRepo = productCategoryRepo;
        this.builderManager = builderManager;
        this.redisCacheService = redisCacheService;
        this.messageBrokerManager = messageBrokerManager;
    }

    public ResponseEntity<ResponseHandler> updateProductCategory(ProductCategoryReqModel request, Integer productId) {

        validateRequest(request);


        Optional<ProductCategoryDbModel> dbListener = productCategoryRepo.findById(productId);

        if(dbListener.isPresent()) {

            if(!dbListener.get().getIsactive()) {
                throw new RunTimeException(
                        new ErrorHandler(false, "The product category is inactive"),
                        HttpStatus.CONFLICT
                );
            }

            ProductCategoryDbModel recordFetchFromDb = dbListener.get();

            ProductCategoryDbModel productCategoryBuilder = builderManager.dbBuilder(
                    recordFetchFromDb.getId(),
                    request.getName(),
                    request.getDescription(),
                    recordFetchFromDb.getParentid(),
                    true,
                    recordFetchFromDb.getCreatedat(),
                    LocalDateTime.now()
            );

            ProductCategoryDbModel onUpdateRecordInDb = updateProductCategory(productCategoryBuilder);

            boolean onUpdateRecordInCache = redisCacheService.saveUpdateProductCategoryInCacheMemory(builderManager.SingleCacheModelBuilder(onUpdateRecordInDb));

            if (!onUpdateRecordInCache) {
                messageBrokerManager.PushTopicToMessageBroker("update", onUpdateRecordInDb);
            }

            return new ResponseEntity<>(new ResponseHandler(true, "Category successfully updated"), HttpStatus.CREATED);
        }

        throw new RunTimeException(
                new ErrorHandler(false, "No product category is associated"),
                HttpStatus.CONFLICT
        );
    }

    private void validateRequest(ProductCategoryReqModel request) {
        if (request == null) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Request cannot be null."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Description is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            throw new RunTimeException(
                    new ErrorHandler(false, "Name is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    private ProductCategoryDbModel updateProductCategory(ProductCategoryDbModel productCategoryId) {
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
