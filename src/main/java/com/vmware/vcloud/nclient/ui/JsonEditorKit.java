package com.vmware.vcloud.nclient.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class JsonEditorKit extends StyledEditorKit {

    private static final long serialVersionUID = 1L;
    final ViewFactory jsonViewFactory;

    public JsonEditorKit() {
        jsonViewFactory = new ViewFactory() {
            @Override
            public View create(Element element) {
                HashMap<Pattern, Color> patternToColor = new HashMap<Pattern, Color>();
                String KEY_PATTERN = "\\s+(\"[^\"]*\") : ";
                String VALUE_PATTERN = " : (\"[^\"]*\")";
                String TRUE_PATTERN = " : (true)";
                String FALSE_PATTERN = " : (false)";

                patternToColor.put(Pattern.compile(KEY_PATTERN), new Color(127, 0, 127));
                patternToColor.put(Pattern.compile(VALUE_PATTERN), new Color(42, 0, 255));
                patternToColor.put(Pattern.compile(TRUE_PATTERN), new Color(42, 0, 255));
                patternToColor.put(Pattern.compile(FALSE_PATTERN), new Color(42, 0, 255));
                return new HighlightView(element, patternToColor);
            }
        };
    }

    @Override
    public ViewFactory getViewFactory() {
        return jsonViewFactory;
    }

    @Override
    public String getContentType() {
        return "application/json";
    }

}
