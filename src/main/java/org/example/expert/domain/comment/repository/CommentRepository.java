package org.example.expert.domain.comment.repository;

import org.example.expert.domain.comment.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.user WHERE c.todo.id = :todoId")  // N+1 문제를 방지하기 위해 댓글&유저를 FETCH JOIN으로 조회
    List<Comment> findByTodoIdWithUser(@Param("todoId") Long todoId);
}
