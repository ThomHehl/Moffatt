package com.heavyweightsoftware.biblehtml

import spock.lang.Specification

class UsfmWriterTest extends Specification {

    private UsfmWriter                  usfmWriter;

    def setup() {
        usfmWriter = new UsfmWriter()
    }

    def "format text no quotes"() {
        given: "Some text"
        String text = "Do, or do not, there is no try.";

        when: "Formatting text"
        String formatted = usfmWriter.formatText(text)

        then: "Should not have changed"
        formatted == text
    }

    def "format text apostrophe"() {
        given: "Some text "
        String text = "That boy ain't right.";

        when: "Formatting text"
        String formatted = usfmWriter.formatText(text)

        then: "Should not have changed"
        formatted == text
    }

    def "format text with both quotes in single line"() {
        given: "Some text"
        String text = "And the prophet said, \"Thus saith the LORD, 'You will be rich and famous.'\"";

        when: "Formatting text"
        String formatted = usfmWriter.formatText(text)

        then: "Should contain USFM quote tags"
        int pos = formatted.indexOf("d, \\qt1T")
        pos > 0;

        formatted.indexOf("D, \\qt2Y", pos) > 0
        formatted.endsWith("s.\\qt2*\\qt1*")
    }

    def "format text one quote in single line"() {
        given: "Some text"
        String text = "Jesus said, \"You must be born again.\""

        when: "Formatting text"
        String formatted = usfmWriter.formatText(text)

        then: "Should contain USFM quote tags"
        int pos = formatted.indexOf("d, \\qt1Y")
        pos > 0;

        formatted.endsWith("n.\\qt1*")
    }

    def "format text single quote in single line"() {
        given: "Some text"
        String text = "the voice of one who cries in the desert, 'Make the way ready for the Lord, level the paths for him' "

        when: "Formatting text"
        String formatted = usfmWriter.formatText(text)

        then: "Should contain USFM quote tags"
        int pos = formatted.indexOf("t, \\qt1M")
        pos > 0;

        String str = "im\\qt1* "
        formatted.endsWith(str)
    }

    def "format text two quotes in multiple lines"() {
        given: "Some text"
        String text1 = "Jesus said, \"When you pray, say"
        String text2 = "\'Our father, who art in heaven"
        String text3 = "Hallowed by thy name,' and"
        String text4 = "don't forget to tithe\""

        when: "Formatting text"
        String formatted1 = usfmWriter.formatText(text1)
        String formatted2 = usfmWriter.formatText(text2)
        String formatted3 = usfmWriter.formatText(text3)
        String formatted4 = usfmWriter.formatText(text4)

        then: "Should contain USFM quote tags"
        int pos = formatted1.indexOf("d, \\qt1W")
        pos > 0;

        formatted2.startsWith("\\qt2O")
        formatted3.indexOf("me,\\qt2* a", pos) > 0;
        formatted4.contains("don't")
        formatted4.endsWith("\\qt1*")
    }
}
