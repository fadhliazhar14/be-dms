package com.bank_dki.be_dms.repository;

import com.bank_dki.be_dms.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Short> {
    Optional<User> findByUserName(String userName);
    Optional<User> findByUserEmail(String userEmail);
    Optional<User> findByUserNameOrUserEmail(String userName, String userEmail);
    Boolean existsByUserName(String userName);
    Boolean existsByUserEmail(String userEmail);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role WHERE u.userName = :userNameOrEmail OR u.userEmail = :userNameOrEmail")
    Optional<User> findByUserNameOrEmailWithRole(@Param("userNameOrEmail") String userNameOrEmail);
    
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.role")
    List<User> findAllWithRole();
    
    @Query("SELECT u FROM User u WHERE u.userIsActive = true")
    List<User> findAllActiveUsers();
    
    @Query("SELECT u FROM User u WHERE u.roleId = :roleId")
    List<User> findByRoleId(@Param("roleId") Short roleId);

    @Query("SELECT u FROM User u " +
            "WHERE u.roleId = :roleId " +
            "AND (:search IS NULL OR :search = '' OR LOWER(u.userName) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<User> findByRoleIdAndSearch(@Param("roleId") Short roleId, @Param("search") String search);
}