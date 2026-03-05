package bg.sofia.uni.fmi.mjt.show.elimination;

import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

public class LowAttributeSumEliminationRule implements EliminationRule {

    private final int threshold;

    public LowAttributeSumEliminationRule(int threshold) {
        this.threshold = threshold;
    }

    @Override
    public Ergenka[] eliminateErgenkas(Ergenka[] ergenkas) {
        if (ergenkas == null || ergenkas.length == 0) {
            return new Ergenka[0];
        }

        int count = 0;
        int nullCount = 0;

        for (Ergenka ergenka : ergenkas) {
            if (ergenka == null) {
                nullCount++;
                continue;
            }
            if (ergenka.getHumorLevel() + ergenka.getRomanceLevel() < threshold) {
                count++;
            }
        }

        Ergenka[] newErgenkas = new Ergenka[ergenkas.length - count];
        count = 0;
        for (Ergenka ergenka : ergenkas) {
            if (ergenka == null) {
                continue;
            }
            if (ergenka.getHumorLevel() + ergenka.getRomanceLevel() >= threshold) {
                newErgenkas[count++] = ergenka;
            }
        }
        for (int i = 0; i < nullCount; i++) {
            newErgenkas[count++] = null;
        }

        return newErgenkas;
    }
}