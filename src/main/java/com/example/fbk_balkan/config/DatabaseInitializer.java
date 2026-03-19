//package com.example.fbk_balkan.config;
//
//import org.springframework.boot.ApplicationArguments;
//import org.springframework.boot.ApplicationRunner;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//@Component
//public class DatabaseInitializer implements ApplicationRunner {
//
//    private final JdbcTemplate jdbcTemplate;
//
//    public DatabaseInitializer(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    @Override
//    public void run(ApplicationArguments args) {
//        jdbcTemplate.execute(
//                "CREATE TABLE IF NOT EXISTS persistent_logins (" +
//                        "  username  VARCHAR(64) NOT NULL," +
//                        "  series    VARCHAR(64) PRIMARY KEY," +
//                        "  token     VARCHAR(64) NOT NULL," +
//                        "  last_used TIMESTAMP   NOT NULL" +
//                        ")"
//        );
//    }
//}