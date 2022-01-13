package com.yespustak.yespustakapp.api.response;

import com.yespustak.yespustakapp.models.Definition;

import java.util.List;

public class DictionaryItem {

    String word;
    List<Meaning> meanings;

    public String getWord() {
        return word;
    }

    public List<Meaning> getMeanings() {
        return meanings;
    }

    public class Meaning {
        String partOfSpeech;
        List<Definition> definitions;

        public String getPartOfSpeech() {
            return partOfSpeech;
        }

        public List<Definition> getDefinitions() {
            return definitions;
        }
    }
}
