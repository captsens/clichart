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

package net.sf.clichart.chart;

import junit.framework.TestCase;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.time.TimeSeriesCollection;
import org.easymock.MockControl;
import net.sf.clichart.data.InvalidDataException;

import java.util.Date;

/**
 * CLASS_DESCRIPTION
 *
 * @author johnd
 */
public class AbstractChartBuilderTest extends TestCase {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final String[] HEADERS = new String[] {"A", "B", "C"};

    private static final String[] HEADERS_WITH_NULL = new String[] {"A", null, "C"};
    private static final String[] HEADERS_WITH_NULL_DEFAULT = new String[] {"A", "Series 2", "C"};
    private static final String[] HEADERS_ALL_DEFAULT = new String[] {"Series 1", "Series 2", "Series 3"};

    /* ========================================================================
     *
     * Instance variables.
     */

    private MockControl m_control;
    private MockDelegate m_delegate;

    private AbstractChartBuilder m_chartBuilder;
    private static final Date DEFAULT_DATE = new Date();
    private static final Object[] Y_VALUES = new Object[]{new Integer(42), new Double(0.125), new Integer(999)};
    private static final Object[] Y_VALUES_WITH_NULL = new Object[]{null, new Double(0.125), new Integer(999)};

    private Options m_options = new OptionsBean();

    /* ========================================================================
     *
     * Constructors
     */

    /* ========================================================================
     *
     * Lifecycle methods
     */

    protected void setUp() throws Exception {
        m_control = MockControl.createStrictControl(MockDelegate.class);
        m_delegate = (MockDelegate) m_control.getMock();

        m_chartBuilder = new AbstractChartBuilder(m_options, null) {
            protected void initialiseSeriesColumn(String title) {
                m_delegate.initialiseSeriesColumn(title);
            }

            protected void addYValue(int columnNumber, Object xValue, Object yValue) {
                m_delegate.addYValue(columnNumber, xValue, yValue);
            }

            protected JFreeChart getChartImpl(Options options) {
                return m_delegate.getChartImpl(options);
            }

            protected XYDataset getDataset() {
                return m_delegate.getDataset();
            }

            protected XYToolTipGenerator getToolTipGenerator() {
                return m_delegate.getToolTipGenerator();
            }
        };
    }

    /* ========================================================================
     *
     * Public methods
     */

    public void testHeaderParsed_ok() throws InvalidDataException {
        headersParsedImpl(HEADERS, HEADERS);
    }

    public void testHeaderParsed_nullHeader() throws InvalidDataException {
        headersParsedImpl(HEADERS_WITH_NULL_DEFAULT, HEADERS_WITH_NULL);
    }

    public void testDataParsed_noHeaders() throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(HEADERS_ALL_DEFAULT);
        setAddYValueExpectations(DEFAULT_DATE, Y_VALUES);
        m_control.replay();

        m_chartBuilder.dataParsed(DEFAULT_DATE, Y_VALUES, 1);

        m_control.verify();
    }

    public void testDataParsed_headers() throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(HEADERS);
        setAddYValueExpectations(DEFAULT_DATE, Y_VALUES);
        m_control.replay();

        m_chartBuilder.headerParsed(HEADERS);
        m_chartBuilder.dataParsed(DEFAULT_DATE, Y_VALUES, 1);

        m_control.verify();
    }

    public void testDataParsed_tooManyColumns() throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(HEADERS);
        setAddYValueExpectations(DEFAULT_DATE, Y_VALUES);
        m_control.replay();

        Object[] tooManyYValues = new Object[4];
        System.arraycopy(Y_VALUES, 0, tooManyYValues, 0, Y_VALUES.length);
        tooManyYValues[3] = new Integer(123);

        m_chartBuilder.headerParsed(HEADERS);
        m_chartBuilder.dataParsed(DEFAULT_DATE, tooManyYValues, 1);

        m_control.verify();
    }

    public void testDataParsed_nullValue() throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(HEADERS);
        setAddYValueExpectations(DEFAULT_DATE, Y_VALUES_WITH_NULL);
        m_control.replay();

        m_chartBuilder.headerParsed(HEADERS);
        m_chartBuilder.dataParsed(DEFAULT_DATE, Y_VALUES_WITH_NULL, 1);

        m_control.verify();
    }

    public void testGetChart() throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(HEADERS);
        Options options = new OptionsBean();
        m_control.expectAndReturn(m_delegate.getChartImpl(options), ChartFactory.createTimeSeriesChart("title",
                "xTitle", "yTitle", new TimeSeriesCollection(), true, false, false));
        m_control.expectAndReturn(null, m_delegate.getToolTipGenerator());

        m_control.replay();

        m_chartBuilder.headerParsed(HEADERS);
        m_chartBuilder.getChart(options);

        m_control.verify();
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void headersParsedImpl(String[] headersAtDelegate, String[] headersAdvised) throws InvalidDataException {
        setInitialiseSeriesColumnExpectations(headersAtDelegate);
        m_control.replay();

        m_chartBuilder.headerParsed(headersAdvised);

        m_control.verify();
    }

    private void setInitialiseSeriesColumnExpectations(String[] headers) {
        for (int i = 0; i < headers.length; i++) {
            String header = headers[i];
            m_delegate.initialiseSeriesColumn(header);
        }
    }

    private void setAddYValueExpectations(Object xValue, Object[] yValues) {
        for (int i = 0; i < yValues.length; i++) {
            Object yValue = yValues[i];
            m_delegate.addYValue(i, xValue, yValue);
        }
    }
}

// We'll delegate all calls to the subclass onto a mock of this type
interface MockDelegate {
    public void initialiseSeriesColumn(String title);
    public void addYValue(int columnNumber, Object xValue, Object yValue);
    public JFreeChart getChartImpl(Options options);
    public XYDataset getDataset();
    public XYToolTipGenerator getToolTipGenerator();
}
