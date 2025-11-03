package com.gamebuilder.canvasobject.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import com.gamebuilder.Point;
import com.gamebuilder.canvasobject.CanvasObject;
import com.gamebuilder.model.Selectable;

class TextStyle {
    public String fontFamily = "Arial";
    public int fontSize = 13;
    public boolean bold = false;
    public boolean italic = false;
    public int letterSpacing = 1;
    public Color color = Color.BLACK;
}

public class Text extends CanvasObject implements Selectable {
    private String content;
    private Point initialPosition;
    private Point cursorPosition;

    private long lastCursorTime = 0;
    private int cursorBlinkSpeed = 300;
    private boolean cursorVisible = false;
    private int currentCursorIndex = 0;

    private Graphics g;
    private boolean focus = true;
    private TextStyle style = new TextStyle();

    private BoundingRect boundingRect;
    private int width = 0;
    private int height = 0;
    private boolean selected;

    public Text(String name, Point initialPosition) {
        setName(name);
        this.content = "";
        this.initialPosition = initialPosition;
        this.cursorPosition = initialPosition.clone();
        this.boundingRect = new BoundingRect(
                initialPosition.getXInt(),
                initialPosition.getYInt(), initialPosition.getXInt() + 5,
                initialPosition.getYInt() + 15);
    }

    public void addCharacter(char character) {
        if (character != '\b') {
            StringBuilder sb = new StringBuilder();
            sb.append(this.content.substring(0, this.currentCursorIndex));
            sb.append(character);
            sb.append(this.content.substring(this.currentCursorIndex, this.content.length()));
            this.content = sb.toString();
            this.currentCursorIndex++;
        } else if (character == '\b' && this.content.length() > 0) {
            String after = this.content.substring(this.currentCursorIndex, this.content.length());
            StringBuilder sb = new StringBuilder();
            sb.append(this.content.substring(0, this.currentCursorIndex - 1));
            sb.append(after);
            this.content = sb.toString();
            this.currentCursorIndex--;
        }

        // Update cursor position
        this.cursorPosition = this.getCursorPointAtIndex();

        this.calculateRect();
    }

    public void onClick(Point mousePosition) {
        if (this.boundingRect != null
                && !this.boundingRect.hit(mousePosition.getXInt(), mousePosition.getYInt())) {
            this.setFocus(false);
            return;
        }

        this.currentCursorIndex = this.getIndexAtPosition(mousePosition);
        this.cursorPosition = this.getCursorPointAtIndex();
    }

    @Override()
    public Point getPosition() {
        return this.initialPosition;
    }

    @Override()
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override()
    public boolean getSelected() {
        return this.selected;
    }

    @Override()
    public String getTooltipText() {
        return "";
    }

    @Override()
    public BoundingRect getBoundingRect() {
        return this.boundingRect;
    }

    @Override()
    public boolean mouseHit(int mx, int my) {
        return this.boundingRect.hit(mx, my);
    }

    @Override()
    public void translate(int dx, int dy) {
        this.initialPosition = new Point(this.initialPosition.getXInt() + dx, this.initialPosition.getYInt() + dy);
        this.calculateRect();
    }

    @Override()
    public int getWidth() {
        return this.width;
    }

    @Override()
    public int getHeight() {
        return this.height;
    }

    @Override()
    public void setSize(int width, int height) {
        int dw = width - this.width;
        int dh = height - this.height;

        this.style.fontSize = this.style.fontSize + dw;
        this.calculateRect();
    }

    @Override()
    public void draw(Graphics g) {
        this.g = g.create();

        this.drawText(this.g);

        if (this.focus) {
            this.drawCursor(this.g);

            this.drawBox(this.g);
        }

        this.g.dispose();
    }

