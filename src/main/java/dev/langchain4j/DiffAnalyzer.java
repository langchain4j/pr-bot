package dev.langchain4j;

import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface DiffAnalyzer {

    @UserMessage("""
            Analyze the following PR to the LangChain4j project and provide insights.
            PR title: <title>{{title}}</title>
            PR body: <body>{{body}}</body>
            PR diff: <diff>{{diff}}</diff>
            """)
    Result analyze(@V("title") String title, @V("body") String body, @V("diff") String diff);
}
