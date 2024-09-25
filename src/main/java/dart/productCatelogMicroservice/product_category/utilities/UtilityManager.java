package dart.productCatelogMicroservice.product_category.utilities;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class UtilityManager {

    public String DateToStringDate(LocalDateTime date){
        LocalDateTime dateTime = date;
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return dateTime.format(outputFormatter);
    }

}


