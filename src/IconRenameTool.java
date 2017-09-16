import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

/**
 * Created by Cody.yi on 2017/9/14.
 * IconRenameTool
 */
public class IconRenameTool {

    /*properties文件名*/
    private static final String PROPERTIES_FILE_NAME = File.separator + "config.mProperties";
    private static final String SUFFIX_PNG = ".png";
    private static final String SUFFIX_2_TIMES = "@2x.png";
    private static final String SUFFIX_3_TIMES = "@3x.png";
    private static final String FILE_PATH_2_TIMES = "drawable-xhdpi";
    private static final String FILE_PATH_3_TIMES = "drawable-xxhdpi";
    /*键*/
    private static final String KEY_INPUT_PATH = "input_path";
    private static final String KEY_OUTPUT_PATH = "output_path";

    private static String sPropertiesPath = System.getProperty("java.io.tmpdir") + PROPERTIES_FILE_NAME;
    private Properties mProperties = new Properties();

    private JPanel panelMain;
    private JTextField mInputPath;
    private JTextField mOutputPath;
    private JButton mLoadButton;
    private JButton mOutputButton;
    private JButton mChooseInputButton;
    private JButton mChooseOutputButton;
    private JButton mAddButton;
    private JButton mDeleteButton;
    private JButton mClearButton;
    private JTable mIconList;
    private JLabel mIconReview;
    private IconFileTableModel mFileTableModel;

    public static void main(String[] args) {
        JFrame frame = new JFrame("Icon Rename Tool");
        frame.setContentPane(new IconRenameTool().panelMain);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, e, "设置UI失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    IconRenameTool() {
        mFileTableModel = new IconFileTableModel();
        mIconList.setModel(mFileTableModel);
        init();
    }

    private void showIcon() {
        if (mIconList.getSelectedRow() == -1 || mFileTableModel.getRowCount() <= mIconList.getSelectedRow()) {
            return;
        }
        FileItem fileItem = mFileTableModel.getFileItems().get(mIconList.getSelectedRow());
        String path = fileItem.getOldPath() + File.separator + fileItem.getOldName() + SUFFIX_2_TIMES;
        //这个是按等比缩放
        ImageIcon icon = new ImageIcon(path);
        int width = mIconReview.getWidth();
        int height = mIconReview.getHeight();
        if (icon.getIconWidth() > icon.getIconHeight()) {
            height = icon.getIconHeight() * width / icon.getIconWidth();
        } else if (icon.getIconWidth() < icon.getIconHeight()) {
            width = icon.getIconWidth() * height / icon.getIconHeight();
        }
        icon = new ImageIcon(icon.getImage().getScaledInstance(width, height, Image.SCALE_DEFAULT));
        mIconReview.setHorizontalAlignment(CENTER);
        mIconReview.setIcon(icon);
    }

