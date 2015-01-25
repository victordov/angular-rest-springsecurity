package net.dontdrinkandroot.example.angularrestspringsecurity.configuration;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import net.dontdrinkandroot.example.angularrestspringsecurity.resources.auth.AuthenticationTokenProcessingFilter;
import net.dontdrinkandroot.example.angularrestspringsecurity.resources.auth.UnauthorizedEntryPoint;
import net.dontdrinkandroot.example.angularrestspringsecurity.services.DataBaseInitializerService;
import net.dontdrinkandroot.example.angularrestspringsecurity.services.NewsService;
import net.dontdrinkandroot.example.angularrestspringsecurity.services.UsrService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.http.HttpMethod;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.Properties;

import static net.dontdrinkandroot.example.angularrestspringsecurity.ConfigProperties.*;


@Configuration
@ComponentScan(basePackages = {BASE_PACKAGES, "net.dontdrinkandroot.example.angularrestspringsecurity.services"})
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = {"net.dontdrinkandroot.example.angularrestspringsecurity.repositories"})
@EnableWebSecurity
@PropertySource(PROPERTY_SOURCE)
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private Environment env;
    
    @Autowired
    private AuthenticationManagerBuilder auth;

    //DATABASE SETUP

    public AppConfig() {
        // TODO Auto-generated constructor stub
    }
    
    @Bean(destroyMethod = "close",name = "dataSource")
    DataSource getDataSource() {
        HikariConfig dataSourceConfig = new HikariConfig();
        dataSourceConfig.setDriverClassName(env.getRequiredProperty(DB_DRIVER));
        dataSourceConfig.setJdbcUrl(env.getRequiredProperty(DB_URL));
        dataSourceConfig.setUsername(env.getRequiredProperty(DB_USER_NAME));
        dataSourceConfig.setPassword(env.getRequiredProperty(DB_PASSWORD));
        
        return new HikariDataSource(dataSourceConfig);
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        
        entityManagerFactoryBean.setDataSource(getDataSource());
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
        entityManagerFactoryBean.setPackagesToScan(ENTITY_PACKAGE);

        Properties jpaProperties = new Properties();

        jpaProperties.put(HIBERNATE_DIALECT, env.getRequiredProperty(HIBERNATE_DIALECT));
        jpaProperties.put(HIBERNATE_HBM2DDL_AUTO, env.getRequiredProperty(HIBERNATE_HBM2DDL_AUTO));
        jpaProperties.put(HIBERNATE_EJB_NAMING_STRATEGY, env.getRequiredProperty(HIBERNATE_EJB_NAMING_STRATEGY));
        jpaProperties.put(HIBERNATE_SHOW_SQL, env.getRequiredProperty(HIBERNATE_SHOW_SQL));
        jpaProperties.put(HIBERNATE_FORMAT_SQL, env.getRequiredProperty(HIBERNATE_FORMAT_SQL));

        entityManagerFactoryBean.setJpaProperties(jpaProperties);
        return entityManagerFactoryBean;
    }

    @Bean(name = "transactionManager")
    JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory);
        return transactionManager;
    }

    @Bean(name = "usrService")
    public UserDetailsService usrService() {
        return new UsrService();
    }
    
    @Bean
    public NewsService newService() {
        return new NewsService();
    }

    @Bean(name = "dataBaseInitializer", initMethod = "initDataBase")
    @DependsOn({"usrService", "newService", "passwordEncoder"})
    public DataBaseInitializerService dataBaseInitializer() {
        return new DataBaseInitializerService();
    }

//    INIT REST COMPONENTS

    @Bean(name = "objectMapper")
    public ObjectMapper getObjectMapper() {
        return new ObjectMapper();
    }

    //	 SPRING SECURITY SETUP

    @Bean(name = "passwordEncoder")
    public StandardPasswordEncoder getPasswordEncoder() {
        return new StandardPasswordEncoder("ThisIsASecretSoChangeMe");
    }

    @Bean(name = "authenticationManager")
    @DependsOn({"usrService", "passwordEncoder"})
    public AuthenticationManager authenticationManager() {
        try {
            auth.userDetailsService(usrService()).passwordEncoder(getPasswordEncoder());
            AuthenticationManager authenticationManager = auth.build();
            return authenticationManager;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic().realmName("Protected API");
        http.csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .httpBasic()
                .authenticationEntryPoint(getUnauthorizedEntryPoint())
                .and()
                .addFilterBefore(getAuthenticationTokenProcessingFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests().antMatchers("/**").access("permitAll")
                .antMatchers("/rest/user/authenticate").access("permitAll")
                .antMatchers(HttpMethod.GET, "/rest/news/**")
                .access("hasRole('user')")
                .antMatchers(HttpMethod.PUT, "/rest/news/**")
                .access("hasRole('admin')")
                .antMatchers(HttpMethod.POST, "/rest/news/**")
                .access("hasRole('admin')")
                .antMatchers(HttpMethod.DELETE, "/rest/news/**")
                .access("hasRole('admin')").anyRequest().authenticated();
    }

    @Bean(name = "unauthorizedEntryPoint")
    public UnauthorizedEntryPoint getUnauthorizedEntryPoint() {
        return new UnauthorizedEntryPoint();
    }

    @Bean(name = "authenticationTokenProcessingFilter")
    @DependsOn("usrService")
    public AuthenticationTokenProcessingFilter getAuthenticationTokenProcessingFilter() {
        return new AuthenticationTokenProcessingFilter(usrService());
    }
}