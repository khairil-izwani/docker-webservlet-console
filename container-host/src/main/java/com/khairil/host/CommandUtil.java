package com.khairil.host;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

/**
 * Utility to forward, create, execute the commands.
 * 
 */
public class CommandUtil {

    private static final String LINE_BREAK = "\n";

    /**
     * Forwards the command to Java Runtime API.
     * 
     * @param command
     * @return
     * @throws IOException
     */
    protected static String executeCommand(String command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        return generateResult(process);
    }

    /**
     * Forwards an array of commands to Java Runtime API.
     * 
     * @param command
     *            command to be executed
     * @return response from the executed command
     * @throws IOException
     */
    protected static String executeCommand(String... command) throws IOException {
        Process process = Runtime.getRuntime().exec(command);
        return generateResult(process);
    }

    /**
     * Generates result from the executed command.
     * 
     * @param process
     *            process instance
     * @return response from the command in the process
     * @throws IOException
     */
    private static String generateResult(Process process) throws IOException {
        StringBuilder strBuilder = new StringBuilder();

        BufferedReader input = null;

        if (process.getInputStream() != null) {
            input = new BufferedReader(new InputStreamReader(process.getInputStream()));

            String line = input.readLine();
            while (line != null) {
                strBuilder.append(line).append(LINE_BREAK);
                line = input.readLine();
            }
            input.close();
        }

        if (process.getErrorStream() != null) {
            input = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            String line = input.readLine();
            while (line != null) {
                strBuilder.append(line).append(LINE_BREAK);
                line = input.readLine();
            }
            input.close();
        }

        return strBuilder.toString();
    }

    /**
     * Starts a container.
     * 
     * @param command
     *            command to start the container
     * @param containerName
     *            targeted container
     * @return returns container name if successful, else it throws an exception.
     * @throws IOException
     */
    protected static String startContainer(String command, String containerName) throws IOException {
        return executeCommand("docker start " + containerName);
    }

    /**
     * Stops a container.
     * 
     * @param command
     *            command to stop the container
     * @param containerName
     *            targeted container
     * @return returns container name if successful, else it throws an exception.
     * @throws IOException
     */
    protected static String stopContainer(String command, String containerName) throws IOException {
        return executeCommand("docker stop " + containerName);
    }

    /**
     * Forwards and executes the command to the targeted container.
     * 
     * @param command
     *            command to be executed
     * @param containerName
     *            targeted container
     * @return response from the command executed.
     * @throws IOException
     */
    protected static String sendCommandToContainer(String command, String containerName) throws IOException {

        validateContainerIsRunning(containerName);

        String ipAddress = getContainerIpAddress(containerName);
        String containerPort = System.getProperty("com.khairil.container.port");
        String urlString = "http://" + ipAddress + ":" + containerPort + "/container";
        String commandStr = URLEncoder.encode(command, "UTF-8");
        StringBuilder strBuilder = new StringBuilder();

        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.setDoOutput(true);

        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write("c=" + commandStr);
        out.close();

        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String decodedString = in.readLine();

        while (decodedString != null) {
            strBuilder.append(decodedString).append(LINE_BREAK);
            decodedString = in.readLine();
        }
        in.close();
        return strBuilder.toString();
    }

    /**
     * Gets the IP address info of the container name.
     * 
     * @param containerName
     *            targeted container
     * @return IP Address of the container
     * @throws IOException
     */
    private static String getContainerIpAddress(String containerName) throws IOException {
        return executeCommand("/bin/bash", "-c",
                "docker inspect " + containerName + " |& grep IPAddress |& tr -d 'IPAddress:|,|\"'").trim();
    }

    /**
     * Checks if container is running.
     * 
     * @param containerName
     *            targeted container
     * @throws IOException
     */
    private static void validateContainerIsRunning(String containerName) throws IOException {
        String output = executeCommand("/bin/bash", "-c", "docker ps " + containerName + " |& grep " + containerName)
                .trim();
        if (output.length() == 0) {
            throw new ValidationException("Container " + containerName + " is not running.");
        }
    }

