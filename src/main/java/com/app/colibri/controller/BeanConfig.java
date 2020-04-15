package com.app.colibri.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.app.colibri.model.Word;

@Configuration
public class BeanConfig {

	@Bean
	@Qualifier(value = "allWords")
	public List<Word> allWords() {
		return WordController.unserializeAllWordsMain();
	}
}
