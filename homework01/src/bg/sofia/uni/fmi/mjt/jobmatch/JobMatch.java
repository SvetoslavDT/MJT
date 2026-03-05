package bg.sofia.uni.fmi.mjt.jobmatch;

import bg.sofia.uni.fmi.mjt.jobmatch.api.JobMatchAPI;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.ComparatorCandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.ComparatorJobCandidateMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.ComparatorSkillRecommendation;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.CandidateNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.JobPostingNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserAlreadyExistsException;
import bg.sofia.uni.fmi.mjt.jobmatch.exceptions.UserNotFoundException;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.CosineSimilarity;
import bg.sofia.uni.fmi.mjt.jobmatch.matching.SimilarityStrategy;
import bg.sofia.uni.fmi.mjt.jobmatch.model.PlatformStatistics;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Candidate;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Employer;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.JobPosting;
import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.CandidateSimilarityMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.comparators.ComparatorCandidateJobMatch;
import bg.sofia.uni.fmi.mjt.jobmatch.model.match.SkillRecommendation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;

public class JobMatch implements JobMatchAPI {

    private final Map<String, Candidate> candidateByEmail;
    private final Map<String, Employer> employerByEmail;
    private final Map<String, JobPosting> jobPostingById;

    public JobMatch() {
        candidateByEmail = new HashMap<>();
        employerByEmail = new HashMap<>();
        jobPostingById = new HashMap<>();
    }

    @Override
    public Candidate registerCandidate(Candidate candidate) {
        if (candidate == null) {
            throw new IllegalArgumentException("Candidate cannot be null");
        }
        if (candidateByEmail.containsKey(candidate.getEmail())) {
            throw new UserAlreadyExistsException("Candidate already exists");
        }

        candidateByEmail.put(candidate.getEmail(), candidate);

        return candidate;
    }

    @Override
    public Employer registerEmployer(Employer employer) {
        if (employer == null) {
            throw new IllegalArgumentException("Employer cannot be null");
        }
        if (employerByEmail.containsKey(employer.email())) {
            throw new UserAlreadyExistsException("Employer already exists");
        }

        employerByEmail.put(employer.email(), employer);

        return employer;
    }

    @Override
    public JobPosting postJobPosting(JobPosting jobPosting) {
        if (jobPosting == null) {
            throw new IllegalArgumentException("JobPosting cannot be null");
        }
        if (!employerByEmail.containsKey(jobPosting.getEmployerEmail())) {
            throw new UserNotFoundException("Employer not registered");
        }

        jobPostingById.put(jobPosting.getId(), jobPosting);

        return jobPosting;
    }

    @Override
    public List<CandidateJobMatch> findTopNCandidatesForJob(String jobPostingId, int limit,
                                                            SimilarityStrategy strategy) {
        checkThreeParameters(jobPostingId, limit, strategy);
        JobPosting jobPost = jobPostingById.get(jobPostingId);

        if (jobPost == null) {
            throw new JobPostingNotFoundException("JobPosting not found");
        }

        ComparatorCandidateJobMatch comp = new ComparatorCandidateJobMatch();
        PriorityQueue<CandidateJobMatch> pq = new PriorityQueue<>(limit, comp);

        double similarity;
        for (Candidate candidate : candidateByEmail.values()) {

            similarity = strategy.calculateSimilarity(candidate.getSkills(), jobPost.getRequiredSkills());
            if (similarity == 0.0) {
                continue;
            }

            CandidateJobMatch match = new CandidateJobMatch(candidate, jobPost, similarity);
            offerToTopN(pq, comp, match, limit);
        }

        return resultReturn(pq);
    }

    @Override
    public List<CandidateJobMatch> findTopNJobsForCandidate(String candidateEmail, int limit,
                                                            SimilarityStrategy strategy) {
        checkThreeParameters(candidateEmail, limit, strategy);
        Candidate candidate = candidateByEmail.get(candidateEmail);

        if (candidate == null) {
            throw new CandidateNotFoundException("Candidate not found");
        }

        ComparatorJobCandidateMatch comp = new ComparatorJobCandidateMatch();
        PriorityQueue<CandidateJobMatch> pq = new PriorityQueue<>(limit, comp);

        double similarity;
        for (JobPosting jobPosting : jobPostingById.values()) {

            similarity = strategy.calculateSimilarity(candidate.getSkills(), jobPosting.getRequiredSkills());
            if (similarity == 0.0) {
                continue;
            }

            CandidateJobMatch match = new CandidateJobMatch(candidate, jobPosting, similarity);
            offerToTopN(pq, comp, match, limit);
        }

        return resultReturn(pq);
    }

