import gnu.io.CommPortIdentifier;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

/**
 * Created by Callum on 21/12/2015.
 */
public class SerialViewer implements ActionListener
{
    private JTabbedPane tabbedPane1;
    private JPanel content;
    private JTextArea textArea1;
    private JScrollPane logScrollPane;
    private JButton sendButton;
    private JTextField textField1;
    private JButton sendByteButton;
    private JSpinner spinner1;
    private JButton sendFileButton;
    private JTextField textField2;
    private JButton button1;

    private ButtonGroup baudGroup;
    private ButtonGroup lineEndingGroup;

    public static String newLine = "\n";

    public static SerialViewer instance;

    public static SerialMain COM3;

    public SerialViewer()
    {
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch(Exception e) {
            e.printStackTrace();
        }

        JFrame frame = new JFrame("Serial Viewer");

        JMenuBar menuBar = new JMenuBar();

        JMenu communication = new JMenu("Comms");
        communication.setMnemonic(KeyEvent.VK_C);
        
        JMenu baud = new JMenu("Baud rate");
        baudGroup = new ButtonGroup();
        
        JRadioButtonMenuItem _75 = new JRadioButtonMenuItem("75");
        baud.add(_75);
        _75.addActionListener(this);
        baudGroup.add(_75);

        JRadioButtonMenuItem _110 = new JRadioButtonMenuItem("110");
        baud.add(_110);
        _110.addActionListener(this);
        baudGroup.add(_110);

        JRadioButtonMenuItem _300 = new JRadioButtonMenuItem("300");
        baud.add(_300);
        _300.addActionListener(this);
        baudGroup.add(_300);

        JRadioButtonMenuItem _1200 = new JRadioButtonMenuItem("1200");
        baud.add(_1200);
        _1200.addActionListener(this);
        baudGroup.add(_1200);

        JRadioButtonMenuItem _2400 = new JRadioButtonMenuItem("2400");
        baud.add(_2400);
        _2400.addActionListener(this);
        baudGroup.add(_2400);

        JRadioButtonMenuItem _4800 = new JRadioButtonMenuItem("4800");
        baud.add(_4800);
        _4800.addActionListener(this);
        baudGroup.add(_4800);

        JRadioButtonMenuItem _9600 = new JRadioButtonMenuItem("9600");
        _9600.setSelected(true);
        baud.add(_9600);
        _9600.addActionListener(this);
        baudGroup.add(_9600);

        JRadioButtonMenuItem _19200 = new JRadioButtonMenuItem("19200");
        baud.add(_19200);
        _19200.addActionListener(this);
        baudGroup.add(_19200);

        JRadioButtonMenuItem _38400 = new JRadioButtonMenuItem("38400");
        baud.add(_38400);
        _38400.addActionListener(this);
        baudGroup.add(_38400);

        JRadioButtonMenuItem _57600 = new JRadioButtonMenuItem("57600");
        baud.add(_57600);
        _57600.addActionListener(this);
        baudGroup.add(_57600);

        JRadioButtonMenuItem _115200 = new JRadioButtonMenuItem("115200");
        baud.add(_115200);
        _115200.addActionListener(this);
        baudGroup.add(_115200);

        communication.add(baud);

        JMenu lineEnding = new JMenu("Line ending");
        lineEndingGroup = new ButtonGroup();

        JRadioButtonMenuItem _unix = new JRadioButtonMenuItem("\\n");
        _unix.setSelected(true);
        lineEnding.add(_unix);
        _unix.addActionListener(a -> onLineEndingChange());
        lineEndingGroup.add(_unix);

        JRadioButtonMenuItem _windows = new JRadioButtonMenuItem("\\r\\n");
        lineEnding.add(_windows);
        _windows.addActionListener(a -> onLineEndingChange());
        lineEndingGroup.add(_windows);

        JRadioButtonMenuItem _none = new JRadioButtonMenuItem("None");
        lineEnding.add(_none);
        _none.addActionListener(a -> onLineEndingChange());
        lineEndingGroup.add(_none);

        communication.add(lineEnding);

        menuBar.add(communication);

        frame.setJMenuBar(menuBar);

        frame.setContentPane(content);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        DefaultCaret logCaret = (DefaultCaret) textArea1.getCaret();
        logCaret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        sendButton.addActionListener(a -> onSend());
        sendByteButton.addActionListener(a -> onSendByte());
        sendFileButton.addActionListener(a -> onSendFile());
        button1.addActionListener(a -> onSelectFile());
    }

    private void onSelectFile()
    {
        JFileChooser jfc = new JFileChooser(".");
        jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int status = jfc.showDialog(content.getParent(), "Select File");

        if(status == JFileChooser.APPROVE_OPTION)
        {
            textField2.setText(jfc.getSelectedFile().getAbsolutePath());
        }
    }

    private void onSendFile()
    {
        Path filePath = Paths.get(textField2.getText());
        byte[] fileBytes = new byte[0];

        write("Sending file (" + filePath.getFileName() + ")");

        try
        {
            fileBytes = Files.readAllBytes(filePath);
        } catch(IOException e) {
            e.printStackTrace();
        }

        for(byte b : fileBytes)
        {
            COM3.write(b);
        }

        write("File transfer done");
    }

    private void onSendByte()
    {
        byte c = (byte) ((int) spinner1.getValue());

        COM3.write(c);
    }

    void onLineEndingChange()
    {
        String ending = "";

        for (Enumeration<AbstractButton> buttons = lineEndingGroup.getElements(); buttons.hasMoreElements();)
        {
            AbstractButton button = buttons.nextElement();

            if(button.isSelected())
            {
                ending = button.getText();
                break;
            }
        }

        switch(ending)
        {
            case "None":
                newLine = "";
                break;
            case "\\n":
                newLine = "\n";
                break;
            case "\\r\\n":
                newLine = "\r\n";
                break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        if(COM3 != null)
        {
            COM3.close();
            COM3 = null;
        }

        COM3 = new SerialMain("COM3");

        String baud = "";

        for (Enumeration<AbstractButton> buttons = baudGroup.getElements(); buttons.hasMoreElements();)
        {
            AbstractButton button = buttons.nextElement();

            if(button.isSelected())
            {
                baud = button.getText();
                break;
            }
        }

        COM3.init(Integer.parseInt(baud));
    }

    private void onSend()
    {
        String text = textField1.getText();
        COM3.write(text + newLine);
    }

    public void write(String s)
    {
        textArea1.setText(textArea1.getText() + s);
    }

    public static void main(String[] args) throws Exception
    {
        instance = new SerialViewer();

        System.out.println("Connecting to :" + args[0]);

        COM3 = new SerialMain(args[0]);
        COM3.init(9600);

        System.out.println("Started");

        while(true)
        {

        }
    }
}
