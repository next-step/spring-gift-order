package gift.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "option")
public class Option {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    @NotNull(message = "이름은 필수입니다.")
    @Size(max=50, message = "옵션명은 최대 50자까지 입력 가능합니다.")
    @Pattern(regexp = "^[a-zA-Z0-9가-힣 ()\\[\\]+\\-&/_]*$", message = "상품 이름의 특수문자는 ( ), [ ], +, -, &, /, _ 이외에는 허용되지 않습니다.")
    private String name;

    @OneToMany(mappedBy = "option")
    private List<ProductOption> products = new ArrayList<>();

    protected Option() {}

    public Option(String name) {
        this.name = name;
    }

    public void update (String name) {
        this.name = name;
    }

    public long getId() { return id; }
    public String getName() { return name; }
}
