package gift.product.entity;


import jakarta.persistence.*;


@Entity
public class Option {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String optionName;

	private int quantity;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	protected Option() {}


	public Option(Long id, String optionName, int quantity, Item item) {
		validateName(optionName);
		validateQuantity(quantity);

		this.id = id;
		this.optionName = optionName;
		this.quantity = quantity;
		this.item = item;
	}


	private void validateName(String optionName) {
		if (optionName == null || optionName.trim().isEmpty()) {
			throw new IllegalArgumentException("옵션명은 필수입니다.");
		}
		if (optionName.length() > 50) {
			throw new IllegalArgumentException("옵션명은 최대 50자까지 가능합니다.");
		}

		String possiblePattern = "^[가-힣a-zA-Z0-9\\s()\\[\\]+\\-&/_]*$";
		if(!optionName.matches(possiblePattern)) {
			throw new IllegalArgumentException("허용되지 않는 특수문자가 포함됐습니다.");
		}
	}


	private void validateQuantity(int quantity) {
		if (quantity < 0 || quantity >= 100000000) {
			throw new IllegalArgumentException("수량은 1개 이상 1억 미만입니다.");
		}
	}


	public void decreaseQuantity(int subtrahend) {
		validateQuantity(this.quantity - subtrahend);
		this.quantity -= subtrahend;
	}

	public Long getId() {
		return id;
	}


	public String getOptionName() {
		return optionName;
	}


	public int getQuantity() {
		return quantity;
	}


	public Item getItem() {
		return item;
	}

}
