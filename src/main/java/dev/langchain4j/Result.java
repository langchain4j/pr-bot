package dev.langchain4j;

import dev.langchain4j.model.output.structured.Description;

public record Result(
        @Description("Are there any changes to the production Java code?") boolean containsProductionCodeChanges,
        @Description("Are there any changes to the test Java code?") boolean containsTestCodeChanges
) {
}