    private void init() {
        if (mProperties.isEmpty()) {
            initProperties();
        }

        mInputPath.setText(mProperties.getProperty(KEY_INPUT_PATH));
        mOutputPath.setText(mProperties.getProperty(KEY_OUTPUT_PATH));

        mIconList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                showIcon();
            }
        });

        mIconList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showIcon();
            }
        });

        mChooseInputButton.addActionListener(e -> {
            setPath(mInputPath);
        });
        mChooseOutputButton.addActionListener(e -> {
            setPath(mOutputPath);
        });

        mLoadButton.addActionListener(e -> {
            loadFiles();
        });

        mOutputButton.addActionListener(e -> {
            outPutIcons();
        });

        mAddButton.addActionListener(e -> {
            addFiles(selectFiles());
        });

        mDeleteButton.addActionListener(e -> {
            if (mIconList.getSelectedRow() == -1) return;
            mFileTableModel.getFileItems().remove(mIconList.getSelectedRow());
            mIconList.updateUI();
        });

        mClearButton.addActionListener(e -> {
            mFileTableModel.getFileItems().clear();
            mIconList.updateUI();
        });
    }

    private void setPath(JTextField pathView) {
        File file = selectFilePath(pathView.getText());
        if (file != null) {
            pathView.setText(file.getPath());
            savePathKey();
        }
    }

    private void loadFiles() {
        if (null == mInputPath.getText() || mInputPath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择载入图片路径", "载入失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File file = new File(mInputPath.getText());

        File[] files = file.listFiles();
        addFiles(files);
    }

    private void outPutIcons() {
        if (mFileTableModel == null || mFileTableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(null, "当前路径下没有需要重命名的图片文件。", "导出失败", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (null == mOutputPath.getText() || mOutputPath.getText().isEmpty()) {
            JOptionPane.showMessageDialog(null, "请选择输出路径。", "导出失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        File dirX = new File(mOutputPath.getText() + File.separator + FILE_PATH_2_TIMES);
        File dirXX = new File(mOutputPath.getText() + File.separator + FILE_PATH_3_TIMES);
        if (mkDir(dirX)) return;
        if (mkDir(dirXX)) return;

        for (int i = 0; i < mFileTableModel.getRowCount(); i++) {
            FileItem fileItem = mFileTableModel.getFileItem(i);
            copyFile(fileItem.getOldPath() + File.separator + fileItem.getOldName() + SUFFIX_2_TIMES,
                    dirX.getPath() + File.separator + fileItem.getNewName() + SUFFIX_PNG);
            copyFile(fileItem.getOldPath() + File.separator + fileItem.getOldName() + SUFFIX_3_TIMES,
                    dirXX.getPath() + File.separator + fileItem.getNewName() + SUFFIX_PNG);
        }
        savePathKey();
        JOptionPane.showMessageDialog(null, "文件生成成功：" + mOutputPath.getText(), "导出成功", JOptionPane.INFORMATION_MESSAGE);
        try {
            Desktop.getDesktop().open(dirX.getParentFile());
        } catch (IOException e1) {
            JOptionPane.showMessageDialog(null, e1, "导出失败", JOptionPane.ERROR_MESSAGE);
            e1.printStackTrace();
        }
    }

    private void addFiles(File[] files) {
        if (files == null) return;

        List<FileItem> fileItems = new ArrayList<>();
        for (File file : files) {
            if (file.isFile() && file.getName().endsWith(SUFFIX_2_TIMES)) {
                FileItem fileItem = new FileItem();
                fileItem.setOldPath(file.getParent());
                fileItem.setOldName(file.getName().replace(SUFFIX_2_TIMES, ""));
                fileItem.setNewName(file.getName().replace(SUFFIX_2_TIMES, ""));
                fileItems.add(fileItem);
            }
        }
        mFileTableModel.setFileItems(fileItems);
        mIconList.updateUI();
    }

    /**
     * 初始化properties，即载入数据
     */
    private void initProperties() {
        try {
            File file = new File(sPropertiesPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream fis = new FileInputStream(file);
            mProperties.load(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 修改path的值，并保存
     */
    private void savePathKey() {
        if (mProperties.isEmpty()) {
            initProperties();
        }
        //修改值
        mProperties.setProperty(KEY_INPUT_PATH, mInputPath.getText());
        mProperties.setProperty(KEY_OUTPUT_PATH, mOutputPath.getText());
        //保存文件
        try {
            FileOutputStream fos = new FileOutputStream(sPropertiesPath);
            mProperties.store(fos, "the primary key of article table");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean mkDir(File dir) {
        if (!dir.exists()) {
            //创建目录
            if (dir.mkdir()) {
                System.out.println("创建目录" + dir.getName() + "成功！");
            } else {
                System.out.println("创建目录" + dir.getName() + "失败！");
                return true;
            }
        }
        return false;
    }

    /**
     * 添加文件
     */
    private File[] selectFiles() {
        File[] files = null;
        JFileChooser fc = new JFileChooser(mInputPath.getText()); //这里可以设置打开默认路径
        try {
            fc.setMultiSelectionEnabled(true);
            fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            //设置 JFileChooser，以允许用户只选择文件、只选择目录，或者可选择文件和目录。
            fc.showOpenDialog(null);
            if (fc.getSelectedFile().isDirectory()) {
                mInputPath.setText(fc.getSelectedFile().getPath());
                files = fc.getSelectedFile().listFiles();
            } else {
                files = fc.getSelectedFiles();
            }
        } catch (HeadlessException he) {
            System.out.println("Save File Dialog ERROR!");
        }
        return files;
    }

    /**
     * 选择路径
     */
    private File selectFilePath(String path) {
        File file = null;
        JFileChooser fc = new JFileChooser(path); //这里可以设置打开默认路径
        try {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //设置 JFileChooser，以允许用户只选择文件、只选择目录，或者可选择文件和目录。
            fc.showOpenDialog(null);
            //打开目录对话框
            file = fc.getSelectedFile();
            if (file == null) {
                file = fc.getCurrentDirectory();
            }
        } catch (HeadlessException he) {
            System.out.println("Save File Dialog ERROR!");
        }
        return file;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     */
    private void copyFile(String oldPath, String newPath) {
        try {
            int byteSum = 0;
            int byteRead = 0;
            File oldFile = new File(oldPath);
            if (oldFile.exists()) { //文件存在时
                InputStream inStream = new FileInputStream(oldPath); //读入原文件
                FileOutputStream fs = new FileOutputStream(newPath);
                byte[] buffer = new byte[1444];
                while ((byteRead = inStream.read(buffer)) != -1) {
                    byteSum += byteRead; //字节数 文件大小
                    System.out.println(byteSum);
                    fs.write(buffer, 0, byteRead);
                }
                inStream.close();
            }
        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "复制单个文件操作出错：" + newPath, "导出失败", JOptionPane.ERROR_MESSAGE);
        }
    }
}
