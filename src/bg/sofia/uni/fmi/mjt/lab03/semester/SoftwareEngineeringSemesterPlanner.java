package bg.sofia.uni.fmi.mjt.burnout.semester;

import bg.sofia.uni.fmi.mjt.burnout.exception.CryToStudentsDepartmentException;
import bg.sofia.uni.fmi.mjt.burnout.exception.InvalidSubjectRequirementsException;
import bg.sofia.uni.fmi.mjt.burnout.plan.SemesterPlan;
import bg.sofia.uni.fmi.mjt.burnout.subject.Category;
import bg.sofia.uni.fmi.mjt.burnout.subject.SubjectRequirement;
import bg.sofia.uni.fmi.mjt.burnout.subject.UniversitySubject;

public final class SoftwareEngineeringSemesterPlanner extends AbstractSemesterPlanner {

    @Override
    public UniversitySubject[] calculateSubjectList(SemesterPlan semesterPlan) throws InvalidSubjectRequirementsException {

        if (semesterPlan == null) {
            throw new IllegalArgumentException("Semester plan cannot be null");
        }

        if (checkForDuplicateCategories(semesterPlan.subjectRequirements())) {
            throw new InvalidSubjectRequirementsException("The semester requirements are not unique");
        }

        //Making a sorted copy of the subjects
        UniversitySubject[] sortedSubjects = sortSubjectsByCredits(semesterPlan.subjects());
        boolean[] used = new boolean[sortedSubjects.length];
        UniversitySubject[] result = new UniversitySubject[sortedSubjects.length];
        int resultIter = 0;
        int foundCredits = 0;

        for (SubjectRequirement requirement : semesterPlan.subjectRequirements()) {
            if (requirement == null) {
                continue;
            }

            Category neededCategory = requirement.category();
            int neededAmount = requirement.minAmountEnrolled();

            int found = 0;
            for (int i = 0; i < sortedSubjects.length && found < neededAmount; i++) {
                UniversitySubject subject = sortedSubjects[i];

                if (subject == null) {
                    continue;
                }

                if (!used[i] && subject.category().equals(neededCategory)) {
                    used[i] = true;
                    result[resultIter++] = subject;
                    foundCredits += subject.credits();
                    ++found;
                }
            }

            if (found < neededAmount) {
                throw new CryToStudentsDepartmentException("Not enough subjects in category");
            }
        }

        if (foundCredits >= semesterPlan.minimalAmountOfCredits()) {
            UniversitySubject[] cutResult = new UniversitySubject[resultIter];
            for (int i = 0; i < cutResult.length; i++) {
                cutResult[i] = result[i];
            }

            return cutResult;
        }

        for (int i = 0; i < sortedSubjects.length && foundCredits < semesterPlan.minimalAmountOfCredits(); i++) {
            if (!used[i]) {
                UniversitySubject subj = sortedSubjects[i];
                if (subj == null) {
                    continue;
                }
                used[i] = true;
                result[resultIter++] = subj;
                foundCredits += subj.credits();
            }
        }

        if (foundCredits < semesterPlan.minimalAmountOfCredits()) {
            throw new CryToStudentsDepartmentException("Not enough subjects to cover the needed credits");
        }

        UniversitySubject[] cutResult = new UniversitySubject[resultIter];
        for (int i = 0; i < resultIter; i++) {
            cutResult[i] = result[i];
        }

        return cutResult;
    }

    private UniversitySubject[] sortSubjectsByCredits(UniversitySubject[] subjects) {
        UniversitySubject[] copyOfSubjects = new UniversitySubject[subjects.length];
        for (int i = 0; i < subjects.length; i++) {
            copyOfSubjects[i] = subjects[i];
        }

        for (int i = 0; i < subjects.length; i++) {
            if (copyOfSubjects[i] == null) {
                continue;
            }
            for (int j = i + 1; j < subjects.length; j++) {
                if (copyOfSubjects[j] == null) {
                    continue;
                }
                if (copyOfSubjects[i].credits() < copyOfSubjects[j].credits()) {
                    UniversitySubject temp = copyOfSubjects[i];
                    copyOfSubjects[i] = copyOfSubjects[j];
                    copyOfSubjects[j] = temp;
                }
            }
        }

        return copyOfSubjects;
    }
}