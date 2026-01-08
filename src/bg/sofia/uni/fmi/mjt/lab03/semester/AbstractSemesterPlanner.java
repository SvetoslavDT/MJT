package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.DisappointmentException;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

abstract sealed class AbstractSemesterPlanner implements SemesterPlannerAPI permits SoftwareEngineeringSemesterPlanner, ComputerScienceSemesterPlanner {

    @Override
    public final int calculateJarCount(UniversitySubject[] subjects, int maximumSlackTime, int semesterDuration) {

        if (subjects == null || subjects.length == 0 || maximumSlackTime <= 0 || semesterDuration <= 0) {
            throw new IllegalArgumentException("Arguments cannot be null, empty or negative");
        }

        int jarCount = 0;
        int studyDays = 0;
        int restDays = 0;

        for (UniversitySubject subject : subjects) {
            if (subject == null) {
                continue;
            }

            studyDays += subject.neededStudyTime();
            jarCount += subject.neededStudyTime() / 5;
            restDays += roundUp(subject.neededStudyTime() * subject.category().getCoefficient());

            if (restDays > maximumSlackTime) {
                throw new DisappointmentException("The maximum slack time reached");
            }
        }

        if (studyDays + restDays > semesterDuration) {
            jarCount *= 2;
        }

        return jarCount;
    }

    protected boolean checkForDuplicateCategories(SubjectRequirement[] subjectRequirements) {
        boolean[] seen = new boolean[Category.values().length];

        for (SubjectRequirement requirement : subjectRequirements) {
            if (requirement == null) {
                continue;
            }

            if (seen[requirement.category().ordinal()]) {
                return true;
            }
            seen[requirement.category().ordinal()] = true;
        }

        return false;
    }

    private int roundUp(double x) {
        int intPart = (int) x;

        if (x > intPart) {
            return intPart + 1;
        }

        if (x < intPart) {
            return intPart;
        }

        return intPart;
    }
}