package tests.base;

import com.microsoft.playwright.*;
import context.TestContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import utils.ConfigReader;

public class BaseTest {


    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext browserContext;
    protected Page page;
    protected TestContext testContext;

    @BeforeEach
    public void setUp(){

        playwright = Playwright.create();
        //todo implement browser factory
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(ConfigReader.getHeadless()));
        browserContext = browser.newContext();
        page = browserContext.newPage();


        testContext = new TestContext();
    }

    @AfterEach
    public void tearDown(){
        if (browser != null){
            try{
                browser.close();
                browser = null;
            }
            finally{
                try{
                    if( playwright != null){
                        playwright.close();
                    }
                }
                finally {
                    playwright = null;
                }
            }
        }
    }
}
