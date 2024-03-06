package com.kkd.myweb.domain.log.entity;

import java.time.LocalDate;
import java.time.LocalTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(indexes = {
		@Index(name = "IDX_ACCESS_LOG_LOGDATE_TIME", columnList = "logDate,logTime"), 
		@Index(name = "IDX_ACCESS_LOG_REG_LOGDATE", columnList = "regDate,logDate"), 
})
public class AccessLog {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long seq;
	private LocalDate logdate; //PARTITION BY RANGE (TO_DAYS(logDate))
	private LocalTime logTime;
	private String uid;
	private LocalDate regDate;
}
