package com.vmware.vcloud.nclient.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainDocument;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;

public class HighlightView extends PlainView {

    final Map<Pattern, Color> patternToColor;

    public HighlightView(Element element, Map<Pattern, Color> patternToColor) {
        super(element);
        this.patternToColor = patternToColor;
        getDocument().putProperty(PlainDocument.tabSizeAttribute, 4);
    }

    @Override
    protected int drawUnselectedText(Graphics graphics, int x, int y, int p0, int p1) throws BadLocationException {
        Document doc = getDocument();
        String text = doc.getText(p0, p1 - p0);
        Segment segment = getLineBuffer();
        Set<ColorSegment> colorSegments = getColorSegments(text);

        int offset = 0;
        for (ColorSegment cs : colorSegments) {
            if (offset < cs.start) {
                graphics.setColor(Color.black);
                doc.getText(p0 + offset, cs.start - offset, segment);
                x = Utilities.drawTabbedText(segment, x, y, graphics, this, offset);
            }

            graphics.setColor(cs.color);
            doc.getText(p0 + cs.start, cs.end - cs.start, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, cs.start);
            offset = cs.end;
        }

        if (offset < text.length()) {
            graphics.setColor(Color.black);
            doc.getText(p0 + offset, text.length() - offset, segment);
            x = Utilities.drawTabbedText(segment, x, y, graphics, this, offset);
        }
        return x;
    }

    Set<ColorSegment> getColorSegments(String text) {
        Set<ColorSegment> result = new TreeSet<ColorSegment>();
        for (Pattern pattern : patternToColor.keySet()) {
            Color color = patternToColor.get(pattern);
            Matcher m = pattern.matcher(text);
            while (m.find()) {
                ColorSegment cs = new ColorSegment(m.start(1), m.end(1), color);
                result.add(cs);
            }
        }
        return result;
    }

    static class ColorSegment implements Comparable<ColorSegment> {
        final int start;
        final int end;
        final Color color;

        public ColorSegment(int start, int end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }

        @Override
        public int compareTo(ColorSegment o) {
            return this.start - o.start;
        }
    }
}