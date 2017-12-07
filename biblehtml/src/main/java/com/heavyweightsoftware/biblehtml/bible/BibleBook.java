package com.heavyweightsoftware.biblehtml.bible;

import java.util.ArrayList;
import java.util.List;

public class BibleBook {
    private List<BibleChapter>          chapters = new ArrayList<>();
    private String                      name;

    public List<BibleChapter> getChapters() {
        return chapters;
    }

    public void addChapter(BibleChapter chapter) {
        chapters.add(chapter);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
