package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;

import java.util.Comparator;

public class ComparatorCandidateSimilarityMatch implements Comparator<CandidateSimilarityMatch> {

    @Override
    public int compare(CandidateSimilarityMatch o1, CandidateSimilarityMatch o2) {
        int cmp = Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore());

        if (cmp != 0) {
            return cmp;
        }

        return o1.getSimilarCandidate().getName().compareTo(o2.getSimilarCandidate().getName());
    }
}