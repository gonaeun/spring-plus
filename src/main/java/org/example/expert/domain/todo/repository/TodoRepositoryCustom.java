package org.example.expert.domain.todo.repository;

import java.time.LocalDateTime;
import org.example.expert.domain.todo.entity.Todo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


// 사용자 정의 인터페이스 : 동적 검색용 로직
public interface TodoRepositoryCustom {
    Page<Todo> searchTodos(String weather, LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);
}
