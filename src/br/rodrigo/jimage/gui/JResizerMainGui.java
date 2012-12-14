package br.rodrigo.jimage.gui;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import net.coobird.thumbnailator.Thumbnails;
import net.miginfocom.swing.MigLayout;

public class JResizerMainGui {

	private static final String VERSAO = "0.1";
	private static final String EMAIL_CONTATO = "ironman.br@gmail.com";
	private Properties properties = new Properties();
	private File fileProperties = new File("JImageResizer.properties");
	private JFrame frmJimageresizer;
	private JTextField tfFolderDestino;
	private String[] columnNames = {"Caminho", "Tamanho Original", "Resolução Original", "Tamanho Destino", "Resolução Destino"};
	private JScrollPane scrollPane;
	private JTable table;
	private JProgressBar progressBar;
	private JPanel panel;
	private JLabel lblTamanho;
	private JTextField tfTamanho;
	private JLabel lblQualidade;
	private JSlider slider;
	private JButton btnProcessarImagens;
	private DialogOptionsEnum checkFileExistsDialog;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					JResizerMainGui window = new JResizerMainGui();
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(window.frmJimageresizer);
					window.frmJimageresizer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public JResizerMainGui() {
		initialize();
		readProperties();
	}

	private void readProperties() {
		if (!fileProperties.exists()) {
			createProperties();
		} else {
			loadProperties();
		}
		tfFolderDestino.setText(properties.getProperty("tfFolderDestino", ""));
	}

