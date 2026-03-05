package bg.sofia.uni.fmi.mjt.jobmatch.comparators;

import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;

import java.util.Comparator;

public class ComparatorJobCandidateMatch implements Comparator<CandidateJobMatch> {

    @Override
    public int compare(CandidateJobMatch o1, CandidateJobMatch o2) {
        int cmp = Double.compare(o1.getSimilarityScore(), o2.getSimilarityScore());

        if (cmp != 0) {
            return cmp;
        }

        return o2.getJobPosting().getTitle().compareTo(o1.getJobPosting().getTitle());
    }
}