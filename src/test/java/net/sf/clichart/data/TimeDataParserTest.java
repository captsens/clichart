/* (C) Copyright 2006-2009, by John Dickson
 *
 * Project Info:  https://github.com/captsens/clichart
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 */

package net.sf.clichart.data;

import junit.framework.TestCase;
import org.easymock.MockControl;

import java.io.StringReader;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.text.ParseException;

/**
 * Unit tests for TimeDataParser.  Note that this test also attempts to cover most of the functionality in the
 * AbstractDataParser base class.
 *
 * @author johnd
 */
public class TimeDataParserTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static DateFormat s_dateParser = new SimpleDateFormat("HH:mm");

    // NOTE: Since we're not really parsing this, (using a mock instead) all it needs is non-blank lines
    private static final String TEST_DATA = "Data 1\n"
            + "Data 2\n"
            + "Data 3";

    private static final String TEST_DATA_WITH_CRLF = "Data 1\r\n"
            + "Data 2\r\n"
            + "Data 3\r\n";

    private static final String TIME_00_17 = "00:17";
    private static final String TIME_07_32 = "07:32";
    private static final String TIME_23_59 = "23:59";

    private static final String[][] SPLIT_TEST_DATA = {
        {TIME_00_17, "0.01", "17", "4.35"},
        {TIME_07_32, ".33", "21", "1.125"},
        {TIME_23_59, "999", "7", "16.3"}
    };
    private static final Object[][] PARSED_TEST_DATA = {
        {parseDate(TIME_00_17), new Double(0.01), new Long(17), new Double(4.35)},
        {parseDate(TIME_07_32), new Double(0.33), new Long(21), new Double(1.125)},
        {parseDate(TIME_23_59), new Long(999), new Long(7), new Double(16.3)}
    };


    private static final String[] SPLIT_HEADER = {"", "Header 1", "Header_2", "The third header"};

    private static final String ALT_DATE_PATTERN = "dd/MM/yy";

    /* ========================================================================
     *
     * Instance variables.
     */

    // Control and mock for data sink
    private MockControl m_sinkControl;
    private DataSink m_sinkMock;

    // Control and mock for line parser
    private MockControl m_lineParserControl;
    private LineParser m_lineParserMock;



    /* ========================================================================
     *
     * Lifecycle
     */

    protected void setUp() throws Exception {
        m_sinkControl = MockControl.createStrictControl(DataSink.class);
        m_sinkControl.setDefaultMatcher(MockControl.ARRAY_MATCHER);
        m_sinkMock = (DataSink) m_sinkControl.getMock();

        m_lineParserControl = MockControl.createStrictControl(LineParser.class);
        m_lineParserMock = (LineParser) m_lineParserControl.getMock();

    }

    /* ========================================================================
     *
     * Public methods
     */

    public void testParseStream_Empty() throws Exception {
        m_sinkMock.parsingFinished();
        m_sinkControl.replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, new int[]{1, 2, 3}, false, false, false,
                m_sinkMock);
        parser.parse(new StringReader(""));

        m_sinkControl.verify();
    }

    public void testParseStream_LF() throws Exception {
        parseDataImpl(TEST_DATA, null);
    }

    public void testParseStream_CRLF() throws Exception {
        parseDataImpl(TEST_DATA_WITH_CRLF, null);
    }

    public void testParseStream_withHeaders_LF() throws Exception {
        parseDataWithHeadersImpl(TEST_DATA);
    }

    public void testParseStream_withHeaders_CRLF() throws Exception {
        parseDataWithHeadersImpl(TEST_DATA_WITH_CRLF);
    }

    public void testParseStream_customDateFormat_LF() throws Exception {
        parseDataDateTimeImpl(TEST_DATA);
    }

    public void testParseStream_customDateFormat_CRLF() throws Exception {
        parseDataDateTimeImpl(TEST_DATA_WITH_CRLF);
    }

    public void testParseStream_blankLine() throws Exception {
        String testDataWithBlankLines =  "Data 1\n\nData 2\n\nData 3";
        parseDataImpl(testDataWithBlankLines, new int[]{1, 3, 5});
    }

    public void testParseStream_insufficientHeaders_error() throws Exception {
        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_HEADER);
        replay();

        try {
            TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, new int[]{1, 2, 3, 4}, true, false,
                    false, m_sinkMock);
            parser.parse(new StringReader(TEST_DATA));
            fail();
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().indexOf("Not enough header") == 0);
        }

        verify();
    }

    public void testParseStream_insufficientHeaders_skip() throws Exception {
        int[] requestedColumnIndexes = {1, 2, 3, 4};
        int[] actualColumnIndexes = {1, 2, 3};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_HEADER);
        m_sinkMock.headerParsed(new String[]{"Header 1", "Header_2", "The third header"});

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, actualColumnIndexes), 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, actualColumnIndexes), 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, requestedColumnIndexes, true, true,
                false, m_sinkMock);
        parser.parse(new StringReader(TEST_DATA));

        verify();
    }

    public void testParseStream_insufficientValues_error() throws Exception {
        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_TEST_DATA[0]);
        replay();

        try {
            TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, new int[]{1, 2, 3, 4}, false, false,
                    false, m_sinkMock);
            parser.parse(new StringReader(TEST_DATA));
            fail();
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().indexOf("Not enough data columns") == 0);
        }

        verify();
    }

    public void testParseStream_insufficientValues_skip() throws Exception {
        int[] requestedColumnIndexes = {1, 2, 3, 4};
        int[] actualColumnIndexes = {1, 2, 3};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_TEST_DATA[0]);
        m_sinkMock.dataParsed(parseDate(TIME_00_17), getParsedTestDataLine(0, actualColumnIndexes), 1);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, actualColumnIndexes), 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, actualColumnIndexes), 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, requestedColumnIndexes, false, true,
                false, m_sinkMock);
        parser.parse(new StringReader(TEST_DATA));

        verify();
    }

    public void testParseStream_emptyValue_error() throws Exception {
        String[] splitTestDataWithEmtpyValue = {TIME_00_17, "0.01", "", "4.35"};
        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), splitTestDataWithEmtpyValue);
        replay();

        try {
            TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, new int[]{1, 2, 3}, false, false,
                    false, m_sinkMock);
            parser.parse(new StringReader(TEST_DATA));
            fail();
        } catch (InvalidDataException expected) {
            assertTrue(expected.getMessage().indexOf("Invalid data value: []") == 0);
        }

        verify();
    }

    public void testParseStream_emptyValue_skip() throws Exception {
        String[][] splitTestData = {
                {TIME_00_17, "0.01", "17", "4.35"},
                {TIME_07_32, ".33", "", "1.125"},
                {TIME_23_59, "999", "7", ""}
        };
        int[] columnIndexes = {1, 2, 3};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), splitTestData[0]);
        m_sinkMock.dataParsed(parseDate(TIME_00_17), getParsedTestDataLine(0, columnIndexes), 1);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2), splitTestData[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), new Object[] {new Double(0.33), null, new Double(1.125)}, 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3), splitTestData[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), new Object[] {new Long(999), new Long(7), null}, 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, columnIndexes, false, false, true,
                m_sinkMock);
        parser.parse(new StringReader(TEST_DATA));

        verify();
    }

    public void testParseStream_invalidDate() throws Exception {
        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1),
                new String[]{"25:43", "0.01", "17", "4.35"});
        replay();

        try {
            TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, new int[]{1, 2, 3}, false, false,
                    false, m_sinkMock);
            parser.parse(new StringReader(TEST_DATA));
            fail();
        } catch (InvalidDataException expected) {

            assertTrue(expected.getMessage().indexOf("Invalid date") == 0);
        }

        verify();
    }

    public void testParseStream_twoAxis() throws Exception {
        int[] lineNumbers = new int[]{1, 2, 3};
        int[] columnIndexes = {1, 2};
        int[] secondAxisColumnIndexes = {3};

        // we'll use the same mock for both sinks (first and second axes), but just set to expect the 2 calls
        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", lineNumbers[0]), SPLIT_TEST_DATA[0]);
        m_sinkMock.dataParsed(parseDate(TIME_00_17), getParsedTestDataLine(0, columnIndexes), lineNumbers[0]);
        m_sinkMock.dataParsed(parseDate(TIME_00_17), getParsedTestDataLine(0, secondAxisColumnIndexes), lineNumbers[0]);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", lineNumbers[1]), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, columnIndexes), lineNumbers[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, secondAxisColumnIndexes), lineNumbers[1]);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", lineNumbers[2]), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, columnIndexes), lineNumbers[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, secondAxisColumnIndexes), lineNumbers[2]);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, columnIndexes, false, false,
                false, m_sinkMock);
        parser.addSecondAxis(secondAxisColumnIndexes, m_sinkMock);
        parser.parse(new StringReader(TEST_DATA));

        verify();
    }

    public void testParseStream_withHeaders_twoAxis() throws Exception {
        int[] columnIndexes = {1};
        int[] secondAxisColumnIndexes = {3, 2};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_HEADER);
        m_sinkMock.headerParsed(new String[]{"Header 1"});
        m_sinkMock.headerParsed(new String[]{"The third header", "Header_2"});

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, columnIndexes), 2);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, secondAxisColumnIndexes), 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, columnIndexes), 3);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, secondAxisColumnIndexes), 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, columnIndexes, true, false, false, 
                m_sinkMock);
        parser.addSecondAxis(secondAxisColumnIndexes, m_sinkMock);
        parser.parse(new StringReader(TEST_DATA));

        verify();
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void replay() {
        m_lineParserControl.replay();
        m_sinkControl.replay();
    }

    private void verify() {
        m_lineParserControl.verify();
        m_sinkControl.verify();
    }

    private static Date parseDate(String dateString) {
        try {
            return s_dateParser.parse(dateString);
        } catch (ParseException e) {
            fail();
            return null;
        }
    }

    private void parseDataImpl(String testData, int[] lineNumbers) throws Exception {
        if (lineNumbers == null) {
            lineNumbers = new int[]{1, 2, 3};
        }
        int[] columnIndexes = {2, 3};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", lineNumbers[0]), SPLIT_TEST_DATA[0]);
        m_sinkMock.dataParsed(parseDate(TIME_00_17), getParsedTestDataLine(0, columnIndexes), lineNumbers[0]);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", lineNumbers[1]), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, columnIndexes), lineNumbers[1]);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", lineNumbers[2]), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, columnIndexes), lineNumbers[2]);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, columnIndexes, false, false, false, 
                m_sinkMock);
        parser.parse(new StringReader(testData));

        verify();
    }

    private void parseDataWithHeadersImpl(String testData) throws Exception {
        int[] columnIndexes = {3, 1};

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1), SPLIT_HEADER);
        m_sinkMock.headerParsed(new String[]{"The third header", "Header 1"});

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2), SPLIT_TEST_DATA[1]);
        m_sinkMock.dataParsed(parseDate(TIME_07_32), getParsedTestDataLine(1, columnIndexes), 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3), SPLIT_TEST_DATA[2]);
        m_sinkMock.dataParsed(parseDate(TIME_23_59), getParsedTestDataLine(2, columnIndexes), 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, null, 0, columnIndexes, true, false, false, 
                m_sinkMock);
        parser.parse(new StringReader(testData));

        verify();
    }

    private void parseDataDateTimeImpl(String testData) throws Exception {
        SimpleDateFormat dateParser = new SimpleDateFormat(ALT_DATE_PATTERN);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 1", 1),
                new String[]{"22/12/06", "0.01"});
        m_sinkMock.dataParsed(dateParser.parse("22/12/06"), new Object[]{new Double(0.01)}, 1);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 2", 2),
                new String[]{"31/12/06", ".33"});
        m_sinkMock.dataParsed(dateParser.parse("31/12/06"), new Object[]{new Double(0.33)}, 2);

        m_lineParserControl.expectAndReturn(m_lineParserMock.parseLine("Data 3", 3),
                new String[]{"1/1/07", "999"});
        m_sinkMock.dataParsed(dateParser.parse("1/1/07"), new Object[]{new Long(999)}, 3);
        m_sinkMock.parsingFinished();
        replay();

        TimeDataParser parser = new TimeDataParser(m_lineParserMock, ALT_DATE_PATTERN, 0, new int[]{1}, false, false,
                false, m_sinkMock);
        parser.parse(new StringReader(testData));

        verify();
    }

    private Object[] getParsedTestDataLine(int dataIndex, int[] columnIndexes) {
        Object[] result = new Object[columnIndexes.length];
        for (int i = 0; i < columnIndexes.length; i++) {
            int columnIndex = columnIndexes[i];
            result[i] = PARSED_TEST_DATA[dataIndex][columnIndex];
        }
        return result;
    }

}
