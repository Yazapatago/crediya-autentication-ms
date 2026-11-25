package co.com.crediya.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDataRepository extends JpaRepository<UserEntity, Long> {

    boolean existsByEmail(String email);
}
