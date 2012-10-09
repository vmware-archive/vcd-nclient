package com.vmware.vcloud.nclient.ui;

import java.awt.Color;
import java.util.HashMap;
import java.util.regex.Pattern;

import javax.swing.text.Element;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

public class XmlEditorKit extends StyledEditorKit {

    private static final long serialVersionUID = 1L;
    final ViewFactory xmlViewFactory;

    public XmlEditorKit() {
        xmlViewFactory = new ViewFactory() {
            @Override
            public View create(Element element) {
                HashMap<Pattern, Color> patternToColor = new HashMap<Pattern, Color>();
                String TAG_PATTERN = "(</?[\\w:]*\\s?>?)";
                String TAG_END_PATTERN = "(/>)";
                String ATTR_KEY_PATTERN = "\\s([\\w:]*)\\=";
                String ATTR_VALUE_PATTERN = "[a-z-]*\\=(\"[^\"]*\")";

                patternToColor.put(Pattern.compile(TAG_PATTERN), new Color(63, 127, 127));
                patternToColor.put(Pattern.compile(TAG_END_PATTERN), new Color(63, 127, 127));
                patternToColor.put(Pattern.compile(ATTR_KEY_PATTERN), new Color(127, 0, 127));
                patternToColor.put(Pattern.compile(ATTR_VALUE_PATTERN), new Color(42, 0, 255));
                return new HighlightView(element, patternToColor);
            }
        };
    }

    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }

    @Override
    public String getContentType() {
        return "application/xml";
    }
}