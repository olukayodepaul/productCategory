package dart.productCatelogMicroservice.product_category.darts_app.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@Getter
@AllArgsConstructor
@RedisHash("categories")
public class CacheModel implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String description;
    private Integer parentid;
    private Boolean isactive;
    private String createdat;
    private String updatedat;

    public List<CacheModel> children = new ArrayList<>();
}

