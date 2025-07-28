package gift.config;

import gift.api.member.domain.Member;
import gift.api.member.domain.MemberRole;
import gift.api.member.repository.MemberRepository;
import gift.api.product.domain.Product;
import gift.api.product.repository.ProductRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public DataInitializer(MemberRepository memberRepository,
            ProductRepository productRepository) {
        this.memberRepository = memberRepository;
        this.productRepository = productRepository;
    }

    @Override
    public void run(String... args) {
        // 관리자 계정 초기화
        String adminEmail = "admin@admin";
        String adminPassword = "admin123";

        if (memberRepository.findByEmail(adminEmail).isEmpty()) {
            Member admin = new Member(
                    adminEmail,
                    BCrypt.hashpw(adminPassword, BCrypt.gensalt()),
                    MemberRole.ADMIN
            );
            memberRepository.save(admin);
        }

        // 테스트용 상품 데이터 초기화
        if (productRepository.count() == 0) {
            Product product = new Product(
                    "테스트 상품",
                    10000L,
                    "https://d8iqbmvu05s9c.cloudfront.net/ajprhqgqg1otf7d5sm7u3brf27gv"
            );
            product.addOption("테스트 옵션", 1);
            product.addOption("테스트 옵션2", 20);
            productRepository.save(product);
        }
    }
}