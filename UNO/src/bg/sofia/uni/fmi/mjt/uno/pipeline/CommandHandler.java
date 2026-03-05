package bg.sofia.uni.fmi.mjt.uno.pipeline;

import bg.sofia.uni.fmi.mjt.uno.command.Command;
import bg.sofia.uni.fmi.mjt.uno.command.CommandFactory;
import bg.sofia.uni.fmi.mjt.uno.command.CommandType;
import bg.sofia.uni.fmi.mjt.uno.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.StartCommand;
import bg.sofia.uni.fmi.mjt.uno.repositories.SessionRepository;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class CommandHandler {

    private static final Path ERROR_FILE_PATH = Paths.get("logs", "errors.txt");

    public static void handleCommand(Session session, String line, SessionRepository sessionRepository) {

        Command command = null;

        try {
            command = CommandFactory.create(line, session);

            if (command.commandType() == CommandType.LOGIN) {
                LoginCommand loginCommand = (LoginCommand) command;
                String username = loginCommand.getUsername();

                Session old = sessionRepository.getByUsername(username);
                if (old != null && old != session) {

                    sessionRepository.removeSession(old);
                    try {
                        var selectionKey = old.getSelectionKey();
                        if (selectionKey != null) {
                            selectionKey.cancel();
                            if (selectionKey.channel() != null) {
                                selectionKey.channel().close();
                            }
                        }
                    } catch (IOException e) {
                        // To be ignored
                    }
                }
            }

            command.execute();

            if (command.commandType() == CommandType.LOGIN) {
                sessionRepository.registerUsername(session, session.getUsername());
            } else if (command.commandType() == CommandType.LOGOUT) {
                sessionRepository.unregisterUsername(session);
            }
            if (command.commandType() == CommandType.START) {
                StartCommand startCommand = (StartCommand) command;
                sessionRepository.broadcastToGame(startCommand.getGameId(), StartCommand.getStartMessage());
            }

        } catch (IOException e) {

            writeExceptionToFile(ERROR_FILE_PATH, e);

        } catch (Exception e) {

            writeExceptionToFile(ERROR_FILE_PATH, e);

            try {
                String userMessage = e.getMessage() == null ? "Error occurred" : e.getMessage();
                session.write(userMessage + System.lineSeparator());
            } catch (IOException io) {
                throw new UncheckedIOException("Failed to reply to client after error: " + io.getMessage(), io);
            }
        }
    }

    private static void writeExceptionToFile(Path filePath, Exception e) {
        try {
            File file = filePath.toFile();

            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }

            try (FileWriter fileWriter = new FileWriter(file, true);
                 BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                 PrintWriter printWriter = new PrintWriter(bufferedWriter)) {

                printWriter.println("MESSAGE: " + e.getMessage());
                printWriter.println("STACKTRACE:");
                e.printStackTrace(printWriter);
                printWriter.println("----------------------------------------");
                printWriter.flush();
            }

        } catch (IOException io) {
            throw new UncheckedIOException("A problem occurred while writing to a file", io);
        }
    }
}
