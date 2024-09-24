package dart.productCatelogMicroservice.product_category.utilities;



import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorHandler {
    private boolean status;
    private String message;
}


