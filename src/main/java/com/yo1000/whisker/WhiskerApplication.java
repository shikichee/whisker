package com.yo1000.whisker;

import com.yo1000.whisker.util.Identifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import javax.sql.DataSource;

@SpringBootApplication
public class WhiskerApplication {
	public static void main(String[] args) {
		SpringApplication.run(WhiskerApplication.class, args);
	}

	@Bean
	@Autowired
	public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	@Bean
	public Identifier identifier() {
		return new Identifier();
	}
}
