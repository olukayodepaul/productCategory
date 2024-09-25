package dart.productCatelogMicroservice.product_category.darts_app.repository;

import com.fasterxml.jackson.databind.ObjectMapper;
import dart.productCatelogMicroservice.product_category.darts_app.entity.CacheModel;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class RedisCacheService {

    private static final Logger logger = LoggerFactory.getLogger(RedisCacheService.class);
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheService(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    public Boolean saveUpdateProductCategoryInCacheMemory(CacheModel profile) {
        try {
            Integer productId = profile.getId();
            String key = "product:category";
            String subKey = productId.toString();
            redisTemplate.opsForHash().put(key, subKey, profile);
            return true;
        } catch (Exception e) {
            logger.info("product: {}", e.getMessage());
            return false;
        }
    }

    public Boolean deleteProductCategoryFromCacheMemory(CacheModel productCategoryId) {
        try {
            Integer productId = productCategoryId.getId();
            String key = "product:category";
            String subKey = productId.toString();
            redisTemplate.opsForHash().delete(key, subKey);
            return true;
        } catch (Exception e) {
            logger.info("products: {}", e.getMessage());
            return false;
        }
    }

    public String getAllCachedRecords() {
        try {
            String pattern = "product:category";
            Set<String> keys = redisTemplate.keys(pattern);

            if (keys == null || keys.isEmpty()) {
                return "";
            }

            Map<Object, Object> addressMap = redisTemplate.opsForHash().entries(pattern);

            if (!addressMap.isEmpty()) {
                List<CacheModel> listOfCategory = addressMap.values().stream()
                        .map(value -> objectMapper.convertValue(value, CacheModel.class))
                        .collect(Collectors.toList());
                return convertToJSONString(buildCategoryHierarchy(listOfCategory));
            }

            return "";
        } catch (Exception e) {
            logger.info("products a: {}", e.getMessage());
            return "";
        }
    }

    public static List<CacheModel> buildCategoryHierarchy(List<CacheModel> categories) {
        Map<Integer, CacheModel> categoryMap = categories.stream()
                .collect(Collectors.toMap(CacheModel::getId, cat -> cat));

        List<CacheModel> rootCategories = new ArrayList<>();
        for (CacheModel category : categories) {
            if (category.getParentid() == null) {
                rootCategories.add(category);  // top-level category
            } else {
                CacheModel parentCategory = categoryMap.get(Integer.parseInt(category.getParentid()));
                // Initialize the children list if it's null
                if (parentCategory != null) {
                    if (parentCategory.getChildren() == null) {
                        parentCategory.setChildren(new ArrayList<>()); // Ensure children list is initialized
                    }
                    parentCategory.getChildren().add(category);  // Add as child
                }
            }
        }
        return rootCategories;  // return top-level categories with children attached
    }

    public static String convertToJSONString(List<CacheModel> categoryHierarchy) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writeValueAsString(categoryHierarchy);
    }

}