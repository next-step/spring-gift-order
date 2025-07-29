package gift.common.mapper;

import gift.dto.option.OptionCreateRequest;
import gift.dto.order.OrderCreateRequest;
import gift.dto.order.OrderUpdateRequest;
import gift.dto.product.ProductCreateRequest;
import gift.dto.product.ProductUpdateRequest;
import gift.dto.user.UserCreateRequest;
import gift.dto.user.UserUpdateRequest;
import gift.entity.Option;
import gift.entity.Order;
import gift.entity.Product;
import gift.entity.User;
import gift.entity.type.UserRole;

import java.util.stream.Collectors;

public class DtoToEntityMapper {

    private DtoToEntityMapper() {
        // 인스턴스 생성 방지
    }

    public static Product toEntity(ProductCreateRequest request) {
        var options = request.options().stream()
                .map(DtoToEntityMapper::toEntity)
                .toList();

        return new Product(
                request.name(),
                request.price(),
                request.imageUrl(),
                options
        );
    }

    public static Product toEntity(ProductUpdateRequest request) {
        return new Product(
                request.name(),
                request.price(),
                request.imageUrl()
        );
    }

    public static User toEntity(UserCreateRequest request) {
        var mappedRoles = request.roles().stream()
                .map(UserRole::valueOf)
                .collect(Collectors.toSet());
        return new User(
                request.email(),
                request.password(),
                mappedRoles
        );
    }

    public static User toEntity(UserUpdateRequest request) {
        var user = new User(
                request.email(),
                request.password()
        );
        if (request.roles() != null) {
            var mappedRoles = request.roles().stream()
                    .map(UserRole::valueOf)
                    .collect(Collectors.toSet());
            user.setRoles(mappedRoles);
        }
        return user;
    }

    public static Option toEntity(OptionCreateRequest request) {
        return new Option(
                request.name(),
                request.quantity()
        );
    }

    public static Order toEntity(OrderCreateRequest request) {
        return new Order(
                null, // 새 주문이므로 ID는 null
                request.quantity(),
                null, // service 단에서 totalPrice를 계산할 것이므로 null (연관 객체에 대한 조회가 필요)
                request.message(),
                null, // 주문 생성 시 User는 null로 설정, 서비스 단에서 설정할 예정
                new Option(request.optionId())
        );
    }

    public static Order toEntity(OrderUpdateRequest request, Long id) {
        return new Order(
                id,
                request.quantity(),
                request.totalPrice(),
                request.message(),
                null, // 주문 업데이트 시 User는 null로 설정, 서비스 단에서 설정할 예정
                null
        );
    }
}
