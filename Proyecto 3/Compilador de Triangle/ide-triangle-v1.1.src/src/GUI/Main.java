/*
 * IDE-Triangle v1.0
 * Main.java
 */

package GUI;
import Core.Console.InputRedirector;
import Core.Console.OutputRedirector;
import Core.IDE.IDEDisassembler;
import Core.IDE.IDEInterpreter;
import Core.Visitors.TableVisitor;
// import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileWriter;
import javax.swing.ImageIcon;
import java.awt.Image;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.awt.Desktop;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import Triangle.IDECompiler;
import Core.ExampleFileFilter;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import Core.Visitors.TreeVisitor;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 * The Main class. Contains the main form.
 *
 * @author Luis Leopoldo P�rez <luiperpe@ns.isi.ulatina.ac.cr>
 */
public class Main extends javax.swing.JFrame {

    // <editor-fold defaultstate="collapsed" desc=" Methods ">
    
    /**
     * Creates new form Main.
     */
    public Main() {        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());            
        } catch (Exception e) { }
        
        initComponents();
        setSize(640, 480);
        setVisible(true);
        directory = new File(".");
    }
    
    /**
     * Checks if the file has been changed - used to enable/disable 
     * the "Save" button.
     */
    private void checkSaveChanges() {
        if (((FileFrame)desktopPane.getSelectedFrame()).hasChanged()) {
            buttonSave.setEnabled(true);
            saveMenuItem.setEnabled(true);
        }
        else {
            buttonSave.setEnabled(false);
            saveMenuItem.setEnabled(false);
        }
    }
    
    /**
     * Checks if there are any open documents. Disables some buttons and
     * menu options if no document is open. 
     */
    private void checkPaneChanges() {
        if (desktopPane.getComponentCount() == 0) {
            saveAsMenuItem.setEnabled(false);
            saveMenuItem.setEnabled(false);
            buttonSave.setEnabled(false);            
            buttonCut.setEnabled(false);
            buttonCopy.setEnabled(false);
            buttonPaste.setEnabled(false);            
            buttonCompile.setEnabled(false);
            buttonRun.setEnabled(false);
            cutMenuItem.setEnabled(false);
            copyMenuItem.setEnabled(false);
            pasteMenuItem.setEnabled(false);            
            compileMenuItem.setEnabled(false);
            compileToLLVMMenuItem.setEnabled(false);
            runMenuItem.setEnabled(false);           
        } else
            checkSaveChanges();
    }
    
    /**
     * Creates a new document frame and inserts it into the desktop pane,
     * enabling its buttons and menu options.
     *
     * @param title Title of the frame.
     * @param contents File contents.
     * @return The new file frame.
     */
    private FileFrame addInternalFrame(String title, String contents) {
        FileFrame x = new FileFrame(delegateSaveButton, delegateMouse, delegateInternalFrame, delegateEnter);
        
        x.setTitle(title);
        x.setSize(540, 250);
        x.setSourcePaneText(contents);
        x.setPreviousSize(contents.length());
        x.setPreviousText(contents);
        desktopPane.add(x);        
        x.setVisible(true);
        
        saveAsMenuItem.setEnabled(true);        
        buttonCut.setEnabled(true);
        buttonCopy.setEnabled(true);
        buttonPaste.setEnabled(true);            
        cutMenuItem.setEnabled(true);
        copyMenuItem.setEnabled(true);
        pasteMenuItem.setEnabled(true);
        compileMenuItem.setEnabled(true);
    compileToLLVMMenuItem.setEnabled(true);
        compileToNativeMenuItem.setEnabled(true);
        buttonCompile.setEnabled(true);
       
        checkSaveChanges();        
        return(x);
    }
    
    /**
     * Opens a file chooser dialog, used in the "Open" and "Save" options.
     * @return The JFileChooser dialog object.
     */
    private JFileChooser drawFileChooser() {
        JFileChooser chooser = new JFileChooser();
        ExampleFileFilter filter = new ExampleFileFilter();        
        filter.setDescription("Triangle files");
        filter.addExtension("tri");
        chooser.setFileFilter(filter);
        chooser.setCurrentDirectory(directory);
        
        return(chooser);
    }

    /**
     * Main method, instantiates the Main class.
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main();
            }
        });
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        toolBarsPanel = new javax.swing.JPanel();
        fileToolBar = new javax.swing.JToolBar();
        buttonNew = new javax.swing.JButton();
        buttonOpen = new javax.swing.JButton();
        buttonSave = new javax.swing.JButton();
        editToolBar = new javax.swing.JToolBar();
        buttonCut = new javax.swing.JButton();
        buttonCopy = new javax.swing.JButton();
        buttonPaste = new javax.swing.JButton();
        triangleToolBar = new javax.swing.JToolBar();
        buttonCompile = new javax.swing.JButton();
        buttonRun = new javax.swing.JButton();
        desktopPane = new javax.swing.JDesktopPane();
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        newMenuItem = new javax.swing.JMenuItem();
        openMenuItem = new javax.swing.JMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        saveAsMenuItem = new javax.swing.JMenuItem();
        separatorExit = new javax.swing.JSeparator();
        exitMenuItem = new javax.swing.JMenuItem();
        editMenu = new javax.swing.JMenu();
        cutMenuItem = new javax.swing.JMenuItem();
        copyMenuItem = new javax.swing.JMenuItem();
        pasteMenuItem = new javax.swing.JMenuItem();
        triangleMenu = new javax.swing.JMenu();
        compileMenuItem = new javax.swing.JMenuItem();
        runMenuItem = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("IDE-Triangle 1.1");
        setFont(new java.awt.Font("Tahoma", 0, 11));
        setIconImage(new ImageIcon(this.getClass().getResource("Icons/iconMain.gif")).getImage());
        setLocationByPlatform(true);
        setName("mainFrame");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        toolBarsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        toolBarsPanel.setFocusable(false);
        fileToolBar.setFocusable(false);
        fileToolBar.setName("File");
        fileToolBar.setRequestFocusEnabled(false);
        buttonNew.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileNew.gif")));
        buttonNew.setToolTipText("New...");
        buttonNew.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonNew.setBorderPainted(false);
        buttonNew.setFocusPainted(false);
        buttonNew.setFocusable(false);
        buttonNew.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });

        fileToolBar.add(buttonNew);

        buttonOpen.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileOpen.gif")));
        buttonOpen.setToolTipText("Open...");
        buttonOpen.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonOpen.setBorderPainted(false);
        buttonOpen.setFocusPainted(false);
        buttonOpen.setFocusable(false);
        buttonOpen.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });

        fileToolBar.add(buttonOpen);

        buttonSave.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileSave.gif")));
        buttonSave.setToolTipText("Save...");
        buttonSave.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonSave.setBorderPainted(false);
        buttonSave.setEnabled(false);
        buttonSave.setFocusPainted(false);
        buttonSave.setFocusable(false);
        buttonSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });

        fileToolBar.add(buttonSave);

        toolBarsPanel.add(fileToolBar);

        editToolBar.setFocusable(false);
        editToolBar.setName("Edit");
        editToolBar.setRequestFocusEnabled(false);
        buttonCut.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditCut.gif")));
        buttonCut.setToolTipText("Cut...");
        buttonCut.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonCut.setBorderPainted(false);
        buttonCut.setEnabled(false);
        buttonCut.setFocusPainted(false);
        buttonCut.setFocusable(false);
        buttonCut.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });

        editToolBar.add(buttonCut);

        buttonCopy.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditCopy.gif")));
        buttonCopy.setToolTipText("Copy...");
        buttonCopy.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonCopy.setBorderPainted(false);
        buttonCopy.setEnabled(false);
        buttonCopy.setFocusPainted(false);
        buttonCopy.setFocusable(false);
        buttonCopy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });

        editToolBar.add(buttonCopy);

        buttonPaste.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditPaste.gif")));
        buttonPaste.setToolTipText("Paste...");
        buttonPaste.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonPaste.setBorderPainted(false);
        buttonPaste.setEnabled(false);
        buttonPaste.setFocusPainted(false);
        buttonPaste.setFocusable(false);
        buttonPaste.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });

        editToolBar.add(buttonPaste);

        toolBarsPanel.add(editToolBar);

        triangleToolBar.setFocusable(false);
        triangleToolBar.setName("Triangle");
        triangleToolBar.setRequestFocusEnabled(false);
        buttonCompile.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconTriangleCompile.gif")));
        buttonCompile.setToolTipText("Compile...");
        buttonCompile.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonCompile.setBorderPainted(false);
        buttonCompile.setEnabled(false);
        buttonCompile.setFocusPainted(false);
        buttonCompile.setFocusable(false);
        buttonCompile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileMenuItemActionPerformed(evt);
            }
        });

        triangleToolBar.add(buttonCompile);

        buttonRun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconTriangleRun.gif")));
        buttonRun.setToolTipText("Run...");
        buttonRun.setBorder(javax.swing.BorderFactory.createEmptyBorder(3, 3, 3, 3));
        buttonRun.setBorderPainted(false);
        buttonRun.setEnabled(false);
        buttonRun.setFocusPainted(false);
        buttonRun.setFocusable(false);
        buttonRun.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMenuItemActionPerformed(evt);
            }
        });

        triangleToolBar.add(buttonRun);

        toolBarsPanel.add(triangleToolBar);

        getContentPane().add(toolBarsPanel, java.awt.BorderLayout.NORTH);

        desktopPane.setBackground(new java.awt.Color(0, 103, 201));
        desktopPane.setAutoscrolls(true);
        getContentPane().add(desktopPane, java.awt.BorderLayout.CENTER);

        menuBar.setFont(new java.awt.Font("Verdana", 0, 11));
        fileMenu.setMnemonic('F');
        fileMenu.setText("File");
        fileMenu.setBorderPainted(true);
        newMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.CTRL_MASK));
        newMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileNew.gif")));
        newMenuItem.setMnemonic('N');
        newMenuItem.setText("New");
        newMenuItem.setRequestFocusEnabled(false);
        newMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(newMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.CTRL_MASK));
        openMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileOpen.gif")));
        openMenuItem.setMnemonic('O');
        openMenuItem.setText("Open");
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(openMenuItem);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.CTRL_MASK));
        saveMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconFileSave.gif")));
        saveMenuItem.setMnemonic('S');
        saveMenuItem.setText("Save");
        saveMenuItem.setEnabled(false);
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setMnemonic('A');
        saveAsMenuItem.setText("Save As...");
        saveAsMenuItem.setEnabled(false);
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(saveAsMenuItem);

        fileMenu.add(separatorExit);

        exitMenuItem.setMnemonic('x');
        exitMenuItem.setText("Exit");
        exitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitMenuItemActionPerformed(evt);
            }
        });

        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setMnemonic('E');
        editMenu.setText("Edit");
        editMenu.setBorderPainted(true);
        cutMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_X, java.awt.event.InputEvent.CTRL_MASK));
        cutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditCut.gif")));
        cutMenuItem.setMnemonic('t');
        cutMenuItem.setText("Cut");
        cutMenuItem.setEnabled(false);
        cutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cutMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(cutMenuItem);

        copyMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_C, java.awt.event.InputEvent.CTRL_MASK));
        copyMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditCopy.gif")));
        copyMenuItem.setMnemonic('C');
        copyMenuItem.setText("Copy");
        copyMenuItem.setEnabled(false);
        copyMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(copyMenuItem);

        pasteMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_V, java.awt.event.InputEvent.CTRL_MASK));
        pasteMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconEditPaste.gif")));
        pasteMenuItem.setMnemonic('P');
        pasteMenuItem.setText("Paste");
        pasteMenuItem.setToolTipText("");
        pasteMenuItem.setEnabled(false);
        pasteMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteMenuItemActionPerformed(evt);
            }
        });

        editMenu.add(pasteMenuItem);

        menuBar.add(editMenu);

        triangleMenu.setMnemonic('T');
        triangleMenu.setText("Triangle");
        triangleMenu.setBorderPainted(true);
        compileMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F5, 0));
        compileMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconTriangleCompile.gif")));
        compileMenuItem.setMnemonic('C');
        compileMenuItem.setText("Compile");
        compileMenuItem.setEnabled(false);
        compileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileMenuItemActionPerformed(evt);
            }
        });

        triangleMenu.add(compileMenuItem);

        // New menu item: compile to LLVM
        compileToLLVMMenuItem = new javax.swing.JMenuItem();
        compileToLLVMMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconTriangleCompile.gif")));
        compileToLLVMMenuItem.setText("To LLVM");
        compileToLLVMMenuItem.setEnabled(false);
        compileToLLVMMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileToLLVMMenuItemActionPerformed(evt);
            }
        });
        triangleMenu.add(compileToLLVMMenuItem);

        // Build runtime menu item
        buildRuntimeMenuItem = new javax.swing.JMenuItem();
        buildRuntimeMenuItem.setText("Build LLVM Runtime");
        buildRuntimeMenuItem.setEnabled(true);
        buildRuntimeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buildRuntimeMenuItemActionPerformed(evt);
            }
        });
        triangleMenu.add(buildRuntimeMenuItem);

        // Compile to native (uses runtime scripts)
        compileToNativeMenuItem = new javax.swing.JMenuItem();
        compileToNativeMenuItem.setText("Compile LLVM -> Native");
        compileToNativeMenuItem.setEnabled(false);
        compileToNativeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                compileToNativeMenuItemActionPerformed(evt);
            }
        });
        triangleMenu.add(compileToNativeMenuItem);

        // Run native executable
        runNativeMenuItem = new javax.swing.JMenuItem();
        runNativeMenuItem.setText("Run Native");
        runNativeMenuItem.setEnabled(false);
        runNativeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runNativeMenuItemActionPerformed(evt);
            }
        });
        triangleMenu.add(runNativeMenuItem);

        // Run native in external shell (opens a new OS shell window)
        runNativeShellMenuItem = new javax.swing.JMenuItem();
        runNativeShellMenuItem.setText("Run Native in Shell");
        runNativeShellMenuItem.setEnabled(false);
        runNativeShellMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runNativeShellMenuItemActionPerformed(evt);
            }
        });
        triangleMenu.add(runNativeShellMenuItem);

        runMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_F6, 0));
        runMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconTriangleRun.gif")));
        runMenuItem.setMnemonic('R');
        runMenuItem.setText("Run");
        runMenuItem.setEnabled(false);
        runMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runMenuItemActionPerformed(evt);
            }
        });

        triangleMenu.add(runMenuItem);

        menuBar.add(triangleMenu);

        helpMenu.setMnemonic('H');
        helpMenu.setText("Help");
        helpMenu.setBorderPainted(true);
        aboutMenuItem.setIcon(new javax.swing.ImageIcon(getClass().getResource("/GUI/Icons/iconHelpAbout.gif")));
        aboutMenuItem.setMnemonic('A');
        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        setJMenuBar(menuBar);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // <editor-fold defaultstate="collapsed" desc=" Event Handlers Implementation ">
    
    /**
     * Handles the "Run TAM Program" button and menu option.
     */
    private void runMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runMenuItemActionPerformed
        ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
        ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
        output.setDelegate(delegateConsole);
        runMenuItem.setEnabled(false);
        buttonRun.setEnabled(false);
        compileMenuItem.setEnabled(false);
        buttonCompile.setEnabled(false);
        interpreter.Run(desktopPane.getSelectedFrame().getTitle().replace(".tri", ".tam"));
    }//GEN-LAST:event_runMenuItemActionPerformed

    /** 
     * Handles the "Close" program option
     */
    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        while (desktopPane.getComponentCount() > 0) {
            try { 
                desktopPane.getSelectedFrame().setClosed(true);
            } catch (Exception e) { }
        }
    }//GEN-LAST:event_formWindowClosing

    /** 
     * Handles the "Paste Text" button and menu option.
     */
    private void pasteMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteMenuItemActionPerformed
        ((FileFrame)desktopPane.getSelectedFrame()).pasteText(Clip.getClipboardContents());
    }//GEN-LAST:event_pasteMenuItemActionPerformed

    /**
     * Handles the "Cut Text" button and menu option.
     */
    private void cutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cutMenuItemActionPerformed
        Clip.setClipboardContents(((FileFrame)desktopPane.getSelectedFrame()).getSelectedText());
        ((FileFrame)desktopPane.getSelectedFrame()).cutText();
    }//GEN-LAST:event_cutMenuItemActionPerformed

    /**
     * Handles the "Copy Text" button and menu option.
     */
    private void copyMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyMenuItemActionPerformed
        Clip.setClipboardContents(((FileFrame)desktopPane.getSelectedFrame()).getSelectedText());
    }//GEN-LAST:event_copyMenuItemActionPerformed

    /** 
     * Handles the "Save As" button and menu option.
     */
    private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
        boolean _previouslySaved = ((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved();        
        ((FileFrame)desktopPane.getSelectedFrame()).setPreviouslySaved(false);
        saveMenuItemActionPerformed(evt);
        ((FileFrame)desktopPane.getSelectedFrame()).setPreviouslySaved(_previouslySaved);        
    }//GEN-LAST:event_saveAsMenuItemActionPerformed

    /**
     * Handles the "About" menu option.
     */
    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        new AboutDialog(this, true).setVisible(true);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    /**
     * Handles the "Open File" button and menu option.
     */
    private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
        JFileChooser chooser = drawFileChooser();
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try {
                directory = chooser.getCurrentDirectory();
                BufferedReader br = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                String sr = "";
                while (br.ready())
                    sr += (char)br.read();
                br.close();
                addInternalFrame(chooser.getSelectedFile().getPath(), sr.replace("\r\n", "\n")).setPreviouslySaved(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred while trying to open the specified file", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_openMenuItemActionPerformed

    /**
     * Handles the "Compile" button and menu option.
     */
    private void compileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileMenuItemActionPerformed
        if ((!((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) || ((FileFrame)desktopPane.getSelectedFrame()).hasChanged()) {
            saveMenuItemActionPerformed(null);
        }
        
        if (((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) {
            ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
            ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
            ((FileFrame)desktopPane.getSelectedFrame()).clearTAMCode();
            ((FileFrame)desktopPane.getSelectedFrame()).clearTree();
            ((FileFrame)desktopPane.getSelectedFrame()).clearTable();
            new File(desktopPane.getSelectedFrame().getTitle().replace(".tri", ".tam")).delete();
            
            output.setDelegate(delegateConsole);            
            if (compiler.compileProgram(desktopPane.getSelectedFrame().getTitle())) {           
                output.setDelegate(delegateTAMCode);
                disassembler.Disassemble(desktopPane.getSelectedFrame().getTitle().replace(".tri", ".tam"));
                ((FileFrame)desktopPane.getSelectedFrame()).setTree((DefaultMutableTreeNode)treeVisitor.visitProgram(compiler.getAST(), null));
                ((FileFrame)desktopPane.getSelectedFrame()).setTable(tableVisitor.getTable(compiler.getAST()));
                
                runMenuItem.setEnabled(true);
                buttonRun.setEnabled(true);
            } else {
                ((FileFrame)desktopPane.getSelectedFrame()).highlightError(compiler.getErrorPosition());
                runMenuItem.setEnabled(false);
                buttonRun.setEnabled(false);
            }
        }
    }//GEN-LAST:event_compileMenuItemActionPerformed


    private void compileToLLVMMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileToLLVMMenuItemActionPerformed
        if ((!((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) || ((FileFrame)desktopPane.getSelectedFrame()).hasChanged()) {
            saveMenuItemActionPerformed(null);
        }

        if (((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) {
            // Prepare console redirector so ErrorReporter output won't NPE
            ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
            ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
            output.setDelegate(delegateConsole);

            String source = desktopPane.getSelectedFrame().getTitle();
            String out = compiler.compileToLLVM(source);
            if (out != null) {
                try {
                    BufferedReader br = new BufferedReader(new FileReader(out));
                    StringBuilder s = new StringBuilder();
                    while (br.ready()) s.append((char)br.read());
                    br.close();
                    javax.swing.JTextArea ta = new javax.swing.JTextArea(s.toString());
                    ta.setEditable(false);
                    javax.swing.JScrollPane sp = new javax.swing.JScrollPane(ta);
                    sp.setPreferredSize(new java.awt.Dimension(700,400));
                    JOptionPane.showMessageDialog(this, sp, "LLVM IR: " + out, JOptionPane.PLAIN_MESSAGE);
                    // Also update tree and table views to reflect the checked AST
                    try {
                        ((FileFrame)desktopPane.getSelectedFrame()).setTree((DefaultMutableTreeNode)treeVisitor.visitProgram(compiler.getAST(), null));
                        ((FileFrame)desktopPane.getSelectedFrame()).setTable(tableVisitor.getTable(compiler.getAST()));
                    } catch (Exception e) {
                        // ignore: best-effort update
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "LLVM generated but error reading file: " + e.getMessage());
                }
            } else {
                ((FileFrame)desktopPane.getSelectedFrame()).highlightError(compiler.getErrorPosition());
            }
        }
    }//GEN-LAST:event_compileToLLVMMenuItemActionPerformed

    private void buildRuntimeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buildRuntimeMenuItemActionPerformed
        // Run in background thread
        new Thread(new Runnable() {
            public void run() {
                try {
                    ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
                    ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
                } catch (Exception e) { }
                String script = new java.io.File("ide-triangle-v1.1.src/runtime/build_runtime.ps1").getAbsolutePath();
                ProcessBuilder pb = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-File", script);
                pb.directory(new java.io.File("."));
                try {
                    final Process p = pb.start();
                    java.io.BufferedReader out = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                    java.io.BufferedReader err = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = out.readLine()) != null) {
                        final String l = line;
                        javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                    }
                    while ((line = err.readLine()) != null) {
                        final String l = line;
                        javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                    }
                    p.waitFor();
                } catch (Exception ex) {
                    final String msg = ex.getMessage();
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, "Error running build_runtime: " + msg); }});
                }
            }
        }).start();
    }//GEN-LAST:event_buildRuntimeMenuItemActionPerformed

    /**
     * Compile the current file to LLVM and then invoke the native compiler script to produce an executable.
     */
    private void compileToNativeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_compileToNativeMenuItemActionPerformed
        if ((!((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) || ((FileFrame)desktopPane.getSelectedFrame()).hasChanged()) {
            saveMenuItemActionPerformed(null);
        }

        if (((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved()) {
            ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
            ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
            output.setDelegate(delegateConsole);

            String source = desktopPane.getSelectedFrame().getTitle();
            String llfile = compiler.compileToLLVM(source);
            if (llfile == null) {
                ((FileFrame)desktopPane.getSelectedFrame()).highlightError(compiler.getErrorPosition());
                return;
            }

            // Use absolute ll path and prepare output exe name next to the .ll (use absolute path to avoid relative cwd issues)
            final String llAbsolute = new java.io.File(llfile).getAbsolutePath();
            String exeName = new java.io.File(llAbsolute.replaceAll("\\.ll$", "") + ".exe").getAbsolutePath();
            // Prefer script relative to current working directory (runtime/...),
            // but fall back to the original path if running from a different CWD.
            java.io.File scriptFile = new java.io.File("runtime/compile_program_native.ps1");
            if (!scriptFile.exists()) {
                scriptFile = new java.io.File("ide-triangle-v1.1.src/runtime/compile_program_native.ps1");
            }
            final String script = scriptFile.getAbsolutePath();
            if (!scriptFile.exists()) {
                final String msg = "Native compile script not found: " + script;
                javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, msg); }});
                return;
            }

            // Run script in background
            new Thread(new Runnable() { public void run() {
                try {
                    ProcessBuilder pb = new ProcessBuilder("powershell", "-ExecutionPolicy", "Bypass", "-File", script, "-ProgramLl", llAbsolute, "-OutExe", exeName);
                    pb.directory(new java.io.File("."));
                    final Process p = pb.start();
                    java.io.BufferedReader out = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                    java.io.BufferedReader err = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = out.readLine()) != null) {
                        final String l = line;
                        javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                    }
                    while ((line = err.readLine()) != null) {
                        final String l = line;
                        javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                    }
                    int rc = p.waitFor();
                    final int exitCode = rc;
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){
                        if (exitCode == 0) {
                            // Store absolute path to the generated exe so Run Native uses the exact file
                            lastNativeExe = exeName;
                            try {
                                // Show a small dialog with a clickable link to the generated EXE
                                final String exeUri = new java.io.File(exeName).toURI().toString();
                                final JEditorPane editor = new JEditorPane("text/html", "<html>Native executable generated:<br/><a href=\"" + exeUri + "\">" + exeName + "</a></html>");
                                editor.setEditable(false);
                                editor.addHyperlinkListener(new javax.swing.event.HyperlinkListener() {
                                    public void hyperlinkUpdate(HyperlinkEvent e) {
                                        if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                                            try {
                                                java.net.URI uri = e.getURL().toURI();
                                                java.awt.Desktop.getDesktop().open(new java.io.File(uri));
                                            } catch (Exception ex2) {
                                                JOptionPane.showMessageDialog(null, "Cannot open file: " + ex2.getMessage());
                                            }
                                        }
                                    }
                                });
                                JScrollPane sp = new JScrollPane(editor);
                                sp.setPreferredSize(new java.awt.Dimension(700,120));
                                JOptionPane.showMessageDialog(null, sp, "Native executable generated", JOptionPane.PLAIN_MESSAGE);
                            } catch (Exception _e) {
                                // Fallback to plain dialog
                                JOptionPane.showMessageDialog(null, "Native executable generated: " + exeName);
                            }
                            runNativeMenuItem.setEnabled(true);
                            // Also write the absolute path to the IDE Console for clarity
                            try {
                                ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole("Native executable: " + exeName + "\n");
                            } catch (Exception _e) { /* best-effort */ }
                        } else {
                            JOptionPane.showMessageDialog(null, "Native compilation failed (exit code " + exitCode + ")");
                        }
                    }});
                } catch (Exception ex) {
                    final String msg = ex.getMessage();
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, "Error running compile_program_native: " + msg); }});
                }
            }}).start();
        }
    }//GEN-LAST:event_compileToNativeMenuItemActionPerformed

    /**
     * Runs the native executable produced by the pipeline and streams its output to the IDE console.
     */
    private void runNativeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runNativeMenuItemActionPerformed
        try {
            ((FileFrame)desktopPane.getSelectedFrame()).selectConsole();
            ((FileFrame)desktopPane.getSelectedFrame()).clearConsole();
        } catch (Exception e) { }
        // Prefer the last generated native exe (absolute path) when available.
        String source = desktopPane.getSelectedFrame().getTitle();
        java.io.File fexe = null;
        if (lastNativeExe != null) {
            fexe = new java.io.File(lastNativeExe);
            if (!fexe.exists()) fexe = null;
        }
        // Fallback: try exe next to the source, then the runtime folder (old behavior)
        if (fexe == null) {
            String exe = source.replaceAll("\\.tri$", ".exe");
            fexe = new java.io.File(exe);
            if (!fexe.exists()) fexe = new java.io.File("ide-triangle-v1.1.src/runtime/" + new java.io.File(exe).getName());
        }

        final java.io.File exeFile = fexe;
        new Thread(new Runnable() { public void run() {
            try {
                ProcessBuilder pb = new ProcessBuilder(exeFile.getAbsolutePath());
                pb.directory(exeFile.getParentFile());
                final Process p = pb.start();
                java.io.BufferedReader out = new java.io.BufferedReader(new java.io.InputStreamReader(p.getInputStream()));
                java.io.BufferedReader err = new java.io.BufferedReader(new java.io.InputStreamReader(p.getErrorStream()));
                String line;
                while ((line = out.readLine()) != null) {
                    final String l = line;
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                }
                while ((line = err.readLine()) != null) {
                    final String l = line;
                    javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ try { ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(l + "\n"); } catch (Exception e) {} }});
                }
                p.waitFor();
            } catch (Exception ex) {
                final String msg = ex.getMessage();
                javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, "Error running native exe: " + msg); }});
            }
        }}).start();
    }//GEN-LAST:event_runNativeMenuItemActionPerformed

    /**
     * Runs the native executable in an external OS shell (PowerShell on Windows).
     */
    private void runNativeShellMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runNativeShellMenuItemActionPerformed
        try {
            // Prefer the last generated native exe absolute path
            java.io.File exeFile = null;
            if (lastNativeExe != null) {
                exeFile = new java.io.File(lastNativeExe);
                if (!exeFile.exists()) exeFile = null;
            }
            if (exeFile == null) {
                String source = desktopPane.getSelectedFrame().getTitle();
                String exe = source.replaceAll("\\.tri$", ".exe");
                exeFile = new java.io.File(exe);
                if (!exeFile.exists()) exeFile = new java.io.File("ide-triangle-v1.1.src/runtime/" + new java.io.File(exe).getName());
            }
            if (exeFile == null || !exeFile.exists()) {
                javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, "Native executable not found."); }});
                return;
            }

            // Build a command that opens a new PowerShell window and runs the exe, leaving it open (-NoExit)
            // Use cmd /c start to ensure a new console window is created on Windows
            String exePath = exeFile.getAbsolutePath();
            ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "powershell", "-NoExit", "-Command", "& '" + exePath + "'");
            pb.directory(exeFile.getParentFile());
            pb.start();
        } catch (Exception ex) {
            final String msg = ex.getMessage();
            javax.swing.SwingUtilities.invokeLater(new Runnable(){ public void run(){ JOptionPane.showMessageDialog(null, "Error launching external shell: " + msg); }});
        }
    }//GEN-LAST:event_runNativeShellMenuItemActionPerformed

    /**
     * Handles the "Save" button and menu option.
     */
    private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
        String fileName = ((FileFrame)desktopPane.getSelectedFrame()).getTitle();
        boolean overwrite = true;
        
        if (!(((FileFrame)desktopPane.getSelectedFrame()).getPreviouslySaved())) {
            JFileChooser chooser = drawFileChooser();
            
            if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                if (chooser.getSelectedFile().exists()) {
                    overwrite = (JOptionPane.showConfirmDialog(this, chooser.getSelectedFile().getName() + " already exists.\nWould you like to replace it?", "Overwrite?", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION);
                }
                
                directory = chooser.getCurrentDirectory();
                
                fileName = chooser.getSelectedFile().getPath();
                if (!(fileName.contains(".tri")))
                    fileName += ".tri";
            } else
                overwrite = false;
        }
 
        if (overwrite) {
            try {
                BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
                bw.write(((FileFrame)desktopPane.getSelectedFrame()).getSourcePaneText().replace("\n", "\r\n"));
                bw.close();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "An error occurred while trying to save the specified file", "Error", JOptionPane.ERROR_MESSAGE);
            }

            ((FileFrame)desktopPane.getSelectedFrame()).setPreviouslySaved(true);
            ((FileFrame)desktopPane.getSelectedFrame()).setTitle(fileName);            
            ((FileFrame)desktopPane.getSelectedFrame()).setPreviouslyModified(false);
            ((FileFrame)desktopPane.getSelectedFrame()).setPreviousSize(((FileFrame)desktopPane.getSelectedFrame()).getSourcePaneText().length());
            ((FileFrame)desktopPane.getSelectedFrame()).setPreviousText(((FileFrame)desktopPane.getSelectedFrame()).getSourcePaneText());
                
            saveMenuItem.setEnabled(false);
            buttonSave.setEnabled(false);
        }
    }//GEN-LAST:event_saveMenuItemActionPerformed

    /**
     * Handles the "New File" button and menu option.
     */
    private void newMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newMenuItemActionPerformed
        addInternalFrame("Untitled-" + String.valueOf(untitledCount), "");      
        untitledCount++;
    }//GEN-LAST:event_newMenuItemActionPerformed

    /**
     * Handles the "Exit" menu option.
     */
    private void exitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitMenuItemActionPerformed
        formWindowClosing(null);
        System.exit(0);
    }//GEN-LAST:event_exitMenuItemActionPerformed

    // </editor-fold>    
           
    // <editor-fold defaultstate="collapsed" desc=" Delegates and Listeners ">    
    /**
     * Runs every time a key is pressed in the text editor frame, thus
     * determining if the file contents have changed, activating the
     * "Save" button and menu option.
     */   
    KeyAdapter delegateSaveButton = new KeyAdapter() {
        public void keyReleased(java.awt.event.KeyEvent evt) {
             checkSaveChanges();
             ((FileFrame)desktopPane.getSelectedFrame()).UpdateRowColNumbers();
        }
    };
    
    MouseListener delegateMouse = new MouseListener() {
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            ((FileFrame)desktopPane.getSelectedFrame()).UpdateRowColNumbers();
        }
        
        public void mouseExited(java.awt.event.MouseEvent evt) {
        }

        public void mouseEntered(java.awt.event.MouseEvent evt) {
        }        
        
        public void mouseReleased(java.awt.event.MouseEvent evt) {
        }       

        public void mousePressed(java.awt.event.MouseEvent evt) {
        }        
    };
    
    /**
     * Several events for the MDI text editor frames. 
     */
    InternalFrameListener delegateInternalFrame = new InternalFrameListener() {        
        
        /**
         * Every time a frame is focused/activated, some buttons can be
         * enabled (e.g. "Save", "Cut", "Copy", "Paste").
         */
        public void internalFrameActivated(InternalFrameEvent evt) { 
            checkPaneChanges();
            ((FileFrame)desktopPane.getSelectedFrame()).UpdateRowColNumbers();
        }
        
        /**
         * Every time a frame is closed, some buttons can be
         * disabled (e.g. "Save", "Cut", "Copy", "Paste").
         */        
        public void internalFrameClosed(InternalFrameEvent evt) {
            checkPaneChanges();
        }
            
        /**
         * Before closing a frame, checks if the file has not been
         * saved yet.
         */
        public void internalFrameClosing(InternalFrameEvent evt) {
            if (((FileFrame)desktopPane.getSelectedFrame()).hasChanged()) { 
                if (JOptionPane.showConfirmDialog(null, "Do you want to save the changes to " + desktopPane.getSelectedFrame().getTitle() + "?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)    
                    saveMenuItemActionPerformed(null);    
            }
        }
        
        // Required by interface - not implemented
        public void internalFrameDeactivated(InternalFrameEvent evt) { }
        public void internalFrameDeiconified(InternalFrameEvent evt) { }
        public void internalFrameIconified(InternalFrameEvent evt) { }
        public void internalFrameOpened(InternalFrameEvent evt) { }
    };
    
    
    /**
     * Used to redirect the console output - writes in the "Console" panel.     
     */
    ActionListener delegateConsole = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            while (output.peekQueue())
                ((FileFrame)desktopPane.getSelectedFrame()).writeToConsole(output.readQueue());
        }
    };
    
    /**
     * Used to redirect the console output - writes in the "TAM Code" pane.
     */
    ActionListener delegateTAMCode = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            while (output.peekQueue())
                ((FileFrame)desktopPane.getSelectedFrame()).writeToTAMCode(output.readQueue());
        }        
    };
    
    /**
     * Used to redirect the console input - enables the "Console Input" text box.
     */
    ActionListener delegateInput = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ((FileFrame)desktopPane.getSelectedFrame()).setInputEnabled(true);
        }
    };
    
    /**
     * Used to redirect the console input - writes the user input in the console.
     */
    ActionListener delegateEnter = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            ((FileFrame)desktopPane.getSelectedFrame()).setInputEnabled(false);
            input.addInput(((FileFrame)desktopPane.getSelectedFrame()).getInputFieldText() + "\n");
            ((FileFrame)desktopPane.getSelectedFrame()).clearInputField();
        }
    };
    
    /**
     * Used to control running programs - only one TAM program can be run at once
     */
    ActionListener delegateRun = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            runMenuItem.setEnabled(true);
            buttonRun.setEnabled(true);
            compileMenuItem.setEnabled(true);
            buttonCompile.setEnabled(true);
        }
    };
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" GUI Variables ">
    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JMenuItem aboutMenuItem;
    javax.swing.JButton buttonCompile;
    javax.swing.JButton buttonCopy;
    javax.swing.JButton buttonCut;
    javax.swing.JButton buttonNew;
    javax.swing.JButton buttonOpen;
    javax.swing.JButton buttonPaste;
    javax.swing.JButton buttonRun;
    javax.swing.JButton buttonSave;
    javax.swing.JMenuItem compileMenuItem;
    javax.swing.JMenuItem compileToLLVMMenuItem;
    javax.swing.JMenuItem buildRuntimeMenuItem;
    javax.swing.JMenuItem compileToNativeMenuItem;
    javax.swing.JMenuItem runNativeMenuItem;
    javax.swing.JMenuItem runNativeShellMenuItem;
    javax.swing.JMenuItem copyMenuItem;
    javax.swing.JMenuItem cutMenuItem;
    javax.swing.JDesktopPane desktopPane;
    javax.swing.JMenu editMenu;
    javax.swing.JToolBar editToolBar;
    javax.swing.JMenuItem exitMenuItem;
    javax.swing.JMenu fileMenu;
    javax.swing.JToolBar fileToolBar;
    javax.swing.JMenu helpMenu;
    javax.swing.JMenuBar menuBar;
    javax.swing.JMenuItem newMenuItem;
    javax.swing.JMenuItem openMenuItem;
    javax.swing.JMenuItem pasteMenuItem;
    javax.swing.JMenuItem runMenuItem;
    javax.swing.JMenuItem saveAsMenuItem;
    javax.swing.JMenuItem saveMenuItem;
    javax.swing.JSeparator separatorExit;
    javax.swing.JPanel toolBarsPanel;
    javax.swing.JMenu triangleMenu;
    javax.swing.JToolBar triangleToolBar;
    // End of variables declaration//GEN-END:variables
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Non-GUI Variables ">
    // [ Non-GUI variables declaration ]
    int untitledCount = 1;                                                  // Counts "Untitled" document names (e.g. "Untitled-1")
    clipBoard Clip = new clipBoard();                                       // Clipboard Management
    IDECompiler compiler = new IDECompiler();                               // Compiler - Analyzes/generates TAM programs
    IDEDisassembler disassembler = new IDEDisassembler();                   // Disassembler - Generates TAM Code
    IDEInterpreter interpreter = new IDEInterpreter(delegateRun);           // Interpreter - Runs TAM programs
    OutputRedirector output = new OutputRedirector();                       // Redirects the console output
    InputRedirector input = new InputRedirector(delegateInput);             // Redirects console input
    TreeVisitor treeVisitor = new TreeVisitor();                            // Draws the Abstract Syntax Trees
    TableVisitor tableVisitor = new TableVisitor();                         // Draws the Identifier Table
    File directory;                                                         // The current directory.
    // Stores the last successfully generated native executable (absolute path).
    String lastNativeExe = null;
    // [ End of Non-GUI variables declaration ]
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Internal Class - ClipboardOwner ">
    /**
     * ClipboardOwner Class
     * Internal clipboard management class, uses the default system clipboard.
     */
    private class clipBoard implements ClipboardOwner {
        
        /**
         * Required by interface - not implemented
         */
        public void lostOwnership(Clipboard aClipboard, Transferable aContents) { }
        
        /**
         * Sets the clipboard contents
         */
        public void setClipboardContents(String _contents) {
            StringSelection stringSelection = new StringSelection(_contents);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, this);
        }
        
        /**
         * Returns the clipboard contents
         */
        public String getClipboardContents() {
            String ret = "";
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable contents = clipboard.getContents(null);
            if (contents != null && contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    DataFlavor flavorSet[] = contents.getTransferDataFlavors();
                    boolean canString = false;
                    for (int i=0;i<flavorSet.length;i++)
                        if (flavorSet[i] == DataFlavor.stringFlavor)
                            canString = true;
                    
                    ret = (String)contents.getTransferData(DataFlavor.stringFlavor);
                } catch (Exception e) { }
            }
            return(ret);
        }        
    }
    
    // </editor-fold>
}
