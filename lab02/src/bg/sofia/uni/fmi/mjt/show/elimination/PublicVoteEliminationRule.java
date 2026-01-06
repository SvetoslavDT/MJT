package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class PublicVoteEliminationRule implements EliminationRule {

    private final String[] votes;

    public PublicVoteEliminationRule(String[] votes) {
        if (votes == null) {
            this.votes = new String[0];
        } else {
            this.votes = Arrays.copyOf(votes, votes.length);
        }
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        //  || ergenkas.length == 0
        if (ergenkas == null) {
            return new Ergenka[0];
        }

        String toEliminate = findMajority(votes);
        if (toEliminate == null) {
            return Arrays.copyOf(ergenkas, ergenkas.length);
        }

        boolean nameExists = false;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka != null && ergenka.getName().equals(toEliminate)) {
                nameExists = true;
            }
        }

        if (!nameExists) {
            return Arrays.copyOf(ergenkas, ergenkas.length);
        }

        Ergenka[] newErgenkas = new Ergenka[ergenkas.length - 1];
        int idx = 0;
        boolean found = false;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka == null) {
                newErgenkas[idx++] = ergenka;
            } else if (ergenka.getName().equals(toEliminate) && !found) {
                found = true;
            } else if (ergenka.getName().equals(toEliminate) && found) {
                newErgenkas[idx++] = ergenka;
            } else if (!ergenka.getName().equals(toEliminate)) {
                newErgenkas[idx++] = ergenka;
            }
        }

        return newErgenkas;
    }

    private String findMajority(String[] votes) {
        //  || votes.length == 0
        if (votes == null) {
            return null;
        }

        String candidate = null;
        int count = 0;
        int nonNullCount = 0;

        for (String vote : votes) {
            if (vote == null) {
                continue;
            }
            nonNullCount++;
            if (count == 0) {
                candidate = vote;
                count = 1;
            } else if (vote.equals(candidate)) {
                count++;
            } else {
                count--;
            }
        }

        if (candidate == null) {
            return null;
        }

        int frequency = 0;
        for (String vote : votes) {
            if (vote != null && vote.equals(candidate)) {
                frequency++;
            }
        }

        return frequency > nonNullCount / 2 ? candidate : null;
    }
}