package dev.langchain4j;

import org.junit.jupiter.api.RepeatedTest;

import static dev.langchain4j.MyAction.DIFF_ANALYZER;
import static org.assertj.core.api.Assertions.assertThat;

class DiffAnalyzerTest {

//    @RepeatedTest(10)
    void test() {

        String diff = """
                diff --git a/langchain4j-embeddings-all-minilm-l6-v2/src/main/java/dev/langchain4j/model/embedding/onnx/allminilml6v2/AllMiniLmL6V2EmbeddingModel.java b/langchain4j-embeddings-all-minilm-l6-v2/src/main/java/dev/langchain4j/model/embedding/onnx/allminilml6v2/AllMiniLmL6V2EmbeddingModel.java
                index ad1f488..cb4df9d 100644
                --- a/langchain4j-embeddings-all-minilm-l6-v2/src/main/java/dev/langchain4j/model/embedding/onnx/allminilml6v2/AllMiniLmL6V2EmbeddingModel.java
                +++ b/langchain4j-embeddings-all-minilm-l6-v2/src/main/java/dev/langchain4j/model/embedding/onnx/allminilml6v2/AllMiniLmL6V2EmbeddingModel.java
                @@ -28,8 +28,8 @@
                 public class AllMiniLmL6V2EmbeddingModel extends AbstractInProcessEmbeddingModel {
                \s
                     private static final OnnxBertBiEncoder MODEL = loadFromJar(
                -            "all-minilm-l6-v2.onnx",
                -            "all-minilm-l6-v2-tokenizer.json",
                +            "all-m i n il m - l6-v2-tokenizer",
                +            " ",
                             PoolingMode.MEAN
                     );
                """;

        Result result = DIFF_ANALYZER.analyze(diff);

        assertThat(result).isEqualTo(new Result(true, false));
    }
}