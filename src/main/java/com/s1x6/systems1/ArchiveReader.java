package com.s1x6.systems1;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class ArchiveReader {
    public static InputStream getContentReader(String path) throws IOException {
        InputStream fin = new FileInputStream(path);
        BufferedInputStream in = new BufferedInputStream(fin);
        return new BZip2CompressorInputStream(in);
    }
}
