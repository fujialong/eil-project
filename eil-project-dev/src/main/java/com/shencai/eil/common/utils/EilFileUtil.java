package com.shencai.eil.common.utils;

import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by fanhj on 2018/10/17.
 */
public class EilFileUtil {
    /**
     * convert word to pdf
     *
     * @param srcPath
     * @param destPath
     */
    public static void word2pdf(String srcPath, String destPath) throws Exception {
        /**
         *  when convert word to pdf, maybe chinese disappeared. should install windows fonts
         */
        XWPFDocument document = new XWPFDocument(new FileInputStream(new File(srcPath)));
        File outFile = new File(destPath);
        outFile.getParentFile().mkdirs();
        OutputStream out = new FileOutputStream(outFile);
        PdfOptions options = PdfOptions.create();
        PdfConverter.getInstance().convert(document, out, options);
    }
}
