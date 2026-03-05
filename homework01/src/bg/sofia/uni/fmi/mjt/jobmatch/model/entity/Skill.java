package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

public record Skill(String name, int level) {
    private static final int MAX_SKILL_LEVEL = 5;

    public Skill {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Skill name cannot be null or blank");
        }
        if (level < 0 || level > MAX_SKILL_LEVEL) {
            throw new IllegalArgumentException("Skill level must be between 0 and 5");
        }
    }
}