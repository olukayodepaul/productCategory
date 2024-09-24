package dart.productCatelogMicroservice.product_category.darts_app.service;


import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryReqModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryResModel;
import dart.productCatelogMicroservice.product_category.darts_app.helper.BuilderManager;
import dart.productCatelogMicroservice.product_category.darts_app.repository.ProductCategoryRepo;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.ErrorHandler;
import dart.productCatelogMicroservice.product_category.utilities.MessageBrokerManager;
import dart.productCatelogMicroservice.product_category.utilities.RunTimeException;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductCategoryImpl {

    private final ProductCategoryRepo productCategoryRepo;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final BuilderManager builderManager;
    private static final Logger logger = LoggerFactory.getLogger(ProductCategoryImpl.class);

    public ProductCategoryImpl(
            ProductCategoryRepo productCategoryRepo,
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
        logger.debug("Attempting to create product category: {}", request.getName());

        // Validate request parameters
        validateRequest(request);

        // Check for existing category
        Optional<ProductCategoryDbModel> existingCategory = productCategoryRepo.findByName(request.getName().toLowerCase());
        if (existingCategory.isPresent()) {
            logger.warn("Product Category already exists: {}", request.getName());
            throw new RunTimeException(
                    new ErrorHandler(false, "Product Category already exists!"),
                    HttpStatus.CONFLICT
            );
        }

        // Build and save the new product category
        ProductCategoryDbModel newCategory = builderManager.dbBuilder(request.getName().toLowerCase(), request.getDescription(), request.getParentid());
        ProductCategoryDbModel savedCategory = saveProfileIntoDb(newCategory);

        // Save to Redis
        boolean cacheStatus = redisCacheService.saveProfileIntoRedis(builderManager.SingleCacheModelBuilder(savedCategory));
        if (!cacheStatus) {
            logger.warn("Redis is offline; pushing update to message broker for category: {}", savedCategory);
            messageBrokerManager.PushTopicToMessageBroker(savedCategory);
        }

        logger.info("Product category created successfully: {}", savedCategory.getName());
        return new ResponseEntity<>(new ProductCategoryResModel(true, "Category successfully created", savedCategory), HttpStatus.CREATED);
    }

    private void validateRequest(ProductCategoryReqModel request) {
        if (request == null) {
            logger.error("Request is null");
            throw new RunTimeException(
                    new ErrorHandler(false, "Request cannot be null."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (request.getDescription() == null || request.getDescription().isEmpty()) {
            logger.error("Description is required");
            throw new RunTimeException(
                    new ErrorHandler(false, "Description is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
        if (request.getName() == null || request.getName().isEmpty()) {
            logger.error("Name is required");
            throw new RunTimeException(
                    new ErrorHandler(false, "Name is required."),
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    public ProductCategoryDbModel saveProfileIntoDb(ProductCategoryDbModel profile) {
        try {
            return productCategoryRepo.save(profile);
        } catch (Exception e) {
            logger.error("Error saving Product Category: {}", e.getMessage());
            throw new RunTimeException(
                    new ErrorHandler(false, "Unable to save your record at this time."),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}


