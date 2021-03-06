package example;
//-*- mode:java; encoding:utf-8 -*-
// vim:set fileencoding=utf-8:
//@homepage@
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.swing.*;
import javax.swing.plaf.basic.*;
import javax.swing.table.*;

public final class MainPanel extends JPanel {
    private MainPanel() {
        super(new BorderLayout());

        // http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html
        String[] columnNames = {"SNo.", "1", "2", "Native", "2", "3"};
        Object[][] data = {
            {"119", "foo", "bar", "ja", "ko", "zh"},
            {"911", "bar", "foo", "en", "fr", "pt"}
        };
        DefaultTableModel model = new DefaultTableModel(data, columnNames);
        JTable table = new JTable(model) {
            @Override protected JTableHeader createDefaultTableHeader() {
                TableColumnModel cm = getColumnModel();
                ColumnGroup gname = new ColumnGroup("Name");
                gname.add(cm.getColumn(1));
                gname.add(cm.getColumn(2));

                ColumnGroup glang = new ColumnGroup("Language");
                glang.add(cm.getColumn(3));

                ColumnGroup gother = new ColumnGroup("Others");
                gother.add(cm.getColumn(4));
                gother.add(cm.getColumn(5));

                glang.add(gother);

                GroupableTableHeader header = new GroupableTableHeader(cm);
                header.addColumnGroup(gname);
                header.addColumnGroup(glang);
                return header;
            }
        };
        add(new JScrollPane(table));
        setPreferredSize(new Dimension(320, 240));
    }
    public static void main(String... args) {
        EventQueue.invokeLater(new Runnable() {
            @Override public void run() {
                createAndShowGUI();
            }
        });
    }
    public static void createAndShowGUI() {
//         try {
//             UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//         } catch (ClassNotFoundException | InstantiationException
//                | IllegalAccessException | UnsupportedLookAndFeelException ex) {
//             ex.printStackTrace();
//         }
        JFrame frame = new JFrame("@title@");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.getContentPane().add(new MainPanel());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}

/** GroupableTableHeader
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html
 * @version 1.0 10/20/98
 * @author Nobuo Tamemasa
 * modified by aterai aterai@outlook.com
 */
class GroupableTableHeader extends JTableHeader {
    private transient List<ColumnGroup> columnGroups;

    protected GroupableTableHeader(TableColumnModel model) {
        super(model);
    }
    @Override public void updateUI() {
        super.updateUI();
        setUI(new GroupableTableHeaderUI());
    }
    // [java] BooleanGetMethodName: Don't report bad method names on @Override #97
    // https://github.com/pmd/pmd/pull/97
    @Override public boolean getReorderingAllowed() {
        return false;
    }
    @Override public void setReorderingAllowed(boolean b) {
        super.setReorderingAllowed(false);
    }
    public void addColumnGroup(ColumnGroup g) {
        if (columnGroups == null) {
            columnGroups = new ArrayList<>();
        }
        columnGroups.add(g);
    }
    public List<?> getColumnGroups(TableColumn col) {
        if (columnGroups == null) {
            return Collections.emptyList();
        }
        for (ColumnGroup cGroup: columnGroups) {
            List<?> groups = cGroup.getColumnGroupList(col, new ArrayList<>());
            if (!groups.isEmpty()) {
                return groups;
            }
        }
        return Collections.emptyList();
    }
}

/** GroupableTableHeaderUI
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html
 * @version 1.0 10/20/98
 * @author Nobuo Tamemasa
 * modified by aterai aterai@outlook.com
 */
class GroupableTableHeaderUI extends BasicTableHeaderUI {
    @Override public void paint(Graphics g, JComponent c) {
        Rectangle clip = g.getClipBounds();
        Point left = clip.getLocation();
        Point right = new Point(clip.x + clip.width - 1, clip.y);
        TableColumnModel cm = header.getColumnModel();
        int cMin = header.columnAtPoint(left);
        int cMax = header.columnAtPoint(right);

        Rectangle cellRect = header.getHeaderRect(cMin);
        int headerY      = cellRect.y;
        int headerHeight = cellRect.height;

        Map<ColumnGroup, Rectangle> h = new ConcurrentHashMap<>();
        //int columnMargin = header.getColumnModel().getColumnMargin();
        //int columnWidth;
        for (int column = cMin; column <= cMax; column++) {
            TableColumn aColumn = cm.getColumn(column);
            cellRect.y = headerY;
            cellRect.setSize(aColumn.getWidth(), headerHeight);

            int groupHeight = 0;
            List<?> cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);
            for (Object o: cGroups) {
                ColumnGroup cGroup = (ColumnGroup) o;
                Rectangle groupRect = (Rectangle) h.get(cGroup);
                if (groupRect == null) {
                    groupRect = new Rectangle(cellRect.getLocation(), cGroup.getSize(header));
                    h.put(cGroup, groupRect);
                }
                paintCellGroup(g, groupRect, cGroup);
                groupHeight += groupRect.height;
                cellRect.height = headerHeight - groupHeight;
                cellRect.y      = groupHeight;
            }
            paintCell(g, cellRect, column);
            cellRect.x += cellRect.width;
        }
    }