	private void loadProperties() {
		try {
			FileInputStream inStream = new FileInputStream(fileProperties);
			properties.load(inStream);
			inStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createProperties() {
		try {
			fileProperties.createNewFile();
			FileOutputStream out = new FileOutputStream(fileProperties);
			properties.store(out, null);
			out.close();
		} catch (IOException e) {
			//TODO logar direito
			e.printStackTrace();
		}
	}
	
	private void saveProperties() {
		try {
			properties.setProperty("tfFolderDestino", tfFolderDestino.getText());
			FileOutputStream out = new FileOutputStream(fileProperties);
			properties.store(out, null);
			out.flush();
			out.close();
		} catch (IOException e) {
			//TODO logar direito
			e.printStackTrace();
		}
	}	

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmJimageresizer = new JFrame();
		frmJimageresizer.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				saveProperties();
			}
		});
		frmJimageresizer.setTitle(getAppTitle());
		frmJimageresizer.setBounds(100, 100, 800, 600);
		frmJimageresizer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmJimageresizer.getContentPane().setLayout(new MigLayout("", "[:128.00:140px,grow,right][][grow][]", "[][grow][][]"));
		
		JButton btnSelecionarImagens = new JButton("Selecionar Imagens");
		btnSelecionarImagens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					
					@Override
					public void run() {
						adicionarImagens();
					}
				});
				t.start();
			}
		});
		frmJimageresizer.getContentPane().add(btnSelecionarImagens, "cell 0 0,alignx center");
		
		progressBar = new JProgressBar();
		frmJimageresizer.getContentPane().add(progressBar, "cell 1 0 3 1,growx");
		
		scrollPane = new JScrollPane();
		frmJimageresizer.getContentPane().add(scrollPane, "cell 0 1 4 1,grow");
		
		table = new JTable();
		scrollPane.setViewportView(table);
		
		JLabel lblPastaDestino = new JLabel("Pasta Destino:");
		frmJimageresizer.getContentPane().add(lblPastaDestino, "cell 0 2,alignx right");
		
		tfFolderDestino = new JTextField();
		frmJimageresizer.getContentPane().add(tfFolderDestino, "cell 1 2 2 1,growx");
		tfFolderDestino.setColumns(10);
		
		JButton btnPastaDestino = new JButton("...");
		btnPastaDestino.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selecionarPastaDestino();
			}
		});
		frmJimageresizer.getContentPane().add(btnPastaDestino, "cell 3 2");
		
		panel = new JPanel();
		frmJimageresizer.getContentPane().add(panel, "cell 0 3 4 1,grow");
		panel.setLayout(new MigLayout("", "[][][100px:100px:120px][100px:n:100px][][][grow]", "[center]"));
		
		lblTamanho = new JLabel("Tamanho (px):");
		panel.add(lblTamanho, "cell 0 0,alignx trailing");
		
		tfTamanho = new JTextField();
		tfTamanho.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				validarTbTamanho();
			}
		});
		tfTamanho.setText("1000");
		panel.add(tfTamanho, "cell 1 0,growx");
		tfTamanho.setColumns(10);
		
		lblQualidade = new JLabel("Qualidade (70%):");
		panel.add(lblQualidade, "cell 2 0,alignx right");
		
		slider = new JSlider();
		slider.setMaximum(99);
		slider.setMinorTickSpacing(1);
		slider.setMajorTickSpacing(1);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				atualizarLabelSlider();
			}
		});
		slider.setValue(75);
		slider.setMinimum(20);
		panel.add(slider, "cell 3 0");
		
		btnProcessarImagens = new JButton("Processar Imagens");
		btnProcessarImagens.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					@Override
					public void run() {
						processarImagensAction();
					}
				});
				t.start();				
			}
		});
		panel.add(btnProcessarImagens, "cell 5 0");
	}

	private String getAppTitle() {
		return MessageFormat.format("JImageResizer {0} - {1}", VERSAO, EMAIL_CONTATO);
	}

	private void validarTbTamanho() {
		String text = tfTamanho.getText();
		text = text.replaceAll("\\D", "");
		if (text.length() > 10) {
			text = text.substring(0, 10);
		}
		long tamanho = Long.parseLong(text);
		if (tamanho > 5000 || tamanho == 0) {
			tamanho = 1000;
		}
		tfTamanho.setText(String.valueOf(tamanho));
	}

	private void selecionarPastaDestino() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FolderFilter());
		fc.setMultiSelectionEnabled(false);
		fc.setCurrentDirectory(new File(tfFolderDestino.getText()));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returnVal = fc.showOpenDialog(this.frmJimageresizer);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
           tfFolderDestino.setText(fc.getSelectedFile().getAbsolutePath());
        }
		
	}

	protected void atualizarLabelSlider() {
		int value = slider.getValue();
		lblQualidade.setText(MessageFormat.format("Qualidade ({0}%) :", value));
	}

	protected void adicionarImagens() {
		JFileChooser fc = new JFileChooser();
		fc.setFileFilter(new FileFilterImages());
		fc.setMultiSelectionEnabled(true);
        int returnVal = fc.showOpenDialog(this.frmJimageresizer);

        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File[] fileJpg = fc.getSelectedFiles();
            DefaultTableModel dataModel = new DefaultTableModelReadOnly(columnNames, fileJpg.length);
			table.setModel(dataModel);
			table.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
			progressBar.setMaximum(fileJpg.length - 1);
			progressBar.setMinimum(0);
            for (int i = 0; i < fileJpg.length; i++) {
				File file = fileJpg[i];
				try {
					table.getModel().setValueAt(file, i, 0);
					table.getModel().setValueAt(getFileSizeKb(file), i, 1);
					table.getModel().setValueAt(ImageUtil.getImageDimension(file), i, 2);
					progressBar.setValue(i);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
            table.getColumnModel().getColumn(0).setPreferredWidth(400);
        } else {
        }		
	}

	private String getFileSizeKb(File file) {
		return file.length() / 1024 + " kb";
	}
	
	private void processarImagensAction() {
		try {
			btnProcessarImagens.setEnabled(false);
			processarImagens();
		} finally {
			btnProcessarImagens.setEnabled(true);
		}
	}
	
	private void processarImagens() {
		checkFileExistsDialog = null;
		if (!chechFolderDestino()) {
			JOptionPane.showMessageDialog(frmJimageresizer, "Não foi possivel acessar a pasta: " + tfFolderDestino.getText());
			return;
		}
		TableModel model = table.getModel();
		int rowCount = model.getRowCount();
		progressBar.setMaximum(rowCount - 1);
		progressBar.setMinimum(0);			
		List<File> files = new ArrayList<File>(rowCount);
		float quality = getQuality();
		int tamanho = Integer.parseInt(tfTamanho.getText());
		for (int i = 0; i < rowCount; i++) {
			File file = (File) model.getValueAt(i, 0);
			files.add(file);
			File fileDestino = createFileDestino(file);
			progressBar.setValue(i);
			try {
				DialogOptionsEnum dialogOptionsEnum = checkFileExistsDialog(fileDestino);
				if (dialogOptionsEnum == null) {
					//botão cancelar
					return;
				}
				if (dialogOptionsEnum == DialogOptionsEnum.YES || dialogOptionsEnum == DialogOptionsEnum.YES_ALL) {
					Thumbnails.of(file).size(tamanho, tamanho).outputQuality(quality).toFile(fileDestino);
					model.setValueAt(getFileSizeKb(fileDestino), i, 3);
					model.setValueAt(ImageUtil.getImageDimension(fileDestino), i, 4);
				}
			} catch (IOException e) {
				System.out.println("Erro ao processar a imagem: " + file.getAbsolutePath());
			}
		}		
	}
	
	private DialogOptionsEnum checkFileExistsDialog(File file) {
		if (!file.exists()) {
			return DialogOptionsEnum.YES;
		} else if (DialogOptionsEnum.NO_ALL.equals(checkFileExistsDialog)) {
			return DialogOptionsEnum.NO;
		} else if (DialogOptionsEnum.YES_ALL.equals(checkFileExistsDialog)) {
			return DialogOptionsEnum.YES;
		}
		String msgConfirm = MessageFormat.format("Deseja sobrescrever o arquivo {0}", file);
		checkFileExistsDialog = (DialogOptionsEnum) JOptionPane.showInputDialog(
				frmJimageresizer, msgConfirm, "", JOptionPane.QUESTION_MESSAGE, 
				null, DialogOptionsEnum.values(), DialogOptionsEnum.NO);
		return checkFileExistsDialog;
	}

	private boolean chechFolderDestino() {
		File destino = new File(tfFolderDestino.getText());
		if (destino.exists() && destino.canWrite()) {
			return true;
		} else if (!destino.exists()) {
			int confirmDialog = JOptionPane.showConfirmDialog(tfFolderDestino, 
					MessageFormat.format("A pasta {0} não existe, deseja cria-la?", destino));
			if (confirmDialog == JOptionPane.YES_OPTION) {
				return destino.mkdirs();
			}
		}
		return false;
	}

	private float getQuality() {
		return slider.getValue() / 100f;
	}
	
	private File createFileDestino(File fileOrigem) {
		return new File(tfFolderDestino.getText() + File.separator + fileOrigem.getName());
	}
	 
	private class FileFilterImages extends FileFilter {

		@Override
		public boolean accept(File f) {
			if (f.isDirectory()) {
				return true;
			} else {
				String lowerCase = f.getName().toLowerCase();
				return lowerCase.endsWith(".jpg") || lowerCase.endsWith(".jpeg");
			}
		}

		@Override
		public String getDescription() {
			return "JPEG Imagens: *.jpg";
		}
		
	}
	
	private class FolderFilter extends FileFilter {

		@Override
		public boolean accept(File f) {
			return f.isDirectory();
		}

		@Override
		public String getDescription() {
			return "Pastas";
		}
		
	}	
	
	private class DefaultTableModelReadOnly extends DefaultTableModel {
		private static final long serialVersionUID = 1L;

		public DefaultTableModelReadOnly(Object[] columnNames, int rowCount) {
			super(columnNames, rowCount);
		}
		
		@Override
		public boolean isCellEditable(int row, int column) {
			return false;
		}
		
	}

}
