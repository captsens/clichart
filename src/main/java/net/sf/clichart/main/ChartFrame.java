/* (C) Copyright 2006, by John Dickson
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

package net.sf.clichart.main;

import net.sf.clichart.chart.ChartSaver;
import net.sf.clichart.chart.ChartSaverException;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;

/**
 * A very simple window for showing a chart, with a menu item to save the chart.
 *
 * <p>Need to set dimensions (if required), then set chart (note that pack is called from setChart)
 *
 * @author johnd
 */
public class ChartFrame extends JFrame {

    /* ========================================================================
     *
     * Class (static) variables.
     */

    private static final long serialVersionUID = 1L;

    /* ========================================================================
     *
     * Instance variables.
     */

    private int m_initialWidth = 800;
    private int m_initialHeight = 600;

    private int m_minimumWidth = 300;
    private int m_minimumHeight = 100;

    private JFreeChart m_chart;

    /* ========================================================================
     *
     * Constructors
     */

    public ChartFrame() throws HeadlessException {
        super("Chart");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        createMenu();
    }

    /* ========================================================================
     *
     * Static methods
     */

    /* ========================================================================
     *
     * Public methods
     */

    public void setInitialWidth(int initialWidth) {
        m_initialWidth = initialWidth;
    }

    public void setInitialHeight(int initialHeight) {
        m_initialHeight = initialHeight;
    }

    public void setMinimumWidth(int minimumWidth) {
        m_minimumWidth = minimumWidth;
    }

    public void setMinimumHeight(int minimumHeight) {
        m_minimumHeight = minimumHeight;
    }

    public void setChart(JFreeChart chart) {
        m_chart = chart;

        // Note that we use an anonymous subclass here, since our save method is better than the default
        ChartPanel chartView = new ChartPanel(chart, m_initialWidth, m_initialHeight, m_minimumWidth, m_minimumHeight,
                m_initialWidth, m_initialHeight, true, true, true, true, true, true) {
            // to stop compiler warnings...
            private static final long serialVersionUID = 1L;
            public void doSaveAs() {
                saveChart();
            }
        };

        getContentPane().add(chartView);
        pack();
    }

    /* ========================================================================
     *
     * Protected / package-private methods
     */

    /* ========================================================================
     *
     * Private methods
     */

    private void createMenu() {
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        JMenuItem saveItem = new JMenuItem("Save as...", KeyEvent.VK_S);
        saveItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveChart();
            }
        });
        fileMenu.add(saveItem);

        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_Q);
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

    }

    private void saveChart() {
        JFileChooser chooser = new JFileChooser(".");
        chooser.addChoosableFileFilter(new ExtensionFileFilter(".jpg", "JPEG files"));
        chooser.addChoosableFileFilter(new ExtensionFileFilter(".png", "PNG files"));

        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File outputFile = chooser.getSelectedFile();

            if (outputFile.exists()) {
                int result = JOptionPane.showConfirmDialog(this, "File exists - overwrite?", "File exists",
                        JOptionPane.OK_CANCEL_OPTION);
                if (result != JOptionPane.OK_OPTION) {
                    return;
                }
            }

            System.err.println("Saving chart to " + outputFile.getPath());
            try {
                new ChartSaver(m_chart, m_initialWidth, m_initialHeight).saveChart(outputFile);
            } catch (ChartSaverException e) {
                JOptionPane.showMessageDialog(this, "Failed to save chart: " + e.getMessage());
            }
        }
    }

    /* ========================================================================
     *
     * Inner classes
     */

    private static class ExtensionFileFilter extends FileFilter {
        private final String m_extension;
        private final String m_description;

        public ExtensionFileFilter(String extension, String description) {
            m_extension = extension.toUpperCase();
            m_description = description;
        }

        public boolean accept(File f) {
            return (f.isDirectory() || f.getName().toUpperCase().endsWith(m_extension));
        }

        public String getDescription() {
            return m_description;
        }
    }



}
