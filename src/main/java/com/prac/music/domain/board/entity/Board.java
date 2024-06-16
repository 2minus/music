package com.prac.music.domain.board.entity;

import com.prac.music.domain.comment.entity.Comment;
import com.prac.music.domain.user.entity.BaseTimeEntity;
import com.prac.music.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor // 기본 생성자는 protected로 설정합니다.
@AllArgsConstructor
@Entity
@Table(name = "board")
public class Board extends BaseTimeEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, name = "title")
	private String title;

	@Column(nullable = false, name = "contents")
	private String contents;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false, name = "user_id")
	private User user;

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private List<Comment> comments;

	@OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<BoardFiles> boardFiles = new ArrayList<>();

	@Builder
	public Board(String title, String contents, User user) {
		this.id = id;
		this.title = title;
		this.contents = contents;
		this.user = user;
	}

	public void update(String title, String contents) {
		this.title = title;
		this.contents = contents;
	}
}