    //Copied from javax/swing/plaf/basic/BasicTableHeaderUI.java
    private Component getHeaderRenderer(int columnIndex) {
        TableColumn aColumn = header.getColumnModel().getColumn(columnIndex);
        TableCellRenderer renderer = aColumn.getHeaderRenderer();
        if (renderer == null) {
            renderer = header.getDefaultRenderer();
        }
        boolean hasFocus = !header.isPaintingForPrint() && header.hasFocus();
        //&& (columnIndex == getSelectedColumnIndex())
        return renderer.getTableCellRendererComponent(header.getTable(), aColumn.getHeaderValue(), false, hasFocus, -1, columnIndex);
    }

    //Copied from javax/swing/plaf/basic/BasicTableHeaderUI.java
    private void paintCell(Graphics g, Rectangle cellRect, int columnIndex) {
        Component component = getHeaderRenderer(columnIndex);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private void paintCellGroup(Graphics g, Rectangle cellRect, ColumnGroup cGroup) {
        TableCellRenderer renderer = header.getDefaultRenderer();
        Component component = renderer.getTableCellRendererComponent(header.getTable(), cGroup.getHeaderValue(), false, false, -1, -1);
        rendererPane.paintComponent(g, component, header, cellRect.x, cellRect.y, cellRect.width, cellRect.height, true);
    }

    private int getHeaderHeight() {
        int height = 0;
        TableColumnModel columnModel = header.getColumnModel();
        for (int column = 0; column < columnModel.getColumnCount(); column++) {
            TableColumn aColumn = columnModel.getColumn(column);
            Component comp = getHeaderRenderer(column);
            int rendererHeight = comp.getPreferredSize().height;
            List<?> cGroups = ((GroupableTableHeader) header).getColumnGroups(aColumn);
            for (Object o: cGroups) {
                ColumnGroup cGroup = (ColumnGroup) o;
                rendererHeight += cGroup.getSize(header).height;
            }
            height = Math.max(height, rendererHeight);
        }
        return height;
    }

    //Copied from javax/swing/plaf/basic/BasicTableHeaderUI.java
    private Dimension createHeaderSize(long width) {
        long w = Math.min(width, Integer.MAX_VALUE);
        return new Dimension((int) w, getHeaderHeight());
    }

    @Override public Dimension getPreferredSize(JComponent c) {
        long width = Collections.list(header.getColumnModel().getColumns()).stream()
                                .mapToLong(TableColumn::getPreferredWidth).sum();
//         long width = 0;
//         Enumeration<TableColumn> enumeration = header.getColumnModel().getColumns();
//         while (enumeration.hasMoreElements()) {
//             TableColumn aColumn = (TableColumn) enumeration.nextElement();
//             width += aColumn.getPreferredWidth();
//         }
        return createHeaderSize(width);
    }
}

/** ColumnGroup
 * http://www2.gol.com/users/tame/swing/examples/JTableExamples1.html
 * @version 1.0 10/20/98
 * @author Nobuo Tamemasa
 * modified by aterai aterai@outlook.com
 */
class ColumnGroup {
    protected final List<Object> v = new ArrayList<>();
    protected final String text;
    protected ColumnGroup(String text) {
        this.text = text;
    }

    /**
     * @param obj: TableColumn or ColumnGroup
     */
    public void add(Object obj) {
        if (obj == null) {
            return;
        }
        v.add(obj);
    }

    public List<?> getColumnGroupList(TableColumn c, List<Object> g) {
        g.add(this);
        if (v.contains(c)) {
            return g;
        }
        for (Object obj: v) {
            if (obj instanceof ColumnGroup) {
                List<?> groups = ((ColumnGroup) obj).getColumnGroupList(c, new ArrayList<>(g));
                if (!groups.isEmpty()) {
                    return groups;
                }
            }
        }
        return Collections.emptyList();
    }

    public Object getHeaderValue() {
        return text;
    }

    public Dimension getSize(JTableHeader header) {
        TableCellRenderer renderer = header.getDefaultRenderer();
        Component c = renderer.getTableCellRendererComponent(header.getTable(), getHeaderValue(), false, false, -1, -1);
        int width = 0;
        for (Object obj: v) {
            if (obj instanceof TableColumn) {
                TableColumn aColumn = (TableColumn) obj;
                width += aColumn.getWidth();
            } else {
                width += ((ColumnGroup) obj).getSize(header).width;
            }
        }
        return new Dimension(width, c.getPreferredSize().height);
    }
}
