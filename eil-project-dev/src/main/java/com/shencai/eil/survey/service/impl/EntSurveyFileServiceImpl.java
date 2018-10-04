package com.shencai.eil.survey.service.impl;

import com.shencai.eil.survey.service.IEntSurveyFileService;
import org.apache.poi.POIXMLDocument;
import org.apache.poi.xwpf.converter.pdf.PdfConverter;
import org.apache.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;

/**
 * Created by fanhj on 2018/10/4.
 */
@Service
public class EntSurveyFileServiceImpl extends IEntSurveyFileService {
    public static void main(String[] args) {
        String srcPath = "D:\\fanhj\\model-eil.docx";
        String destPath = "D:\\fanhj\\new-model-eil.docx";
        String pdfPath = "D:\\fanhj\\new-model-eil.pdf";
        Map<String, String> map = new HashMap<>();
        map.put("${Ru-s-point}", "35.6");
        map.put("${Ru-s-level}", "高");
        map.put("${Ru-s-avg-level}", "较高");
        map.put("${Ru-g-point}", "19.4");
        map.put("${Ru-g-level}", "低");
        map.put("${Ru-g-avg-level}", "较低");
        map.put("${gradual-desc}", "公司【需要】进行进一步的现场风险查勘。");
        replaceWords(srcPath, destPath, map);
        word2pdf(destPath, pdfPath);
    }


    public static void replaceWords(String srcPath, String destPath, Map<String, String> replaceMap) {
        try {
            XWPFDocument doc = new XWPFDocument(POIXMLDocument.openPackage(srcPath));
            Iterator itPara = doc.getParagraphsIterator();
            while(itPara.hasNext()) {
                XWPFParagraph paragraph = (XWPFParagraph) itPara.next();
                Set<String> set = replaceMap.keySet();
                Iterator iterator = set.iterator();
                while(iterator.hasNext()) {
                    String key = (String) iterator.next();
                    List<XWPFRun> run = paragraph.getRuns();
                    for (int i = 0; i < run.size(); i++) {
                        if (run.get(i).getText(run.get(i).getTextPosition()) != null
                                && run.get(i).getText(run.get(i).getTextPosition()).equals(key)) {
                            run.get(i).setText(replaceMap.get(key), 0);
                            run.get(i).setBold(true);
                            run.get(i).setColor("ff0000");
                        }
                    }
                }
            }

            OutputStream os = new FileOutputStream(destPath);
            doc.write(os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void word2pdf(String srcPath, String destPath) {
        try {
            XWPFDocument document=new XWPFDocument(new FileInputStream(new File(srcPath)));
            File outFile=new File(destPath);
            outFile.getParentFile().mkdirs();
            OutputStream out = new FileOutputStream(outFile);
            PdfOptions options= PdfOptions.create();
            PdfConverter.getInstance().convert(document,out,options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



}
