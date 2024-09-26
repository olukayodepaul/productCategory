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

/**
 * Service implementation for managing product category creation.
 * This service handles the logic for creating new product categories,
 * including validation, persistence, and caching.
 */
@Service
public class CreateProductCategoryImpl {

    private final CreateProductCategoryRepo productCategoryRepo;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;
    private final BuilderManager builderManager;
    private static final Logger logger = LoggerFactory.getLogger(CreateProductCategoryImpl.class);

    /**
     * Constructs a CreateProductCategoryImpl instance with the required dependencies.
     *
     * @param productCategoryRepo the repository for product categories
     * @param redisCacheService the Redis cache service for product categories
     * @param messageBrokerManager the message broker manager for handling messages
     * @param builderManager the builder manager for constructing product category models
     */
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

    /**
     * Creates a new product category based on the provided request model.
     *
     * @param request the request model containing details of the product category to create
     * @return a ResponseEntity containing the response model for the created product category
     *         along with the appropriate HTTP status code
     * @throws RunTimeException if the request is invalid or if the category already exists
     */
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

        // Send message to message broker if Redis cache service fails
        if (!onSaveRecordInCache) {
            messageBrokerManager.PushTopicToMessageBroker("create", onSaveRecordInDb);
        }

        return new ResponseEntity<>(new ProductCategoryResModel(true, "Category successfully created", onSaveRecordInDb), HttpStatus.CREATED);
    }

    /**
     * Validates the request parameters for creating a product category.
     *
     * @param request the request model to validate
     * @throws RunTimeException if the request is null or if required fields are missing
     */
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

    /**
     * Saves a product category to the database.
     *
     * @param productCategoryId the product category model to save
     * @return the saved product category model
     * @throws RunTimeException if an error occurs during the save operation
     */
    private ProductCategoryDbModel saveProductCategory(ProductCategoryDbModel productCategoryId) {
        try {
            return productCategoryRepo.save(productCategoryId);
        } catch (Exception e) {
            logger.error("CreateProductCategoryImpl createProductCategory: Error creating category: {}", e.getMessage(), e);
            throw new RunTimeException(new ErrorHandler(false, "Unable to save your record at this time."), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
