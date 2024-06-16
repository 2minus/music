package com.prac.music.domain.user.entity;

import com.prac.music.domain.user.dto.ProfileRequestDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;


@Entity
@Getter
@Builder
@Table(name = "user")
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, name = "userid")
    private String userId;

    @Column(unique = true, nullable = false, name = "email")
    private String email;

    @Column(nullable = false, name = "password")
    private String password;

    @Column(nullable = false, name = "name")
    private String name;

    @Column(nullable = false, name = "intro")
    private String intro;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private UserStatusEnum status;

    @Column(name = "refresh_token")
    private String refreshToken;

    @Column(name = "profile_Image")
    private String profileImage;

    public boolean isExist() {
        return this.status == UserStatusEnum.NORMAL;
    }

    public void nonPasswordProfileUpdate(ProfileRequestDto requestDto, String profileImage) {
        this.name = requestDto.getName();
        this.email = requestDto.getEmail();
        this.intro = requestDto.getIntro();
        this.profileImage = profileImage;
    }

    public void inPasswordProfileUpdate(ProfileRequestDto requestDto, String encodedPasswdDto, String profileImage) {
        this.name = requestDto.getName();
        this.email = requestDto.getEmail();
        this.intro = requestDto.getIntro();
        this.password = encodedPasswdDto;
        this.profileImage = profileImage;
    }

    public void updateRefresh(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateStatusVeryfied() {
        this.status = UserStatusEnum.NORMAL;
    }

    public void updateStatusSignout() {
        this.status = UserStatusEnum.SECESSION;
    }

}
