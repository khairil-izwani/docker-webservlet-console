package com.khairil.host;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Served as a command forwarder to the targeted machines (host or client container).
 * 
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/host" })
public class CommandServlet extends HttpServlet {

    private static final String DEFAULT_INPUT_TEXT = "type command here...";

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        htmlOutput(response, DEFAULT_INPUT_TEXT, "Docker Client Web Terminal v0.1");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException,
            IOException {

        String command = request.getParameter("c");
        if (command != null) {
            String[] output = new String[2];
            try {
                String outputData = request.getParameter("o");
                output = CommandUtil.executeClientCommand(command, outputData);
            } catch (ValidationException ve) {
                output[1] = ve.getMessage();
            } catch (RuntimeException re) {
                output[1] = re.getMessage();
            } catch (IOException ioe) {
                output[1] = ioe.getMessage();
            } catch (Exception e) {
                output[1] = e.getMessage();
            } finally {
                htmlOutput(response, output[0], output[1]);
            }
        } else {
            htmlOutput(response, DEFAULT_INPUT_TEXT, "");
        }
    }

    /**
     * Produces HTML output. One JavaScript function is added to reduce overhead whenever possible.
     * 
     * @param response
     *            HTTP response
     * @param input
     *            command to be executed
     * @param output
     *            output result from the command executed
     * @throws IOException
     */
    private void htmlOutput(HttpServletResponse response, String input, String output) throws IOException {
        PrintWriter out = response.getWriter();
        StringBuilder html = new StringBuilder();
        String commandInput = input == null ? DEFAULT_INPUT_TEXT : input;
        String commandOutput = output.trim().isEmpty() ? "Command executed." : output;

        // @formatter:off
        html.append("<html>");
        html.append("   <head>");
        html.append("       <style>");
        html.append("           input{width:100%;height:8%;font-family:monospace;color:#EDD400;background-color:#000;border:0;}");
        html.append("           textarea{width:100%;height:90%;color:#EDD400;background-color:#000;border:0;}");
        html.append("           input:focus,textarea:focus {outline:none; border: 2px dashed #000 !important;background-color:#0f0f0f;}");
        html.append("       </style>");
        html.append("       <script>");
        html.append("           function sanitizeOutput() {");
        html.append("               var input = document.getElementById('c').value;");
        html.append("               var cmd = input.split(']')[1].split(' ')[0];");
        html.append("               if(cmd != 'save-as') {");
        html.append("                   document.getElementById('o').value = '';");
        html.append("               }");
        html.append("           }");
        html.append("       </script>");
        html.append("   </head>");
        html.append("   <body style='background-color:#000;'>");
        html.append("       <form action='/host' method='post' onsubmit='sanitizeOutput();'>");
        html.append("           <textarea id='o' name='o' autofocus='autofocus'>").append(commandOutput).append("</textarea>");                     
        html.append("           <input id='c' name='c' type='text' value='").append(commandInput).append("'/>");                      
        html.append("       </form>");
        html.append("   </body>");
        html.append("</html>");
        // @formatter:on

        out.println(html.toString());
        out.close();
    }
}