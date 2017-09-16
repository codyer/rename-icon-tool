import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cody.yi on 2017/9/14.
 * IconFileTableModel
 */
public class IconFileTableModel implements TableModel {

    private List<FileItem> mFileItems = new ArrayList<>();

    public List<FileItem> getFileItems() {
        return mFileItems;
    }

    public FileItem getFileItem(int index) {
        return mFileItems.get(index);
    }

    public void setFileItems(List<FileItem> fileItems) {
        if (fileItems != null && fileItems.size() > 0) {
            for (FileItem fileItem : fileItems) {
                if (!mFileItems.contains(fileItem)) {
                    mFileItems.add(fileItem);
                }
            }
        }
    }

    public IconFileTableModel() {
    }

    @Override
    public int getRowCount() {
        return mFileItems.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public String getColumnName(int columnIndex) {
        if (columnIndex == 0) return "老名字（iOS）";
        return "新名字（Android）";
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
        if (rowIndex < mFileItems.size()) {
            if (columnIndex == 0) {
                return mFileItems.get(rowIndex).getOldName();
            }else {
                return mFileItems.get(rowIndex).getNewName();
            }
        }
        return "--";
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if (columnIndex == 1 && rowIndex < mFileItems.size()) {
            mFileItems.get(rowIndex).setNewName((String) aValue);
        }
    }

    @Override
    public void addTableModelListener(TableModelListener l) {
    }

    @Override
    public void removeTableModelListener(TableModelListener l) {
    }
}
