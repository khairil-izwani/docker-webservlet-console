package com.khairil.container;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Utility to execute the commands that is passed to this client container.
 * 
 */
public class CommandUtil {

    private static final String LINE_BREAK = "\n";

    /**
     * Forwards the command to Java Runtime API.
     * 
     * @param command
     *            command to be executed
     * @return response from the executed command.
     * @throws IOException
     */
    protected static String executeCommand(String... command) throws IOException {
        Process pb = Runtime.getRuntime().exec(command);
        StringBuilder strBuilder = new StringBuilder();

        BufferedReader input = null;

        if (pb.getInputStream() != null) {
            input = new BufferedReader(new InputStreamReader(pb.getInputStream()));

            String line = input.readLine();
            while (line != null) {
                strBuilder.append(line).append(LINE_BREAK);
                line = input.readLine();
            }
            input.close();
        }

        if (pb.getErrorStream() != null) {
            input = new BufferedReader(new InputStreamReader(pb.getErrorStream()));

            String line = input.readLine();
            while (line != null) {
                strBuilder.append(line).append(LINE_BREAK);
                line = input.readLine();
            }
            input.close();
        }

        return strBuilder.toString();
    }
}
