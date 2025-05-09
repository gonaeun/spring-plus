package org.example.expert.domain.todo.repository;

import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

// 기본 CRUD용 repository
public interface TodoRepository extends JpaRepository<Todo, Long>, TodoRepositoryCustom {

    @Query("SELECT t FROM Todo t LEFT JOIN FETCH t.user u ORDER BY t.modifiedAt DESC")
    Page<Todo> findAllByOrderByModifiedAtDesc(Pageable pageable);

//    @Query("SELECT t FROM Todo t " +
//            "LEFT JOIN t.user " +
//            "WHERE t.id = :todoId")
//    Optional<Todo> findByIdWithUser(@Param("todoId") Long todoId);
    // 기존의 JPQL 삭제하는 과정
    // JPQL로 작성된 메서드를 제거하면 QueryDSL 방식으로 완전히 바뀌어서 n+1문제 방지할 수 있음!
    // findByIdWithUser 메서드는 커스텀인터페이스에서 이미 정의되었기 때문에 여기에서 선언하지 않고도 사용할 수 있음
}
