package com.heavyweightsoftware.biblehtml;

import com.heavyweightsoftware.biblehtml.bible.BibleBook;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
    Read files downloaded from studybible.info
 **/
public class StudyBibleInfoReader {

    /**
     * Read a whole book of the Bible
     * @param startingWith the file pointing to the first chapter
     */
    public BibleBook readBook(File startingWith) {
        String bookName = getBookName(startingWith);

        BibleBook bibleBook = new BibleBook();
        bibleBook.setName(bookName);

        return bibleBook;
    }

    private static String getBookName(File startingWith) {
        String name = startingWith.getName();
        int pos = name.indexOf(" 1.html");
        if (pos >= 0) {
            name = name.substring(0, pos);
        }
        else {
            throw new IllegalStateException("Not found:" + name);
        }

        return name;
    }

    private static List<File> getChapterFiles(File startingWith, String bookName) {
        List<File> result = new ArrayList<>();

        for(File file : startingWith.getParentFile().listFiles()) {
            if( file.getName().startsWith(bookName)) {
                result.add(file);
            }
        }

        return result;
    }
}
