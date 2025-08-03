package gift.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import gift.dto.OptionRequest;
import gift.dto.OptionResponse;
import gift.model.Option;
import gift.model.Product;
import gift.repository.OptionRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @Mock
    private ProductService productService;

    @InjectMocks
    private OptionService optionService;

    @Test
    void 옵션_생성_성공() {
        // given
        OptionRequest request = new OptionRequest(1L, "tall", 100);
        Product product = new Product(1L, "coffee", 2000, "http://coffee.jpg", false);
        Option savedOption = new Option(product, "tall", 100);

        when(optionRepository.existsByName("tall")).thenReturn(false);
        when(productService.findById(1L)).thenReturn(product);
        when(optionRepository.save(any(Option.class))).thenReturn(savedOption);

        // when
        OptionResponse response = optionService.createOption(request);

        // then
        assertThat(response.getName()).isEqualTo("tall");
        assertThat(response.getQuantity()).isEqualTo(100);
    }

    @Test
    void 옵션_생성_중복_예외() {
        // given
        OptionRequest request = new OptionRequest(1L, "tall", 100);

        when(optionRepository.existsByName("tall")).thenReturn(true);

        // when, then
        assertThatThrownBy(() -> optionService.createOption(request))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("이미 존재합니다");
    }

    @Test
    void 옵션_목록_조회() {
        // given
        Product product = new Product(1L, "coffee", 2000, "http://coffee.jpg", false);
        Option option1 = new Option(product, "hot", 10);
        Option option2 = new Option(product, "ice", 20);

        when(productService.getProduct(1L)).thenReturn(product);
        when(optionRepository.findAllByProduct(product)).thenReturn(
            Arrays.asList(option1, option2));

        // when
        List<OptionResponse> responses = optionService.findAllByProductId(1L);

        // then
        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getName()).isEqualTo("hot");
        assertThat(responses.get(0).getQuantity()).isEqualTo(10);
        assertThat(responses.get(1).getName()).isEqualTo("ice");
        assertThat(responses.get(1).getQuantity()).isEqualTo(20);
    }

    @Test
    void 옵션_단건_조회_성공() {
        // given
        Product product = new Product(1L, "coffee", 2000, "http://coffee.jpg", false);
        Option option = new Option(product, "hot", 15);

        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        // when
        OptionResponse response = optionService.getOptionResponse(1L);

        // then
        assertThat(response.getName()).isEqualTo("hot");
    }

    @Test
    void 존재하지않는_옵션_단건_조회_() {
        // given, when
        when(optionRepository.findById(anyLong())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> optionService.getOptionResponse(1L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("존재하지 않습니다");
    }

    @Test
    void 옵션_수량_감소() {
        // given
        Product product = new Product(1L, "coffee", 2000, "http://coffee.jpg", false);
        Option option = new Option(product, "ice", 30);

        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        // when
        optionService.decreaseQuantity(1L, 5);

        // then
        assertThat(option.getQuantity()).isEqualTo(25);
    }

    @Test
    void 옵션_수량_감소시_유효하지_않은_결과() {
        // given
        Product product = new Product(1L, "coffee", 2000, "http://coffee.jpg", false);
        Option option = new Option(product, "ice", 10);

        when(optionRepository.findById(1L)).thenReturn(Optional.of(option));

        // when, then
        assertThatThrownBy(() -> optionService.decreaseQuantity(1L, 100))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("변경할 옵션 수량은 1 이상 1억 미만이어야 합니다.");

        assertThatThrownBy(() -> optionService.decreaseQuantity(1L, -100000000))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("변경할 옵션 수량은 1 이상 1억 미만이어야 합니다.");
    }

    @Test
    void 옵션_삭제() {
        // given
        doNothing().when(optionRepository).deleteById(1L);

        // when
        optionService.deleteOption(1L);

        // then
        verify(optionRepository, times(1)).deleteById(1L);
    }
}
