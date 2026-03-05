package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Comparator;

public class ComparatorCandidateJobMatch implements Comparator<CandidateJobMatch> {

    @Override
    public int compare(CandidateJobMatch o1, CandidateJobMatch o2) {
        int cmp = Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore());

        if (cmp != 0) {
            return cmp;
        }

        return o2.getCandidate().getName().compareTo(o1.getCandidate().getName());
    }
}
