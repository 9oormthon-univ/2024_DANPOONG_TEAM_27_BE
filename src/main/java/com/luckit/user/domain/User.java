package com.luckit.user.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Table(name = "users")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder(toBuilder = true) // `toBuilder` 활성화
public class User {

    @Id
    @Column(name = "USER_ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer userId;

    private String name;

    private String nickName;

    private String email;

    private String profileImage;

    private String gender;

    private String solarOrLunar;

    private LocalDateTime date_of_birth;

    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Enumerated(EnumType.STRING)
    private LoginType loginType;

    public void updateInfo(String nickName) {this.nickName = nickName;}

    @Builder
    private User(String email,String name, String nickName, String profileImage, LoginType loginType, RoleType roleType){
        this.email = email;
        this.name = name;
        this.nickName = nickName;
        this.profileImage = profileImage;
        this.loginType = loginType;
        this.roleType = roleType;
    }
}
