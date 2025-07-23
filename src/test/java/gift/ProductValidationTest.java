package gift;

import gift.domain.member.Email;
import gift.domain.member.Password;
import gift.domain.member.Role;
import gift.domain.product.MdApprovalStatus;
import gift.entity.Member;
import gift.entity.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import gift.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductValidationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        Email email = new Email("admin_" + UUID.randomUUID() + "@email.com");
        Password password = new Password("encodedPasswordHere");
        Member adminMember = new Member(email, password, Role.ADMIN);
        Member savedAdmin = memberRepository.save(adminMember);

        token = jwtUtil.generateToken(
                savedAdmin.getEmail().getValue(),
                savedAdmin.getId(),
                savedAdmin.getRole().name()
        );
    }

    @DisplayName("상품명 15자 초과 시 유효성 에러 발생")
    @Test
    void createProduct_NameTooLong_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/admin/products-add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "이것은15자를초과한상품이름입니다")
                        .param("price", "1000")
                        .param("imageUrl", "image.jpg"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(model().attributeHasFieldErrorCode("product", "name", "Size"))
                .andExpect(view().name("admin/product-add"));
    }

    @DisplayName("상품명에 특수문자 포함 시 유효성 에러 발생")
    @Test
    void createProduct_InvalidSpecialCharacters_ShouldReturnValidationError() throws Exception {
        mockMvc.perform(post("/admin/products-add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "잘못된@상품이름")
                        .param("price", "1000")
                        .param("imageUrl", "image.jpg"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(model().attributeHasFieldErrorCode("product", "name", "Pattern"))
                .andExpect(view().name("admin/product-add"));
    }

    @DisplayName("MD 승인 없이 '카카오'가 포함된 상품명 등록 시도 시 오류 발생")
    @Test
    void createProduct_KakaoWithoutApproval_ShouldReturnMdApprovalError() throws Exception {
        mockMvc.perform(post("/admin/products-add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "카카오 선물세트")
                        .param("price", "1000")
                        .param("imageUrl", "image.jpg"))
                .andExpect(status().isOk())
                .andExpect(model().attributeHasFieldErrors("product", "name"))
                .andExpect(model().attributeHasFieldErrorCode("product", "name", "md.not.approved"))
                .andExpect(view().name("admin/product-add"));
    }

    @Test
    @DisplayName("MD 승인 후 '카카오'가 포함된 수정 시도 시 정상 리다이렉트")
    void editProduct_KakaoWithApproval_ShouldRedirect() throws Exception {
        Product approvedProduct = new Product(
                "카카오 선물세트",
                1000L,
                "image.jpg",
                MdApprovalStatus.approved()
        );
        Product savedProduct = productRepository.save(approvedProduct);

        mockMvc.perform(post("/admin/" + savedProduct.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "카카오 선물세트")
                        .param("price", "1000")
                        .param("imageUrl", "image.jpg"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin"));
    }
}

