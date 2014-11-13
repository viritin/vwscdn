/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.cdn;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * Standalone servlet that serves the WidgetSet files.
 *
 * @author Sami Ekblad
 */
@WebServlet(value = {"/ws/*"}, asyncSupported = true)
public class WidgetSetServlet extends HttpServlet {

}
