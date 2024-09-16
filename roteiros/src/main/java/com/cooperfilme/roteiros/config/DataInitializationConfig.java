package com.cooperfilme.roteiros.config;

import com.cooperfilme.roteiros.model.User;
import com.cooperfilme.roteiros.model.UserRole;
import com.cooperfilme.roteiros.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializationConfig {

    @Bean
    public CommandLineRunner initializeData(UserService userService) {
        return args -> {
            if (userService.getAllUsers().isEmpty()) {
                List<User> predefinedUsers = List.of(
                    new User("Analista", "analista@example.com", "password123", UserRole.ANALISTA),
                    new User("Revisor", "revisor@example.com", "password123", UserRole.REVISOR),
                    new User("Aprovador1", "aprovador1@example.com", "password123", UserRole.APROVADOR),
                    new User("Aprovador2", "aprovador2@example.com", "password123", UserRole.APROVADOR),
                    new User("Aprovador3", "aprovador3@example.com", "password123", UserRole.APROVADOR)
                );

                predefinedUsers.forEach(userService::saveUser);

                System.out.println("Predefined users created successfully.");
            }
        };
    }
}