package dart.productCatelogMicroservice.product_category.darts_app.service;


import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryResModel;
import dart.productCatelogMicroservice.product_category.darts_app.helper.BuilderManager;
import dart.productCatelogMicroservice.product_category.darts_app.repository.CreateProductCategoryRepo;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ErrorHandler;
import dart.productCatelogMicroservice.product_category.darts_app.kafka.MessageBrokerManager;
import dart.productCatelogMicroservice.product_category.utilities.RunTimeException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CreateProductCategoryImpl {

    private final CreateProductCategoryRepo productCategoryRepo;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final BuilderManager builderManager;
    private static final Logger logger = LoggerFactory.getLogger(CreateProductCategoryImpl.class);

    public CreateProductCategoryImpl(
            CreateProductCategoryRepo productCategoryRepo,
            RedisCacheService redisCacheService,
            MessageBrokerManager messageBrokerManager,
            BuilderManager builderManager
    ) {
        this.productCategoryRepo = productCategoryRepo;
        this.redisCacheService = redisCacheService;
        this.messageBrokerManager = messageBrokerManager;
        this.builderManager = builderManager;
    }

    @Transactional
    public ResponseEntity<ProductCategoryResModel> createProductCategory(ProductCategoryReqModel request) {

        // Validate request parameters
        validateRequest(request);

        // Check for existing category
        Optional<ProductCategoryDbModel> dbListener = productCategoryRepo.findByName(request.getName().toLowerCase());

        if (dbListener.isPresent()) {
            logger.warn("Product Category already exists: {}", request.getName());
            throw new RunTimeException(
                    new ErrorHandler(false, "Product Category already exists!"),
                    HttpStatus.CONFLICT
            );
        }

        // Build and save the new product category
        ProductCategoryDbModel productCategoryBuilder = builderManager.dbBuilder(
                0,
                request.getName().toLowerCase(),
                request.getDescription(),
                request.getParentid(),
                true,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        ProductCategoryDbModel onSaveRecordInDb = saveProductCategory(productCategoryBuilder);

        boolean onSaveRecordInCache = redisCacheService.saveUpdateProductCategoryInCacheMemory(builderManager.CacheModelBuilder(onSaveRecordInDb));

        if (!onSaveRecordInCache) {
            messageBrokerManager.PushTopicToMessageBroker("create",onSaveRecordInDb);
        }

        return new ResponseEntity<>(new ProductCategoryResModel(true, "Category successfully created", onSaveRecordInDb), HttpStatus.CREATED);
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

    private ProductCategoryDbModel saveProductCategory(ProductCategoryDbModel productCategoryId) {
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


