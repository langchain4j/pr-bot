package dev.langchain4j;

import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubaction.Commands;
import io.quarkiverse.githubapp.event.PullRequestTarget;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;

import java.io.IOException;

public class MyAction {

    @Action
    void action(@PullRequestTarget GHEventPayload.PullRequest pullRequest, Commands commands) throws IOException {
        GHPullRequest pr = pullRequest.getPullRequest();

        commands.notice("getChanges().getTitle(): " + pullRequest.getChanges().getTitle());
        commands.notice("getChanges().getBody(): " + pullRequest.getChanges().getBody());
        commands.notice("getChanges().getBase(): " + pullRequest.getChanges().getBase());

        commands.notice("getSender: " + pullRequest.getSender());
        commands.notice("pr.getTitle(): " + pr.getTitle());

        pr.comment("@" + pullRequest.getSender() + " hi!");
    }
}