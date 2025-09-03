package com.bank_dki.be_dms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UserId")
    private Short userId;
    
    @Column(name = "UserName", length = 100, nullable = true)
    private String userName;

    @Column(name = "userJobCode", length = 100, nullable = true)
    private String userJobCode;
    
    @Column(name = "UserEmail", length = 100, nullable = true)
    private String userEmail;
    
    @Column(name = "UserCreateAt", nullable = true)
    private LocalDateTime userCreateAt;
    
    @Column(name = "UserUpdateAt", nullable = true)
    private LocalDateTime userUpdateAt;
    
    @Column(name = "UserSalt", length = 100, nullable = true)
    private String userSalt;
    
    @Column(name = "UserIsActive", nullable = true)
    private Boolean userIsActive;
    
    @Column(name = "UserHashPassword", length = 100, nullable = true)
    private String userHashPassword;
    
    @Column(name = "RoleId", nullable = false)
    private Short roleId;
    
    @Column(name = "UserCreateBy", length = 50, nullable = true)
    private String userCreateBy;
    
    @Column(name = "UserUpdateBy", length = 50, nullable = true)
    private String userUpdateBy;
    
    @Lob
    @Column(name = "UserImage", nullable = true)
    private byte[] userImage;
    
    @Column(name = "UserImage_GXI", length = 2048, nullable = true)
    private String userImageGxi;
    
    @Column(name = "UserTglLahir", length = 100, nullable = true)
    private String userTglLahir;
    
    @Column(name = "UserJabatan", length = 40, nullable = true)
    private String userJabatan;
    
    @Column(name = "UserTempatLahir", length = 100, nullable = true)
    private String userTempatLahir;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RoleId", insertable = false, updatable = false)
    private Role role;
    
    @PrePersist
    protected void onCreate() {
        userCreateAt = LocalDateTime.now();
        userUpdateAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        userUpdateAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId) && 
               Objects.equals(userName, user.userName) && 
               Objects.equals(userEmail, user.userEmail);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, userName, userEmail);
    }
}