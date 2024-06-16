package com.prac.music.entity;

import com.prac.music.domain.board.dto.BoardRequestDto;
import com.prac.music.domain.board.dto.BoardResponseDto;
import com.prac.music.domain.board.entity.Board;
import com.prac.music.domain.board.entity.BoardFiles;
import com.prac.music.domain.board.entity.BoardLike;
import com.prac.music.domain.board.repository.BoardRepository;
import com.prac.music.domain.board.service.BoardService;
import com.prac.music.domain.comment.dto.CommentRequestDto;
import com.prac.music.domain.comment.dto.CommentResponseDto;
import com.prac.music.domain.comment.entity.Comment;
import com.prac.music.domain.comment.entity.CommentLike;
import com.prac.music.domain.comment.repository.CommentRepository;
import com.prac.music.domain.comment.service.CommentService;
import com.prac.music.domain.mail.entity.Mail;
import com.prac.music.domain.mail.service.MailService;
import com.prac.music.domain.user.dto.SignupRequestDto;
import com.prac.music.domain.user.entity.User;
import com.prac.music.domain.user.entity.UserStatusEnum;
import com.prac.music.domain.user.repository.UserRepository;
import com.prac.music.domain.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class EntityTest {

    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;
    @Autowired
    BoardService boardService;
    @Autowired
    CommentService commentService;

    @Mock
    UserRepository userRepository;
    @Mock
    BoardRepository boardRepository;
    @Mock
    CommentRepository commentRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Test
    @DisplayName("User Entity Test")
    void test1() throws IOException {
        // given
        SignupRequestDto requestDto = new SignupRequestDto(
                "testId1",
                "testPassword1!",
                "testName",
                "test@email.com",
                "test Introduce"
        );

        MultipartFile file = null;

        // when
        User user = userService.createUser(requestDto, file);

        // then
        assertEquals(requestDto.getUserId(), user.getUserId());
        assertTrue(passwordEncoder.matches(requestDto.getPassword(), user.getPassword()));
        assertEquals(requestDto.getName(), user.getName());
        assertEquals(requestDto.getEmail(), user.getEmail());
        assertEquals(requestDto.getIntro(), user.getIntro());
    }

    @Test
    @DisplayName("Mail Entity Test")
    void test2() {
        //given
        User user = userBuild();
        String code = createCode();

        //when
        Mail mail = Mail.builder()
                .user(user)
                .build();

        mail.mailAddCode(code);

        //then
        assertNotNull(mail);
        assertEquals(user.getEmail(), mail.getEmail());
        assertEquals(code, mail.getCode());
    }

    @Test
    @DisplayName("Board Entity Test")
    void test3() throws IOException {
        //given
        User user = userBuild();
        BoardRequestDto requestDto = BoardRequestDto.builder()
                .title("test title")
                .contents("test contents")
                .build();
        //when
        BoardResponseDto responseDto = boardService.createBoard(requestDto, user, null);

        //then
        assertNotNull(responseDto);
        assertEquals(responseDto.getTitle(), requestDto.getTitle());
        assertEquals(responseDto.getContents(), requestDto.getContents());
    }

    @Test
    @DisplayName("Comment Entity Test")
    void test4() {
        //given
        User user = userBuild();
        Board board = boardBuild();
        CommentRequestDto requestDto = CommentRequestDto.builder()
                .contents("test contents")
                .build();

        //when
        CommentResponseDto responseDto = commentService.createComment(requestDto, board.getId(), user);


        //then
        assertNotNull(responseDto);
        assertEquals(requestDto.getContents(), responseDto.getContents());
    }

    @Test
    @DisplayName("BoardLike Entity Test")
    void test5() {
        //given
        User user = userBuild();
        Board board = boardBuild();
        BoardLike boardLike = new BoardLike(board, user);
        //when

        //then
        assertNotNull(boardLike);
    }

    @Test
    @DisplayName("CommentLike Entity Test")
    void test6() {
        //given
        User user = userBuild();
        Comment comment = commentBuild();
        CommentLike commentLike = new CommentLike(comment, user);
        //when

        //then
        assertNotNull(commentLike);
    }

    @Test
    @DisplayName("BoardFiles Entity Test")
    void test7() {
        //given
        Board board = boardBuild();
        BoardFiles boardFiles = BoardFiles.builder()
                .board(board)
                .file("awsS3Url")
                .build();
        //when

        //then
        assertNotNull(boardFiles);
        assertEquals("awsS3Url", boardFiles.getFile());
    }

    public User userBuild() {
        return User.builder()
                .userId("testId1")
                .password("testPassword1!")
                .name("testName")
                .email("test@email.com")
                .intro("test Introduce")
                .status(UserStatusEnum.NORMAL)
                .profileImage(null)
                .build();
    }

    public String createCode() {
        int numberzero = 48; // 0 아스키 코드
        int alphbetz = 122; // z 아스키 코드
        int codeLength = 8; // 인증코드의 길이
        Random rand = new Random(); // 임의 생성

        return rand.ints(numberzero, alphbetz + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)) // 숫자와 알파벳만 허용
                .limit(codeLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
    }

    public Board boardBuild() {
        return new Board(1L, "test title", "test contents", userBuild(), null, null);
    }

    public Comment commentBuild() {
        return Comment.builder()
                .board(boardBuild())
                .user(userBuild())
                .contents("test contents")
                .build();
    }
}
