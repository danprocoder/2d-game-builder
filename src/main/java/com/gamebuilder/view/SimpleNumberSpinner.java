package com.gamebuilder.view;

import java.awt.Dimension;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeListener;

import com.gamebuilder.util.Log;

// Exception in thread "AWT-EventQueue-0" java.lang.IllegalArgumentException: (minimum <= value <= maximum) is false

/** Creates a simple number spinner with step of 1 */
public class SimpleNumberSpinner extends JSpinner {
    public SimpleNumberSpinner(int value, int max, ChangeListener listener) {
        super(new SpinnerNumberModel(value, 0, max, 1));
        Log.d("SimpleNumberSpinner", "Creating number spinner with value: " + value + " max: " + max);

        setPreferredSize(new Dimension(70, getPreferredSize().height));
        addChangeListener(listener);
    }

    public SimpleNumberSpinner(int value, ChangeListener listener) {
        this(value, Integer.MAX_VALUE, listener);
    }
}
