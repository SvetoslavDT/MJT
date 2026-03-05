package bg.sofia.uni.fmi.mjt.jobmatch.matching;

import bg.sofia.uni.fmi.mjt.jobmatch.model.entity.Skill;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CosineSimilarity implements SimilarityStrategy {

    public CosineSimilarity() {

    }

    @Override
    public double calculateSimilarity(Set<Skill> candidateSkills, Set<Skill> jobSkills) {
        if (candidateSkills == null || jobSkills == null) {
            throw new IllegalArgumentException("candidateSkills and jobSkills cannot be null");
        }

        Map<String, Integer> candidateMap = new HashMap<>();
        for (Skill skill : candidateSkills) {
            candidateMap.putIfAbsent(skill.name(), skill.level());
        }

        Map<String, Integer> jobMap = new HashMap<>();
        for (Skill jobSkill : jobSkills) {
            jobMap.putIfAbsent(jobSkill.name(), jobSkill.level());
        }

        int sumOfSquaresCandidate = getSumOfSquares(candidateMap);
        int sumOfSquaresJob = getSumOfSquares(jobMap);

        if (sumOfSquaresCandidate == 0.0 || sumOfSquaresJob == 0.0) {
            return 0.0;
        }

        Map<String, Integer> smaller = candidateMap.size() <= jobMap.size() ? candidateMap : jobMap;
        Map<String, Integer> bigger = candidateMap.size() <= jobMap.size() ? jobMap : candidateMap;

        int product = getProduct(smaller, bigger);

        return product / (Math.sqrt((double) sumOfSquaresCandidate) * Math.sqrt((double) sumOfSquaresJob));
    }

    private int getProduct(Map<String, Integer> smallerMap, Map<String, Integer> biggerMap) {
        int product = 0;

        for (var entry : smallerMap.entrySet()) {

            Integer other = biggerMap.get(entry.getKey());

            if (other != null) {
                product += entry.getValue() * other;
            }
        }

        return product;
    }

    private int getSumOfSquares(Map<String, Integer> set) {
        int sum = 0;

        for (int level : set.values()) {
            sum += level * level;
        }

        return sum;
    }
}