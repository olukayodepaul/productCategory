package dart.productCatelogMicroservice.product_category.darts_app.service;


import dart.productCatelogMicroservice.product_category.darts_app.repository.GetProductCategoryRepo;
import dart.productCatelogMicroservice.product_category.darts_app.repository.RedisCacheService;
import dart.productCatelogMicroservice.product_category.utilities.MessageBrokerManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;


@Service
public class FetchProductCategoryByIdImpl {

    private final GetProductCategoryRepo getProductCategoryRepo;
    private final RedisCacheService redisCacheService;
    private final MessageBrokerManager messageBrokerManager;

    public FetchProductCategoryByIdImpl(
            GetProductCategoryRepo getProductCategoryRepo,
            RedisCacheService redisCacheService,
            MessageBrokerManager messageBrokerManager
    )
    {
        this.getProductCategoryRepo = getProductCategoryRepo;
        this.redisCacheService = redisCacheService;
        this.messageBrokerManager = messageBrokerManager;
    }

//    public ResponseEntity<ProductCategoryResDataModel> fetchProductCategoryById(Integer id) {
//        return null;
//    }
}
