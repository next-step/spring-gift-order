package gift.repository;

@Deprecated
public class H2ProductRepository {

    /*
    private final JdbcClient jdbcClient;

    public H2ProductRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Product> findAll() {
        return jdbcClient.sql("select * from products")
                .query(Product.class)
                .list();
    }

    @Override
    public Optional<Product> findById(Long id) {
        return jdbcClient.sql("select * from products where id = :id")
                .param("id", id)
                .query(Product.class)
                .optional();
    }

    @Override
    public Product save(Product product) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into products (name, price, imageUrl) values (:name, :price, :imageUrl)")
                .param("name", product.getName())
                .param("price", product.getPrice())
                .param("imageUrl", product.getImageUrl())
                .update(keyHolder);

        product.setId(keyHolder.getKey().longValue());
        return product;
    }

    @Override
    public boolean update(Product product) {
        return jdbcClient.sql("update products set name = :name, price = :price, imageUrl = :imageUrl where id = :id")
                .param("id", product.getId())
                .param("name", product.getName())
                .param("price", product.getPrice())
                .param("imageUrl", product.getImageUrl())
                .update() != 0;
    }

    @Override
    public boolean deleteById(Long id) {
        return jdbcClient.sql("delete from products where id = :id")
                .param("id", id)
                .update() != 0;
    }
     */
}
