package com.vmware.vcloud.nclient.ui;

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
                return new XmlView(element);
            }
        };
    }

    @Override
    public ViewFactory getViewFactory() {
        return xmlViewFactory;
    }

    @Override
    public String getContentType() {
        return "text/xml";
    }
}