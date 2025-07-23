package gift.repository;

@Deprecated
public class H2UserRepository {

    /*
    private final JdbcClient jdbcClient;

    public H2UserRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public Optional<User> findById(Long id) {
        return jdbcClient.sql("select * from users where id = :id")
                .param("id", id)
                .query(User.class)
                .optional();
    }

    public Optional<User> findByEmail(Email email) {
        return jdbcClient.sql("select * from users where email = :email")
                .param("email", email.value())
                .query(User.class)
                .optional();
    }

    public User save(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcClient.sql("insert into users (email, password) values (:email, :password)")
                .param("email", user.email().value())
                .param("password", user.password().value())
                .update(keyHolder);

        user.setId(keyHolder.getKey().longValue());
        return user;
    }
    */
}
