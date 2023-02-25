package org.productenginetest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.Required;

@Getter
@RequiredArgsConstructor
public class TelnetConnector {
    @Required
    int terminalPort;
    String searchMask;
    int searchDepth;

    public void terminalConnector(int terminalPort) throws IOException {
        try (ServerSocket portSocket = new ServerSocket(terminalPort)) {
            Socket socket = portSocket.accept();
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(printWriter);
            searchDepth = readSearchDepth(bufferedWriter, bufferedReader);
            writeToTerminal(bufferedWriter, "Please enter the search mask: ");
            searchMask = bufferedReader.readLine();
            socket.close();
        } catch (IOException e) {
            throw new IOException("Can't connect to port " + terminalPort, e);
        } catch (Exception e) {
            throw new RuntimeException("Can't connect to terminal", e);
        }
    }

    public Integer readSearchDepth(BufferedWriter bufferedWriter,
                                   BufferedReader bufferedReader) throws Exception {
        int searchDepth = 0;
        while (true) {
            readDepth:
            {
                writeToTerminal(bufferedWriter, "Please enter the positive integer depth (0...50)"
                        + " for search: ");
                try {
                    searchDepth = Integer.parseInt(bufferedReader.readLine());
                    if (searchDepth < 0 || searchDepth > 50) {
                        break readDepth;
                    }
                    break;
                } catch (NumberFormatException e) {
                    writeToTerminal(bufferedWriter,
                            "Wrong number format, you should input a number");
                }
            }
        }
        return searchDepth;
    }

    private void writeToTerminal(BufferedWriter bufferedWriter,
                                 String message) throws Exception {
        bufferedWriter.write(message);
        bufferedWriter.write(System.lineSeparator());
        bufferedWriter.flush();
    }
}
