package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;

public class CandidateJobMatch {
    private static final double MAX_SIMILARITY_SCORE = 1.0;

    private final Candidate candidate;
    private final JobPosting jobPosting;
    private final double similarityScore;

    public CandidateJobMatch(Candidate candidate, JobPosting jobPosting, double similarityScore) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }
        if (jobPosting == null) {
            throw new IllegalArgumentException("JobPosting cannot be null");
        }
        if (similarityScore <= 0.0 || similarityScore > MAX_SIMILARITY_SCORE) {
            throw new IllegalArgumentException("Similarity score must be between 0.0 and 1.0");
        }

        this.candidate = candidate;
        this.jobPosting = jobPosting;
        this.similarityScore = similarityScore;
    }

    public Candidate getCandidate() {
        // Candidate is immutable ( no altering methods exist in the class )
        return candidate;
    }

    public JobPosting getJobPosting() {
        return jobPosting;
    }

    public double getSimilarityScore() {
        return similarityScore;
    }
}
