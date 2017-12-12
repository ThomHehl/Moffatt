package com.heavyweightsoftware.biblehtml;

import com.heavyweightsoftware.biblehtml.bible.BibleBook;
import com.heavyweightsoftware.biblehtml.bible.BibleChapter;
import com.heavyweightsoftware.biblehtml.bible.BibleVerse;

import java.io.File;
import java.io.PrintWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Stack;
import java.util.function.Consumer;

public class UsfmWriter {
    private static final char           DOUBLE_QUOTE = '\"';
    private static final char           SINGLE_QUOTE = '\'';

    private static final MessageFormat  QUOTE_END = new MessageFormat("\\qt{0}*");
    private static final MessageFormat  QUOTE_START = new MessageFormat("\\qt{0}");

    private static final MessageFormat  FORMAT_CHAPTER = new MessageFormat("\\c {0}\n");
    private static final MessageFormat  FORMAT_VERSE = new MessageFormat("\\v {0} {1}\n");
    private static final MessageFormat  FORMAT_TITLE1 = new MessageFormat("\\mt1 {0}\n");

    private Stack<Character>            lastQuote = new Stack<>();
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

        lastQuote.clear();
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

        final int stateAddingCharacters = 0;
        final int stateLookingForQuote = 1;

        char[] chars = bibleText.toCharArray();
        boolean done = false;
        int idx = 0;
        int state = stateAddingCharacters;

        if(!lastQuote.isEmpty()) {
            state = stateLookingForQuote;
        }

        while (!done) {
            char ch = chars[idx];

            switch (state) {
                case stateAddingCharacters:
                    if(ch == DOUBLE_QUOTE || ch == SINGLE_QUOTE) {
                        if(ch == SINGLE_QUOTE && isApostrophe(chars, idx)) {
                            sb.append(ch);
                        }
                        else {
                            char prevQuote = lastQuote.isEmpty() ? '\n' : lastQuote.peek();
                            if (ch == prevQuote) {
                                sb.append(quoteEnd());
                                lastQuote.pop();
                            }
                            else {
                                lastQuote.push(ch);
                                sb.append(quoteStart());
                            }
                            state = stateLookingForQuote;
                        }
                    }
                    else {
                        sb.append(ch);
                    }

                    idx++;
                    break;

                case stateLookingForQuote:
                    if(ch == DOUBLE_QUOTE || ch == SINGLE_QUOTE) {
                        if(isApostrophe(chars, idx)) {
                            sb.append(ch);
                        }
                        else {
                            char prevQuote = lastQuote.peek();
                            if (ch == prevQuote) {
                                sb.append(quoteEnd());
                                lastQuote.pop();
                            }
                            else {
                                lastQuote.push(ch);
                                sb.append(quoteStart());
                            }

                            if(lastQuote.isEmpty()) {
                                state = stateAddingCharacters;
                            }
                        }
                    }
                    else {
                        sb.append(ch);
                    }

                    idx++;
                    break;
            }

            if (idx >= chars.length) {
                done = true;
            }
        }

        return sb.toString();
    }

    private boolean isApostrophe(final char[] chars, final int idx) {
        boolean result = false;

        if(idx > 0 && idx < (chars.length - 1)) {
            int afterIdx = idx + 1;
            char after = chars[afterIdx];

            int beforeIdx = idx - 1;
            char before = chars[beforeIdx];

            if(Character.isAlphabetic(after) && Character.isAlphabetic(before)) {
                result = true;
            }
        }

        return result;
    }

    private static String chapter(int num) {
        String result = FORMAT_CHAPTER.format(new Object[]{num});
        return result;
    }

    private String quoteEnd() {
        String result = QUOTE_END.format(new Object[] {lastQuote.size()});
        return result;
    }

    private String quoteStart() {
        String result = QUOTE_START.format(new Object[]{lastQuote.size()});
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
