package net.masterthought.cucumber.generators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class TagsOverviewPageIntegrationTest extends Page {

    @Test
    public void generatePage_generatesTitle() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new TagsOverviewPage(reportResult, configuration);
        final String titleValue = String.format("Cucumber-JVM Html Reports  - Tags Overview",
                configuration.getBuildNumber());

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        String title = getTitle(document).text();

        assertThat(title).isEqualTo(titleValue);
    }

    @Test
    public void generatePage_generatesLead() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        String leadHeader = getLeadHeader(document).text();
        String leadDescription = getLeadDescription(document).text();

        assertThat(leadHeader).isEqualTo("Tags Statistics");
        assertThat(leadDescription).isEqualTo("The following graph shows passing and failing statistics for tags");
    }

    @Test
    public void generatePage_generatesCharts() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());

        assertThat(document.byId("charts")).isNotNull();
    }

    @Test
    public void generatePage_generatesStatsTableHeader() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        ElementWrapper headerTable = getHeaderOfStatsTable(document);
        ElementWrapper[] headerRows = getRows(headerTable);

        assertThat(headerRows).hasSize(2);

        ElementWrapper[] firstRow = getHeaderCells(headerRows[0]);
        validateElements(firstRow, "", "Scenarios", "Steps", "", "");

        ElementWrapper[] secondRow = getHeaderCells(headerRows[1]);
        validateElements(secondRow, "Tag", "Total", "Passed", "Failed", "Total", "Passed", "Failed", "Skipped",
                "Pending", "Undefined", "Missing", "Duration", "Status");
    }

    @Test
    public void generatePage_generatesStatsTableBody() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setStatusFlags(true, false, false, true);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        ElementWrapper[] bodyRows = getBodyOfStatsTable(document);

        assertThat(bodyRows).hasSize(3);

        ElementWrapper[] firstRow = getCells(bodyRows[0]);
        validateElements(firstRow, "@checkout", "2", "1", "1", "16", "8", "1", "3", "2", "1", "1", "231 ms",
                "Failed");
        validateCSSClasses(firstRow, "tagname", "", "", "", "", "", "failed", "skipped", "pending", "undefined",
                "missing", "duration", "failed");
        validateReportLink(firstRow, "checkout.html", "@checkout");

        ElementWrapper[] secondRow = getCells(bodyRows[1]);
        validateElements(secondRow, "@fast", "1", "1", "0", "7", "4", "0", "0", "2", "1", "0", "229 ms", "Passed");
        validateCSSClasses(secondRow, "tagname", "", "", "", "", "", "", "", "pending", "undefined", "", "duration",
                "passed");
        validateReportLink(secondRow, "fast.html", "@fast");

        ElementWrapper[] thirdRow = getCells(bodyRows[2]);
        validateElements(thirdRow, "@featureTag", "1", "1", "0", "7", "4", "0", "0", "2", "1", "0", "229 ms", "Passed");
        validateCSSClasses(thirdRow, "tagname", "", "", "", "", "", "", "", "pending", "undefined", "", "duration",
                "passed");
        validateReportLink(thirdRow, "featureTag.html", "@featureTag");
    }

    @Test
    public void generatePage_generatesStatsTableFooter() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setStatusFlags(true, false, false, true);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        ElementWrapper[] footerCells = getFooterCellsOfStatsTable(document);

        validateElements(footerCells, "3", "4", "3", "1", "30", "16", "1", "3", "6", "3", "1", "689 ms", "Totals");
    }

    @Test
    public void generatePage_onEmptyJsons_generatesProperMessage() {

        // given
        setUpWithJson(EMPTY_JOSN);
        page = new TagsOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        assertThat(getEmptyReportMessage(document).text()).isEqualTo("You have no tags in your cucumber report");
    }
}