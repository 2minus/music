package com.prac.music.domain.board.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.prac.music.domain.board.entity.Board;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
	@Query("SELECT b FROM Board b LEFT JOIN FETCH b.comments WHERE b.id = :boardId")
	Optional<Board> findByIdWithComments(@Param("boardId") Long boardId);

	Page<Board> findAllByOrderByCreatedAtDesc(Pageable pageable);
}