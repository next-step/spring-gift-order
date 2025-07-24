package gift.repository;

import gift.global.exception.CustomException;
import gift.global.exception.ErrorCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, ID> extends JpaRepository<T, ID> {
    default T findOrThrow(ID id) {
        return findById(id)
            .orElseThrow(() -> CustomException.from(ErrorCode.NOT_EXISTS));
    }
}
