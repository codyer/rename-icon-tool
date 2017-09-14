import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cody.yi on 2017/9/14.
 * IconNameListModel
 */
public class IconNameListModel implements TableModel {
    private List<String> mNames = new ArrayList<>();
    private List<String> mNewNames = new ArrayList<>();

    public IconNameListModel(List<String> mNames) {
        this.mNames.clear();
        this.mNewNames.clear();
        this.mNames.addAll(mNames);
        this.mNewNames.addAll(mNames);
    }

    public List<String> getNames() {
        return mNames;
    }

    public void setNames(List<String> mNames) {
        this.mNames.clear();
        this.mNewNames.clear();
        this.mNames.addAll(mNames);
        this.mNewNames.addAll(mNames);
    }

    public List<String> getNewNames() {
        return mNewNames;
    }

    public void setNewNames(List<String> mNewNames) {
        this.mNewNames = mNewNames;
    }

    @Override
    public int getRowCount() {
        return mNames.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) return "ios icon name";
        return "android icon name";
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0 && rowIndex < mNames.size()) {
            return mNames.get(rowIndex);
        } else if (rowIndex < mNewNames.size()) {
            return mNewNames.get(rowIndex);
        }
        return "--";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1 && rowIndex < mNewNames.size()) {
            mNewNames.set(rowIndex, (String) aValue);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }
}
