package zeta.payments.repository;

import zeta.payments.commons.enums.UserRole;
import zeta.payments.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    @Query("SELECT u FROM User u WHERE u.userName = :userName")
    Optional<User> getUserByUserName(@Param("userName")String userName);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.role = :role WHERE u.userName = :userName")
    int updateUserRole(@Param("userName") String userName, @Param("role") UserRole role);

    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.userName = :userName")
    int updateUserPassword(@Param("userName") String userName, @Param("password") String password);
}
