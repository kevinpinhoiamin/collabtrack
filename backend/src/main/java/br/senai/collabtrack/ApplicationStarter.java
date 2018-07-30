package br.senai.collabtrack;

import java.beans.PropertyVetoException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableAsync;

import com.mchange.v2.c3p0.ComboPooledDataSource;

@SpringBootApplication
@EnableAsync
public class ApplicationStarter {
	
	@Value("${database.drive}")
	private String databaseDrive;
	
	@Value("${database.user}")
	private String databaseUser;
	
	@Value("${database.password}")
	private String databasePassword;
	
	@Value("${database.url}")
	private String databaseUrl;
	
	@Value("${connection.pool.size.min}")
	private int connectionMinPoolSize;
	
	@Value("${connection.pool.size.max}")
	private int connectionMaxPoolSize;
	
	@Value("${connection.helper.threads}")
	private int connectionHelperThreads;
	
	@Value("${connection.test.period}")
	private int connectionTestPeriod;
	
	@Value("${application.mainpackage}")
	private String mainPackage;
	
	@Value("${hibernate.dialect}")
	private String hibernateDialect;
	
	@Value("${hibernate.sql.show}")
	private String hibernateShowSql;
	
	@Value("${hibernate.sql.format}")
	private String hibernateFormatSql;
	
	@Value("${hibernate.hbm2ddl}")
	private String hibernateHbm2ddl;
	
	@Value("${hibernate.statistics}")
	private String hibernateStatistics;
	
	@Value("${hibernate.cache.enable}")
	private String hibernateEnableCache;
	
	@Value("${hibernate.cache.factory}")
	private String hibernateCacheFactory;
	
	public static void main(String[] args) {
		SpringApplication.run(ApplicationStarter.class, args);
	}
	
	@Bean(destroyMethod = "close")
	public DataSource getDataSource() throws PropertyVetoException {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
	    
		// Database
		dataSource.setDriverClass(databaseDrive);
	    dataSource.setUser(databaseUser);
	    dataSource.setPassword(databasePassword);
	    dataSource.setJdbcUrl(databaseUrl);
	    
	    // Connection pool
	    dataSource.setMinPoolSize(connectionMinPoolSize);
	    dataSource.setMaxPoolSize(connectionMaxPoolSize);
	    dataSource.setNumHelperThreads(connectionHelperThreads);
	    dataSource.setIdleConnectionTestPeriod(connectionTestPeriod);

	    return dataSource;
	}
	
	@Bean
	public Statistics statistics(EntityManagerFactory emf) {
		SessionFactory factory = emf.unwrap(SessionFactory.class);
		return factory.getStatistics();
	}
	
	@Bean(name = "entityManagerFactory")
	public LocalContainerEntityManagerFactoryBean getEntityManagerFactory(DataSource dataSource) {
		LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
		
		entityManagerFactory.setPackagesToScan(mainPackage);
		entityManagerFactory.setDataSource(dataSource);

		entityManagerFactory.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

		Properties props = new Properties();

		props.setProperty("hibernate.dialect", hibernateDialect);
		props.setProperty("hibernate.show_sql", hibernateShowSql);
		props.setProperty("hibernate.format_sql", hibernateFormatSql);
		props.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddl);
		props.setProperty("hibernate.generate_statistics", hibernateStatistics);
		props.setProperty("hibernate.cache.use_second_level_cache", hibernateEnableCache);
		props.setProperty("hibernate.cache.use_query_cache", hibernateEnableCache);
		props.setProperty("hibernate.cache.region.factory_class", hibernateCacheFactory);

		entityManagerFactory.setJpaProperties(props);
		return entityManagerFactory;
	}
	
}
