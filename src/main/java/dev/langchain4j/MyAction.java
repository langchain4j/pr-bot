package dev.langchain4j;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubapp.event.PullRequestTarget;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

public class MyAction {

    static ChatLanguageModel MODEL = OpenAiChatModel.builder()
            .baseUrl(System.getenv("OPENAI_BASE_URL"))
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .organizationId(System.getenv("OPENAI_ORGANIZATION_ID"))
            .modelName(GPT_4_O_MINI)
            .responseFormat("json_schema")
            .strictJsonSchema(true)
            .temperature(0.0)
            .logRequests(true)
            .logResponses(true)
            .build();

    static DiffAnalyzer DIFF_ANALYZER = AiServices.builder(DiffAnalyzer.class)
            .chatLanguageModel(MODEL)
            .build();

    @Action
    void action(@PullRequestTarget GHEventPayload.PullRequest pullRequest, Commands commands) throws IOException {
        GHPullRequest pr = pullRequest.getPullRequest();

        commands.notice("pr.getPatchUrl(): " + pr.getPatchUrl());
        commands.notice("pr.getUrl(): " + pr.getUrl());
        commands.notice("pr.getHtmlUrl(): " + pr.getHtmlUrl());
        commands.notice("pr.getDiffUrl(): " + pr.getDiffUrl());
        commands.notice("pr.getIssueUrl(): " + pr.getIssueUrl());

        commands.notice("getSender: " + pullRequest.getSender());
        commands.notice("pr.getTitle(): " + pr.getTitle());

        String diff = getContents(pr.getDiffUrl());

        Result result = DIFF_ANALYZER.analyze(diff);

        if (result.containsChangesToProductionCode()
                && !result.changesToProductionCodeAreCoveredWithTests()) {
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