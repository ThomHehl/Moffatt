package com.heavyweightsoftware.biblehtml

import spock.lang.Specification

class StudyBibleInfoReaderTest extends Specification{

    def setup() {
    }

    def "get book name"() {
        given: "A test file"
        File file = new File("../html/1 Corinthians 1.html")

        when: "Getting the book name"
        String bookname = StudyBibleInfoReader.getBookName(file)

        then: "Should be successful"
        bookname == "1 Corinthians"
    }

    def "get chapter files"() {
        given: "A test file and a book name"
        File file = new File("../html/1 Corinthians 1.html")
        String bookname = StudyBibleInfoReader.getBookName(file)

        when: "Getting the chapter files"
        List<File> files = StudyBibleInfoReader.getChapterFiles(file, bookname)

        then: "Should be successful"
        files.size() == 16
        files.get(0).name == "1 Corinthians 1.html"
        files.get(15).name == "1 Corinthians 16.html"
    }
}
