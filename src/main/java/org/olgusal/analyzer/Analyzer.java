package org.olgusal.analyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class Analyzer {

	public static final String DATA_DIRECTORY = "/Users/jak/olgusal/data";
	public static final String SOLR_SERVER = "http://olgusal.cloudapp.net:8080/solr";
	public static final String SOLR_ID = "id";
	public static final String SOLR_CITY = "city";
	public static final String SOLR_TEXT = "text";

	public static void main(String[] args) throws IOException, SolrServerException {
		System.out.println("Analyze");

		Analyzer.sendAll(DATA_DIRECTORY);
	}

	private static void parseFiles(String directory) throws IOException {
		File dir = new File(directory);
		for (File file : dir.listFiles()) {
			InputStream is = new FileInputStream(file);
			String fileContent = IOUtils.toString(is);
			is.close();
			System.out.println(fileContent);
		}
	}

	private static void sendAll(String directory) throws IOException, SolrServerException {
		File dir = new File(directory);
		int i = 0;
		for (File file : dir.listFiles()) {
			InputStream is = new FileInputStream(file);
			String fileContent = IOUtils.toString(is);

			HttpSolrServer server = new HttpSolrServer(SOLR_SERVER);
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(SOLR_ID, i);
			doc.addField(SOLR_CITY, file.toString());
			doc.addField(SOLR_TEXT, fileContent);
			server.add(doc);
			System.out.println("i : " + i);
			if (i % 100 == 0)
				server.commit();
			server.commit();
			i++;
			is.close();
			System.out.println(fileContent);
		}
	}

}
