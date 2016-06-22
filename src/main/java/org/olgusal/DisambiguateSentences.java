package org.olgusal;

import java.io.IOException;

import zemberek.morphology.ambiguity.Z3MarkovModelDisambiguator;
import zemberek.morphology.apps.TurkishMorphParser;
import zemberek.morphology.apps.TurkishSentenceParser;
import zemberek.morphology.parser.MorphParse;
import zemberek.morphology.parser.SentenceMorphParse;

public class DisambiguateSentences {

    TurkishSentenceParser sentenceParser;

    public DisambiguateSentences(TurkishSentenceParser sentenceParser) {
        this.sentenceParser = sentenceParser;
    }

    void parseAndDisambiguate(String sentence) {
        System.out.println("Sentence  = " + sentence);
        SentenceMorphParse sentenceParse = sentenceParser.parse(sentence);

        System.out.println("Before disambiguation.");
        writeParseResult(sentenceParse);

        System.out.println("\nAfter disambiguation.");
        sentenceParser.disambiguate(sentenceParse);
        writeParseResult(sentenceParse);

    }

    private void writeParseResult(SentenceMorphParse sentenceParse) {
        for (SentenceMorphParse.Entry entry : sentenceParse) {
            System.out.println("Word = " + entry.input);
            for (MorphParse parse : entry.parses) {
                System.out.println(parse.formatLong());
            }
        }
    }

    public static void main(String[] args) throws IOException {
        TurkishMorphParser morphParser = TurkishMorphParser.createWithDefaults();
        Z3MarkovModelDisambiguator disambiguator = new Z3MarkovModelDisambiguator();
        TurkishSentenceParser sentenceParser = new TurkishSentenceParser(
                morphParser,
                disambiguator
        );
//        new DisambiguateSentences(sentenceParser)
//                .parseAndDisambiguate("Kırmızı kalemi al.");
//        
//        new DisambiguateSentences(sentenceParser)
//        .parseAndDisambiguate("istanbul'a Kardeşlerim internet internete internette interneti internetteki internet'te merhabalar, benim batırdınız ADIM kendimize taçlandırmaya ona duygulandı omurgalıyız zamanla kemal jak ben montajlarla chp'li çıkartmayacaksın gelin istiklalimize kıskanıyorlar mescidinden uğurlamayla darbukayla ilerleteceğiz cezaevleri sevdan zehirleyerek");
        new DisambiguateSentences(sentenceParser)
        .parseAndDisambiguate("bizi yıpratmaya geldiyse çağıracaksınız.");
        new DisambiguateSentences(sentenceParser)
        .parseAndDisambiguate("Cumhuriyetin kurulduğu günden bu güne kadar karşılaşıyoruz.");
    }
}
