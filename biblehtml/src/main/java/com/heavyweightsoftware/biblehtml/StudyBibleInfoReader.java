package com.heavyweightsoftware.biblehtml;

import com.heavyweightsoftware.biblehtml.bible.BibleBook;
import com.heavyweightsoftware.biblehtml.bible.BibleChapter;
import com.heavyweightsoftware.biblehtml.bible.BibleVerse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;

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

        getChapterFiles(startingWith, bookName).forEach(new Consumer<File>() {
            @Override
            public void accept(File file) {
                BibleChapter chapter = new BibleChapter();
                int num = getChapterValue(file);
                chapter.setNumber(num);
                readChapter(file, chapter);
                bibleBook.addChapter(chapter);
            }
        });

        return bibleBook;
    }

    private void readChapter(File chapterFile, BibleChapter chapter) {
        Document doc;
        try {
            doc = Jsoup.parse(chapterFile, "UTF-8");
        } catch (IOException ioe) {
            throw new IllegalStateException(chapterFile.getAbsolutePath(), ioe);
        }

        Element maintext = doc.select("div.maintext").first();
        Elements verses = maintext.select("sup");

        verses.forEach(new Consumer<Element>() {
            @Override
            public void accept(Element element) {
                BibleVerse verse = parseVerse(element);
                if(verse != null) {
                    chapter.addVerse(verse);
                }
            }

            private BibleVerse parseVerse(Element element) {
                BibleVerse result;

                String verseNum = element.text();
                int num = 0;
                try {
                    num = Integer.parseInt(verseNum);
                    result = new BibleVerse();
                    result.setNumber(num);
                    result.setBibleText(element.nextSibling().toString());
                } catch (NumberFormatException nfe) {
                    result = null;
                }

                return result;
            }
        });
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
            String fileName = file.getName();
            if( fileName.endsWith(".html") && fileName.startsWith(bookName)) {
                result.add(file);
            }
        }

        Collections.sort(result, new Comparator<File>() {
            @Override
            public int compare(File file1, File file2) {
                int val1 = getChapterValue(file1);
                int val2 = getChapterValue(file2);
                return val1 - val2;
            }
        });
        return result;
    }

    private static int getChapterValue(File file) {
        String name = file.getName();
        int posSpace = name.lastIndexOf(' ');
        int posDot = name.lastIndexOf('.');
        String valString = name.substring(posSpace + 1, posDot);

        int result = Integer.parseInt(valString);
        return result;
    }
}
