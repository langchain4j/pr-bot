package dev.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.service.AiServices;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubapp.event.PullRequestTarget;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.kohsuke.github.GHPullRequestFileDetail;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static dev.langchain4j.model.chat.request.ResponseFormat.JSON;

public class MyAction {

    private static final String YES = "✅";
    private static final String NO = "❌";

    private static final String MEDIUM = "⚠\uFE0F";
    private static final String MAJOR = "\uD83D\uDD25";
    private static final String CRITICAL = "\uD83D\uDEA8";

    static ChatLanguageModel MODEL = GoogleAiGeminiChatModel.builder()
            .apiKey(System.getenv("GOOGLE_AI_GEMINI_API_KEY")) // TODO get from action inputs?
            .modelName("gemini-2.0-flash-exp") // TODO get from action inputs?
            .responseFormat(JSON)
            .temperature(0.0) // TODO get from action inputs?
            .logRequestsAndResponses(true) // TODO get from action inputs?
            .build();

    static DiffAnalyzer DIFF_ANALYZER = AiServices.builder(DiffAnalyzer.class)
            .chatLanguageModel(MODEL)
            .build();

    @Action
    void action(@PullRequestTarget GHEventPayload.PullRequest pullRequest, Commands commands) throws IOException {
        GHPullRequest pr = pullRequest.getPullRequest();
        commands.notice("pullRequest.getChanges(): " + pullRequest.getChanges());

        commands.notice("pr.getPatchUrl(): " + pr.getPatchUrl());
        commands.notice("pr.getUrl(): " + pr.getUrl());
        commands.notice("pr.getHtmlUrl(): " + pr.getHtmlUrl());
        commands.notice("pr.getDiffUrl(): " + pr.getDiffUrl());
        commands.notice("pr.getIssueUrl(): " + pr.getIssueUrl());

        commands.notice("getSender: " + pullRequest.getSender());
        commands.notice("pr.getTitle(): " + pr.getTitle());
        commands.notice("pr.getBody(): " + pr.getBody());

        StringBuilder diffBuilder = new StringBuilder();

        for (GHPullRequestFileDetail file : pr.listFiles()) {
            commands.notice("file.getFilename():  " + file.getFilename());
            commands.notice("file.getStatus():  " + file.getStatus());
            commands.notice("file.getPatch():  " + file.getPatch());
            commands.notice("file.getContentsUrl():  " + file.getContentsUrl());
            commands.notice("file.getBlobUrl():  " + file.getBlobUrl());
            commands.notice("file.getRawUrl():  " + file.getRawUrl());
            commands.notice("file.getPreviousFilename():  " + file.getPreviousFilename());
            diffBuilder.append("Changes in ").append(file.getFilename()).append(":").append("\n");
            diffBuilder.append(file.getPatch()).append("\n\n");
        }

        String diff = diffBuilder.toString();
        commands.notice("diff: " + diff);

        Result result = DIFF_ANALYZER.analyze(pr.getTitle(), pr.getBody(), diff);
        commands.notice("result: " + result);

        String userHandle = pullRequest.getSender().getLogin();

        StringBuilder commentBuilder = new StringBuilder();

        commentBuilder.append("Hi @").append(userHandle).append(", thanks a lot for your PR! ");
        commentBuilder.append("I'm a bot, here is an initial analysis of your PR:")
                .append("\n\n");

        commentBuilder.append("## Potential Issues").append("\n\n");
        addPotentialProblems(commentBuilder, result.potentialBreakingChanges(), "Breaking Changes");
        addPotentialProblems(commentBuilder, result.potentialDesignIssues(), "Design Issues");
        addPotentialProblems(commentBuilder, result.potentialBugs(), "Bugs");

        commentBuilder.append("## Testing").append("\n\n");
        addTestScenarios(commentBuilder, result.positiveTestScenarios(), "Positive");
        addTestScenarios(commentBuilder, result.negativeTestScenarios(), "Negative");
        addTestScenarios(commentBuilder, result.cornerCaseTestScenarios(), "Corner Case");

        commentBuilder.append("Your PR contains production code changes: ")
                .append(result.containsProductionCodeChanges() ? YES : NO)
                .append("\n\n");
        commentBuilder
                .append("Changes in this PR are sufficiently tested: ")
                .append(result.changesAreSufficientlyTested() ? YES : NO)
                .append("\n");
        commentBuilder
                .append("Changes in this PR are sufficiently documented: ")
                .append(result.changesAreSufficientlyDocumented() ? YES : NO)
                .append("\n");

        pr.comment(commentBuilder.toString());
    }

    private static void addPotentialProblems(StringBuilder commentBuilder,
                                             List<PotentialProblem> potentialProblems,
                                             String title) {
        if (!potentialProblems.isEmpty()) {
            commentBuilder.append("### Potential ").append(title).append("\n");
            for (PotentialProblem potentialProblem : potentialProblems) {
                commentBuilder
                        .append("- ")
                        .append(map(potentialProblem.severity()))
                        .append(" ")
                        .append(potentialProblem.description())
                        .append("\n");
            }
            commentBuilder.append("\n\n");
        }
    }

    private static void addTestScenarios(StringBuilder commentBuilder,
                                         List<TestScenario> testScenarios,
                                         String title) {
        if (!testScenarios.isEmpty()) {
            commentBuilder.append("### Suggested ").append(title).append(" Test Scenarios").append("\n");
            for (TestScenario testScenario : testScenarios) {
                commentBuilder
                        .append("-").append("\n")
                        .append("  - Given: ").append(testScenario.given()).append("\n")
                        .append("  - When: ").append(testScenario.when()).append("\n")
                        .append("  - Then: ").append(testScenario.then()).append("\n")
                        .append("\n");
            }
            commentBuilder.append("\n\n");
        }
    }

    static String map(Severity severity) {
        return switch (severity) {
            case MINOR -> "";
            case MEDIUM -> MEDIUM;
            case MAJOR -> MAJOR;
            case CRITICAL -> CRITICAL;
        };
    }

    static String getContents(URL url) {
        try {
            HttpClient httpClient = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .GET()
                    .uri(url.toURI())
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}