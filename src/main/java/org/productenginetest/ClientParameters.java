package org.productenginetest;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class ClientParameters {
    private String searchMask;
    private int searchDepth;

    public ClientParameters getClientParameters(Socket socket) throws IOException {
        try {
            InputStream inputStream = socket.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
            BufferedWriter bufferedWriter = new BufferedWriter(printWriter);
            searchDepth = readSearchDepth(bufferedWriter, bufferedReader);
            writeToTerminal(bufferedWriter, "Please enter the search mask: ");
            searchMask = bufferedReader.readLine();
        } catch (IOException e) {
            log.error("Can't connect to socket {}", socket);
            throw new IOException("Can't connect to socket " + socket, e);
        } catch (Exception e) {
            log.error("Can't connect to socket {}", socket);
            throw new RuntimeException("Can't connect to terminal", e);
        }
        return this;
    }

    public void writeToTerminal(BufferedWriter bufferedWriter,
                                String message) throws Exception {
        bufferedWriter.write(message);
        bufferedWriter.write(System.lineSeparator());
        bufferedWriter.flush();
    }

    private Integer readSearchDepth(BufferedWriter bufferedWriter,
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
}
