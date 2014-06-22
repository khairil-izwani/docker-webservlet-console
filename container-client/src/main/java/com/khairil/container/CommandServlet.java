package com.khairil.container;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Served as a command forwarder this client container.
 * 
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns = { "/container" })
public class CommandServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            int len = req.getContentLength();
            byte[] input = new byte[len];

            ServletInputStream sin = req.getInputStream();
            int c, count = 0;
            while ((c = sin.read(input, count, input.length - count)) != -1) {
                count += c;
            }
            sin.close();

            String inString = new String(input);
            int index = inString.indexOf("=");
            String value = inString.substring(index + 1);

            // decode application/x-www-form-urlencoded string
            String command = URLDecoder.decode(value, "UTF-8");

            String responseText = CommandUtil.executeCommand("/bin/bash", "-c", command);

            // set the response code and write the response data
            resp.setStatus(HttpServletResponse.SC_OK);
            OutputStreamWriter writer = new OutputStreamWriter(resp.getOutputStream());

            writer.write(responseText);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            try {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print(e.getMessage());
                resp.getWriter().close();
            } catch (IOException ioe) {
            }
        }
    }
}