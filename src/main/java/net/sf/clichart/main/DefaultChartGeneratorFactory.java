/**
 * 
 */
package net.sf.clichart.main;

public class DefaultChartGeneratorFactory implements ChartGeneratorFactory {

    public ChartGenerator createChartGenerator() {
        return new DefaultChartGenerator();
    }
}
