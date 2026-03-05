package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.*;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.List;
import java.util.Set;

public class JobMatchDemo {
    public static void main(String[] args) {
        JobMatch jobMatch = new JobMatch();

        // 1) регистрираме работодател
        Employer acme = new Employer("Acme Corp", "hr@acme.com");
        jobMatch.registerEmployer(acme);

        // 2) създаваме и публикуваме няколко обяви
        JobPosting backend = new JobPosting(
                "job-1",
                "Backend Developer",
                "hr@acme.com",
                Set.of(new Skill("Java", 5), new Skill("SQL", 4), new Skill("AWS", 3)),
                Education.BACHELORS,
                2,
                4500.0
        );

        JobPosting devops = new JobPosting(
                "job-2",
                "DevOps Engineer",
                "hr@acme.com",
                Set.of(new Skill("AWS", 4), new Skill("Docker", 3), new Skill("Linux", 3)),
                Education.BACHELORS,
                3,
                4800.0
        );

        jobMatch.postJobPosting(backend);
        jobMatch.postJobPosting(devops);

        // 3) регистрираме кандидати
        Candidate alice = new Candidate(
                "Alice Ivanova",
                "alice@example.com",
                Set.of(new Skill("Java", 4), new Skill("Python", 3)),
                Education.BACHELORS,
                1
        );

        Candidate bob = new Candidate(
                "Bob Petrov",
                "bob@example.com",
                Set.of(new Skill("AWS", 4), new Skill("Docker", 2)),
                Education.BACHELORS,
                2
        );

        Candidate clara = new Candidate(
                "Clara Georgieva",
                "clara@example.com",
                Set.of(new Skill("Java", 5), new Skill("SQL", 3), new Skill("AWS", 2)),
                Education.MASTERS,
                4
        );

        jobMatch.registerCandidate(alice);
        jobMatch.registerCandidate(bob);
        jobMatch.registerCandidate(clara);

        // 4) дефинираме стратегията (по подразбиране Cosine)
        SimilarityStrategy strategy = new CosineSimilarity();

        // 5) find top candidates for job-1
        System.out.println("Top candidates for Backend Developer:");
        List<CandidateJobMatch> topCandidates = jobMatch.findTopNCandidatesForJob("job-1", 5, strategy);
        for (CandidateJobMatch m : topCandidates) {
            System.out.printf("  %s (email=%s) -> score=%.4f%n",
                    m.getCandidate().getName(), m.getCandidate().getEmail(), m.getSimilarityScore());
        }

        // 6) find top jobs for a candidate
        System.out.println("\nTop jobs for Alice:");
        List<CandidateJobMatch> topJobsForAlice = jobMatch.findTopNJobsForCandidate("alice@example.com", 5, strategy);
        for (CandidateJobMatch m : topJobsForAlice) {
            System.out.printf("  %s (id=%s) -> score=%.4f%n",
                    m.getJobPosting().getTitle(), m.getJobPosting().getId(), m.getSimilarityScore());
        }

        // 7) find similar candidates to Alice
        System.out.println("\nCandidates similar to Alice:");
        List<CandidateSimilarityMatch> similarToAlice = jobMatch.findSimilarCandidates("alice@example.com", 5, strategy);
        for (CandidateSimilarityMatch s : similarToAlice) {
            System.out.printf("  %s (email=%s) -> score=%.4f%n",
                    s.getSimilarCandidate().getName(), s.getSimilarCandidate().getEmail(), s.getSimilarityScore());
        }

        // 8) skill recommendations for Alice
        System.out.println("\nSkill recommendations for Alice:");
        List<SkillRecommendation> recs = jobMatch.getSkillRecommendationsForCandidate("alice@example.com", 5);
        for (SkillRecommendation r : recs) {
            System.out.printf("  %s -> improvement=%.4f%n", r.skillName(), r.improvementScore());
        }

        // 9) platform statistics
        System.out.println("\nPlatform statistics:");
        PlatformStatistics stats = jobMatch.getPlatformStatistics();
        System.out.printf("  totalCandidates=%d, totalEmployers=%d, totalJobPostings=%d%n",
                stats.totalCandidates(), stats.totalEmployers(), stats.totalJobPostings());
        System.out.printf("  mostCommonSkill=%s, highestPaidJob=%s%n",
                stats.mostCommonSkillName(), stats.highestPaidJobTitle());
    }
}