    @Override
    public List<CandidateSimilarityMatch> findSimilarCandidates(String candidateEmail, int limit,
                                                                SimilarityStrategy strategy) {
        checkThreeParameters(candidateEmail, limit, strategy);

        Candidate target = candidateByEmail.get(candidateEmail);
        if (target == null) {
            throw new CandidateNotFoundException("Candidate not found");
        }

        ComparatorCandidateSimilarityMatch comp = new ComparatorCandidateSimilarityMatch();
        PriorityQueue<CandidateSimilarityMatch> pq = new PriorityQueue<>(limit, comp);

        double similarity;
        for (Candidate other : candidateByEmail.values()) {
            similarity = strategy.calculateSimilarity(target.getSkills(), other.getSkills());

            if (other.getEmail().equals(candidateEmail) || similarity == 0.0) {
                continue;
            }

            CandidateSimilarityMatch match = new CandidateSimilarityMatch(target, other, similarity);
            offerToTopN(pq, comp, match, limit);
        }
        if (pq.isEmpty()) {
            return List.of();
        }

        CandidateSimilarityMatch[] pqArray = new CandidateSimilarityMatch[pq.size()];
        for (int i = pq.size() - 1; i >= 0; --i) {
            pqArray[i] = pq.poll();
        }

        return Collections.unmodifiableList(Arrays.asList(pqArray));
    }

    @Override
    public List<SkillRecommendation> getSkillRecommendationsForCandidate(String candidateEmail, int limit) {
        checkTwoParameters(candidateEmail, limit);
        Candidate target = candidateByEmail.get(candidateEmail);
        if (target == null) {
            throw new CandidateNotFoundException("Candidate not found");
        }
        Set<String> missingSkillNames = getMissingSkillNames(target);
        if (missingSkillNames.isEmpty()) {
            return List.of();
        }
        SimilarityStrategy strategy = new CosineSimilarity();
        List<JobPosting> jobs = new ArrayList<>(jobPostingById.values());
        double[] similarities = new double[jobs.size()];
        double similarity;
        for (int i = 0; i < jobs.size(); ++i) {
            similarity = strategy.calculateSimilarity(target.getSkills(), jobs.get(i).getRequiredSkills());
            similarities[i] = similarity;
        }

        ComparatorSkillRecommendation comp = new ComparatorSkillRecommendation();
        PriorityQueue<SkillRecommendation> pq = new PriorityQueue<>(limit, comp);
        Map<String, Integer> maxLevelOfSkills = getMaxLevelsOfSkills();

        for (String missingName : missingSkillNames) {
            double totalImprovement = computeTotalImprovementForSkill(missingName, target, jobs, similarities,
                    strategy, maxLevelOfSkills);
            if (totalImprovement <= 0.0) {
                continue;
            }
            offerToTopN(pq, comp, new SkillRecommendation(missingName, totalImprovement), limit);
        }
        return resultReturn(pq);
    }

    @Override
    public PlatformStatistics getPlatformStatistics() {

        int totalCandidates = candidateByEmail.size();
        int totalEmployers = employerByEmail.size();
        int totalJobPostings = jobPostingById.size();

        String mostCommonSkillName = getMostCommonSkillNameFromCandidates();
        String bestPaidJobTitle = getBestPaidJobTitle();

        return new PlatformStatistics(totalCandidates, totalEmployers, totalJobPostings,
                mostCommonSkillName, bestPaidJobTitle);
    }

    private String getBestPaidJobTitle() {
        if (jobPostingById.isEmpty()) {
            return null;
        }

        String bestPaidJobTitle = null;
        double bestSalary = 0.0;

        double salary;
        String title;
        for (JobPosting post : jobPostingById.values()) {

            salary = post.getSalary();
            title = post.getTitle();

            if (salary > bestSalary) {
                bestSalary = salary;
                bestPaidJobTitle = title;
            } else if (salary == bestSalary) {
                if (title.compareTo(bestPaidJobTitle) < 0) {
                    bestPaidJobTitle = title;
                }
            }
        }

        return bestPaidJobTitle;
    }

