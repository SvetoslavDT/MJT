package bg.sofia.uni.fmi.mjt.uno.command;

import bg.sofia.uni.fmi.mjt.uno.cards.enums.ColorType;
import bg.sofia.uni.fmi.mjt.uno.command.commands.AcceptEffectCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.CreateGameCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.DrawCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.JoinCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.LeaveCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.ListGamesCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.LoginCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.LogoutCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.PlayJokerCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.PlayCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.RegisterCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.ShowHandCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.ShowLastCardCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.ShowPlayedCardsCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.SpectateCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.StartCommand;
import bg.sofia.uni.fmi.mjt.uno.command.commands.SummaryCommand;
import bg.sofia.uni.fmi.mjt.uno.command.exceptions.IncorrectCommand;
import bg.sofia.uni.fmi.mjt.uno.pipeline.Session;

import java.util.ArrayList;
import java.util.List;

public final class CommandFactory {

    private static final int PLAYERS_IF_TOKEN_MISSING = 2;

    private static final String SPLIT_REGEX = " --";
    private static final int TOKEN_ONE = 1;
    private static final int TOKEN_TWO = 2;
    private static final int TOKEN_THREE = 3;
    private static final int TOKEN_FOUR = 4;

    private static final int SIZE_ONE = 1;
    private static final int SIZE_TWO = 2;
    private static final int SIZE_THREE = 3;
    private static final int SIZE_FOUR = 4;
    private static final int SIZE_FIVE = 5;

    public static Command create(String commandLine, Session session) throws IncorrectCommand {
        List<String> words = tokenize(commandLine);

        return switch (words.getFirst()) {
            case "register" -> registerCorrectness(words, session);
            case "login" -> loginCorrectness(words, session);
            case "logout" -> logoutCorrectness(words, session);
            case "list-games" -> listGamesCorrectness(words, session);
            case "create-game" -> createGameCorrectness(words, session);
            case "join" -> joinCorrectness(words, session);
            case "start" -> startCorrectness(words, session);
            case "show-hand" -> showHandCorrectness(words, session);
            case "show-last-card" -> showLastCardCorrectness(words, session);
            case "accept-effect" -> acceptEffectCorrectness(words, session);
            case "play" -> playCorrectness(words, session);
            case "play-choose", "play-plus-four" -> playJokerCorrectness(words, session);
            case "show-played-cards" -> showPlayedCardsCorrectness(words, session);
            case "leave" -> leaveCorrectness(words, session);
            case "spectate" -> spectateCorrectness(words, session);
            case "draw" -> drawCorrectness(words, session);
            case "summary" -> summaryCorrectness(words, session);
            default -> throw new IncorrectCommand("Unknown command");
        };
    }

    private static List<String> tokenize(String line) {
        String[] tokens = line.split(SPLIT_REGEX);

        List<String> profoundTokens = new ArrayList<>();
        for (String token : tokens) {
            if (token.contains("=")) {
                String[] parts = token.split("=", 2);
                profoundTokens.add(parts[0].trim());
                profoundTokens.add(parts[1].trim());
            } else {
                profoundTokens.add(token.trim());
            }
        }

        return profoundTokens;
    }

    private static Command registerCorrectness(List<String> tokens, Session session)
        throws IncorrectCommand {
        checkUsernamePassword(tokens);
        return new RegisterCommand(tokens.get(TOKEN_TWO), tokens.get(TOKEN_FOUR), session);
    }

