package com.heavyweightsoftware.biblehtml.bible;

import com.heavyweightsoftware.biblehtml.StudyBibleInfoReader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BibleHtml {

    List<File> fileList = new ArrayList<>();

    public BibleHtml(File htmlDir) {
        for (File file : htmlDir.listFiles() ) {
            String filename = file.getName();
            if(filename.toLowerCase().endsWith(" 1.html")) {
                fileList.add(file);
            }
        }
    }

    public void readBooks() {
        StudyBibleInfoReader bibleReader = new StudyBibleInfoReader();

        fileList.forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                bibleReader.readBook(file);
            }
        });
    }

    public static void main(String[] args) {
        File dir = new File("./html");
        BibleHtml bibleHtml = new BibleHtml(dir);
        bibleHtml.readBooks();
    }
}
