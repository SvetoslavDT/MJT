package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.HashSet;
import java.util.Set;

public class Candidate {

    private final String name;
    private final String email;
    private final Set<Skill> skills;
    private final Education education;
    private final int yearsOfExperience;

    public Candidate(String name, String email, Set<Skill> skills, Education education, int yearsOfExperience) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name cannot be null or blank");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank");
        }
        if (skills == null || skills.isEmpty()) {
            throw new IllegalArgumentException("Skills cannot be null or empty");
        }
        if (yearsOfExperience < 0) {
            throw new IllegalArgumentException("Years of Experience cannot be negative");
        }

        this.name = name;
        this.email = email;
        this.skills = new HashSet<>(skills);
        this.education = education;
        this.yearsOfExperience = yearsOfExperience;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Set<Skill> getSkills() {
        return skills;
    }

    public Education getEducation() {
        return education;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }
}