package gift.product.entity;


import jakarta.persistence.*;

import java.util.List;


@Entity
public class Item {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;

	private String name;

	private Integer price;

	private String imageUrl;

	@OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Option> options;

	public Item(Long id, User user, String name, Integer price, String imageUrl) {
		validateKakaoKeyword(name);

		this.id = id;
		this.user = user;
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
	}

	// for jpa
	protected Item() {}

	public static ItemBuilder builder() {
		return new ItemBuilder();
	}


	public boolean duplicateOptionNameCheck(String optionName) {
		return this.options.stream()
			.anyMatch(option -> option.getOptionName().equals(optionName));
	}

	public static class ItemBuilder {
		private User user;
		private String name;
		private Integer price;
		private String imageUrl;

		public ItemBuilder user(User user) {
			this.user = user;
			return this;
		}
		public ItemBuilder name(String name){
			this.name = name;
			return this;
		}
		public ItemBuilder price(Integer price){
			this.price = price;
			return this;
		}
		public ItemBuilder imageUrl(String imageUrl){
			this.imageUrl = imageUrl;
			return this;
		}
		public Item build() {
			return new Item(null, user, name, price, imageUrl);
		}
	}

	private void validateKakaoKeyword(String name){
		if (name.contains("카카오")){
			throw new IllegalArgumentException("'카카오'는 담당자와 협의 후 사용가능한 키워드입니다.");
		}
	}

	public void isItemAuthor(User user) {
		if(this.user.getId() != user.getId()){
			throw new IllegalArgumentException("작성자만 수정,삭제 가능합니다.");
		}
	}

	public void updateItem(String name, int price, String imageUrl) {
		this.name = name;
		this.price = price;
		this.imageUrl = imageUrl;
	}

	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public User getUser() {
		return user;
	}

	public Integer getPrice() {
		return price;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public List<Option> getOptions() {
		return options;
	}

}
