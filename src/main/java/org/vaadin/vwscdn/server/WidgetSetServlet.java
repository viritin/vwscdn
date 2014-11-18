/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.vaadin.vwscdn.server.WSCompilerService;

/**
 * Standalone servlet that serves the WidgetSet files.
 *
 * @author Sami Ekblad
 */
@WebServlet(value = {"/ws/*"}, asyncSupported = true)
public class WidgetSetServlet extends HttpServlet {

    public static File PUBLIC_ROOT_DIR = new File("/Users/se/ws/public");

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

        FileInputStream fileInputStream = null;
        OutputStream responseOutputStream = null;
        try {
            String wsName = request.getPathInfo();
            wsName = wsName.substring(1); // remove leading "/"
            File wsFile = new File(PUBLIC_ROOT_DIR, wsName);
            response.setContentType("text/javascript");
            response.setContentLength((int) wsFile.length());
            fileInputStream = new FileInputStream(wsFile);
            responseOutputStream = response.getOutputStream();
            int bytes;
            while ((bytes = fileInputStream.read()) != -1) {
                responseOutputStream.write(bytes);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(WidgetSetServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WidgetSetServlet.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (responseOutputStream != null) {
                    responseOutputStream.close();
                }
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                Logger.getLogger(WidgetSetServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
