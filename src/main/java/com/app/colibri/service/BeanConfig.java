package com.app.colibri.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.app.colibri.model.User;
import com.app.colibri.registry.TagRegistry;
import com.app.colibri.service.crypt.Password;

@Configuration
public class BeanConfig {

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TagRegistry tagRegistry() {
		return new TagRegistry();
	}

	@Bean
	@Qualifier(value = "defaultUser")
	public User defaultUser() {
		return new User("guest", Password.hashPassword("guest"));
	}

}
