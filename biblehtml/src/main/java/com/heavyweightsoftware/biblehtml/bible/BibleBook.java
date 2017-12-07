package com.heavyweightsoftware.biblehtml.bible;

import java.util.List;

public class BibleBook {
    private List<BibleChapter>          chapters;
    private String                      name;

    public List<BibleChapter> getChapters() {
        return chapters;
    }

    public void setChapters(List<BibleChapter> chapters) {
        this.chapters = chapters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
