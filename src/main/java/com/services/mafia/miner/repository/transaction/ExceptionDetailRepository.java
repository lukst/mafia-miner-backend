package com.services.mafia.miner.repository.transaction;

import com.services.mafia.miner.entity.transaction.ExceptionDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExceptionDetailRepository extends JpaRepository<ExceptionDetail, Long> {
}