package bg.sofia.uni.fmi.mjt.jobmatch.model.entity;

import java.util.HashSet;
import java.util.Set;

public class JobPosting {

    private final String id;
    private final String title;
    private final String employerEmail;
    private final Set<Skill> requiredSkills;
    private final Education RequiredEducation;
    private final int requiredYearsOfExperience;
    private final double salary;

    public JobPosting(String id, String title, String employerEmail, Set<Skill> requiredSkills,
                      Education requiredEducation, int requiredYearsOfExperience, double salary) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Id cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (employerEmail == null || employerEmail.isBlank()) {
            throw new IllegalArgumentException("Employer Email cannot be null or blank");
        }
        if (requiredSkills == null || requiredSkills.isEmpty()) {
            throw new IllegalArgumentException("Required Skills cannot be null or empty");
        }
        if (requiredYearsOfExperience < 0) {
            throw new IllegalArgumentException("Required Years of Experience cannot be negative");
        }
        if (salary < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }

        this.id = id;
        this.title = title;
        this.employerEmail = employerEmail;
        this.requiredSkills = new HashSet<>(requiredSkills);
        this.RequiredEducation = requiredEducation;
        this.requiredYearsOfExperience = requiredYearsOfExperience;
        this.salary = salary;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getEmployerEmail() {
        return employerEmail;
    }

    public Set<Skill> getRequiredSkills() {
        return requiredSkills;
    }

    public Education getRequiredEducation() {
        return RequiredEducation;
    }

    public int getRequiredYearsOfExperience() {
        return requiredYearsOfExperience;
    }

    public double getSalary() {
        return salary;
    }
}