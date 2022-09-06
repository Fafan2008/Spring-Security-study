package com.ivo.my.configs.security;

import com.ivo.my.services.MyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.access.intercept.RunAsImplAuthenticationProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
@EnableAsync
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private MyUserDetailsService myUserDetailsService;
    @Autowired
    private DataSource dataSource;
    @Autowired
    MySecurityFilter mySecurityFilter;
    @Autowired
    private SessionRegistry sessionRegistry;
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    private AppWebAuthenticationDetailsSource appWebAuthenticationDetailsSource;
    @Autowired
    private Google2FAuthProvider google2FAuthProvider;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .addFilterBefore(mySecurityFilter, AnonymousAuthenticationFilter.class)

                .authorizeRequests()
                .antMatchers("/signup",
                        "/user/register",
                        "/registrationConfirm",
                        "/code*",
                        "/isUsing2FA*",
                        "/forgotPassword*",
                        "/user/resetPassword*",
                        "/user/changePassword*",
                        "/user/savePassword*",
                        "/js/**").permitAll()
                .antMatchers("/user/delete/*").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()

                .formLogin()
                .loginPage("/login")
                .permitAll()
                .loginProcessingUrl("/doLogin")
                .defaultSuccessUrl("/profile")
                // was for 2fa auth
//                .authenticationDetailsSource(appWebAuthenticationDetailsSource)
                .and()

                .logout().permitAll().logoutUrl("/doLogout")
                .and()

                .rememberMe().rememberMeCookieName("remember-me-bitch").rememberMeParameter("remember")
                /*.useSecureCookie(true)*/
                .tokenValiditySeconds(604800).tokenRepository(persistentTokenRepository())
                .key("secret-key-word")
                .and()

                .sessionManagement().maximumSessions(1).sessionRegistry(sessionRegistry).and().sessionFixation().none()
                .and()

                .csrf().disable();
    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//         Стандартный путь инициализации аутентификации
//         auth.userDetailsService(myUserDetailsService);

//        Настройка провайдеров аутентификации
        auth.authenticationProvider(runAsAuthenticationProvider())
//                .authenticationProvider(google2FAuthProvider)
                .authenticationProvider(daoAuthenticationProvider());

        // Настройка провайдер менеджера
//        ProviderManager authenticationManager = new ProviderManager(List.of(
//                customAuthenticationProvider,
//                daoAuthenticationProvider(),
//                runAsAuthenticationProvider())
//        );
//        authenticationManager.setEraseCredentialsAfterAuthentication(false);
//        auth.parentAuthenticationManager(authenticationManager);

//        Настройка стореджа для креденшинолов
//        In - Memory User Storage
//        auth.inMemoryAuthentication().
//                withUser("test@email.com").password("pass").roles("USER");

//        JDBC User Storage
//        auth.jdbcAuthentication().dataSource(dataSource).withDefaultSchema();

//        JDBC with MySQL
//        If we want to use MySQL, the withDefaultSchema() API won’t work unfortunately (the script isn’t following MySQL syntax).
//        create schema if not exists schemaName;
//        USE schemaName;
//        create table users(
//                username varchar(50) not null primary key,
//        password varchar(500) not null,
//                enabled boolean not null);
//        create table authorities (
//                username varchar(50) not null,
//                authority varchar(50) not null,
//                constraint fk_authorities_users foreign key(username) references users(username));
//        create unique index ix_auth_username on authorities (username,authority);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    @Qualifier(value = "runAsAuthenticationProvider")
    public AuthenticationProvider runAsAuthenticationProvider() {
        final RunAsImplAuthenticationProvider authProvider = new RunAsImplAuthenticationProvider();
        authProvider.setKey("MyRunAsKey");
        return authProvider;
    }

    @Bean
    @Qualifier(value = "daoAuthenticationProvider")
    public AuthenticationProvider daoAuthenticationProvider() {
        final DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(myUserDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        final JdbcTokenRepositoryImpl jdbcTokenRepository = new JdbcTokenRepositoryImpl();
        jdbcTokenRepository.setDataSource(dataSource);
        return jdbcTokenRepository;
    }
}
