package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;

public class CandidateSimilarityMatch {
    private static final double MAX_SIMILARITY_SCORE = 1.0;

    private final Candidate targetCandidate;
    private final Candidate similarCandidate;
    private final double similarityScore;

    public CandidateSimilarityMatch(Candidate targetCandidate, Candidate similarCandidate, double similarityScore) {
        if (targetCandidate == null) {
            throw new IllegalArgumentException("TargetCandidate cannot be null");
        }
        if (similarCandidate == null) {
            throw new IllegalArgumentException("SimilarCandidate cannot be null");
        }
        if (similarityScore <= 0.0 || similarityScore > MAX_SIMILARITY_SCORE) {
            throw new IllegalArgumentException("Similarity score must be between 0.0 and 1.0");
        }

        this.targetCandidate = targetCandidate;
        this.similarCandidate = similarCandidate;
        this.similarityScore = similarityScore;
    }

    public Candidate getTargetCandidate() {
        return targetCandidate;
    }

    public Candidate getSimilarCandidate() {
        return similarCandidate;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }
}
