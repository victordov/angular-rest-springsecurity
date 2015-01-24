package net.dontdrinkandroot.example.angularrestspringsecurity.configuration;

import net.dontdrinkandroot.example.angularrestspringsecurity.dao.DataBaseInitializer;
import net.dontdrinkandroot.example.angularrestspringsecurity.dao.newsentry.JpaNewsEntryDao;
import net.dontdrinkandroot.example.angularrestspringsecurity.dao.user.JpaUserDao;
import net.dontdrinkandroot.example.angularrestspringsecurity.rest.AuthenticationTokenProcessingFilter;
import net.dontdrinkandroot.example.angularrestspringsecurity.rest.UnauthorizedEntryPoint;

import org.apache.commons.dbcp.BasicDataSource;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpMethod;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@ComponentScan(basePackages = "net.dontdrinkandroot.example.angularrestspringsecurity")
@EnableTransactionManagement
@EnableWebSecurity
public class AppConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private AuthenticationManagerBuilder auth;

    //DATABASE SETUP
            
    public AppConfig() {
        // TODO Auto-generated constructor stub
    }

    @Bean(name = "dataSource")
    public BasicDataSource getDataSource() {
        BasicDataSource ds = new BasicDataSource();
        ds.setDriverClassName("org.hsqldb.jdbcDriver");
        ds.setUrl("jdbc:hsqldb:mem:example");
        ds.setUsername("sa");
        ds.setPassword("");
        return ds;
    }

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean entityManagerFactory = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactory.setDataSource(getDataSource());
        entityManagerFactory
                .setPackagesToScan("net.dontdrinkandroot.example.angularrestspringsecurity.entity");

        HibernateJpaVendorAdapter jpaAdaptor = new HibernateJpaVendorAdapter();
        jpaAdaptor.setShowSql(true);
        jpaAdaptor.setGenerateDdl(true);
        jpaAdaptor.setDatabase(Database.HSQL);

        entityManagerFactory.setJpaVendorAdapter(jpaAdaptor);
        return entityManagerFactory;
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager getTransactionManager() {
        return new JpaTransactionManager(entityManagerFactory().getObject());
    }

    @Bean
    public net.dontdrinkandroot.example.angularrestspringsecurity.dao.user.UserDao userDao() {
        return new JpaUserDao();
    }

    @Bean
    public net.dontdrinkandroot.example.angularrestspringsecurity.dao.newsentry.NewsEntryDao newsEntryDao() {
        return new JpaNewsEntryDao();
    }

    @Bean(name = "dataBaseInitializer", initMethod = "initDataBase")
    @DependsOn({ "userDao", "newsEntryDao","passwordEncoder" })
    public DataBaseInitializer dataBaseInitializer() {
        return new DataBaseInitializer(userDao(), newsEntryDao(), getPasswordEncoder());
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
    @DependsOn({ "userDao", "passwordEncoder" })
    public AuthenticationManager authenticationManager() {
        try {
            auth.userDetailsService(userDao()).passwordEncoder(
                    getPasswordEncoder());
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
                .addFilterBefore(getAuthenticationTokenProcessingFilter(),
                        UsernamePasswordAuthenticationFilter.class)
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
    @DependsOn("userDao")
    public AuthenticationTokenProcessingFilter getAuthenticationTokenProcessingFilter() {
        return new AuthenticationTokenProcessingFilter(userDao());
    }
}