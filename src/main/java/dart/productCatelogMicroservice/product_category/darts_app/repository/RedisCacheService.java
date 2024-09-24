package dart.productCatelogMicroservice.product_category.darts_app.repository;

import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import dart.productCatelogMicroservice.product_category.darts_app.entity.ProductCategoryDbModel;
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

    public Boolean saveProfileIntoRedis(CacheModel profile) {
        try {

            Integer productId = profile.getId();
            String key = "product:category";
            String subKey = productId.toString();

            redisTemplate.opsForHash().putIfAbsent(key, subKey, profile);
            return true;

        } catch (Exception e) {
            logger.info("product_category.darts_app.entity.ProductCategoryDbModel saveProfileIntoRedis: {}", e.getMessage());
            return false;
        }
    }

}