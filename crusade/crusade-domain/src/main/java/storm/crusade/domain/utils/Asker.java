package storm.crusade.domain.utils;

import java.awt.GridLayout;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

/**
 * Utility to setup properties from a UI window.
 * 
 * <pre>
 * Asker asker = new Asker(&quot;My Properties&quot;);
 * asker.setScalar(&quot;email&quot;);
 * asker.setScalar(&quot;day&quot;, &quot;Monday&quot;);
 * asker.setChoices(&quot;url&quot;, &quot;http://www.apple.com&quot;, &quot;http://www.google.com&quot;);
 * asker.setChoices(true, &quot;color&quot;, &quot;red&quot;, &quot;blue&quot;);
 * 
 * asker.getValue(&quot;email&quot;);
 * asker.getValue(&quot;day&quot;);
 * asker.getValue(&quot;url&quot;);
 * asker.getValue(&quot;color&quot;);
 * </pre>
 * 
 * @author <a href="timothystorm@gmail.com">Timothy Storm</a>
 */
public class Asker {
    /**
     * Presents a custom dialog to the user for capturing properties
     */
    private class AskerDialog extends JDialog {
        private static final long  serialVersionUID = 1L;
        private Map<String, Model> _models;
        private JPanel             _panel;

        AskerDialog(Map<String, Model> models) {
            _models = models;
            initPanel();
            pack();
            load();
        }

        private void initPanel() {
            // setup the base panel
            _panel = new JPanel(true);
            _panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));

            // add the different components to the panel
            for (Entry<String, Model> entry : _models.entrySet()) {
                JPanel comp = new JPanel(new GridLayout(0, 2), false);
                comp.add(new JLabel(entry.getKey() + StringUtils.SPACE));
                comp.add(entry.getValue().getComponent());
                _panel.add(comp);
            }
        }

        /**
         * Configures and shows the dialog to the user. On close captures the answers from the user.
         */
        private void load() {
            // setup the dialog
            String[] ANSWERS = new String[] { "Cancel", "Submit" };
            int answer = JOptionPane.showOptionDialog(null, _panel, _title, JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, ANSWERS, ANSWERS[1]);

            // extract the results
            if (JOptionPane.CLOSED_OPTION == answer || 0 == answer) throw new IllegalStateException("Asker Cancelled");
            for (Model model : _models.values()) {
                // allow the models to capture their values
                model.capture();
            }
        }
    }

    /**
     * Component model that both builds the swing component and stores the resulting value.
     * 
     * @param <T>
     *            Type this component stores
     */
    interface Model {
        /**
         * Called by the dialog on close to allow the model to capture the user value from the component before it is
         * disposed.
         */
        void capture();

        /**
         * @return the component of this model
         */
        JComponent getComponent();

        /**
         * @return user value captured by the component of this model
         */
        String getValue();
    }

    /**
     * Used to capture a single value from a set of known values. Creates a combo box control.
     */
    private static class OptionsControl implements Model {
        JComboBox<String> _options;
        String            _value;

        OptionsControl(boolean editable, String... options) {
            if (options == null || options.length <= 0)
                throw new IllegalArgumentException("at least 1 option required!");

            _options = new JComboBox<String>(options);
            _options.setSelectedItem(options[0]);
            _options.setEditable(editable);
        }

        @Override
        public void capture() {
            _value = (String) _options.getSelectedItem();
        }

        @Override
        public JComponent getComponent() {
            return _options;
        }

        @Override
        public String getValue() {
            return _value;
        }
    }

    /**
     * Used to capture a single free form value. Creates a text field control.
     */
    private static class TextControl implements Model {
        JTextField _comp;
        String     _value;

        TextControl() {
            this(StringUtils.EMPTY);
        }

        TextControl(String value) {
            _comp = new JTextField(value);
        }

        @Override
        public void capture() {
            _value = _comp.getText();
        }

        @Override
        public JComponent getComponent() {
            return _comp;
        }

        public String getValue() {
            return _value;
        }
    }

    private boolean            _asked  = false;

    private Map<String, Model> _models = Collections.synchronizedMap(new LinkedHashMap<String, Model>());

    private final String       _title;

    public Asker() {
        this("Aker");
    }

    public Asker(String title) {
        _title = title;
    }

    /**
     * Fetches the value associated to the key or null if no value associated. The type of the value is dependent on the
     * type of association made with {@link #setScalar(String)}, {@link #setChoices(String, String...)}
     * 
     * @param key
     *            - with which the user value is to be associated
     * @return value or null if key is not associated
     */
    public String getValue(String key) {
        initAsker();

        Model control = _models.get(key);
        if (control == null) throw new IllegalStateException("Unknown model: " + key);
        if (control instanceof TextControl) return ((TextControl) control).getValue();
        if (control instanceof OptionsControl) return ((OptionsControl) control).getValue();

        return null;
    }

    public Integer getInt(String key) {
        String value = getValue(key);
        if (value == null) return null;
        return NumberUtils.toInt(value);
    }

    public Long getLong(String key) {
        String value = getValue(key);
        if (value == null) return null;
        return NumberUtils.toLong(value);
    }

    public Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) return null;
        return BooleanUtils.toBoolean(value);
    }

    public Character getChar(String key) {
        String value = getValue(key);
        if (value == null) return null;
        return CharUtils.toCharacterObject(value);
    }

    /**
     * If the asker has not already been shown to the user, this creates and shows the Asker
     */
    private void initAsker() {
        if (_asked) return;
        new AskerDialog(_models);
        _asked = true;
    }

    /**
     * @param key
     *            - with which the user value is to be associated
     * @param editable
     *            - allow values not part of the specified options
     * @param options
     *            - options available to the user to choose
     */
    public void setChoices(String key, boolean editable, String... options) {
        _models.put(key, new OptionsControl(editable, options));
    }

    /**
     * 
     * @param key
     *            - with which the user value is to be associated
     * @param options
     *            - options available to the user to choose
     */
    public void setChoices(String key, String... options) {
        setChoices(key, false, options);
    }

    /**
     * @param key
     *            - with which the user value is to be associated
     */
    public void setScalar(String key) {
        _models.put(key, new TextControl());
    }

    /**
     * @param key
     *            - with which the user value is to be associated
     * @param defaultValue
     *            - value that will be default if explicitly provided by the user
     */
    public void setScalar(String key, String defaultValue) {
        _models.put(key, new TextControl(defaultValue));
    }
}