    private String getMostCommonSkillNameFromCandidates() {
        if (candidateByEmail.isEmpty()) {
            return null;
        }
        String mostCommonSkillName = null;
        Map<String, Integer> counter = new HashMap<>();

        for (Candidate candidate : candidateByEmail.values()) {
            for (Skill skill : candidate.getSkills()) {

                String name = skill.name();
                Integer previous = counter.get(name);
                counter.put(name, previous == null ? 1 : previous + 1);
            }
        }

        int maxCount = -1;
        for (var entry : counter.entrySet()) {
            String name = entry.getKey();
            Integer count = entry.getValue();

            if (count > maxCount) {
                maxCount = count;
                mostCommonSkillName = name;
            } else if (count == maxCount) {
                if (name.compareTo(mostCommonSkillName) < 0) {
                    mostCommonSkillName = name;
                }
            }
        }

        return mostCommonSkillName;
    }

    private static void checkThreeParameters(String str, int limit, SimilarityStrategy strategy) {
        checkTwoParameters(str, limit);

        if (strategy == null) {
            throw new IllegalArgumentException("strategy cannot be null");
        }
    }

    private static void checkTwoParameters(String str, int limit) {
        if (str == null || str.isBlank()) {
            throw new IllegalArgumentException("String cannot be null or blank");
        } else if (limit <= 0) {
            throw new IllegalArgumentException("limit must be greater than 0");
        }
    }

    private Map<String, Integer> getMaxLevelsOfSkills() {
        Map<String, Integer> result = new HashMap<>();

        for (JobPosting jp : jobPostingById.values()) {

            for (Skill skill : jp.getRequiredSkills()) {

                Integer prev = result.get(skill.name());

                if (prev == null || skill.level() > prev) {
                    result.put(skill.name(), skill.level());
                }

            }
        }

        return result;
    }

    private static <T> void offerToTopN(PriorityQueue<T> pq, Comparator<? super T> comp, T match, int limit) {
        if (pq.size() < limit) {
            pq.offer(match);
        }

        T worst = pq.peek();
        if (comp.compare(worst, match) > 0) {
            pq.poll();
            pq.offer(match);
        }
    }

    private static <T> List<T> resultReturn(PriorityQueue<T> pq) {
        if (pq == null || pq.isEmpty()) {
            return List.of();
        }

        List<T> list = new ArrayList<>(pq.size());
        while (!pq.isEmpty()) {
            list.add(pq.poll());
        }

        return Collections.unmodifiableList(list.reversed());
    }

    private Set<String> getMissingSkillNames(Candidate target) {
        List<JobPosting> jobs = new ArrayList<>(jobPostingById.values());
        Set<String> candidateSkillNames = new HashSet<>();

        for (Skill skill : target.getSkills()) {
            candidateSkillNames.add(skill.name());
        }

        Set<String> missingSkillNames = new HashSet<>();
        for (JobPosting jobPosting : jobs) {

            for (Skill skill : jobPosting.getRequiredSkills()) {

                if (!candidateSkillNames.contains(skill.name())) {
                    missingSkillNames.add(skill.name());
                }
            }
        }

        return missingSkillNames;
    }

    private static double computeTotalImprovementForSkill(String missingName, Candidate target, List<JobPosting> jobs,
                                                          double[] baseSimilarities, SimilarityStrategy strategy,
                                                          Map<String, Integer> maxLevelOfSkills) {
        Integer level = maxLevelOfSkills.get(missingName);
        Set<Skill> candidateWithAdded = new HashSet<>(target.getSkills());
        candidateWithAdded.add(new Skill(missingName, level));

        double total = 0.0;
        double similarity;

        for (int i = 0; i < jobs.size(); i++) {
            similarity = strategy.calculateSimilarity(candidateWithAdded, jobs.get(i).getRequiredSkills());
            total += (similarity - baseSimilarities[i]);
        }

        return total;
    }
}