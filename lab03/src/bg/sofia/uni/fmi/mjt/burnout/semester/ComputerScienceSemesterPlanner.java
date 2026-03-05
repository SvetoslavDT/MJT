package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

final public class ComputerScienceSemesterPlanner extends AbstractSemesterPlanner {

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException {
        if (semesterPlan == null) {
            throw new IllegalArgumentException("Semester plan cannot be null");
        }

        if (checkForDuplicateCategories(semesterPlan.subjectRequirements())) {
            throw new InvalidSubjectRequirementsException("The semester requirements are not unique");
        }

        if (semesterPlan.minimalAmountOfCredits() == 0) {
            return new UniversitySubject[0];
        }

        UniversitySubject[] result = new UniversitySubject[semesterPlan.subjects().length];

        UniversitySubject[] sortedSubjects = sortSubjectsByCredits(semesterPlan.subjects());
        int foundCredits = 0;
        int sortedSubjectsIter = 0;

        for (int i = 0; i < sortedSubjects.length && foundCredits < semesterPlan.minimalAmountOfCredits(); i++) {
            if (sortedSubjects[i] == null) {
                continue;
            }

            result[sortedSubjectsIter++] = sortedSubjects[i];
            foundCredits += sortedSubjects[i].credits();
        }

        if (foundCredits < semesterPlan.minimalAmountOfCredits()) {
            throw new CryToStudentsDepartmentException("Not enough subjects to cover the minimal amount of credits");
        }

        UniversitySubject[] shrinkedResult = new UniversitySubject[sortedSubjectsIter];
        for (int i = 0; i < sortedSubjectsIter; i++) {
            shrinkedResult[i] = result[i];
        }

        return shrinkedResult;
    }

    private UniversitySubject[] sortSubjectsByCredits(UniversitySubject[] subjects) {
        UniversitySubject[] copyOfSubjects = new UniversitySubject[subjects.length];
        for (int i = 0; i < subjects.length; i++) {
            copyOfSubjects[i] = subjects[i];
        }

        for (int i = 0; i < subjects.length; i++) {
            for (int j = i + 1; j < subjects.length; j++) {
                if (copyOfSubjects[i].rating() < copyOfSubjects[j].rating()) {
                    UniversitySubject temp = copyOfSubjects[i];
                    copyOfSubjects[i] = copyOfSubjects[j];
                    copyOfSubjects[j] = temp;
                }
            }
        }

        return copyOfSubjects;
    }
}