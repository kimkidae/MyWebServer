package com.kkd.myweb.domain.log.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkd.myweb.domain.log.entity.AccessLog;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {

}
