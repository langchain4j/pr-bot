package dev.langchain4j;

import dev.langchain4j.model.output.structured.Description;

public record Result(
        @Description("Are there changes to the production Java code?") boolean containsProductionCodeChanges,
        @Description("Are there changes to the Java tests?") boolean containsTestChanges
) {
}
