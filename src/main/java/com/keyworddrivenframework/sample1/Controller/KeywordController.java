package com.keyworddrivenframework.sample1.Controller;

import com.keyworddrivenframework.sample1.Entity.ActionKeywords;
import com.keyworddrivenframework.sample1.Entity.ExecutionRequest;
import com.keyworddrivenframework.sample1.Entity.Test;
import com.keyworddrivenframework.sample1.Entity.TestResults;
import com.keyworddrivenframework.sample1.Service.KeywordService;
import com.keyworddrivenframework.sample1.Utils.TestExecutor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/keyword")
@CrossOrigin(origins = "http://localhost:3000")
public class KeywordController {

    private KeywordService keywordService;
    private final TestExecutor testExecutor;

    public KeywordController(TestExecutor testExecutor, KeywordService keywordService) {
        this.testExecutor = testExecutor;
        this.keywordService = keywordService;
    }

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
    public ResponseEntity<String> deleteById(@PathVariable int id) {
        ActionKeywords deletedKeyword = keywordService.deleteById(id);
        if (deletedKeyword != null) {
            return ResponseEntity.ok("Deleted keyword with ID: " + id);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    //to update the headless state
    @PostMapping("/updateHeadlessMode")
    public ResponseEntity<String> updateHeadlessMode(@RequestBody Map<String, Boolean> headless) {
        boolean headlessMode = headless.get("headless");
        keywordService.setHeadlessMode(headlessMode);
        return ResponseEntity.ok("Headless mode updated successfully to " + headless);
    }

    @PostMapping("/executeAll/{testId}")
    public ResponseEntity<List<TestResults>> executeAndRetrieveResults(@RequestBody ExecutionRequest requestBody, @PathVariable Test testId) {
        List<Map<String, String>> keywordActions = requestBody.getActionKeyword();
        List<String> browsers = requestBody.getBrowsers();
        try {
            List<TestResults> executionResults = keywordService.executeAllKeywords(keywordActions, browsers, testId);
            if (executionResults.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(executionResults);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }
    }

    //to execute the provided keywords
//    @PostMapping("/executeAll/{testId}")
//    public ResponseEntity<List<TestResults>> executeAndRetrieveResults(@RequestBody ExecutionRequest requestBody, @PathVariable Test testId) {
//        keywordService.resetExecutionResults();
//        List<Map<String, String>> keywordAction = requestBody.getActionKeyword();
//        List<String> browsers = requestBody.getBrowsers();
//        keywordAction.sort(Comparator.comparingDouble(action -> Double.parseDouble(action.get("orderOfExecution"))));
//
//        if (browsers != null && !browsers.isEmpty()) {
//            for (String browser : browsers) {
//                keywordService.openBrowser(browser);
//                String startTime = getCurrentTime();
//                for (Map<String, String> actionKeywords : keywordAction) {
//                    String flag = actionKeywords.get("flag");
//                    if (flag != null && flag.equalsIgnoreCase("Y")) {
//                        try {
//                            keywordService.executeKeyword(actionKeywords, testId, startTime);
//                        } catch (IllegalArgumentException e) {
//                            return ResponseEntity.badRequest().body(Collections.emptyList());
//                        }
//                    }
//                }
//            }
//        }
//        List<TestResults> executionResults = keywordService.getExecutionResults();
//        if (executionResults.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(executionResults);
//    }
}
