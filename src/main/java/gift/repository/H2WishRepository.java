package gift.repository;

@Deprecated
public class H2WishRepository {

    /*
    private final JdbcClient jdbcClient;

    public H2WishRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public List<WishProductView> findAllByUserId(Long userId) {
        return jdbcClient.sql("select w.productId, w.quantity, p.name as productName " +
                              "from wishes w " +
                              "join products p on w.productId = p.id " +
                              "where userId = :userId")
                        .param("userId", userId)
                        .query(WishProductView.class)
                        .list();
    }

    public Optional<Wish> save(Wish wish) {
        boolean success = jdbcClient.sql("insert into wishes (userId, productId, quantity) values (:userId, :productId, :quantity)")
                                    .param("userId", wish.getUserId())
                                    .param("productId", wish.getProductId())
                                    .param("quantity", wish.getQuantity())
                                    .update() != 0;

        return (success ? Optional.of(wish) : Optional.empty());
    }

    public Optional<Wish> update(Wish wish) {
        boolean success = jdbcClient.sql("update wishes set quantity = :quantity " +
                                         "where userId = :userId and productId = :productId")
                                    .param("userId", wish.getUserId())
                                    .param("productId", wish.getProductId())
                                    .param("quantity", wish.getQuantity())
                                    .update() != 0;

        return (success ? Optional.of(wish) : Optional.empty());
    }

    public boolean deleteById(Long userId, Long productId) {
        return jdbcClient.sql("delete from wishes where userId = :userId and productId = :productId")
                .param("userId", userId)
                .param("productId", productId)
                .update() != 0;
    }


    public boolean isWishExist(Long userId, Long productId) {
        return jdbcClient.sql("select 1 from wishes where userId = :userId and productId = :productId")
                .param("userId", userId)
                .param("productId", productId)
                .query(Integer.class)
                .optional().isPresent();
    }
     */
}
