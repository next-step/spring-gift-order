package gift.product.dto;

public record GetItemResponse(Long id, Long authorId, String name, Integer price, String imageUrl) {

	public static GetItemResponseBuilder builder(){
		return new GetItemResponseBuilder();
	}

	public static class GetItemResponseBuilder {
		private Long id;
		private Long authorId;
		private String name;
		private Integer price;
		private String imageUrl;

		public GetItemResponseBuilder id(Long id) {
			this.id = id;
			return this;
		}

		public GetItemResponseBuilder authorId(Long authorId) {
			this.authorId = authorId;
			return this;
		}

		public GetItemResponseBuilder name(String name) {
			this.name = name;
			return this;
		}

		public GetItemResponseBuilder price(Integer price) {
			this.price = price;
			return this;
		}

		public GetItemResponseBuilder imageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
			return this;
		}

		public GetItemResponse build() {
			return new GetItemResponse(id, authorId, name, price, imageUrl);
		}
	}
}
