package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashSet;
import java.util.Set;

public class JaccardSimilarity implements SimilarityStrategy {

    public JaccardSimilarity() {

    }

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null || jobSkills == null) {
            throw new IllegalArgumentException("candidateSkills and jobSkills cannot be null");
        }

        if (candidateSkills.isEmpty() && jobSkills.isEmpty()) {
            return 0.0;
        }

        Set<Skill> smaller = candidateSkills.size() <= jobSkills.size() ? candidateSkills : jobSkills;
        Set<Skill> bigger = candidateSkills.size() <= jobSkills.size() ? jobSkills : candidateSkills;

        Set<Skill> intersection = new HashSet<>(smaller);
        intersection.retainAll(bigger);

        int unionSize = candidateSkills.size() + jobSkills.size() - intersection.size();

        return (double) intersection.size() / unionSize;
    }
}