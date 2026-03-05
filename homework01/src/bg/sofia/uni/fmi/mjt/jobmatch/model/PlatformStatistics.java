package bg.sofia.uni.fmi.mjt.jobmatch.model;

public record PlatformStatistics(int totalCandidates, int totalEmployers, int totalJobPostings,
                                 String mostCommonSkillName, String highestPaidJobTitle) {
}