package com.prac.music.domain.comment.entity;

import com.prac.music.domain.board.entity.Board;
import com.prac.music.domain.user.entity.BaseTimeEntity;
import com.prac.music.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "comment")
@NoArgsConstructor
public class Comment extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String contents;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Comment(String contents, User user, Board board) {
        this.id = id;
        this.contents = contents;
        this.user = user;
        this.board = board;
    }

    public void update(String contents) {
        this.contents = contents;
    }
}
