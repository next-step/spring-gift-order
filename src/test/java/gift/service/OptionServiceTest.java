package gift.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import gift.domain.Option;
import gift.domain.Product;
import gift.dto.OptionResponse;
import gift.repository.OptionRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OptionServiceTest {

    @Mock
    private OptionRepository optionRepository;

    @InjectMocks
    private OptionService optionService;

    @Test
    void getOptionsByProductId() {
        // given
        Product product = new Product("Test Product", 10000, "test.jpg");
        given(optionRepository.findByProductId(1L))
            .willReturn(List.of(
                new Option("Option 1", 10, product),
                new Option("Option 2", 20, product)
            ));

        // when
        List<OptionResponse> options = optionService.getOptionsByProductId(1L);

        // then
        assertThat(options).hasSize(2);
        assertThat(options.get(0).getName()).isEqualTo("Option 1");
        assertThat(options.get(1).getQuantity()).isEqualTo(20);
    }
}