    /**
     * Validates if command is actually exist.
     * 
     * @param command
     *            command to be validated
     */
    protected static void validateCommandExistence(String command) {
        if (!CommandCollection.values().contains(command)) {
            throw new ValidationException("Command not found.");
        }
    }

    /**
     * Validates the command string length.
     * 
     * @param actualLength
     *            length of the actual command to be validated
     * @param expectedLength
     *            length of the expected command
     */
    protected static void validateCommandLength(int actualLength, int expectedLength) {
        if (actualLength != expectedLength) {
            throw new ValidationException("Invalid parameter(s).");
        }
    }

    /**
     * Differentiates between the actual command and the subject.
     * 
     * @param command
     *            command to be executed
     * @return an array that contains command and its targeted subject
     */
    private static String[] extractCommands(String command) {
        String[] defaultCmd = { command };
        return command.contains(" ") ? command.split(" ") : defaultCmd;
    }

    /**
     * Decides if the command sent by the client is meant for the host or container.
     * 
     * @param command
     *            command to be executed
     * @param output
     *            if applicable, save output data to the container.
     * @return response after command is executed
     * @throws IOException
     */
    protected static String[] executeClientCommand(String command, String output) throws IOException {
        return command.startsWith("[connected@") ? executeCommandToContainer(command, output) : executeCommandToHost(command);
    }

    /**
     * Executes command that is targeted for the container.
     * 
     * @param command
     *            command to be executed
     * @return response after command is executed
     * @throws IOException
     */
    protected static String[] executeCommandToContainer(String command, String output) throws IOException {
        String[] input = validateAndExtractContainerInput(command.trim() + " ");
        String[] result = new String[2];
        result[0] = command.split("]")[0] + "]";

        // default output response if client send an empty command to servlet.
        if (input[0].trim().isEmpty()) {
            input[0] = "echo you are already in the container!";
        }

        String[] cmdArray = extractCommands(input[0]);
        if (CommandCollection.SAVE_AS.equals(cmdArray[0])) {
            validateCommandLength(cmdArray.length, 2);
            input[0] = "cat >> " + cmdArray[1] + " <<EOL\n" + output + "\nEOL";
        }

        result[1] = sendCommandToContainer(input[0], input[1]);
        return result;
    }

    /**
     * Validates and extract the commands.
     * 
     * @param command
     *            command to be executed
     * @return response after command is executed or exception if command is invalid
     */
    private static String[] validateAndExtractContainerInput(String command) {
        try {
            String[] result = { command.split("]")[1], command.split("@")[1].split("]")[0] };
            return result;
        } catch (RuntimeException e) {
            throw new ValidationException("Invalid command.");
        }
    }

    /**
     * Executes command that is targeted for the host.
     * 
     * @param command
     *            command to be executed
     * @return response after command is executed
     * @throws IOException
     */
    protected static String[] executeCommandToHost(String command) throws IOException {

        String[] result = new String[2];
        String[] cmdArray = extractCommands(command.trim());
        validateCommandExistence(cmdArray[0]);

        if (CommandCollection.START.equals(cmdArray[0])) {
            validateCommandLength(cmdArray.length, 2);
            String output = startContainer(cmdArray[0], cmdArray[1]);

            if (output.trim().equals(cmdArray[1])) {
                result[0] = "[connected@" + output + "]";
                result[1] = "connected to " + output;
            } else {
                result[0] = "";
                result[1] = output;
            }

        } else if (CommandCollection.STOP.equals(cmdArray[0])) {
            validateCommandLength(cmdArray.length, 2);
            String output = stopContainer(cmdArray[0], cmdArray[1]);

            if (output.trim().equals(cmdArray[1])) {
                result[0] = "type command here...";
                result[1] = "disconnected from " + output;
            } else {
                result[0] = "";
                result[1] = output;
            }

        } else {
            result[0] = "";
            result[1] = "Command is reserved. No implementation";
        }
        return result;
    }
}
