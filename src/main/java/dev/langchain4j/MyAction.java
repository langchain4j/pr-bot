package dev.langchain4j;

import io.quarkiverse.githubaction.Action;
import io.quarkiverse.githubapp.event.PullRequestTarget;
import org.kohsuke.github.GHEventPayload;
import org.kohsuke.github.GHPullRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class MyAction {

    private static final Logger LOG = LoggerFactory.getLogger(MyAction.class);

    @Action
    void action(@PullRequestTarget GHEventPayload.PullRequest pullRequest) throws IOException {
        GHPullRequest pr = pullRequest.getPullRequest();

        LOG.info("Repository: " + pr.getRepository().getFullName());
        LOG.info("Issue title: " + pr.getTitle());

        pr.comment("test comment");
    }
}