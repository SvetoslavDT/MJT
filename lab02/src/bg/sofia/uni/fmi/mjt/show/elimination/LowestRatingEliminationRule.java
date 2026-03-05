package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowestRatingEliminationRule implements EliminationRule {

    public LowestRatingEliminationRule() {

    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        //  || ergenkas.length == 0
        if (ergenkas == null) {
            return new Ergenka[0];
        }

        int smallestRating = Integer.MAX_VALUE;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka != null && ergenka.getRating() < smallestRating) {
                smallestRating = ergenka.getRating();
            }
        }

        int count = 0;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka == null || ergenka.getRating() == smallestRating) {
                ++count;
            }
        }

        Ergenka[] newErgenkas = new Ergenka[ergenkas.length - count];
        count = 0;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka == null) {
                continue;
            }

            if (ergenka.getRating() != smallestRating) {
                newErgenkas[count++] = ergenka;
            }
        }

        return newErgenkas;
    }
}