package gift.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import gift.exception.ProductNotFoundException;
import gift.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


@ExtendWith(MockitoExtension.class)
@DisplayName("productExist_상품없을 때")
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productServiceImpl;

    @Test
    void productExistTest_상품이_없는_경우() {

        //given
        given(productRepository.existsById(1234L)).willReturn(false);

        //when,then
        assertThrows(ProductNotFoundException.class, () -> {
            productServiceImpl.productExist(1234);
        });
    }

}