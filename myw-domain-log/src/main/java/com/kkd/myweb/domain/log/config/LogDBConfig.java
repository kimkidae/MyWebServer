package com.kkd.myweb.domain.log.config;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import javax.sql.DataSource;

import org.apache.shardingsphere.driver.api.ShardingSphereDataSourceFactory;
import org.apache.shardingsphere.infra.config.algorithm.AlgorithmConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.api.rule.ReadwriteSplittingDataSourceRuleConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateProperties;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateSettings;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.kkd.myweb.domain.log.config.sharding.LogShardProperties;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@EnableTransactionManagement
@EnableJpaRepositories(
		basePackages = {"com.kkd.myweb.domain.log"}
		,entityManagerFactoryRef = "logEntityManagerFactory"
		,transactionManagerRef = "logTransactionManager"
)
@Configuration
public class LogDBConfig {

    @Bean
    @ConfigurationProperties("spring.jpa.log")
    JpaProperties logJpaProperties() {
		return new JpaProperties();
	}

    @Bean
    @ConfigurationProperties("spring.jpa.log.hibernate")
    HibernateProperties logHibernateProperties() {
		return new HibernateProperties();
	}

	@Autowired
	private LogShardProperties logShardProperties;

	@Bean
    DataSource shardDataSource() throws SQLException {
		if(logShardProperties.getLogs() == null || logShardProperties.getLogs().isEmpty())
			throw new RuntimeException("log slave shardDataSource is empty");

		Map<String, DataSource> dataSources = new HashMap<>();
		for(var sharDataSource : logShardProperties.getLogs()) {
			log.debug("name:{}, usewrname:{}", sharDataSource.getName(), sharDataSource.getHikari().getUsername());
			dataSources.put(sharDataSource.getName(), sharDataSource.getHikari());
			for(var slaveDataSource : sharDataSource.getSlaves()) {
				dataSources.put(slaveDataSource.getName(), slaveDataSource.getHikari());
			}
		}

		//TODO shardingsphere 5.4.2 나오면 snakeymal 2.0 호환 확인 필요

        ReadwriteSplittingDataSourceRuleConfiguration ds1 = new ReadwriteSplittingDataSourceRuleConfiguration("myw_log1_ds", "myw_log1", Arrays.asList("myw_log1_read1"), "random_lb");
        ReadwriteSplittingDataSourceRuleConfiguration ds2 = new ReadwriteSplittingDataSourceRuleConfiguration("myw_log2_ds", "myw_log2", Arrays.asList("myw_log2_read1"), "random_lb");
        Map<String, AlgorithmConfiguration> algorithmConfigMap = new HashMap<>(1);
        algorithmConfigMap.put("random_lb", new AlgorithmConfiguration("RANDOM", new Properties()));

        ReadwriteSplittingRuleConfiguration ruleConfig = new ReadwriteSplittingRuleConfiguration(Arrays.asList(ds1, ds2), algorithmConfigMap);

        Properties props = new Properties();
        props.setProperty("sql-show", Boolean.TRUE.toString());

        return ShardingSphereDataSourceFactory.createDataSource(dataSources, Collections.singleton(ruleConfig), props);
	}

//	
//	public DataSource getDataSource() throws SQLException {
//        ReadwriteSplittingDataSourceRuleConfiguration dataSourceConfig = new ReadwriteSplittingDataSourceRuleConfiguration("demo_read_query_ds", "demo_write_ds", Arrays.asList("demo_read_ds_0", "demo_read_ds_1"), "random_lb");
//        Map<String, AlgorithmConfiguration> algorithmConfigMap = new HashMap<>(1);
//        algorithmConfigMap.put("demo_weight_lb", new AlgorithmConfiguration("WEIGHT", new Properties()));
//        ReadwriteSplittingRuleConfiguration ruleConfig = new ReadwriteSplittingRuleConfiguration(Collections.singleton(dataSourceConfig), algorithmConfigMap);
//        
//        Properties props = new Properties();
//        props.setProperty("sql-show", Boolean.TRUE.toString());
//        return ShardingSphereDataSourceFactory.createDataSource(createDataSourceMap(), Collections.singleton(ruleConfig), props);
//    }
//    
//    private Map<String, DataSource> createDataSourceMap() {
//        Map<String, DataSource> result = new HashMap<>(3, 1);
//        result.put("demo_write_ds", DataSourceUtil.createDataSource("demo_write_ds"));
//        result.put("demo_read_ds_0", DataSourceUtil.createDataSource("demo_read_ds_0"));
//        result.put("demo_read_ds_1", DataSourceUtil.createDataSource("demo_read_ds_1"));
//        return result;
//    }
	
