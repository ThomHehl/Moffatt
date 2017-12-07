package com.heavyweightsoftware.biblehtml;

import java.io.File;

public class UsfmWriter {

    private File                        outputFile;

    public UsfmWriter(File directory, String name) {
        outputFile = new File(directory, name);
    }
}