    private static Command loginCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkUsernamePassword(tokens);
        return new LoginCommand(tokens.get(TOKEN_TWO), tokens.get(TOKEN_FOUR), session);
    }

    private static Command logoutCorrectness(List<String> tokens, Session session)
        throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new LogoutCommand(session);
    }

    private static Command listGamesCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_THREE);
        if (!tokens.get(TOKEN_ONE).equals("status")) {
            throw new IncorrectCommand("Incorrect command");
        }

        String predicate = tokens.get(TOKEN_TWO);
        if (predicate.isEmpty() || predicate.equals("all")) {
            return new ListGamesCommand("all", session);
        } else if (predicate.equals("started") || predicate.equals("ended") || predicate.equals("available")) {
            return new ListGamesCommand(predicate, session);
        }

        throw new IncorrectCommand("Unrecognizable command");
    }

    private static Command createGameCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_FIVE);
        if (!tokens.get(TOKEN_ONE).equals("number-of-players") || !tokens.get(TOKEN_THREE).equals("game-id")) {
            throw new IncorrectCommand("Incorrect command");
        }

        try {
            int numberOfPlayers;
            if (tokens.get(TOKEN_TWO).isEmpty()) {
                numberOfPlayers = PLAYERS_IF_TOKEN_MISSING;
            } else {
                numberOfPlayers = Integer.parseInt(tokens.get(TOKEN_TWO));
            }
            long gameId = Long.parseLong(tokens.get(TOKEN_FOUR));

            return new CreateGameCommand(numberOfPlayers, gameId, session);
        } catch (NumberFormatException e) {
            throw new IncorrectCommand("Game id and number of players must be numbers");
        }
    }

    private static Command joinCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_FIVE);
        if (!tokens.get(TOKEN_ONE).equals("game-id") || !tokens.get(TOKEN_THREE).equals("display-name")) {
            throw new IncorrectCommand("Incorrect command");
        }

        try {
            long gameId = Long.parseLong(tokens.get(TOKEN_TWO));
            String username = tokens.get(TOKEN_FOUR);
            if (username.isEmpty()) {
                return new JoinCommand(gameId, null, session);
            } else {
                return new JoinCommand(gameId, username, session);
            }
        } catch (NumberFormatException e) {
            throw new IncorrectCommand("Game id must be a number");
        }
    }

    private static Command startCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new StartCommand(session);
    }

    private static Command showHandCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new ShowHandCommand(session);
    }

    private static Command showLastCardCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new ShowLastCardCommand(session);
    }

    private static Command acceptEffectCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new AcceptEffectCommand(session);
    }

    private static Command playCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_THREE);
        if (tokens.get(TOKEN_ONE).equals("card-id")) {
            try {
                short cardId = Short.parseShort(tokens.get(TOKEN_TWO));
                return new PlayCommand(session, cardId);
            } catch (NumberFormatException e) {
                throw new IncorrectCommand("Card id must be a number");
            }
        }

        throw new IncorrectCommand("Unrecognizable command");
    }

    private static Command playJokerCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_FIVE);
        if (!tokens.get(TOKEN_ONE).equals("card-id") || !tokens.get(TOKEN_THREE).equals("color")) {
            throw new IncorrectCommand("Unrecognizable command");
        }

        try {
            short cardId = Short.parseShort(tokens.get(TOKEN_TWO));
            String color = tokens.get(TOKEN_FOUR);
            for (var colorType : ColorType.values()) {
                if (color.equals(colorType.toString().toLowerCase())) {
                    return new PlayJokerCommand(session, cardId, colorType);
                }
            }
        } catch (NumberFormatException e) {
            throw new IncorrectCommand("Card id must be a number", e);
        }

        throw new IncorrectCommand("Unrecognizable command");
    }

    private static Command showPlayedCardsCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new ShowPlayedCardsCommand(session);
    }

    private static Command leaveCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new LeaveCommand(session);
    }

    private static Command spectateCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new SpectateCommand(session);
    }

    private static Command drawCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_ONE);
        return new DrawCommand(session);
    }

    private static Command summaryCorrectness(List<String> tokens, Session session) throws IncorrectCommand {
        checkSize(tokens.size(), SIZE_THREE);
        if (!tokens.get(TOKEN_ONE).equals("game-id")) {
            throw new IncorrectCommand("Unrecognizable command");
        }
        try {
            long gameId = Long.parseLong(tokens.get(TOKEN_TWO));
            return new SummaryCommand(session, gameId);
        } catch (NumberFormatException e) {
            throw new IncorrectCommand("Game id must be a number");
        }
    }

    private static void checkSize(int actualSize, int expectedSize) throws IncorrectCommand {
        if (actualSize != expectedSize) {
            throw new IncorrectCommand("Unrecognizable command");
        }
    }

    private static void checkUsernamePassword(List<String> tokens) throws IncorrectCommand {
        if (tokens.size() != SIZE_FIVE ||
            !(tokens.get(TOKEN_ONE).equals("username") && tokens.get(TOKEN_THREE).equals("password"))) {
            throw new IncorrectCommand("Unrecognizable command");
        }

        String username = tokens.get(TOKEN_TWO);
        String password = tokens.get(TOKEN_FOUR);
        if (username.isBlank() || password.isBlank()) {
            throw new IncorrectCommand("Username and password cannot be blank");
        }
    }
}
