(function ($) {
    $(document).ready(function () {

        function extractTestsResults($singleResult, selector) {
            var failedTests = [];
            $singleResult.find(selector).each(function () {
                failedTests.push($(this).text());
            });
            return failedTests;
        }

        function formatArrayResult(prefix, arrayResult) {
            var resultFormatted = '';
            if (arrayResult.length !== 0) {
                resultFormatted = prefix + '\n' + arrayResult.join('\n') + '\n';
            }
            return resultFormatted;
        }

        function prepareDataOfSingleResult(singleParsedResult) {
            var resultFormatted = '';
            if (singleParsedResult.error) {
                resultFormatted = 'Error: ' + singleParsedResult.error.toString() + '\n'
                                  + 'Exception: ' + '\n' + singleParsedResult.failedTests.join('\n')
            } else {
                resultFormatted = 'Passed: ' + singleParsedResult.passed + '\n'
                                  + formatArrayResult('Failed Tests: ',
                                                      singleParsedResult.failedTests)
                                  + formatArrayResult('Succeeded Tests: ',
                                                      singleParsedResult.succeededTests)
            }
            return resultFormatted + '========================================================\n\n';
        }

        function parseResults(testsResults) {
            var parsedResults = [];
            testsResults.each(function () {
                var $singleResult = $(this);
                parsedResults.push({
                                       testName: $singleResult.find('h2>a').text(),
                                       description: $singleResult.find('p').text(),
                                       error: $singleResult.find('div.icon-exception').length !== 0,
                                       passed: $singleResult.find('div.icon-fail').length === 0,
                                       severity: $singleResult.find('li:contains(Severity)').text()
                                           .trim(),
                                       failedTests: extractTestsResults($singleResult,
                                                                        'div.secureaem-error'),
                                       succeededTests: extractTestsResults($singleResult,
                                                                           'div.secureaem-info')
                                   });
            });
            return parsedResults;
        }

        function prepareDataToExport(parsedResults) {
            var dataToExport = '';
            parsedResults.forEach(function (singleParsedResult) {
                dataToExport = dataToExport + 'Test name: ' + singleParsedResult.testName + '\n'
                               + 'Description: ' + singleParsedResult.description + '\n'
                               + singleParsedResult.severity + '\n'
                               + prepareDataOfSingleResult(singleParsedResult)
            });
            return dataToExport;
        }

        function download(filename, content) {
            var anchor = document.createElement('a');
            document.body.appendChild(anchor);
            anchor.setAttribute('href',
                                'data:text/plain;charset=utf-8,' + encodeURIComponent(content));
            anchor.setAttribute('download', filename);
            anchor.setAttribute('target', '_self');
            anchor.click();
            anchor.remove();
        }

        $('button#secureaem-export').on('click', function () {
            var testsResults = $('div.mainRenderer.page>div.test:not(.disabled)'),
                parsedResults = parseResults(testsResults),
                dataToExport = prepareDataToExport(parsedResults);
            download('security_report.txt', dataToExport);
        });

    });
}(jQuery.noConflict(true)));