/**
 * Created by codyer on 2017/9/16.
 * item
 */
public class FileItem {
    private String mOldName;
    private String mOldPath;
    private String mNewName;

    public String getOldName() {
        return mOldName;
    }

    public void setOldName(String oldName) {
        mOldName = oldName;
    }

    public String getOldPath() {
        return mOldPath;
    }

    public void setOldPath(String oldPath) {
        mOldPath = oldPath;
    }

    public String getNewName() {
        return mNewName;
    }

    public void setNewName(String newName) {
        mNewName = newName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileItem fileItem = (FileItem) o;

        if (mOldName != null ? !mOldName.equals(fileItem.mOldName) : fileItem.mOldName != null) return false;
        if (mOldPath != null ? !mOldPath.equals(fileItem.mOldPath) : fileItem.mOldPath != null) return false;
        return mNewName != null ? mNewName.equals(fileItem.mNewName) : fileItem.mNewName == null;
    }

    @Override
    public int hashCode() {
        int result = mOldName != null ? mOldName.hashCode() : 0;
        result = 31 * result + (mOldPath != null ? mOldPath.hashCode() : 0);
        result = 31 * result + (mNewName != null ? mNewName.hashCode() : 0);
        return result;
    }
}
