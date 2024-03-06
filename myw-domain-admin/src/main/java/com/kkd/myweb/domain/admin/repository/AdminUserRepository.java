package com.kkd.myweb.domain.admin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kkd.myweb.domain.admin.entity.AdminUser;

public interface AdminUserRepository extends JpaRepository<AdminUser, String>{

	Optional<AdminUser> findByUsername(String username);

}
