package gift;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import gift.controller.wishlist.WishListController;
import gift.dto.member.MemberCredentialDto;
import gift.dto.product.ProductResponseDto;
import gift.exception.UnAuthenicatedException;
import gift.resolver.LoginMemberArgumentResolver;
import gift.service.member.MemberService;
import gift.service.wishlist.WishListService;
import gift.util.JwtUtil;
import gift.util.Sha256Util;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WishListController.class)
@Import({LoginMemberArgumentResolver.class})
public class WishListControllerTest {

    @Autowired
    private MockMvc mockmvc;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private Sha256Util sha256Util;

    @MockitoBean
    private MemberService memberService;

    @MockitoBean
    private WishListService wishListService;

    @Test
    @DisplayName("위시리스트 생성 - 성공")
    void createWishTest_success() throws Exception {
        String token = "fake-jwt-token";
        Long memberId = 1L;
        String email = "example@naver.com";
        String encryptedPassword = sha256Util.encrypt("qwer");

        given(jwtUtil.getMemberIdFromToken(token))
            .willReturn(memberId);
        given(memberService.findById(memberId))
            .willReturn(new MemberCredentialDto(memberId, email, encryptedPassword));

        mockmvc.perform(post("/api/wishes/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated());
    }

    @Test
    @DisplayName("위시리스트 생성 - 실패")
    void createWishTest_fail() throws Exception {
        String token = "fake-jwt-token";

        given(jwtUtil.getMemberIdFromToken(token))
            .willThrow(new UnAuthenicatedException());

        mockmvc.perform(post("/api/wishes/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("위시리스트 조회 - 성공")
    void findAllWishTest_success() throws Exception {
        String token = "fake-jwt-token";
        Long memberId = 1L;
        String email = "example@naver.com";
        String encryptedPassword = sha256Util.encrypt("qwer");

        List<ProductResponseDto> productList = new ArrayList<>();
        productList.add(new ProductResponseDto(1L, "테스트 1", 1, 1, "test"));
        productList.add(new ProductResponseDto(2L, "테스트 2", 1, 1, "test"));
        productList.add(new ProductResponseDto(3L, "테스트 3", 1, 1, "test"));

        Pageable pageRequest = PageRequest.of(0, 10);
        Page<ProductResponseDto> productPage = new PageImpl<>(productList, pageRequest,
            productList.size());

        given(jwtUtil.getMemberIdFromToken(token))
            .willReturn(memberId);
        given(memberService.findById(memberId))
            .willReturn(new MemberCredentialDto(memberId, email, encryptedPassword));
        given(wishListService.findAll(memberId, 0, 10))
            .willReturn(productPage);

        mockmvc.perform(get("/api/wishes")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("위시리스트 조회 - 실패")
    void findAllWishTest_fail() throws Exception {
        String token = "fake-jwt-token";
        Long memberId = 1L;
        String email = "example@naver.com";
        String encryptedPassword = sha256Util.encrypt("qwer");

        given(jwtUtil.getMemberIdFromToken(token))
            .willThrow(new UnAuthenicatedException());

        mockmvc.perform(get("/api/wishes")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("위시리스트 삭제 - 성공")
    void deleteWishTest_success() throws Exception {
        String token = "fake-jwt-token";
        Long memberId = 1L;
        String email = "example@naver.com";
        String encryptedPassword = sha256Util.encrypt("qwer");

        given(jwtUtil.getMemberIdFromToken(token))
            .willReturn(memberId);
        given(memberService.findById(memberId))
            .willReturn(new MemberCredentialDto(memberId, email, encryptedPassword));

        mockmvc.perform(delete("/api/wishes/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("위시리스트 삭제 - 실패")
    void deleteWishTest_fail() throws Exception {
        String token = "fake-jwt-token";
        Long memberId = 1L;
        String email = "example@naver.com";
        String encryptedPassword = sha256Util.encrypt("qwer");

        given(jwtUtil.getMemberIdFromToken(token))
            .willThrow(new UnAuthenicatedException());

        mockmvc.perform(delete("/api/wishes/1")
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
            .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isUnauthorized());
    }
}