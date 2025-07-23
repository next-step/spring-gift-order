package gift.domain.product;

import gift.exception.product.MdApprovalException;
import jakarta.persistence.Embeddable;

@Embeddable
public class MdApprovalStatus {

    private static final String REQUIRES_MD_APPROVAL_KEYWORD = "카카오";

    private boolean approved;

    protected MdApprovalStatus() {}

    private MdApprovalStatus(boolean approved) {
        this.approved = approved;
    }

    public static MdApprovalStatus of(String productName) {
        if (productName.contains(REQUIRES_MD_APPROVAL_KEYWORD)) {
            throw new MdApprovalException("'" + REQUIRES_MD_APPROVAL_KEYWORD + "'가 포함된 상품명은 MD 승인 후 등록 가능합니다.");
        }
        return new MdApprovalStatus(false);
    }

    public static MdApprovalStatus approved() {
        return new MdApprovalStatus(true);
    }

    public static MdApprovalStatus notApproved() {
        return new MdApprovalStatus(false);
    }

    public boolean isApproved() {
        return approved;
    }
}
