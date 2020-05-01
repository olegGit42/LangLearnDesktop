package com.app.colibri.registry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.app.colibri.model.Word;

import lombok.Data;

@Component
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Data
public class UserWordRegistry implements Serializable {
	private static final long serialVersionUID = -6921273139257920727L;

	@Autowired
	private TagRegistry tagRegistry;
	private List<Word> allUserWordsList = new ArrayList<>();

	public UserWordRegistry() {
	};

}
