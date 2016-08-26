package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import javax.swing.*;
//import javax.swing.plaf.*;
import javax.swing.text.*;

public final class MainPanel extends JPanel {
    private final JTextField field0 = new JTextField("aaaaaaaaaa");
    private final JTextField field1 = new JTextField("bbbbbbbbbbbb");
    private final JTextField field2 = new JTextField("123465789735");
    public MainPanel() {
        super(new BorderLayout());

        field1.setSelectedTextColor(Color.RED);
        field1.setSelectionColor(Color.GREEN);

        Highlighter.HighlightPainter selectionPainter = new DefaultHighlighter.DefaultHighlightPainter(Color.WHITE) {
            @Override public Shape paintLayer(Graphics g, int offs0, int offs1, Shape bounds, JTextComponent c, View view) {
                Shape s = super.paintLayer(g, offs0, offs1, bounds, c, view);
                if (s instanceof Rectangle) {
                    Rectangle r = (Rectangle) s;
                    g.setColor(Color.ORANGE);
                    g.fillRect(r.x, r.y + r.height - 2, r.width, 2);
                }
                return s;
            }
        };
        Caret caret = new DefaultCaret() {
            @Override protected Highlighter.HighlightPainter getSelectionPainter() {
                return selectionPainter;
            }
        };
        caret.setBlinkRate(field2.getCaret().getBlinkRate());
        field2.setSelectedTextColor(Color.RED);
        field2.setCaret(caret);

        Box box = Box.createVerticalBox();
        box.add(makePanel("Default", field0));
        box.add(Box.createVerticalStrut(10));
        box.add(makePanel("JTextComponent#setSelectionColor(...)", field1));
        box.add(Box.createVerticalStrut(10));
        box.add(makePanel("JTextComponent#setCaret(...)", field2));
        box.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(box, BorderLayout.NORTH);
        setPreferredSize(new Dimension(320, 240));
    }
    private static JPanel makePanel(String title, JComponent c) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createTitledBorder(title));
        p.add(c);
        return p;
    }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException
               | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            ex.printStackTrace();
        }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}