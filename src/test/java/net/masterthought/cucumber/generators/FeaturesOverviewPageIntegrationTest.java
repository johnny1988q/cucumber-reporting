package net.masterthought.cucumber.generators;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * @author Damian Szczepanik (damianszczepanik@github)
 */
public class FeaturesOverviewPageIntegrationTest extends Page {

    @Test
    public void generatePage_generatesTitle() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setRunWithJenkins(true);
        configuration.setBuildNumber("1");
        page = new FeaturesOverviewPage(reportResult, configuration);
        final String titleValue = String.format("Cucumber-JVM Html Reports (no %s) - Features Overview",
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
        page = new FeaturesOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        String leadHeader = getLeadHeader(document).text();
        String leadDescription = getLeadDescription(document).text();

        assertThat(leadHeader).isEqualTo("Features Statistics");
        assertThat(leadDescription).isEqualTo("The following graphs show passing and failing statistics for features");
    }

    @Test
    public void generatePage_generatesCharts() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        page = new FeaturesOverviewPage(reportResult, configuration);

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
        page = new FeaturesOverviewPage(reportResult, configuration);

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
        validateElements(secondRow, "Feature", "Total", "Passed", "Failed", "Total", "Passed", "Failed", "Skipped",
                "Pending", "Undefined", "Missing", "Duration", "Status");
    }

    @Test
    public void generatePage_generatesStatsTableBody() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setStatusFlags(true, false, false, true);
        page = new FeaturesOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        ElementWrapper[] bodyRows = getBodyOfStatsTable(document);

        assertThat(bodyRows).hasSize(2);

        ElementWrapper[] firstRow = getCells(bodyRows[0]);
        validateElements(firstRow, "First feature", "1", "1", "0", "10", "7", "0", "0", "2", "1", "0", "343 ms",
                "Passed");
        validateCSSClasses(firstRow, "tagname", "", "", "", "", "", "", "", "pending", "undefined", "", "duration",
                "passed");
        validateReportLink(firstRow, "net-masterthought-example-s--ATM-local-feature.html", "First feature");

        ElementWrapper[] secondRow = getCells(bodyRows[1]);
        validateElements(secondRow, "2nd feature", "1", "0", "1", "9", "4", "1", "3", "0", "0", "1", "002 ms",
                "Failed");
        validateCSSClasses(secondRow, "tagname", "", "", "", "", "", "failed", "skipped", "", "", "missing", "duration",
                "failed");
        validateReportLink(secondRow, "net-masterthought-example-ATMK-feature.html", "2nd feature");
    }

    @Test
    public void generatePage_generatesStatsTableFooter() {

        // given
        setUpWithJson(SAMPLE_JOSN);
        configuration.setStatusFlags(true, false, false, true);
        page = new FeaturesOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        ElementWrapper[] footerCells = getFooterCellsOfStatsTable(document);

        validateElements(footerCells, "2", "2", "1", "1", "19", "11", "1", "3", "2", "1", "1", "345 ms", "Totals");
    }

    @Test
    public void generatePage_onEmptyJsons_generatesProperMessage() {

        // given
        setUpWithJson(EMPTY_JOSN);
        page = new FeaturesOverviewPage(reportResult, configuration);

        // when
        page.generatePage();

        // then
        ElementWrapper document = documentFrom(page.getWebPage());
        assertThat(getEmptyReportMessage(document).text()).isEqualTo("You have no features in your cucumber report");
    }
}