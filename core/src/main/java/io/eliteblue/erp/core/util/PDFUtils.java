package io.eliteblue.erp.core.util;

import org.apache.pdfbox.cos.COSDictionary;
import org.apache.pdfbox.multipdf.PDFCloneUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
@PropertySource("classpath:admin-config.properties")
public class PDFUtils {

    private static String pathProp;

    @Value("${data.dir}")
    public void setPathProp(String prop) {
        pathProp = prop;
    }

    private static final String propDir = System.getProperty("user.dir");
    private static final String filepath = propDir+"/data/payslip_template.pdf";
    private static final Path currentRelativePath = Paths.get("");
    private static final String s = currentRelativePath.toAbsolutePath().toString()+"/";
    private static final DataFormatter format = new DataFormatter();

    public static PDDocument document;
    public static PDPageContentStream contentStream;
    public static PDDocument clonedDoc;

    public static void initializePDFDocument() throws Exception {
        document = PDDocument.load(new File(pathProp+"/data/payslip_template.pdf"));
        addPageTemplate();
    }

    public static void initializePDFDocumentWithFileName(String fileName) throws Exception {
        document = PDDocument.load(new File(pathProp+"/data/"+fileName));
        addPageTemplate();
    }

    private static void cloneDoc() throws Exception {
        if(clonedDoc == null) {
            clonedDoc = new PDDocument();
            clonedDoc.getDocument().setVersion(document.getDocument().getVersion());
            clonedDoc.setDocumentInformation(document.getDocumentInformation());
            clonedDoc.getDocumentCatalog().setViewerPreferences(document.getDocumentCatalog().getViewerPreferences());
        }
    }

    public static void addPageTemplate() throws Exception {
        cloneDoc();
        PDFCloneUtility cloneUtility = new PDFCloneUtility(clonedDoc);
        COSDictionary pageDictionary = (COSDictionary) cloneUtility.cloneForNewDocument(document.getPage(0));
        PDPage page = new PDPage(pageDictionary);
        clonedDoc.addPage(page);
        if(contentStream != null) {
            contentStream.close();
            contentStream = null;
        }
        contentStream = new PDPageContentStream(clonedDoc, page, PDPageContentStream.AppendMode.APPEND, true);
    }

    public static void setFont(PDFont font, float fontSize) throws Exception {
        contentStream.setFont(font, fontSize);
    }

    public static void beginText() throws Exception {
        contentStream.beginText();
    }

    public static void endText() throws Exception {
        contentStream.endText();
    }

    public static void setFontColor(float r, float g, float b) throws Exception {
        contentStream.setNonStrokingColor(r,g,b);
    }

    public static void appendText(float posx, float posy, String text) throws Exception {
        PDPage page = (PDPage) document.getPage(0);
        float pageHeight = page.getMediaBox().getHeight();
        contentStream.beginText();
        contentStream.newLineAtOffset(posx,pageHeight-posy);
        contentStream.showText(text);
        contentStream.endText();
    }

    public static void close() throws Exception {
        contentStream.close();
    }
}

