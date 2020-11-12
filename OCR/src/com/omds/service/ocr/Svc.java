package com.omds.service.ocr;


import java.awt.image.BufferedImage;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import javax.imageio.ImageIO;


import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class Svc {
	
		
	public static String intakePdf(InputStream requestBody) throws IOException
	{
		
		String strippedText = "";
	
		try {
			byte[] buffer = new byte[requestBody.available()];
			requestBody.read(buffer);
			// Load file into PDFBox class
			PDDocument document = PDDocument.load(buffer);
					
			PDFTextStripper stripper = new PDFTextStripper();
			strippedText = stripper.getText(document);

			// Check that no text exists into the file
			// If no text then run OCR 
			if (strippedText.equals("\r\n") || strippedText.trim().isEmpty()){
				strippedText = extractTextFromScannedDocument(document);
			}
		}catch(Error e){
			//update response so client knows what error happened
			strippedText = e.toString();
		}catch(Exception e) {
			//update response so client knows what error happened
			strippedText = e.toString();
		}

		return strippedText;
		//return OCR results
//		return Response.status(status).encoding(strippedText).build();	
    }
	
	
	private static String extractTextFromScannedDocument(PDDocument document) 
            throws IOException, TesseractException {

		//working directories
//#	String dataPath = "/usr/local/share/tessdata/";
		String dataPath = "/usr/share/tessdata";
		
		// Extract images from file
		PDFRenderer pdfRenderer = new PDFRenderer(document);
		StringBuilder out = new StringBuilder();
		
		ITesseract _tesseract = new Tesseract();
		
		System.err.println(System.getProperty("os.name") );
		
		
    //	_tesseract.setDatapath(dataPath);
		_tesseract.setLanguage("eng"); // set language
	//	_tesseract.setOcrEngineMode(0);
		
		//workaround for tesseract 4.0, fixed in 4.1+ I guess...
		Locale us = Locale.US;
		Locale.setDefault(us);
		
		for (int page = 0; page < document.getNumberOfPages(); page++)
		{ 
			BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
			
			// Create a temp image file
			File temp = File.createTempFile("tempfile_" + page, ".png"); 
			ImageIO.write(bim, "png", temp);
			
			String result = _tesseract.doOCR(temp);
			out.append(result);
			
			// Delete temp file
			temp.delete();
		}
		
		return out.toString();
		
	}
	
	public String toXml(Object obj) { // default encoding
        String xml = null;
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            XMLEncoder encoder = new XMLEncoder(out);
            encoder.writeObject(obj);
            encoder.close();
            xml = out.toString();
        }
        catch(Exception e) { }
        return xml;
    }

}
