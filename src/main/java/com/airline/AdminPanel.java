package com.airline;

import javax.swing.*;
import java.awt.*;

public class AdminPanel extends JPanel {
    public AdminPanel() {
        setLayout( new BorderLayout() );

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Planes", new PlanesPanel());
        tabs.addTab("Routes", new RoutesPanel());
        tabs.addTab("Flights", new FlightsPanel());

        add(tabs, BorderLayout.CENTER );
    }
}