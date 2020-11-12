package com.omds.service.controller;
//st comment
import com.omds.service.ocr.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tomcat.util.codec.binary.Base64;

/**
 * Servlet implementation class Controller
 */
@WebServlet({ "/Controller", "/svc" })
public class Controller extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Controller() {
        super();
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String temp = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
		byte[] data = Base64.decodeBase64(temp);
		InputStream in = new ByteArrayInputStream(data);
		temp = Svc.intakePdf(in);
		sendResponse(response, temp);
	}
	
    // Send the response payload (Xml or Json) to the client.
    private void sendResponse(HttpServletResponse response, String payload) {
		try {
			response.setContentLength( (payload.getBytes("UTF-8")).length);
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(payload);
			response.getWriter().flush();
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }

}
