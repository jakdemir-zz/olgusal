package org.olgusal.pojo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

//@Entity("corpus")
public class Corpus {
	//@Id ObjectId id;

	private Map<String, Term> termMap = new HashMap<String, Term>();

	private List<String> sentenceList = new ArrayList<String>();

	public Map<String, Term> getTermMap() {
		return termMap;
	}

	public void setTermMap(Map<String, Term> termMap) {
		this.termMap = termMap;
	}

	public List<String> getSentenceList() {
		return sentenceList;
	}

	public void setSentenceList(List<String> sentenceList) {
		this.sentenceList = sentenceList;
	}
}
