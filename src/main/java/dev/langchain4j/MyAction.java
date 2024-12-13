package dev.langchain4j;

import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubapp.event.Issue;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHIssue;

import java.io.IOException;

public class MyAction {

    @Action
    void action(@Issue GHEventPayload.Issue issuePayload) throws IOException {
        GHIssue issue = issuePayload.getIssue();

        System.out.println("Repository: " + issue.getRepository().getFullName());
        System.out.println("Issue title: " + issue.getTitle());

        issue.comment("test comment");
    }
}