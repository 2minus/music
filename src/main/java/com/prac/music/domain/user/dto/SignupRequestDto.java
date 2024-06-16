package com.prac.music.domain.user.dto;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SignupRequestDto {
    // 기본적인 영문, 숫자를 조합하여 최소 6자리 이상 최대 16자리 이하
    @Pattern(regexp = "^[A-Za-z0-9]{6,16}$")
    private String userId;

    // 기본적인 영문, 숫자를 조합하여 최소 8자리 이상 최대 16자리 이하
    @Size(min = 10, message = "비밀번호는 최소 10자 이상 입니다.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{10,}$",
            message = "비밀번호는 대소문자 포함 영문, 숫자, 특수문자를 최소 1글자씩 포함해야 합니다."
    )
    private String password;

    // 한글, 영문
    @Pattern(regexp = "^[가-힣a-zA-Z]+$")
    private String name;

    //    '시작을' 0~9 사이 숫자 or a-z A-Z 알바펫 아무거나로 시작하고
    //    중간에 - _ . 같은 문자가 있을수도 있고 없을수도 있으며
    //    그 후에 0~9 사이 숫자 or a-z A-Z 알바펫중 하나의 문자가 없거나 연달아 나올수 있으며
    //    @ 가 반드시 존재하고 0-9a-zA-Z 여기서 하나가 있고
    //    중간에 - _ . 같은 문자가 있을수도 있고 없을수도 있으며
    //    그 후에 0~9 사이 숫자 or a-z A-Z 알바펫중 하나의 문자가 없거나 연달아 나올수 있으며
    //    반드시 . 이 존재하고 /[a-zA-Z] 의 문자가 2개나 3개가 존재
    @Pattern(regexp = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$")
    private String email;

    private String intro;

}