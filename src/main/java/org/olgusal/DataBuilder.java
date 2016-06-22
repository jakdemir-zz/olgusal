package org.olgusal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Morphia;
import org.olgusal.pojo.Sentence;
import org.olgusal.pojo.Term;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import zemberek.core.io.SimpleTextReader;
import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.apps.TurkishSentenceParser;
import zemberek.morphology.apps.UnidentifiedTokenParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.morphology.parser.SentenceMorphParse;
import zemberek.tokenizer.ZemberekLexer;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;

public class DataBuilder {
	static Logger logger = LoggerFactory.getLogger(DataBuilder.class);

	public static final String DATA_DIRECTORY = "./data";

	public static void test1() throws IOException {
		String test = "istanbul'a Kardeşlerim internet internete internette interneti internetteki internet'te merhabalar, benim batırdınız ADIM kendimize taçlandırmaya ona duygulandı omurgalıyız zamanla kemal jak ben montajlarla chp'li çıkartmayacaksın gelin istiklalimize kıskanıyorlar mescidinden uğurlamayla darbukayla ilerleteceğiz cezaevleri sevdan zehirleyerek";
		ZemberekLexer lexer = new ZemberekLexer();
		List<String> wordList = lexer.tokenStrings(test);
		// TurkishMorphParser parser = TurkishMorphParser.builder().addTextDictFiles(new File("/Users/jak/Desktop/jak_master_v2.txt")).build();
		// TurkishMorphParser parser = TurkishMorphParser.builder().addTextDictFiles(new File("/Users/jak/Desktop/master-dictionary.dict.txt")).build();
		// TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("/Users/jak/Desktop/master-dictionary.dict.txt"),new File("/Users/jak/Desktop/bigdict.txt"),new File("/Users/jak/Desktop/top-20K-words.txt")).build();
		// TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("/Users/jak/Desktop/master-dictionary.dict.txt")).build();
		// TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("/Users/jak/Desktop/bigdict.txt")).build();

		TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("./resources/top-20K-words.txt")).build();
		// TurkishMorphParser parser = TurkishMorphParser.createWithDefaults();
		UnidentifiedTokenParser uiParser = new UnidentifiedTokenParser(parser);

