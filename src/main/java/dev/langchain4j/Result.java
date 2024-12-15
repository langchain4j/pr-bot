package dev.langchain4j;

import dev.langchain4j.model.output.structured.Description;

import java.util.List;

public record Result(
        @Description("Are there any changes to the production Java code?")
        boolean containsProductionCodeChanges,
        @Description("Are there any changes to the test Java code?")
        boolean containsTestCodeChanges,

        @Description("Potential breaking changes to the public APIs that might affect consumers of those APIs")
        List<PotentialProblem> potentialBreakingChanges,
        @Description("What potential design issues this PR might introduce?")
        List<PotentialProblem> potentialDesignIssues,
        @Description("What potential bugs this PR might introduce?")
        List<PotentialProblem> potentialBugs,

        @Description("Suggest positive test scenarios related to the changes in this PR")
        List<TestScenario> positiveTestScenarios,
        @Description("Suggest negative test scenarios related to the changes in this PR")
        List<TestScenario> negativeTestScenarios,
        @Description("Suggest corner case test scenarios related to the changes in this PR")
        List<TestScenario> cornerCaseTestScenarios,
        @Description("Are changes in this PR sufficiently tested?")
        boolean changesAreSufficientlyTested,

        @Description("Are changes in this PR sufficiently documented?")
        boolean changesAreSufficientlyDocumented
) {
}
