package com.hse.fileanalysisservice.service;

import com.hse.fileanalysisservice.model.AnalysisResult;
import org.springframework.stereotype.Service;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class TextProcessingService {

    private static final Pattern PARAGRAPH_SPLIT_PATTERN = Pattern.compile("(\\r\\n|\\r|\\n){2,}");
    private static final Pattern WORD_SPLIT_PATTERN = Pattern.compile("[\\s\\p{Punct}]+");


    public void calculateStatistics(AnalysisResult analysisResult, String content) {
        if (content == null || content.isBlank()) {
            analysisResult.setParagraphCount(0);
            analysisResult.setWordCount(0);
            analysisResult.setCharacterCount(0);
            return;
        }

        analysisResult.setCharacterCount(content.length());
        
        String[] paragraphs = PARAGRAPH_SPLIT_PATTERN.split(content.trim());
        long paragraphCount = Arrays.stream(paragraphs)
                                     .map(String::trim)
                                     .filter(p -> !p.isEmpty())
                                     .count();
        analysisResult.setParagraphCount((int) paragraphCount);

        String[] words = WORD_SPLIT_PATTERN.split(content.trim());
        long wordCount = Arrays.stream(words)
                               .filter(word -> !word.isEmpty())
                               .count();
        analysisResult.setWordCount((int) wordCount);
    }

    public String getTextForWordCloud(String content) {
        if (content == null) return "";
        return content.replaceAll(WORD_SPLIT_PATTERN.pattern(), " ").trim();
    }
}