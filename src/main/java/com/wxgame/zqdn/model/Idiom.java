package com.wxgame.zqdn.model;

import java.util.HashSet;
import java.util.Set;

public class Idiom {

	private String question;

	private Set<String> options = new HashSet<String>();

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public Set<String> getOptions() {
		return options;
	}

}
