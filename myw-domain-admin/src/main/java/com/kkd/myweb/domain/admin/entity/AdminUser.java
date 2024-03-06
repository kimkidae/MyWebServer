package com.kkd.myweb.domain.admin.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class AdminUser {
	@Id
	@Column(length = 50)
	private String auid;

	@Column(length = 50, unique = true)
	private String username;

	@Column(nullable = false)
	private String password;

	//TODO role

	@CreationTimestamp
	private LocalDateTime insertDt;

	@UpdateTimestamp
	private LocalDateTime updateDt;

}
