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

import static dev.langchain4j.model.chat.request.ResponseFormat.JSON;

public class MyAction {

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

        String diff = getContents(pr.getDiffUrl());
        commands.notice("diff: " + diff);

        for (GHPullRequestFileDetail file : pr.listFiles()) {
            commands.notice("file.getFilename():  " + file.getFilename());
            commands.notice("file.getStatus():  " + file.getStatus());
            commands.notice("file.getPatch():  " + file.getPatch());
            commands.notice("file.getContentsUrl():  " + file.getContentsUrl());
            commands.notice("file.getBlobUrl():  " + file.getBlobUrl());
            commands.notice("file.getRawUrl():  " + file.getRawUrl());
            commands.notice("file.getPreviousFilename():  " + file.getPreviousFilename());
        }

        Result result = DIFF_ANALYZER.analyze(diff);
        commands.notice("result: " + result);

        if (result.containsProductionCodeChanges() && !result.containsTestCodeChanges()) {
            String userHandle = pullRequest.getSender().getLogin();
            pr.comment("Hi @" + userHandle + ", thanks a lot for your PR!\n" +
                    "It seems that tests are missing, could you please add them?");
        }
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