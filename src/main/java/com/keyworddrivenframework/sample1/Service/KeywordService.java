package com.keyworddrivenframework.sample1.Service;

import com.keyworddrivenframework.sample1.Entity.ActionKeywords;
import com.keyworddrivenframework.sample1.Entity.Test;
import com.keyworddrivenframework.sample1.Entity.TestResults;
import com.keyworddrivenframework.sample1.Repository.KeywordRepository;
import com.keyworddrivenframework.sample1.Utils.TestExecutor;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KeywordService {

    private final TestExecutor testExecutor = new TestExecutor();

    @Getter
    private final List<TestResults> executionResults = new ArrayList<>();

    @Autowired
    private KeywordRepository keywordRepository;

    public List<ActionKeywords> getActionKeywordsByTestId(Test testId) {
        return keywordRepository.findByTestId(testId);
    }

    public ActionKeywords addTestUnderTestId(Test testId, ActionKeywords actionKeywords) {
        actionKeywords.setTestId(testId);
        return keywordRepository.save(actionKeywords);
    }

    public ActionKeywords deleteById(int id) {
        Optional<ActionKeywords> keywordOptional = keywordRepository.findById(id);
        if (keywordOptional.isPresent()) {
            ActionKeywords deletedKeyword = keywordOptional.get();
            keywordRepository.delete(deletedKeyword);
            return deletedKeyword;
        } else {
            return null;
        }
    }

    public void executeKeyword(Map<String, String> actionKeywords, Test testId, String startTime) {

        String runId = testId.getTestId() + "_" + startTime;

        String keyword = actionKeywords.get("keyword");
        String locatorType = actionKeywords.get("locatorType");
        String locatorValue = actionKeywords.get("locatorValue");
        String value = actionKeywords.get("value");
        String description = actionKeywords.get("description");
        boolean screenshotValue = Boolean.parseBoolean(actionKeywords.get("screenshot"));

        TestResults testResult = new TestResults();
        testResult.setDescription(description);
        testResult.setTestId(testId);
        testResult.setRunId(runId);

        switch (keyword.toLowerCase()) {
            case "openbrowser":
                testResult.setResult(testExecutor.openBrowser(value,screenshotValue,testResult));
                break;
            case "gotourl":
                testResult.setResult(testExecutor.goToURL(value,screenshotValue,testResult));
                break;
            case "typetext":
                testResult.setResult(testExecutor.typeText(locatorType, locatorValue, value, screenshotValue,testResult));
                break;
            case "typemaskedtext":
                testResult.setResult(testExecutor.typeMaskedText(locatorType, locatorValue, value, screenshotValue,testResult));
                break;
            case "cleartext":
                testResult.setResult(testExecutor.clearText(locatorType, locatorValue, screenshotValue,testResult));
                break;
            case "click":
                testResult.setResult(testExecutor.click(locatorType, locatorValue,value, screenshotValue,testResult));
                break;
            case "doubleclick":
                testResult.setResult(testExecutor.doubleClick(locatorType, locatorValue, screenshotValue,testResult));
                break;
            case "getvalue":
                testResult.setResult(testExecutor.getValue(locatorType,locatorValue,screenshotValue,testResult));
                break;
            case "typevalue":
                testResult.setResult(testExecutor.typeValue(locatorType,locatorValue,screenshotValue,testResult));
                break;
            case "scrolltobottom":
                testResult.setResult(testExecutor.scrollToBottom(screenshotValue,testResult));
                break;
            case "scrolltotop":
                testResult.setResult(testExecutor.scrollToTop(screenshotValue,testResult));
                break;
            case "scrolltoelement":
                testResult.setResult(testExecutor.scrollToElement(locatorType,locatorValue,screenshotValue,testResult));
                break;
            case "generaterandomnumber":
                testResult.setResult(testExecutor.generateRandomNumber(locatorType,locatorValue,value));
                break;
            case "generaterandomtext":
                testResult.setResult(testExecutor.generateRandomText(locatorType,locatorValue,value));
                break;
            case "waitfor":
                testResult.setResult(testExecutor.waitFor(value));
                break;
            case "waitforelement":
                testResult.setResult(testExecutor.waitForElement(locatorType,locatorValue));
                break;
            case "verifytext":
                testResult.setResult(testExecutor.verifyText(locatorType,locatorValue,value,screenshotValue,testResult));
                break;
            case "verifyelement":
                testResult.setResult(testExecutor.verifyElement(locatorType, locatorValue, screenshotValue,testResult));
                break;
            case "verifyurl":
                testResult.setResult(testExecutor.verifyURL(value,screenshotValue,testResult));
                break;
            case "verifypagetitle":
                testResult.setResult(testExecutor.verifyPageTitle(value,screenshotValue,testResult));
                break;
            case "selectfromdropdown":
                testResult.setResult(testExecutor.selectFromDropdown(locatorType, locatorValue, value, screenshotValue,testResult));
                break;
            case "mousehover":
                testResult.setResult(testExecutor.mouseHover(locatorType, locatorValue, screenshotValue, testResult));
                break;
            case "refreshpage":
                testResult.setResult(testExecutor.refreshPage(screenshotValue,testResult));
                break;
            case "enter":
                testResult.setResult(testExecutor.enter(locatorType, locatorValue));
                break;
            case "downkeyandenter":
                testResult.setResult(testExecutor.downKeyAndEnter());
                break;
            case "acceptalert":
                testResult.setResult(testExecutor.acceptAlert(screenshotValue,testResult));
                break;
            case "dismissalert":
                testResult.setResult(testExecutor.dismissAlert(screenshotValue,testResult));
                break;
            case "closebrowser":
                testResult.setResult(testExecutor.closeBrowser());
                break;
            case "fileupload":
                testResult.setResult(testExecutor.fileUpload(locatorType,locatorValue,value));
                break;
            case "draganddrop":
                testResult.setResult(testExecutor.dragAndDrop(locatorType,locatorValue));
                break;
            case "rightclick":
                testResult.setResult(testExecutor.rightClick(locatorType,locatorValue));
                break;
            default:
                throw new IllegalArgumentException("Invalid keyword: " + keyword);
        }
        executionResults.add(testResult);
    }

    public void resetExecutionResults() {
        executionResults.clear();
        testExecutor.resetExecutionFlag();
    }
}
