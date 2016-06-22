package org.olgusal.pojo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

@Entity("term")
public class Term {
	@Id
	ObjectId id;

	private String root;

	private Set<String> originalSet = new HashSet<String>();

	@Embedded("documentCount")
	private Map<String, Integer> documentTermCountMap = new HashMap<String, Integer>();

	private int totalCount;

	public ObjectId getId() {
		return id;
	}

	public void setId(ObjectId id) {
		this.id = id;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public Set<String> getOriginalSet() {
		return originalSet;
	}

	public void setOriginalSet(Set<String> originalSet) {
		this.originalSet = originalSet;
	}

	public Map<String, Integer> getDocumentTermCountMap() {
		return documentTermCountMap;
	}

	public void setDocumentTermCountMap(Map<String, Integer> documentTermCountMap) {
		this.documentTermCountMap = documentTermCountMap;
	}

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

}
