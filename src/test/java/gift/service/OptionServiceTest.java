package gift.service;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.exception.BusinessException;
import gift.exception.ErrorCode;
import gift.repository.OptionJpaRepository;
import gift.repository.ProductJpaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionJpaRepository optionJpaRepository;

    @Mock
    private ProductJpaRepository productJpaRepository;

    @InjectMocks
    private OptionService optionService;

    @Test
    @DisplayName("상품 ID로 옵션 목록을 조회할 수 있다")
    void getOptionsByProductId() {
        // given
        Long productId = 1L;
        Product product = Product.withId(productId, "테스트 상품", 10000, "test.jpg");
        Option option1 = Option.of("옵션1", 100, product);
        Option option2 = Option.of("옵션2", 200, product);

        given(productJpaRepository.existsById(productId)).willReturn(true);
        given(optionJpaRepository.findByProductId(productId)).willReturn(List.of(option1, option2));

        // when
        List<OptionResponse> responses = optionService.getOptionsByProductId(productId);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).name()).isEqualTo("옵션1");
        assertThat(responses.get(1).name()).isEqualTo("옵션2");
    }

    @Test
    @DisplayName("존재하지 않는 상품 ID로 옵션을 조회하면 예외가 발생한다")
    void getOptionsByNonExistentProductId() {
        // given
        Long productId = 999L;
        given(productJpaRepository.existsById(productId)).willReturn(false);

        // when & then
        assertThatThrownBy(() -> optionService.getOptionsByProductId(productId))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    @DisplayName("옵션을 추가할 수 있다")
    void addOption() {
        // given
        Long productId = 1L;
        Product product = Product.withId(productId, "테스트 상품", 10000, "test.jpg");
        OptionRequest request = new OptionRequest("새 옵션", 100);
        Option savedOption = Option.of(request.name(), request.quantity(), product);

        given(productJpaRepository.findById(productId)).willReturn(Optional.of(product));
        given(optionJpaRepository.existsByProductIdAndName(productId, request.name())).willReturn(false);
        given(optionJpaRepository.save(any(Option.class))).willReturn(savedOption);

        // when
        OptionResponse response = optionService.addOption(productId, request);

        // then
        assertThat(response.name()).isEqualTo("새 옵션");
        assertThat(response.quantity()).isEqualTo(100);
    }

    @Test
    @DisplayName("중복된 옵션 이름으로 추가하면 예외가 발생한다")
    void addDuplicateOption() {
        // given
        Long productId = 1L;
        Product product = Product.withId(productId, "테스트 상품", 10000, "test.jpg");
        OptionRequest request = new OptionRequest("기존 옵션", 100);

        given(productJpaRepository.findById(productId)).willReturn(Optional.of(product));
        given(optionJpaRepository.existsByProductIdAndName(productId, request.name())).willReturn(true);

        // when & then
        assertThatThrownBy(() -> optionService.addOption(productId, request))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OPTION_NAME_DUPLICATE);
    }

    @Test
    @DisplayName("옵션 수량을 차감할 수 있다")
    void subtractOptionQuantity() {
        // given
        Long optionId = 1L;
        Product product = Product.of("테스트 상품", 10000, "test.jpg");
        Option option = Option.of("테스트 옵션", 100, product);

        given(optionJpaRepository.findById(optionId)).willReturn(Optional.of(option));

        // when
        optionService.subtractOptionQuantity(optionId, 30);

        // then
        assertThat(option.quantity()).isEqualTo(70);
        verify(optionJpaRepository, times(1)).save(option);
    }

    @Test
    @DisplayName("존재하지 않는 옵션 수량을 차감하려 하면 예외가 발생한다")
    void subtractNonExistentOptionQuantity() {
        // given
        Long optionId = 999L;
        given(optionJpaRepository.findById(optionId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> optionService.subtractOptionQuantity(optionId, 30))
                .isInstanceOf(BusinessException.class)
                .hasFieldOrPropertyWithValue("errorCode", ErrorCode.OPTION_NOT_FOUND);
    }
}
