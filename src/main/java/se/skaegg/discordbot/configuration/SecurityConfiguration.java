package se.skaegg.discordbot.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.io.*;
import java.util.Properties;

@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${user.config.file}")
    private String usersFile;

    @Bean
    public UserDetailsService integrationUserDetailsService() throws IOException {
        Properties properties = new Properties();
        File file = new File(usersFile);
        try (InputStream inputStream = new FileInputStream(file)) {
                     //new ByteArrayInputStream(fileStorage.get(configNamespace, "users.properties"))) {
            properties.load(inputStream);
        }
        return new InMemoryUserDetailsManager(properties);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(integrationUserDetailsService()).passwordEncoder(PasswordEncoderFactories.createDelegatingPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers(HttpMethod.GET).permitAll()
                .anyRequest().hasRole("admin")
                .and()
                .httpBasic();
    }

}
