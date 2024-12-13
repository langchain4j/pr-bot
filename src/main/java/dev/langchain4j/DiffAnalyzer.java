package dev.langchain4j;

import dev.langchain4j.service.UserMessage;

public interface DiffAnalyzer {

    @UserMessage("Analyze the following diff of a GitHub PR: {{diff}}")
    Result analyze(String diff);
}
