package com.prac.music.domain.board.service;

import com.prac.music.domain.board.dto.BoardRequestDto;
import com.prac.music.domain.board.dto.BoardResponseDto;
import com.prac.music.domain.board.dto.UpdateRequestDto;
import com.prac.music.domain.board.dto.UpdateResponseDto;
import com.prac.music.domain.board.entity.Board;
import com.prac.music.domain.board.entity.BoardFiles;
import com.prac.music.domain.board.repository.BoardFilesRepository;
import com.prac.music.domain.board.repository.BoardRepository;
import com.prac.music.domain.user.entity.User;
import com.prac.music.domain.user.repository.UserRepository;
import com.prac.music.domain.user.service.S3Service;
import com.prac.music.exception.BoardNotFoundException;
import com.prac.music.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {

	private final BoardRepository boardRepository;
	private final UserRepository userRepository;
	private final BoardFilesRepository boardFilesRepository;
	private final S3Service s3Service;

	@Transactional
	public BoardResponseDto createBoard(BoardRequestDto requestDto, User user,List<MultipartFile> files) throws IOException {

		User persistentUser = findUserById(user.getId());
		Board board = Board.builder()
			.title(requestDto.getTitle())
			.contents(requestDto.getContents())
			.user(persistentUser)
			.build();
        boardRepository.save(board);

		if(files.size() > 5) {
			throw new IllegalArgumentException("파일은 5개까지 올릴 수 있습니다.");
		}

		if(files != null && !files.isEmpty()) {
			List<BoardFiles> boardFiles = new ArrayList<>();
			for (MultipartFile file : files) {
				String fileUrl = s3Service.s3Upload(file);


				BoardFiles boardFile = BoardFiles.builder()
						.file(fileUrl)
						.board(board)
						.build();

				boardFiles.add(boardFile);
			}
            boardFilesRepository.saveAll(boardFiles);
		}
		return new BoardResponseDto(board);

	}

	@Transactional
	public UpdateResponseDto updateBoard(Long id, UpdateRequestDto requestDto, User user,List<MultipartFile> files) throws IOException {
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

	private User findUserById(Long userId) {
		return userRepository.findById(userId)
			.orElseThrow(() -> new BoardNotFoundException("사용자 ID " + userId + "를 찾을 수 없습니다."));
	}

	private Board findBoardById(Long boardId) {
		return boardRepository.findById(boardId)
			.orElseThrow(() -> new BoardNotFoundException("게시물 ID " + boardId + "를 찾을 수 없습니다."));
	}

	private void validateUserAuthorization(Board board, User user) {
		if (!board.getUser().equals(user)) {
			throw new UnauthorizedAccessException("권한이 없습니다.");
		}
	}
}
