package gift;

import gift.domain.product.MdApprovalStatus;
import gift.exception.product.MdApprovalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MdApprovalStatusTest {
    @Test
    @DisplayName("상품명에 '카카오' 포함 시 MdApprovalException 예외 발생")
    void of_containsForbiddenKeyword_throwsException() {
        String productName = "카카오 초코파이";

        MdApprovalException exception = assertThrows(MdApprovalException.class,
                () -> MdApprovalStatus.of(productName));

        assertEquals("'카카오'가 포함된 상품명은 MD 승인 후 등록 가능합니다.", exception.getMessage());
    }

    @Test
    @DisplayName("상품명에 '카카오' 미포함 시 notApproved 상태 생성")
    void of_withoutForbiddenKeyword_returnsNotApproved() {
        String productName = "초코파이";

        MdApprovalStatus status = MdApprovalStatus.of(productName);

        assertFalse(status.isApproved());
    }

    @Test
    @DisplayName("approved() 호출 시 approved 상태 생성")
    void approved_returnsApprovedStatus() {
        MdApprovalStatus status = MdApprovalStatus.approved();

        assertTrue(status.isApproved());
    }

    @Test
    @DisplayName("notApproved() 호출 시 notApproved 상태 생성")
    void notApproved_returnsNotApprovedStatus() {
        MdApprovalStatus status = MdApprovalStatus.notApproved();

        assertFalse(status.isApproved());
    }
}
