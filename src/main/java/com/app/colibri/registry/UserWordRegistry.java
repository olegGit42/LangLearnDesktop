package com.app.colibri.registry;

import java.io.Serializable;
import java.util.List;

import com.app.colibri.model.Word;

import lombok.Data;

@Data
public class UserWordRegistry implements Serializable {

	private static final long serialVersionUID = -6921273139257920727L;
	private List<Word> allUserWordsList;

}
