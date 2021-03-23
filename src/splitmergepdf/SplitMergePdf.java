/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package splitmergepdf;

import java.io.FileOutputStream;

/**
 *
 * @author gabri
 */
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfCopy;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class SplitMergePdf {

    @SuppressWarnings("unchecked")
    public static void disableAccessWarnings() {

        try {
            Class unsafeClass = Class.forName("sun.misc.Unsafe");
            Field field = unsafeClass.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            Object unsafe = field.get(null);

            Method putObjectVolatile = unsafeClass.getDeclaredMethod("putObjectVolatile", Object.class, long.class, Object.class);
            Method staticFieldOffset = unsafeClass.getDeclaredMethod("staticFieldOffset", Field.class);

            Class loggerClass = Class.forName("jdk.internal.module.IllegalAccessLogger");
            Field loggerField = loggerClass.getDeclaredField("logger");
            Long offset = (Long) staticFieldOffset.invoke(unsafe, loggerField);
            putObjectVolatile.invoke(unsafe, loggerClass, offset, null);
        } catch (Exception ignored) {
        }
    }
    
    public static String[] getFilesNames(String directory) {
        File folder = new File(directory);
        File[] listOfFiles = folder.listFiles();
        String[] fileNames = new String[listOfFiles.length];
        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile()) {
                //String [] splitedName=listOfFiles[i].getName().split(".");
                //List<String> l = Arrays.asList(splitedName);
                //  if(l.contains("xls"))
                fileNames[i] = listOfFiles[i].getName();
            }
        }
        return fileNames;
    }


    static void splitPdfFile(String inputPdf, PdfReader pdfReader, int startPage, int endPage) throws Exception {
        disableAccessWarnings();
        //Create pdfReader objects.
        //Get total no. of pages in the pdf file.
        int totalPages = pdfReader.getNumberOfPages();

        //Check the startPage should not be greater than the endPage
        //and endPage should not be greater than total no. of pages.
        if (startPage > endPage || endPage > totalPages) {
            System.out.println("Kindly pass the valid values "
                    + "for startPage and endPage.");
        } else {

            while (startPage <= endPage) {
                String destination = inputPdf.substring(0, inputPdf.indexOf(".pdf")) + "-" + String.format("%03d", startPage) + ".pdf";
                System.out.println("Writing " + destination);

                // create new document with corresponding page size
                Document document = new Document(pdfReader.getPageSizeWithRotation(1));

                // create writer and assign document and destination
                PdfCopy copy = new PdfCopy(document, new FileOutputStream(destination));
                document.open();

                // read original page and copy to writer
                PdfImportedPage page = copy.getImportedPage(pdfReader, startPage);
                copy.addPage(page);

                // close and write the document
                document.close();
                startPage++;
            }

        }
    }

    static void mergePdfFiles(ArrayList<InputStream> inputPdfList,
            OutputStream outputStream) throws Exception {

        //Create document and pdfReader objects.
        Document document = new Document();
        ArrayList<PdfReader> readers
                = new ArrayList<PdfReader>();
        int totalPages = 0;

        //Create pdf Iterator object using inputPdfList.
        Iterator<InputStream> pdfIterator
                = inputPdfList.iterator();

        // Create reader list for the input pdf files.
        while (pdfIterator.hasNext()) {
            InputStream pdf = pdfIterator.next();
            PdfReader pdfReader = new PdfReader(pdf);
            readers.add(pdfReader);
            totalPages = totalPages + pdfReader.getNumberOfPages();
        }

        // Create writer for the outputStream
        PdfWriter writer = PdfWriter.getInstance(document, outputStream);

        //Open document.
        document.open();

        //Contain the pdf data.
        PdfContentByte pageContentByte = writer.getDirectContent();

        PdfImportedPage pdfImportedPage;
        int currentPdfReaderPage = 1;
        Iterator<PdfReader> iteratorPDFReader = readers.iterator();

        // Iterate and process the reader list.
        while (iteratorPDFReader.hasNext()) {
            PdfReader pdfReader = iteratorPDFReader.next();
            //Create page and add content.
            while (currentPdfReaderPage <= pdfReader.getNumberOfPages()) {
                document.newPage();
                pdfImportedPage = writer.getImportedPage(
                        pdfReader, currentPdfReaderPage);
                pageContentByte.addTemplate(pdfImportedPage, 0, 0);
                currentPdfReaderPage++;
            }
            currentPdfReaderPage = 1;
        }

        //Close document and outputStream.
        outputStream.flush();
        document.close();
        outputStream.close();

        System.out.println("Pdf files merged successfully.");
    }
/*
    public static void main(String[] args) {
        // TODO code application logic here
        /*
        try {
            //Prepare output stream for 
            //new pdf file after split process.
            String inputPdf = "C:\\Users\\gabri\\Documents\\PdfSpliterTest\\Source\\extrato_20190702_100259.pdf";

            //call method to split pdf file.
            splitPdfFile(inputPdf, 1, 146);

            System.out.println("Pdf file splitted successfully.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        try {
            String inputPdf = "C:\\Users\\gabri\\Documents\\PdfSpliterTest\\Source";
            String[] files = getFilesNames (inputPdf);
            Arrays.sort(files);
            
            ArrayList<String> l1  = new ArrayList<>(Arrays.asList(files));
            
             ArrayList<InputStream> inputPdfList = new ArrayList<InputStream>();
             for (int i = 0; i < l1.size(); i++) 
                inputPdfList.add(new FileInputStream(inputPdf+"\\"+l1.get(i)));
            
             String outPut = "C:\\Users\\gabri\\Documents\\PdfSpliterTest\\Source\\MergeFile.pdf";
            //Prepare output stream for merged pdf file.
            OutputStream outputStream = 
            		new FileOutputStream(outPut);
 
            //call method to merge pdf files.
            mergePdfFiles(inputPdfList, outputStream); 
            
         } catch (Exception e) {
            e.printStackTrace();
        }
        
        
        
        

    }
    */
}