    @Bean
    LocalContainerEntityManagerFactoryBean logEntityManagerFactory(
            @Qualifier("shardDataSource") DataSource shardDataSource,
            @Qualifier("logHibernateProperties") HibernateProperties logHibernateProperties,
            @Qualifier("logJpaProperties") JpaProperties logJpaProperties,
            EntityManagerFactoryBuilder builder) {

		var properties = logHibernateProperties.determineHibernateProperties(logJpaProperties.getProperties(), new HibernateSettings());

		return builder
				.dataSource(shardDataSource)
				.properties(properties)
				.packages("com.kkd.myweb.domain.log")	// entity packages
				.persistenceUnit("logUnit")
				.build();
	}

    @Bean
    PlatformTransactionManager logTransactionManager(@Qualifier("logEntityManagerFactory") LocalContainerEntityManagerFactoryBean logEntityManagerFactory) {
		return new JpaTransactionManager(Objects.requireNonNull(logEntityManagerFactory.getObject()));
	}

//
//    private Collection<TableRuleConfiguration> getTableRoleConfigs() {
//		var shardingAlgorithm = new CustomShardingAlgorithm(logShardProperties.getPrefix(), logShardProperties.getLogs().size());
//		var configs = new ArrayList<TableRuleConfiguration>();
//
//		String workerId = getWorkerId();
//		//access log table
//		configs.add(createTableRuleConfig("access_log", "seq", "uid", shardingAlgorithm, workerId));
//
//		return configs;
//	}
//
//	private TableRuleConfiguration createTableRuleConfig(String tableName, String primaryKeyName, String shardingKeyName, PreciseShardingAlgorithm<String> shardingAlgorithm, String workerId) {
//		TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration(tableName);
//
//		Properties properties = new Properties();
//		properties.setProperty("worker.id", workerId); // 프로세스별로 다른 ID값으로 지정
//
//		tableRuleConfig.setKeyGeneratorConfig(new KeyGeneratorConfiguration("SNOWFLAKE", primaryKeyName, properties));
//		tableRuleConfig.setDatabaseShardingStrategyConfig(new StandardShardingStrategyConfiguration(shardingKeyName, shardingAlgorithm));
//
//		return tableRuleConfig;
//	}
//
//	private List<MasterSlaveRuleConfiguration> getMasterSlaveRuleConfigurations() {
//		List<MasterSlaveRuleConfiguration> masterSlaveRuleConfigs = new ArrayList<>();
//		for(var logs : logShardProperties.getLogs()) {
//			var slaveNames = logs.getSlaves().stream().map(value -> value.getName()).collect(Collectors.toList());
//	        MasterSlaveRuleConfiguration masterSlaveRuleConfig = new MasterSlaveRuleConfiguration(logs.getName(), logs.getName(), slaveNames);
//	        masterSlaveRuleConfigs.add(masterSlaveRuleConfig);
//		}
//		return masterSlaveRuleConfigs;
//    }

	private String getWorkerId() {
        try {
            InetAddress ip = InetAddress.getLocalHost();
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            long id;
            if (network == null || network.getHardwareAddress() == null) {
            	return String.valueOf((0x0000FFFF & ThreadLocalRandom.current().nextLong()) >> 6);//10 bit workerId
            }else {
                byte[] address = network.getHardwareAddress();
                id = ((0x000000FF & (long) address[address.length - 1]) | (0x0000FF00 & (((long) address[address.length - 2]) << 8))) >> 6;//10 bit workerId
            }
            return String.valueOf(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
