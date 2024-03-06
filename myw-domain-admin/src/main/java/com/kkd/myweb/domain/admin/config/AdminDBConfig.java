package com.kkd.myweb.domain.admin.config;

import java.util.Objects;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.support.Repositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.zaxxer.hikari.HikariDataSource;


@EnableJpaRepositories(
		basePackages = {"com.kkd.myweb.domain.admin"}
		,entityManagerFactoryRef = "adminEntityManagerFactory"
		,transactionManagerRef = "adminTransactionManager"
)
@EnableTransactionManagement
@Configuration
public class AdminDBConfig {
    @Primary
    @Bean
    @ConfigurationProperties("spring.jpa.admin")
    JpaProperties adminJpaProperties() {
		return new JpaProperties();
	}

    @Primary
    @Bean
    @ConfigurationProperties("spring.jpa.admin.hibernate")
    HibernateProperties adminHibernateProperties() {
		return new HibernateProperties();
	}

    @Primary
    @ConfigurationProperties("spring.datasource.admin.hikari")
    @Bean
    DataSource adminDataSource() {
		return DataSourceBuilder.create().type(HikariDataSource.class).build();
	}

    @Primary
    @Bean
    LocalContainerEntityManagerFactoryBean adminEntityManagerFactory(
            @Qualifier("adminDataSource") DataSource adminDataSource,
            @Qualifier("adminHibernateProperties") HibernateProperties adminHibernateProperties,
            @Qualifier("adminJpaProperties") JpaProperties adminJpaProperties,
            EntityManagerFactoryBuilder builder) {

		var properties = adminHibernateProperties.determineHibernateProperties(adminJpaProperties.getProperties(), new HibernateSettings());

		return builder
				.dataSource(adminDataSource)
				.properties(properties)
				.packages("com.kkd.myweb.domain.admin")	// entity packages
				.persistenceUnit("adminUnit")
				.build();
	}

	@Primary
	@Bean
	PlatformTransactionManager adminTransactionManager(@Qualifier("adminEntityManagerFactory") LocalContainerEntityManagerFactoryBean adminEntityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(adminEntityManagerFactory.getObject()));
	}

	@Bean
	Repositories getRepositories(ApplicationContext applicationContext) {
		return new Repositories(applicationContext);
	}


}
