package org.example.expert.domain.todo.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.core.types.dsl.*;
import com.querydsl.core.types.Predicate;
import org.example.expert.domain.todo.entity.QTodo;
import org.example.expert.domain.user.entity.QUser;


// Custom에 선언된 메서드를 JPQL로 직접 구현
@Repository
@RequiredArgsConstructor
public class TodoRepositoryImpl implements TodoRepositoryCustom {

    private final EntityManager em;
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<Todo> searchTodos(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {

        // 쿼리 베이스 생성
        StringBuilder jpql = new StringBuilder("SELECT t FROM Todo t JOIN FETCH t.user u WHERE 1=1");  // join fetch로 n+1 방지

        // null이 아닌 경우에만 조회되도록 >> 날씨, 날짜 조건이 없으면 전체 리스트가 조회됨 (동적 조건 필터링!!)
        if (weather != null) {
            jpql.append(" AND t.weather = :weather");
        }
        if (startDate != null) {
            jpql.append(" AND t.modifiedAt >= :startDate");
        }
        if (endDate != null) {
            jpql.append(" AND t.modifiedAt <= :endDate");
        }

        // 쿼리 객체 생성
        TypedQuery<Todo> query = em.createQuery(jpql.toString(), Todo.class);

        // 조건이 null이 아닐 때, 파라미터 바인딩
        if (weather != null) query.setParameter("weather", weather);
        if (startDate != null) query.setParameter("startDate", startDate);
        if (endDate != null) query.setParameter("endDate", endDate);

        // 페이징 처리 (몇번째 데이터부터 조회할지(offset), 몇 개 가져올지)
        query.setFirstResult((int) pageable.getOffset());
        query.setMaxResults(pageable.getPageSize());

        // 결과 조회
        List<Todo> content = query.getResultList();
        // page로 변환
        return new PageImpl<>(content, pageable, content.size());
    }

    // QueryDSL 구현
    @Override
    public Optional<Todo> findByIdWithUser(Long todoId) {
        QTodo todo = QTodo.todo;
        QUser user = QUser.user;

        Todo result = queryFactory
            .selectFrom(todo)
            .leftJoin(todo.user, user).fetchJoin()
            .where(todo.id.eq(todoId))
            .fetchOne();

        return Optional.ofNullable(result);
    }
}
