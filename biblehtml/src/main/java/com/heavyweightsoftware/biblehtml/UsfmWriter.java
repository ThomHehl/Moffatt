package com.heavyweightsoftware.biblehtml;

import com.heavyweightsoftware.biblehtml.bible.BibleBook;
import com.heavyweightsoftware.biblehtml.bible.BibleChapter;
import com.heavyweightsoftware.biblehtml.bible.BibleVerse;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.function.Consumer;

public class UsfmWriter {
    private static final char           DOUBLE_QUOTE = '\"';
    private static final char           SINGLE_QUOTE = '\'';

    private static final String         DOUBLE_END = "\\qt1*";
    private static final String         DOUBLE_START = "\\qt1";
    private static final String         SINGLE_END = "\\qt2*";
    private static final String         SINGLE_START = "\\qt2";

    private static final MessageFormat  FORMAT_CHAPTER = new MessageFormat("\\c {0}\n");
    private static final MessageFormat  FORMAT_VERSE = new MessageFormat("\\v {0} {1}\n");
    private static final MessageFormat  FORMAT_TITLE1 = new MessageFormat("\\mt1 {0}\n");

    private boolean                     inDoubleQuote;
    private boolean                     inSingleQuote;
    private File                        outputDirectory;

    public UsfmWriter(File directory) {
        outputDirectory = directory;
    }

    public synchronized void write(BibleBook bibleBook) {
        File usfm = createOutput(bibleBook);
        PrintWriter writer;
        try {
            writer = new PrintWriter(usfm);
        } catch (IOException ioe) {
            throw new IllegalStateException("Error opening output file:" + usfm.getAbsolutePath(), ioe);
        }

        writeBook(bibleBook, writer);
        writer.close();
    }

    private File createOutput(BibleBook bibleBook) {
        String fileName = bibleBook.getName() + ".usfm";
        File result = new File(outputDirectory, fileName);
        try {
            result.createNewFile();
        } catch (IOException ioe) {
            throw new IllegalStateException("Error creating:" + fileName, ioe);
        }

        return result;
    }

    private void writeBook(final BibleBook bibleBook, final PrintWriter writer) {
        writeBookName(bibleBook, writer);

        bibleBook.getChapters().forEach(new Consumer<BibleChapter>() {
            @Override
            public void accept(BibleChapter bibleChapter) {
                writeChapter(bibleChapter, writer);
            }
        });
    }

    private static void writeBookName(BibleBook bibleBook, PrintWriter writer) {
        String titleText = title1(bibleBook.getName());
        writer.print(titleText);
    }

    private void writeChapter(final BibleChapter bibleChapter, final PrintWriter writer) {
        String chapterText = chapter(bibleChapter.getNumber());
        writer.print(chapterText);

        bibleChapter.getVerses().forEach(new Consumer<BibleVerse>() {
            @Override
            public void accept(BibleVerse bibleVerse) {
                writeVerse(bibleVerse, writer);
            }
        });
    }

    private void writeVerse(final BibleVerse bibleVerse, final PrintWriter writer) {
        String bibleText = formatText(bibleVerse.getBibleText());
        writer.print(verse(bibleVerse.getNumber(), bibleText));
    }

    private String formatText(String bibleText) {
        StringBuilder sb = new StringBuilder(bibleText.length() + 10);

        for(char ch : bibleText.toCharArray()) {
            if(inSingleQuote) {
                if(ch == SINGLE_QUOTE) {
                    sb.append(singleEnd());
                    inSingleQuote = false;
                }
                else {
                    sb.append(ch);
                }
            }
            else if(inDoubleQuote) {
                if(ch == DOUBLE_QUOTE) {
                    sb.append(doubleEnd());
                    inDoubleQuote = false;
                }
                else if(ch == SINGLE_QUOTE) {
                    sb.append(singleStart());
                    inSingleQuote = true;
                }
                else {
                    sb.append(ch);
                }
            }
            else {
                if(ch == DOUBLE_QUOTE) {
                    sb.append(doubleStart());
                    inDoubleQuote = true;
                }
                else {
                    sb.append(ch);
                }
            }
        }

        return sb.toString();
    }

    private static String chapter(int num) {
        String result = FORMAT_CHAPTER.format(new Object[]{num});
        return result;
    }

    private static String doubleEnd() {
        String result = DOUBLE_END;
        return result;
    }

    private static String doubleStart() {
        String result = DOUBLE_START;
        return result;
    }

    private static String singleEnd() {
        String result = SINGLE_END;
        return result;
    }

    private static String singleStart() {
        String result = SINGLE_START;
        return result;
    }

    private static String title1(String text) {
        String result = FORMAT_TITLE1.format(new Object[]{text});
        return result;
    }

    private static String verse(int num, String text) {
        String result = FORMAT_VERSE.format(new Object[]{num, text});
        return result;
    }
}
