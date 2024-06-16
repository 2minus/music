package com.prac.music.domain.comment.service;

import com.prac.music.common.exception.CommentServiceException;
import com.prac.music.common.exception.UnauthorizedAccessException;
import com.prac.music.domain.board.entity.Board;
import com.prac.music.domain.board.repository.BoardRepository;
import com.prac.music.domain.comment.dto.CommentRequestDto;
import com.prac.music.domain.comment.dto.CommentResponseDto;
import com.prac.music.domain.comment.dto.CommentUpdateRequestDto;
import com.prac.music.domain.comment.entity.Comment;
import com.prac.music.domain.comment.repository.CommentRepository;
import com.prac.music.domain.user.entity.User;
import com.prac.music.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

	private final CommentRepository commentRepository;
	private final UserRepository userRepository;
	private final BoardRepository boardRepository;

	@Transactional
	public CommentResponseDto createComment(CommentRequestDto requestDto,
											Long boardId,
											User user) {
		Board findBoard = findBoardById(boardId);
		User persistentUser = findUserById(user.getId());

		Comment comment = Comment.builder()
			.contents(requestDto.getContents())
			.board(findBoard)
			.user(user)
			.build();

		Comment savedComment = commentRepository.save(comment);
		return new CommentResponseDto(savedComment);
	}

	@Transactional
	public CommentResponseDto updateComment(Long commentId,
											User user,
											CommentUpdateRequestDto requestDto) {
		Comment comment = findCommentById(commentId);
		User persistentUser = findUserById(user.getId());

		validateUserAuthorization(comment, persistentUser);

		comment.update(requestDto.getContents());
		Comment updatedComment = commentRepository.save(comment);
		return new CommentResponseDto(updatedComment);
	}

	@Transactional
	public void deleteComment(Long commentId,
							  User user) {
		Comment comment = findCommentById(commentId);
		User persistentUser = findUserById(user.getId());

		validateUserAuthorization(comment, persistentUser);

		commentRepository.delete(comment);
	}

	public Comment findCommentById(Long commentId) {
		return commentRepository.findById(commentId)
			.orElseThrow(() -> new CommentServiceException("찾으시는 댓글이 없습니다."));
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new CommentServiceException("사용자를 찾을 수 없습니다."));
	}

	private Board findBoardById(Long boardId) {
		return boardRepository.findById(boardId)
			.orElseThrow(() -> new CommentServiceException("게시글을 찾을 수 없습니다."));
	}

	private void validateUserAuthorization(Comment comment,
										   User user) {
		if (!comment.getUser().equals(user)) {
			throw new UnauthorizedAccessException("권한이 없습니다.");
		}
	}
}
