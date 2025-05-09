package org.example.expert.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QuerydslConfig {

    @PersistenceContext
    private EntityManager entityManager;

    // jpaQueryFactory를 스프링빈으로 등록하여 TodoRepositoryImpl에서 생성자 주입으로 사용하도록 함!!!!!!!
    // 이거 없으면 TodoRepository에서 jpaqueryFactory를 주입 못받아서 오류 발생
    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(entityManager);
    }
}