		for (String word : wordList) {
			String wordNormalized = parser.normalize(word);
			List<MorphParse> parses = parser.parse(wordNormalized);
			if (parses.size() != 0) {
				for (MorphParse morphParse : parses) {
					logger.info(" word orig: " + word + ", normalize : " + wordNormalized + ", lemma : " + morphParse.getLemma() + ", root :"
							+ morphParse.root);
				}
			} else {
				List<MorphParse> specialWordList = uiParser.parse(wordNormalized);

				if (specialWordList.size() != 0) {
					for (MorphParse morphParse : specialWordList) {
						logger.info(" word orig: " + word + ", normalize : " + wordNormalized + ", lemma : " + morphParse.getLemma() + ", root :"
								+ morphParse.root);
					}
				} else {
					logger.info("Unidentified word : " + word);
				}

			}
		}
	}

	public static void test2() throws IOException {
		String sentence = "Biz kan dursun diye çırpınıyoruz, 30 yılda 40 binden fazla maalesef ölüme neden olan terör artık bitsin diyoruz";
		TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("./resources/top-20K-words.txt")).build();
		Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
		TurkishSentenceParser sentenceParser = new TurkishSentenceParser(parser, disambiguator);

		SentenceMorphParse sentenceParse = sentenceParser.parse(sentence);
		for (SentenceMorphParse.Entry entry : sentenceParse) {
			String word = entry.input;
			String wordNormalized = "";

			if (entry.parses.size() != 0) {

				for (MorphParse morphParse : entry.parses) {
					logger.info(" word orig: " + word + ", normalize : " + wordNormalized + ", lemma : " + morphParse.getLemma() + ", root :"
							+ morphParse.root);

				}
			}
		}
	}

	private static MorphParse getShortestWord(List<MorphParse> wordList) {

		String shortestWord = "";
		MorphParse result = null;
		for (MorphParse morphParse : wordList) {
			if (shortestWord.equals("") || morphParse.getLemma().length() < shortestWord.length()) {
				shortestWord = morphParse.getLemma();
				result = morphParse;
			}
		}
		return result;
	}

	// SentenceBuilder
	// id- sentence - doc
	public static List<Sentence> getZemberekSentenceList() throws IOException {
		File dir = new File(DATA_DIRECTORY);
		List<Sentence> sentenceList = new ArrayList<Sentence>();

		for (File file : dir.listFiles()) {
			String fileName = file.getName().replaceAll(".", "");
			fileName = fileName.replaceAll("-", "");

			List<String> sentences = SimpleTextReader.trimmingUTF8Reader(file).asStringList();

			for (String sentence : sentences) {
				Sentence sentenceObj = new Sentence();
				sentenceObj.setSentence(sentence);
				sentenceObj.setDocument(fileName);

				sentenceList.add(sentenceObj);
			}
		}

		return sentenceList;

	}

	public static List<Sentence> getSentenceList() throws IOException {

		File dir = new File(DATA_DIRECTORY);
		List<Sentence> sentenceList = new ArrayList<Sentence>();

		for (File file : dir.listFiles()) {
			String filePath = file.getPath();
			String fileName = file.getName().replaceAll("\\.", "");
			fileName = fileName.replaceAll("-", "");
			InputStream is = new FileInputStream(new File(filePath));
			String fileContent = IOUtils.toString(is);

			String[] sentenceArr = fileContent.split("\n|\\.(?!\\d)|(?<!\\d)\\.");

			for (String sentence : sentenceArr) {
				if (!sentence.equals("") && !sentence.trim().equals("") && !sentence.equals("\\n")) {

					Sentence sentenceObj = new Sentence();
					sentenceObj.setSentence(sentence);
					sentenceObj.setDocument(fileName);
					sentenceList.add(sentenceObj);

				}
			}
		}

		return sentenceList;
	}

	// WordBuilder
	// id(word - hash)- wordstemmed - wordorig- perdoc occurance - total occurance

	public static Map<String, Term> getWordMapOrig() throws IOException {
		// String filePath = "/Users/jak/olgusal/data/rte.txt";

		File dir = new File(DATA_DIRECTORY);

		ZemberekLexer lexer = new ZemberekLexer();
		TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("./resources/top-20K-words.txt")).build();
		UnidentifiedTokenParser uiParser = new UnidentifiedTokenParser(parser);
		Map<String, Term> unsortedMap = new HashMap<String, Term>();

		for (File file : dir.listFiles()) {
			String filePath = file.getPath();
			String fileName = file.getName().replaceAll("\\.", "");
			fileName = fileName.replaceAll("-", "");

			InputStream is = new FileInputStream(new File(filePath));
			String fileContent = IOUtils.toString(is);

			List<String> wordList = lexer.tokenStrings(fileContent);
			// TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("/Users/jak/Desktop/master-dictionary.dict.txt")).build();

			// TurkishMorphParser parser = TurkishMorphParser.createWithDefaults();

			for (String word : wordList) {
				String result = "";
				String wordNormalized = parser.normalize(word);
				List<MorphParse> parses = parser.parse(wordNormalized);
				if (parses.size() == 0 && wordNormalized.length() != 0) {
					parses = uiParser.parse(wordNormalized);
				}

				if (parses.size() != 0) {
					MorphParse morphParse = getShortestWord(parses);
					logger.info(" word orig: " + word + ", normalize : " + wordNormalized + ", lemma : " + morphParse.getLemma() + ", root :"
							+ morphParse.root);
					result = morphParse.root.toLowerCase(Locale.forLanguageTag("tr-TR"));
				} else {
					logger.info("Unidentified word : " + word);
					result = word.toLowerCase(Locale.forLanguageTag("tr-TR"));
				}

				if (unsortedMap.containsKey(result)) {
					unsortedMap.get(result).getOriginalSet().add(word);
					unsortedMap.get(result).setTotalCount(unsortedMap.get(result).getTotalCount() + 1);
					if (unsortedMap.get(result).getDocumentTermCountMap().containsKey(fileName)) {
						unsortedMap.get(result).getDocumentTermCountMap()
								.put(fileName, unsortedMap.get(result).getDocumentTermCountMap().get(fileName) + 1);
					} else {
						unsortedMap.get(result).getDocumentTermCountMap().put(fileName, 1);
					}

				} else {
					Term term = new Term();
					term.setId(ObjectId.massageToObjectId(result));
					term.setRoot(result);
					term.getOriginalSet().add(word);
					term.setTotalCount(1);
					term.getDocumentTermCountMap().put(fileName, 1);
					unsortedMap.put(result, term);
				}

			}

		}

		return unsortedMap;
	}

	public static Map<String, Term> getWordMarkovMap() throws IOException {

		//TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("/Users/jak/Desktop/top-20K-words.txt")).build();
		TurkishMorphParser parser = TurkishMorphParser.newBuilder().addTextDictFiles(new File("./resources/master-dictionary.dict.txt")).build();

		Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
		TurkishSentenceParser sentenceParser = new TurkishSentenceParser(parser, disambiguator);

		Map<String, Term> unsortedMap = new HashMap<String, Term>();

		List<Sentence> sentenceList = getSentenceList();

		for (Sentence sentence : sentenceList) {

			try {
				SentenceMorphParse sentenceParse = sentenceParser.parse(sentence.getSentence());
				logger.info("Sentence : " + sentence.getSentence());
				for (SentenceMorphParse.Entry entry : sentenceParse) {
					String word = entry.input;
					String result = "";
					String wordNormalized = "";

					if (entry.parses.size() != 0 && !entry.parses.get(0).getLemma().equals("UNK")) {
						MorphParse morphParse = entry.parses.get(0);
						logger.info(" word orig: " + word + ", normalize : " + wordNormalized + ", lemma : " + morphParse.getLemma() + ", root :"
								+ morphParse.root);
						result = morphParse.getLemma().toLowerCase(Locale.forLanguageTag("tr-TR"));
					} else {
						logger.info("Unidentified word : " + word);
						result = word.toLowerCase(Locale.forLanguageTag("tr-TR"));
					}

					if (unsortedMap.containsKey(result)) {
						unsortedMap.get(result).getOriginalSet().add(word);
						unsortedMap.get(result).setTotalCount(unsortedMap.get(result).getTotalCount() + 1);
						if (unsortedMap.get(result).getDocumentTermCountMap().containsKey(sentence.getDocument())) {
							unsortedMap.get(result).getDocumentTermCountMap()
									.put(sentence.getDocument(), unsortedMap.get(result).getDocumentTermCountMap().get(sentence.getDocument()) + 1);
						} else {
							unsortedMap.get(result).getDocumentTermCountMap().put(sentence.getDocument(), 1);
						}

					} else {
						Term term = new Term();
						term.setId(ObjectId.massageToObjectId(result));
						term.setRoot(result);
						term.getOriginalSet().add(word);
						term.setTotalCount(1);
						term.getDocumentTermCountMap().put(sentence.getDocument(), 1);
						unsortedMap.put(result, term);
					}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

		return unsortedMap;
	}

	public static void dumpToMongo(Map<String, Term> wordMap) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("olgusal.cloudapp.net", 27017);
		DB db = mongoClient.getDB("localElection");
		DBCollection coll = db.getCollection("wordMapMaster");

		Morphia morphia = new Morphia();
		morphia.map(Term.class);

		Iterator<Entry<String, Term>> it = wordMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Term> pairs = (Map.Entry<String, Term>) it.next();
			DBObject dbObject = morphia.toDBObject(pairs.getValue());
			coll.save(dbObject);
		}
	}

	public static void dumpToMongo(List<Sentence> sentenceList) throws UnknownHostException {
		MongoClient mongoClient = new MongoClient("olgusal.cloudapp.net", 27017);
		DB db = mongoClient.getDB("localElection");
		DBCollection coll = db.getCollection("sentenceMap");

		Morphia morphia = new Morphia();
		morphia.map(Sentence.class);

		for (Sentence sentence : sentenceList) {
			DBObject dbObject = morphia.toDBObject(sentence);
			coll.save(dbObject);
		}
	}

	public static void main(String[] args) throws IOException {

		test1();
		// Map<String, Term> wordMap = getWordMarkovMap();
		 //Util.printTermMap(wordMap);
		 //dumpToMongo(wordMap);
		//
		// // List<Sentence> sentenceList = getZemberekSentenceList();
		// List<Sentence> sentenceList = getSentenceList();
		// dumpToMongo(sentenceList);
	}

}
