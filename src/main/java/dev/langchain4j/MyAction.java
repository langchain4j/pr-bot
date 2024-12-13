package dev.langchain4j;

import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubapp.event.PullRequest;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;

public class MyAction {

    @Action
    void action(@PullRequest GHEventPayload.PullRequest pullRequest) throws IOException {
        GHPullRequest pr = pullRequest.getPullRequest();

        System.out.println("Repository: " + pr.getRepository().getFullName());
        System.out.println("Issue title: " + pr.getTitle());

        pr.comment("test comment");
    }
}