package com.app.colibri.controller;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import com.app.colibri.registry.TagRegistry;

@Configuration
public class BeanConfig {

	@Bean
	@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
	public TagRegistry tagRegistry() {
		return new TagRegistry();
	}

}
