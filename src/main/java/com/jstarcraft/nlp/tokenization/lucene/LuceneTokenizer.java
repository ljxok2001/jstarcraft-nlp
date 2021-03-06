package com.jstarcraft.nlp.tokenization.lucene;

import java.io.Reader;
import java.io.StringReader;
import java.util.function.Consumer;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Analyzer.TokenStreamComponents;
import org.apache.lucene.analysis.LuceneAdapter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;

import com.jstarcraft.nlp.tokenization.NlpTokenizer;

/**
 * Lucene分词器
 * 
 * @author Birdy
 *
 */
public class LuceneTokenizer implements NlpTokenizer<LuceneToken> {

    private Consumer<Reader> reader;

    private TokenStream stream;

    public LuceneTokenizer(Analyzer analyzer) {
        try (LuceneAdapter lucene = new LuceneAdapter(analyzer)) {
            TokenStreamComponents component = lucene.createComponents("text");
            this.reader = component.getSource();
            this.stream = component.getTokenStream();
        }
    }

    public LuceneTokenizer(Tokenizer tokenizer) {
        this.reader = tokenizer::setReader;
        this.stream = tokenizer;
    }

    @Override
    public Iterable<LuceneToken> tokenize(CharSequence text) {
        try {
            reader.accept(new StringReader(text.toString()));
            stream.reset();
            // TODO 暂时由LuceneToken负责关闭stream
            LuceneToken iterable = new LuceneToken(stream);
            return iterable;
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }

}
