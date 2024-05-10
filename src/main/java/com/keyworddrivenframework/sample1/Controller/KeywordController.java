package com.keyworddrivenframework.sample1.Controller;

import com.keyworddrivenframework.sample1.Entity.ActionKeywords;
import com.keyworddrivenframework.sample1.Entity.Test;
import com.keyworddrivenframework.sample1.Entity.TestResults;
import com.keyworddrivenframework.sample1.Service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static com.keyworddrivenframework.sample1.Utils.TestExecutor.getCurrentTime;

@RestController
@RequestMapping("/keyword")
@CrossOrigin(origins = "http://localhost:3000")
public class KeywordController {

    @Autowired
    private KeywordService keywordService;

    //to get the subtests under a particular test
    @GetMapping("/subTests/{testId}")
    public List<ActionKeywords> getActionKeywordsByTestId(@PathVariable Test testId) {
        List<ActionKeywords> actionKeywords = keywordService.getActionKeywordsByTestId(testId);
        actionKeywords.sort(Comparator.comparingDouble(ActionKeywords::getOrderOfExecution));
        return actionKeywords;
    }
    //to add a subtest under a specific test
    @PostMapping("/addSubTest/{testId}")
    public ActionKeywords addTestUnderTestId(@PathVariable Test testId, @RequestBody ActionKeywords actionKeywords) {
        return keywordService.addTestUnderTestId(testId, actionKeywords);
    }
    //to delete a particular subtest
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteById(@PathVariable int id){
        ActionKeywords deletedKeyword = keywordService.deleteById(id);
        if (deletedKeyword != null) {
            return ResponseEntity.ok("Deleted keyword with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    //to execute the provided keywords
    @PostMapping("/executeAll/{testId}")
    public ResponseEntity<List<TestResults>> executeAndRetrieveResults(@RequestBody List<Map<String, String>> actionKeyword, @PathVariable Test testId) {
        keywordService.resetExecutionResults();
        actionKeyword.sort(Comparator.comparingDouble(action -> Double.parseDouble(action.get("orderOfExecution"))));

        String startTime = getCurrentTime();
        for (Map<String, String> actionKeywords : actionKeyword) {
            String flag = actionKeywords.get("flag");
            if (flag != null && flag.equalsIgnoreCase("Y")) {
                try {
                    keywordService.executeKeyword(actionKeywords, testId, startTime);
                } catch (IllegalArgumentException e) {
                    return ResponseEntity.badRequest().body(Collections.emptyList());
                }
            }
        }
        List<TestResults> executionResults = keywordService.getExecutionResults();
        if (executionResults.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(executionResults);
    }
}
