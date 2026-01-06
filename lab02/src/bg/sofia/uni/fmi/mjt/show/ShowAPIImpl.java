package bg.sofia.uni.fmi.mjt.show;

import bg.sofia.uni.fmi.mjt.show.date.DateEvent;
import bg.sofia.uni.fmi.mjt.show.elimination.EliminationRule;
import bg.sofia.uni.fmi.mjt.show.elimination.LowestRatingEliminationRule;
import bg.sofia.uni.fmi.mjt.show.ergenka.Ergenka;

import java.util.Arrays;

public class ShowAPIImpl implements ShowAPI {

    private Ergenka[] ergenkas;
    private final EliminationRule[] eliminationRules;

    public ShowAPIImpl(Ergenka[] ergenkas, EliminationRule[] defaultEliminationRules) {
        this.ergenkas = ergenkas;
        this.eliminationRules = defaultEliminationRules == null ? new EliminationRule[0] :
                Arrays.copyOf(defaultEliminationRules, defaultEliminationRules.length);
    }

    @Override
    public Ergenka[] getErgenkas() {
        return ergenkas;
    }

    @Override
    public void playRound(DateEvent dateEvent) {
        for (Ergenka ergenka : ergenkas) {
            ergenka.reactToDate(dateEvent);
        }
    }

    @Override
    public void eliminateErgenkas(EliminationRule[] eliminationRules) {
        if (eliminationRules == null || eliminationRules.length == 0) {

            if (this.eliminationRules == null || this.eliminationRules.length == 0) {
                LowestRatingEliminationRule rule = new LowestRatingEliminationRule();
                ergenkas = rule.eliminateErgenkas(ergenkas);
            } else {
                for (EliminationRule rule : this.eliminationRules) {
                    if (rule == null) {
                        continue;
                    }
                    ergenkas = rule.eliminateErgenkas(ergenkas);
                }
            }
        } else {
            for (EliminationRule rule : eliminationRules) {
                if (rule == null) {
                    continue;
                }
                ergenkas = rule.eliminateErgenkas(ergenkas);
            }
        }
    }

    @Override
    public void organizeDate(Ergenka ergenka, DateEvent dateEvent) {
        if (ergenkas == null) {
            return;
        }

        ergenka.reactToDate(dateEvent);
    }
}