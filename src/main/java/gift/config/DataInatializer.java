package gift.config;

import gift.Entity.Member;
import gift.Entity.Product;
import gift.repository.MemberRepository;
import gift.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInatializer {

    @Bean
    public CommandLineRunner initData(MemberRepository memberRepository, ProductRepository productRepository) {
        return args -> {
            System.out.println("DataInitializer 실행됨");

            if (memberRepository.findById("admin01").isEmpty()) {
                memberRepository.save(new Member(
                        "admin01",
                        "admin@kakao.com",
                        "123456789",
                        "관리자",
                        "서울",
                        "ADMIN"
                ));
            }

            if (memberRepository.findById("helloworld").isEmpty()) {
                memberRepository.save(new Member(
                        "helloworld",
                        "helloworld@test.com",
                        "123456789",
                        "황윤규",
                        "광주",
                        "USER"
                ));
            }

            if (productRepository.findAll().isEmpty()) {
                Product p = new Product();
                p.setName("아이스 아메리카노 T");
                p.setPrice(4500);
                p.setImageUrl("https://image.example.com/americano.jpg");
                p.setMDapproved(true);
                productRepository.save(p);
            }
        };
    }
}
