package com.yespustak.yespustakapp.models;

import java.util.List;

public class Definition {

    String partOfSpeech;
    String definition;
    String example;
    List<String> synonyms;

    public String getPartOfSpeech() {
        return partOfSpeech;
    }

    public void setPartOfSpeech(String partOfSpeech) {
        this.partOfSpeech = partOfSpeech;
    }

    public String getDefinition() {
        return definition;
    }

    public String getExample() {
        return example;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }
}
