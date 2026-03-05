package bg.sofia.uni.fmi.mjt.jobmatch.model.match;

public record SkillRecommendation(String skillName, double improvementScore) {
    public SkillRecommendation {
        if (skillName == null || skillName.isBlank()) {
            throw new IllegalArgumentException("SkillName cannot be null or blank");
        }
        if (improvementScore < 0.0) {
            throw new IllegalArgumentException("ImprovementScore cannot be negative");
        }
    }
}
