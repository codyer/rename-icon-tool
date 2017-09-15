import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.List;

import static javax.swing.SwingConstants.CENTER;

/**
 * Created by Cody.yi on 2017/9/14.
 * IconRenameTool
 */
public class IconRenameTool {

    private JPanel panelMain;
    private JTextField filePath;
    private JButton openButton;
    private JButton outputButton;
    private JTable iconList;
    private JLabel review;
    private JButton loadButton;
    private IconNameListModel mList;
    private File file;
    private String pathXhdpi = "\\drawable-xhdpi\\";
    private String pathXXhdpi = "\\drawable-xxhdpi\\";
    private static String propertiesPath;
    private static Properties properties = new Properties();
    /*properties文件名*/
    private static final String PROPERTIES_FILE_NAME = "\\config.properties";
    /*键*/
    private static final String KEY_PATH = "path";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Icon Rename Tool");
        frame.setContentPane(new IconRenameTool().panelMain);
        try {
            UIManager.LookAndFeelInfo[] lookAndFeels = UIManager.getInstalledLookAndFeels();
            UIManager.setLookAndFeel(lookAndFeels[1].getClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            JOptionPane.showMessageDialog(null, e, "设置UI失败", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public IconRenameTool() {
        //截掉路径的”file:/“前缀
        propertiesPath = System.getProperty("user.dir") + PROPERTIES_FILE_NAME;
//        propertiesPath = this.getClass().getResource(PROPERTIES_FILE_NAME).toString().substring(6);
        init();
        iconList.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                super.keyReleased(e);
                showIcon();
            }
        });
        iconList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showIcon();
            }
        });
    }

    private void showIcon() {
        if ((iconList.getSelectedRow()) == -1) {
            return;
        }
        String path = file.getPath() + "\\" + mList.getNames().get(iconList.getSelectedRow()) + "@2x.png";
        ImageIcon icon = new ImageIcon(path);
        review.setHorizontalAlignment(CENTER);
        review.setIcon(icon);
    }

    private void init() {
        if(properties.isEmpty()) {
            initProperties();
        }
        filePath.setText(properties.getProperty(KEY_PATH));
        openButton.addActionListener(e -> {
            filePath.setText(fileOpen());
            loadNames();
        });
        loadButton.addActionListener(e -> {
            loadNames();
        });
        outputButton.addActionListener(e -> {
            if (mList == null) {
                JOptionPane.showMessageDialog(null, "当前路径下没有需要重命名的图片文件。", "导出失败", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File dirXX = new File(pathXXhdpi);
            File dirX = new File(pathXhdpi);
            if (mkDir(dirX)) return;
            if (mkDir(dirXX)) return;
            if (mList.getRowCount() > 0) {
                for (int i = 0; i < mList.getRowCount(); i++) {
                    copyFile(file.getPath() + "\\" + mList.getNames().get(i) + "@2x.png", dirX + "\\" + mList.getNewNames().get(i) + ".png");
                    copyFile(file.getPath() + "\\" + mList.getNames().get(i) + "@3x.png", dirXX + "\\" + mList.getNewNames().get(i) + ".png");
                }
                JOptionPane.showMessageDialog(null, "文件生成成功：" + filePath.getText(), "导出成功", JOptionPane.INFORMATION_MESSAGE);
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException e1) {
                    JOptionPane.showMessageDialog(null, e1, "导出失败", JOptionPane.ERROR_MESSAGE);
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "当前路径下没有需要重命名的图片文件。", "导出失败", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private void loadNames() {
        while (null == filePath.getText() || filePath.getText().isEmpty()) {
            filePath.setText(fileOpen());
        }
        savePathKey(filePath.getText());

        List<String> name = new ArrayList<>();
        file = new File(filePath.getText());
        pathXhdpi = file.getPath() + "\\drawable-xhdpi\\";
        pathXXhdpi = file.getPath() + "\\drawable-xxhdpi\\";
        String[] fileNames = file.list();
        for (String fileName : fileNames) {
            if (fileName.endsWith("@2x.png")) {
                name.add(fileName.replace("@2x.png", ""));
            }
        }
        if (mList == null) {
            mList = new IconNameListModel(name);
        } else {
            mList.setNames(name);
        }
        iconList.setModel(mList);
        iconList.updateUI();
    }

    /**
     * 初始化properties，即载入数据
     */
    private static void initProperties() {
        try {
            File file = new File(propertiesPath);
            if (!file.exists()) {
                file.createNewFile();
            }
            InputStream fis = new FileInputStream(file);
            properties.load(fis);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**修改path的值，并保存
     * @param path
     */
    public static void savePathKey(String path){
        if(properties.isEmpty()) {
            initProperties();
        }
        //修改值
        properties.setProperty(KEY_PATH, path);
        //保存文件
        try {
            FileOutputStream fos = new FileOutputStream(propertiesPath);
            properties.store(fos, "the primary key of article table");
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean mkDir(File dir) {
        if (!dir.exists()) {
            //创建目录
            if (dir.mkdirs()) {
                System.out.println("创建目录" + dir.getName() + "成功！");
            } else {
                System.out.println("创建目录" + dir.getName() + "失败！");
                return true;
            }
        }
        return false;
    }

    /**
     * 打开路径
     */
    private String fileOpen() {
        File f;
        JFileChooser fc = new JFileChooser(filePath.getText()); //这里可以设置打开默认路径
        String fileName = "";
        String flags;
        try {
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            //设置 JFileChooser，以允许用户只选择文件、只选择目录，或者可选择文件和目录。
            fc.showOpenDialog(null);
            //打开目录对话框
            flags = fc.getSelectedFile().getName();
            f = fc.getCurrentDirectory();
            //获得文件名
            fileName = f.getAbsolutePath() + "\\" + flags;
            System.out.println(fileName);
        } catch (HeadlessException he) {
            System.out.println("Save File Dialog ERROR!");
        }
        return fileName;
    }

    /**
     * 复制单个文件
     *
     * @param oldPath String 原文件路径 如：c:/fqf.txt
     * @param newPath String 复制后路径 如：f:/fqf.txt
     * @return boolean
     */
    public void copyFile(String oldPath, String newPath) {
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
