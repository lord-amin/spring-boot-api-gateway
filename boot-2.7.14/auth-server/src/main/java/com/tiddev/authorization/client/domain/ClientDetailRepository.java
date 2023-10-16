package com.tiddev.authorization.client.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import javax.transaction.Transactional;

public interface ClientDetailRepository extends JpaRepository<ClientDetailsEntity, Long> {
    @Transactional
    @Modifying
    @Query(
            value = "truncate table oauth2_registered_client",
            nativeQuery = true
    )
    void truncateTable();
}