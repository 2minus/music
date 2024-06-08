package com.prac.music.domain.board.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.prac.music.domain.board.dto.BoardRequestDto;
import com.prac.music.domain.board.dto.BoardResponseDto;
import com.prac.music.domain.board.dto.UpdateRequestDto;
import com.prac.music.domain.board.dto.UpdateResponseDto;
import com.prac.music.domain.board.entity.Board;
import com.prac.music.domain.board.repository.BoardRepository;
import com.prac.music.domain.user.entity.User;
import com.prac.music.domain.user.repository.UserRepository;
import com.prac.music.exception.BoardNotFoundException;
import com.prac.music.exception.UnauthorizedAccessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;
	private final UserRepository userRepository;

	@Transactional
	public BoardResponseDto createBoard(BoardRequestDto requestDto, User user) {
		User persistentUser = findUserById(user.getId());
		Board board = Board.builder()
			.title(requestDto.getTitle())
			.contents(requestDto.getContents())
			.user(persistentUser)
			.build();

		Board savedBoard = boardRepository.save(board);
		return new BoardResponseDto(savedBoard);
	}

	@Transactional
	public UpdateResponseDto updateBoard(Long id, UpdateRequestDto requestDto, User user) {
		Board board = findBoardById(id);
		User persistentUser = findUserById(user.getId());

		validateUserAuthorization(board, persistentUser);

		board.update(requestDto.getTitle(), requestDto.getContents());
		Board updatedBoard = boardRepository.save(board);
		return new UpdateResponseDto(updatedBoard);
	}

	@Transactional
	public void deleteBoard(Long id, User user) {
		Board board = findBoardById(id);
		User persistentUser = findUserById(user.getId());

		validateUserAuthorization(board, persistentUser);

		boardRepository.delete(board);
	}

	@Transactional(readOnly = true)
	public List<BoardResponseDto> getAllBoard() {
		return boardRepository.findAll().stream()
			.map(BoardResponseDto::new)
			.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<BoardResponseDto> paging(Pageable pageable) {
		Page<Board> boards = boardRepository.findAllByOrderByCreatedAtDesc(pageable);
		return boards.map(BoardResponseDto::new);
	}

	@Transactional(readOnly = true)
	public BoardResponseDto getBoardById(Long id) {
		Board board = findBoardByIdWithComments(id);
		return new BoardResponseDto(board);
	}

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BoardNotFoundException("사용자 ID " + userId + "를 찾을 수 없습니다."));
	}

	private Board findBoardById(Long boardId) {
		return boardRepository.findById(boardId)
			.orElseThrow(() -> new BoardNotFoundException("게시물 ID " + boardId + "를 찾을 수 없습니다."));
	}

	private Board findBoardByIdWithComments(Long boardId) {
		return boardRepository.findByIdWithComments(boardId)
			.orElseThrow(() -> new BoardNotFoundException("게시물 ID " + boardId + "를 찾을 수 없습니다."));
	}

	private void validateUserAuthorization(Board board, User user) {
		if (!board.getUser().equals(user)) {
			throw new UnauthorizedAccessException("권한이 없습니다.");
		}
	}
}
