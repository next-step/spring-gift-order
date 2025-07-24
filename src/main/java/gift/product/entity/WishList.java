package gift.product.entity;


import jakarta.persistence.*;


@Entity
public class WishList {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "item_id")
	private Item item;

	private int amount;

	protected WishList() {}

	public WishList(Long id, User user, Item item, int amount) {
		this.id = id;
		this.user = user;
		this.item = item;
		this.amount = amount;
	}

	public WishList(User user, Item item, int amount) {
		this(null, user, item, amount);
	}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public Item getItem() {
		return item;
	}

	public int getAmount() { return amount; }

	public void updateAmount(int amount) {
		if (amount <= 0) {
			throw new IllegalArgumentException("수량은 반드시 1 이상이여야 합니다");
		}
		this.amount = amount;
	}

	public void validateOwner(User user) {
		if(user == null || user != this.user) {
			throw new IllegalArgumentException("잘못된 사용자 정보입니다.");
		}
	}

}
