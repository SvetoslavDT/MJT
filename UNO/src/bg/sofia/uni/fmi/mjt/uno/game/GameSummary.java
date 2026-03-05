package bg.sofia.uni.fmi.mjt.uno.game;

public class GameSummary {

    private int number;
    private final StringBuilder text;

    public GameSummary() {
        number = 1;
        text = new StringBuilder();
        text.append("Congratulations!").append(System.lineSeparator());
    }

    public void addWinner(String username) {
        text.append(number).append(" place winner is - ").append(username).append(System.lineSeparator());
        number++;
    }

    public void addLoser(String username) {
        text.append(number).append(" loser of the game is - ").append(username).append(System.lineSeparator());
    }

    @Override
    public String toString() {
        return text.toString();
    }
}
