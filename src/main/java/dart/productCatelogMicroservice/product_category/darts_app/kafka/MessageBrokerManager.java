package dart.productCatelogMicroservice.product_category.darts_app.kafka;

import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
import org.springframework.stereotype.Service;

@Service
public class MessageBrokerManager {
    public void PushTopicToMessageBroker(String eventChannel, ProductCategoryDbModel saveCategoryIntoDb){

    }
}
