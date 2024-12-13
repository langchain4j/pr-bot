package dev.langchain4j;

public record Result
        (boolean containsChangesToProductionCode,
         boolean changesToProductionCodeAreCoveredWithTests
        ) {
}
