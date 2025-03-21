package com.example.gateway.repository;

import com.example.gateway.model.RequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<RequestEntity, Long> {

    @Query("select distinct r.sessionId from RequestEntity r where r.producerId = :producerId and r.sessionId is not null")
    List<Long> findDistinctSessionIdsByProducerId(String producerId);

    @Query("select r.requestId from RequestEntity r where r.sessionId = :sessionId")
    List<String> findRequestIdsBySessionId(Long sessionId);

    boolean existsBySessionId(Long sessionId);


}
