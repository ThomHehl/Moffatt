package com.heavyweightsoftware.biblehtml;

import com.heavyweightsoftware.biblehtml.bible.BibleBook;

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
        File outputDirectory = fileList.get(0).getParentFile();
        UsfmWriter usfmWriter = new UsfmWriter(outputDirectory);

        fileList.forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                BibleBook bibleBook = bibleReader.readBook(file);
                usfmWriter.write(bibleBook);
            }
        });
    }

    public static void main(String[] args) {
        File dir = new File("./html");
        BibleHtml bibleHtml = new BibleHtml(dir);
        bibleHtml.readBooks();
    }
}
