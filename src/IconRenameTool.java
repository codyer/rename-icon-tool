import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;

/**
 * Created by Cody.yi on 2017/9/14.
 * IconRenameTool
 */
public class IconRenameTool {

    private JPanel panelMain;
    private JTextField filePath;
    private JButton inputButton;
    private JButton outputButton;
    private JTable iconList;
    private JLabel review;
    private IconNameListModel mList;
    private File file;
    private String pathXhdpi = "\\drawable-xhdpi\\";
    private String pathXXhdpi = "\\drawable-xxhdpi\\";

    public static void main(String[] args) {
        JFrame frame = new JFrame("Icon Rename Tool");
        frame.setContentPane(new IconRenameTool().panelMain);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public IconRenameTool() {
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
        review.setIcon(icon);
    }

    private void init() {
        filePath.setText("E:");
        inputButton.addActionListener(e -> {
            filePath.setText(fileOpen());
            while (null == filePath.getText() || filePath.getText().isEmpty()) {
                filePath.setText(fileOpen());
            }
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
        });
        outputButton.addActionListener(e -> {
            if (mList == null){
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
                    e1.printStackTrace();
                }
            } else {
                JOptionPane.showMessageDialog(null, "当前路径下没有需要重命名的图片文件。", "导出失败", JOptionPane.ERROR_MESSAGE);
            }
        });
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
