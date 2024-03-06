package com.kkd.myweb.domain.admin.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import com.kkd.myweb.domain.admin.entity.AdminUser;

@DataJpaTest
@ContextConfiguration(classes = AdminUserRepositoryTest.class)
@EnableJpaRepositories(basePackages = {"com.kkd.myweb.domain.admin.repository"})
@EntityScan("com.kkd.myweb.domain.admin")
public class AdminUserRepositoryTest {
	@Autowired
	private AdminUserRepository adminUserRepository;

	@Test
	void saveAdminUser() {
		AdminUser adminUser = new AdminUser();
		adminUser.setAuid(UUID.randomUUID().toString());
		adminUser.setUsername("testuser");
		adminUser.setPassword("1234");

		AdminUser saveAdminUser = adminUserRepository.save(adminUser);

		assertThat(adminUser).isEqualTo(saveAdminUser);
	}
}
