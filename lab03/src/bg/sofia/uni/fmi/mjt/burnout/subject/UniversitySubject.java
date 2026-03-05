package bg.sofia.uni.fmi.mjt.burnout.subject;

/**
 * @param name            the name of the subject
 * @param credits         number of credit hours for this subject
 * @param rating          difficulty rating of the subject (1-5)
 * @param category        the academic category this subject belongs to
 * @param neededStudyTime estimated study time in days required for this subject
 * @throws IllegalArgumentException if the name of the subject is null or blank
 * @throws IllegalArgumentException if the credits are not positive
 * @throws IllegalArgumentException if the rating is not in its bounds
 * @throws IllegalArgumentException if the Category is null
 * @throws IllegalArgumentException if the neededStudy time is not positive
 */
public record UniversitySubject(String name, int credits, int rating, Category category, int neededStudyTime) {

    public UniversitySubject {
        if (name == null || name.length() == 0) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        } else if (credits <= 0) {
            throw new IllegalArgumentException("Credits cannot be negative");
        } else if (rating <= 0 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 0 and 5");
        } else if (category == null) {
            throw new IllegalArgumentException("Category cannot be null");
        } else if (neededStudyTime <= 0) {
            throw new IllegalArgumentException("Needed studyTime cannot be negative");
        }
    }
}