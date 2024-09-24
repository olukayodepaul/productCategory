package dart.productCatelogMicroservice.product_category.darts_app.repository;

import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service
public class RedisCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean saveUpdateProductCategoryInCacheMemory(CacheModel profile) {
        try {

            Integer productId = profile.getId();
            String key = "product:category";
            String subKey = productId.toString();

            redisTemplate.opsForHash().put(key, subKey, profile);
            return true;

        } catch (Exception e) {
            logger.info("product_category.darts_app.entity.ProductCategoryDbModel saveProfileCategoryInCacheMemory: {}", e.getMessage());
            return false;
        }
    }

    public Boolean deleteProductCategoryFromCacheMemory(CacheModel productCategoryId) {
        try {

            Integer productId = productCategoryId.getId();
            String key = "product:category";
            String subKey = productId.toString();

            Long result = redisTemplate.opsForHash().delete(key, subKey);

            return true;
        } catch (Exception e) {
            logger.info("product_category.darts_app.entity.ProductCategoryDbModel deleteProductCategoryFromCacheMemory: {}", e.getMessage());
            return false;
        }
    }

}