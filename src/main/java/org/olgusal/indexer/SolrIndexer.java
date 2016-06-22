package org.olgusal.indexer;

import java.io.IOException;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

public class SolrIndexer {

	public static final String SOLR_SERVER = "http://olgusal.cloudapp.net:8080/solr";
	public static final String SOLR_ID = "id";
	public static final String SOLR_CITY = "city";
	public static final String SOLR_TEXT = "text";

	private static void sendData() throws SolrServerException, IOException {
		HttpSolrServer server = new HttpSolrServer(SOLR_SERVER);
		for (int i = 0; i < 1000; ++i) {
			SolrInputDocument doc = new SolrInputDocument();
			doc.addField(SOLR_ID, i);
			doc.addField(SOLR_CITY, "konya");
			doc.addField(SOLR_TEXT, "merhaba");
			server.add(doc);
			System.out.println("i : "+i);
			if (i % 100 == 0)
				server.commit();
		}
		server.commit();
	}
	
	public static void main(String[] args) throws SolrServerException, IOException {
		sendData();
	}
}