    @Override()
    public String toXml() {
        return "<Text></Text>";
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    public void moveCursorLeft() {
        if (this.currentCursorIndex > 0) {
            this.currentCursorIndex--;
        }

        this.cursorPosition = this.getCursorPointAtIndex();
    }

    public void moveCursorRight() {
        if (this.currentCursorIndex < this.content.length()) {
            this.currentCursorIndex++;
        }

        this.cursorPosition = this.getCursorPointAtIndex();
    }

    public void moveCursorUp() {

    }

    public void moveCursorDown() {

    }

    private Point getCursorPointAtIndex() {
        FontMetrics fm = this.g.getFontMetrics();
        
        int x = this.initialPosition.getXInt();
        int y = this.initialPosition.getYInt();

        char[] chars = this.content.toCharArray();
        for (int i = 0; i < this.currentCursorIndex; i++) {
            if (chars[i] == '\n') {
                x = initialPosition.getXInt();
                y += fm.getHeight();
            } else {
                x += this.getCharLength(chars[i]);
            }
        }

        return new Point(x, y);
    }

    private int getIndexAtPosition(Point mousePosition) {
        FontMetrics fm = this.g.getFontMetrics();

        // Get line
        String[] lines = this.content.split("\n");
        int clickedLineIndex = -1;

        // Get the y position relative to this text box
        int dy = mousePosition.getYInt() - this.initialPosition.getYInt();
        for (int i = 0; i < lines.length; i++) {
            int lineY1 = i * fm.getHeight();
            int lineY2 = lineY1 + fm.getHeight();

            if (dy >= lineY1 && dy <= lineY2) {
                clickedLineIndex = i;
            }
        }

        if (clickedLineIndex >= 0) {
            int cursorIndex = 0;

            // First get the index of the last character of the line
            // before the line the user clicked
            for (int i = 0; i < clickedLineIndex; i++) {
                cursorIndex += lines[i].length() + 1;
            }

            String lastLine = lines[clickedLineIndex];
            int dx = mousePosition.getXInt() - this.initialPosition.getXInt();
            // If the user did not click on any character,
            // set the index to the index of the last character of that line
            if (dx > this.getStringLength(lastLine)) {
                return cursorIndex + lastLine.length();
            }

            // Find the index of the character that the user clicked on (where the first character
            // on that line 0) then add it to the index of the last character on the line before.
            // Also add 1 as adding the index of character clicked is length - 1 returns
            // the index of the character before the character the user clicked on.
            for (int i = 0; i < lastLine.length(); i++) {
                int x1 = this.getStringLength(lastLine.substring(0, i));
                int halfCharLength = this.getCharLength(lastLine.charAt(i)) / 2;
                int x1_5 = x1 + halfCharLength;
                int x2 = x1_5 + halfCharLength;

                if (dx >= x1 && dx <= x1_5) {
                    return cursorIndex + i;
                } else if (dx > x1_5 && dx <= x2) {
                    return cursorIndex + i + 1;
                }
            }

            return cursorIndex;
        }

        return 0;
    }

    private void drawText(Graphics g) {
        g.setColor(this.style.color);
        g.setFont(new Font(this.style.fontFamily, Font.PLAIN, this.style.fontSize));

        FontMetrics fm = g.getFontMetrics();

        char[] chars = this.content.toCharArray();
        int x = this.initialPosition.getXInt();
        int y = this.initialPosition.getYInt() + fm.getAscent();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == '\n') {
                x = initialPosition.getXInt();
                y += fm.getHeight();
                continue;
            }

            String c = String.valueOf(chars[i]);
            g.drawString(c, x, y);
            x += this.getCharLength(chars[i]);
        }
    }

    private void drawCursor(Graphics g) {
        if (this.lastCursorTime == 0) {
            this.lastCursorTime = System.currentTimeMillis();
        }

        long now = System.currentTimeMillis();
        if (now - lastCursorTime >= cursorBlinkSpeed) {
            this.cursorVisible = !this.cursorVisible;
            this.lastCursorTime = now;
        }

        if (this.cursorVisible) {
            g.setColor(Color.BLACK);
            g.fillRect(this.cursorPosition.getXInt(), this.cursorPosition.getYInt(), 2, 15);
        }
    }

    private int getCharLength(char c) {
        FontMetrics fm = this.g.getFontMetrics();

        return fm.stringWidth(String.valueOf(c)) + this.style.letterSpacing;
    }

    private int getStringLength(String str) {
        FontMetrics fm = this.g.getFontMetrics();

        return fm.stringWidth(str) + (this.style.letterSpacing * str.length());
    }

    private void drawBox(Graphics g) {
        if (this.boundingRect != null) {
            g.drawRect(this.boundingRect.left, this.boundingRect.top,
                    this.boundingRect.right - this.boundingRect.left,
                    this.boundingRect.bottom - this.boundingRect.top);
        }
    }

    private void calculateRect() {
        String[] lines = this.content.split("\n", -1);
        int x = this.initialPosition.getXInt();
        int y = this.initialPosition.getYInt();

        int longestLineWidth = 0;
        int totalLineHeight = 0;

        FontMetrics fm = this.g.getFontMetrics();
        for (int i = 0; i < lines.length; i++) {
            int w = this.getStringLength(lines[i]);
            if (w > longestLineWidth) longestLineWidth = w;

            totalLineHeight += fm.getHeight();
        }

        this.width = longestLineWidth + 2 + 1;
        this.height = totalLineHeight;
        this.boundingRect = new BoundingRect(y, x + this.width,
                y + totalLineHeight, x);
    }
}
