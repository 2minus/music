package com.prac.music.domain.user.service;

import com.prac.music.common.exception.PasswordRuntimeException;
import com.prac.music.common.exception.UserServiceException;
import com.prac.music.common.service.S3Service;
import com.prac.music.domain.mail.service.MailService;
import com.prac.music.domain.user.dto.LoginRequestDto;
import com.prac.music.domain.user.dto.LoginResponseDto;
import com.prac.music.domain.user.dto.SignoutRequestDto;
import com.prac.music.domain.user.dto.SignupRequestDto;
import com.prac.music.domain.user.entity.User;
import com.prac.music.domain.user.entity.UserStatusEnum;
import com.prac.music.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;
    private final JwtService jwtService;
    private final S3Service s3Service;

    public User createUser(SignupRequestDto requestDto,
                           MultipartFile file) throws IOException {
        String userId = requestDto.getUserId();
        String password = passwordEncoder.encode(requestDto.getPassword());

        String imageUrl = "";
        if (file != null && !file.isEmpty()) {
            imageUrl = s3Service.s3Upload(file);
        } else {
            imageUrl = "null";
        }
        Optional<User> checkUser = userRepository.findByUserId(userId);

        if (checkUser.isPresent()) {
            throw new UserServiceException("이미 중복된 사용자가 존재합니다.");
        }

        User user = User.builder()
                .userId(userId)
                .password(password)
                .status(UserStatusEnum.TEMPORARY)
                .name(requestDto.getName())
                .email(requestDto.getEmail())
                .intro(requestDto.getIntro())
                .profileImage(imageUrl)
                .build();

        return userRepository.save(user);
    }

    public LoginResponseDto loginUser(@RequestBody LoginRequestDto requestDto) {
        User user = this.userRepository.findByUserId(requestDto.getUserId()).orElseThrow(
                () -> new UserServiceException("아이디를 다시 확인해주세요")
        );

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getUserId(),
                        requestDto.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtService.createToken(requestDto.getUserId());
        String refreshToken = jwtService.createRefreshToken(requestDto.getUserId());
        user.updateRefresh(refreshToken);
        userRepository.save(user);

        return new LoginResponseDto(token, refreshToken, "로그인에 성공했습니다.");
    }

    @Transactional
    public void logoutUser(User user) {
        user.updateRefresh(null);
        userRepository.save(user);
    }

    @Transactional
    public void signoutUser(SignoutRequestDto requestDto,
                            User user) {
        if (!user.isExist()) {
            throw new UserServiceException("이미 탈퇴된 사용자입니다.");
        }

        String password = requestDto.getPassword();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new PasswordRuntimeException("잘못된 비밀번호입니다.");
        }

        user.updateStatusSignout();
        userRepository.save(user);
    }

    private boolean checkUserStatus(User user) {
        return user.getStatus().equals(UserStatusEnum.NORMAL);
    }
}
