package org.acme.domain.models.demo;

import java.util.ArrayList;
import java.util.List;

public class TodoDemoResult {
    private String strategy;
    private long queryCount;
    private long millis;
    private int rows;
    private String explanation;
    private List<String> sample = new ArrayList<>();
    private String error;

    public TodoDemoResult() {
    }

    public TodoDemoResult(String strategy, String explanation) {
        this.strategy = strategy;
        this.explanation = explanation;
    }

    public String getStrategy() { return strategy; }
    public void setStrategy(String strategy) { this.strategy = strategy; }

    public long getQueryCount() { return queryCount; }
    public void setQueryCount(long queryCount) { this.queryCount = queryCount; }

    public long getMillis() { return millis; }
    public void setMillis(long millis) { this.millis = millis; }

    public int getRows() { return rows; }
    public void setRows(int rows) { this.rows = rows; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public List<String> getSample() { return sample; }
    public void setSample(List<String> sample) { this.sample = sample; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
