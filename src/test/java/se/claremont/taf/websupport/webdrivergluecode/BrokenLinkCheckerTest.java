package se.claremont.taf.websupport.webdrivergluecode;

import org.junit.*;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;
import se.claremont.taf.core.testcase.TestCase;
import se.claremont.taf.core.testset.UnitTestClass;
import se.claremont.taf.websupport.utils.SimpleHttpClient;

import java.net.MalformedURLException;
import java.net.URL;

import static se.claremont.taf.core.testcase.TestCaseResult.ResultStatus.FAILED_WITH_ONLY_NEW_ERRORS;
import static se.claremont.taf.core.testcase.TestCaseResult.ResultStatus.PASSED;

/**
 * Test class for checkning broken links
 *
 * Created by jordam on 2017-03-31.
 */
public class BrokenLinkCheckerTest extends UnitTestClass {
    private TestCase testCase;
    //TestActionsForBrokenLinkChecker testActionsForBrokenLinkChecker = new TestActionsForBrokenLinkChecker();
    private WebInteractionMethods web;

    @Before
    public void setup(){
        testCase = new TestCase();
        HtmlUnitDriver driver = new HtmlUnitDriver();
        driver.setJavascriptEnabled(true);
        web = new WebInteractionMethods(testCase, driver);
        Assume.assumeTrue("Internet access needed.", internetAccessExist(testCase));
    }

    private boolean internetAccessExist(TestCase testCase) {
        try {
            Integer responseCode = SimpleHttpClient.sendHttpGet(new URL("http://www.zingtongroup.com"));
            return (responseCode != null);
        } catch (MalformedURLException e) {
            return false;
        }
    }

    @After
    public void teardown(){
        web.makeSureDriverIsClosed();
    }

    @Test
    public void linkReportsShouldBeProducedNegative(){
        web.navigate("file://" + TestHelper.getTestFileFromTestResourcesFolder("brokenLinkCheckTest.html"));
        web.reportBrokenLinksOnCurrentPage();
        testCase.testCaseResult.evaluateResultStatus();
        Assert.assertTrue(testCase.testCaseResult.resultStatus.equals(FAILED_WITH_ONLY_NEW_ERRORS));
    }

    @Test
    public void linkReportsShouldBeProducedPositive(){
        web.navigate("file://" + TestHelper.getTestFileFromTestResourcesFolder("brokenLinkCheckTestPositive.html"));
        web.reportBrokenLinksOnCurrentPage();
        testCase.testCaseResult.evaluateResultStatus();
        Assert.assertTrue(testCase.testCaseResult.resultStatus.equals(PASSED));
    }

    @Test
    public void linkReportsShouldBeProducedWithHiddenLinks(){
        web.navigate("file://" + TestHelper.getTestFileFromTestResourcesFolder("brokenLinkCheckTest.html"));
        web.reportBrokenLinksOnCurrentPage_IncludeAllLinksAlsoNonDisplayedLinks();
        testCase.testCaseResult.evaluateResultStatus();
        Assert.assertTrue(testCase.toString(), testCase.testCaseResult.resultStatus.equals(FAILED_WITH_ONLY_NEW_ERRORS));
    }

}
