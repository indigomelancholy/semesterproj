import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;

public class StudentManagement extends JFrame{
    private JTextField searchbar;
    private JTable table1;
    private JButton addStudentButton;
    private JButton deleteStudentButton;
    private JButton backButton;
    private JScrollPane table;
    private JComboBox comboBox;
    private JPanel panel;
    private JLabel studentm;

    private DefaultTableModel model;
    private ArrayList<String[]> students;

    public StudentManagement() {
        setSize(1000, 700);
        setContentPane(panel);

        panel.setBackground(new Color(241, 182, 238));

        Font font = new Font("Roboto", Font.PLAIN, 20);
        Font buttonFont = new Font("Cambria", Font.BOLD, 24);
        Font labelFont = new Font("Roboto", Font.BOLD, 18);

        model = new DefaultTableModel(new String[]{"ID", "FirstName", "LastName", "Photo", "Age", "Email", "GPA"}, 0){
            @Override
            public Class<?> getColumnClass(int column) {
                return (column == 3) ? ImageIcon.class : Object.class;
            }
        };

        table1.setModel(model);
        table1.setFont(font);
        table1.setPreferredScrollableViewportSize(new Dimension(900, 500));
        table1.setFillsViewportHeight(true);
        table1.setRowHeight(100);

        table1.getColumnModel().getColumn(0).setPreferredWidth(100);
        table1.getColumnModel().getColumn(1).setPreferredWidth(150);
        table1.getColumnModel().getColumn(2).setPreferredWidth(200);
        table1.getColumnModel().getColumn(3).setPreferredWidth(150);
        table1.getColumnModel().getColumn(4).setPreferredWidth(50);
        table1.getColumnModel().getColumn(5).setPreferredWidth(150);
        table1.getColumnModel().getColumn(6).setPreferredWidth(100);

        table1.setBackground(new Color(230, 230, 250));
        table1.setSelectionBackground(new Color(115, 76, 187));
        table.setViewportView(table1);

        studentm.setFont(labelFont);
        studentm.setForeground(new Color(87, 40, 105));


        searchbar.setFont(font);
        searchbar.setForeground(Color.MAGENTA);
        searchbar.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 255), 2));

        addStudentButton.setBackground(new Color(115, 76, 187));
        addStudentButton.setForeground(Color.WHITE);
        addStudentButton.setFont(buttonFont);
        addStudentButton.setPreferredSize(new Dimension(100, 20));

        deleteStudentButton.setBackground(new Color(115, 76, 187));
        deleteStudentButton.setForeground(Color.WHITE);
        deleteStudentButton.setFont(buttonFont);
        deleteStudentButton.setPreferredSize(new Dimension(100, 20));

        backButton.setBackground(new Color(200, 100, 133));
        backButton.setForeground(Color.WHITE);
        backButton.setFont(buttonFont);
        backButton.setPreferredSize(new Dimension(100, 20));

        comboBox.setFont(font);
        comboBox.setBackground(new Color(230, 230, 250));
        comboBox.addItem("All");
        comboBox.addItem("High GPA (>50.0)");
        comboBox.addItem("Low GPA (<50.0)");


        loadStudents();
        loadPhotos();

        setVisible(true);


        table1.setDefaultEditor(Object.class, new DefaultCellEditor(new JTextField()) {
            private String oldValue;

            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {

                oldValue = (value != null) ? value.toString() : "";

                return super.getTableCellEditorComponent(table, value, isSelected, row, column);
            }

            @Override
            public boolean stopCellEditing() {
                String newValue = getCellEditorValue().toString();

                int row = table1.getEditingRow();
                int column = table1.getEditingColumn();

                String columnName = table1.getColumnName(column);
                String studentID = (String) table1.getValueAt(row, 0);

                if (!newValue.equals(oldValue) && !columnName.equals("GPA") && !columnName.equals("ID")) {

                    connect.updateStudentField(studentID, columnName, newValue);

                    loadStudents();
                }
                return super.stopCellEditing();
            }
        });


        addStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                AddDialog();
            }
        });




        searchbar.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                String searchText = searchbar.getText().trim().toLowerCase();
                filterSearch(searchText);

            }
        });


        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GPAFilter();
            }
        });

        deleteStudentButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                int selectedRow = table1.getSelectedRow();

                if (selectedRow == -1) {
                    JOptionPane.showMessageDialog(null, "Please select a student!");
                    return;
                }

                String studentID = (String) table1.getValueAt(selectedRow, 0);

                int confirm = JOptionPane.showConfirmDialog(null, "Delete this student?", " ", JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {

                    connect.deleteStudent(studentID);
                    loadStudents();
                }
            }
        });


        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                new TeacherMain();

                dispose();

            }
        });
    }




        private void loadStudents() {
            model.setRowCount(0);
            students = connect.getAllStudentsWithGPA();

            for (String[] student : students) {
                model.addRow(student);
            }
        }


        private void filterSearch (String searchText) {
            model.setRowCount(0);

            ArrayList<String[]> allStudents = connect.getAllStudentsWithGPA();

            for (String[] student : allStudents) {

                boolean matches = false;

                for (String value : student) {
                    if (value.toLowerCase().contains(searchText)) {
                        matches = true;
                        break;
                    }
                }
                if (matches) {
                    model.addRow(student);

                }

            }
        }

        private void GPAFilter() {

            String selected = (String) comboBox.getSelectedItem();
            model.setRowCount(0);

            ArrayList<String[]> allStudents = connect.getAllStudentsWithGPA();


            for (String[] student : allStudents) {

                double gpa = Double.parseDouble(student[6]);

                if (selected.equals("All") ||
                        (selected.equals("High GPA (>50.0)") && gpa > 50.0) ||
                        (selected.equals("Low GPA (<50.0)") && gpa < 50.0)) {

                    model.addRow(student);
                }
            }
            loadPhotos();
        }

        private void AddDialog() {
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Add Student", true);
            dialog.setSize(300, 400);
            dialog.setLayout(new GridLayout(8, 2));

            JTextField idField = new JTextField();
            JTextField firstNameField = new JTextField();
            JTextField lastNameField = new JTextField();
            JTextField ageField = new JTextField();
            JTextField emailField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            JLabel photoLabel = new JLabel("Image here");
            JButton choosePhotoButton = new JButton("Choose a photo");

            final InputStream[] photoStream = {null};

            choosePhotoButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                    JFileChooser fileChooser = new JFileChooser();

                    int option = fileChooser.showOpenDialog(dialog);

                    if (option == JFileChooser.APPROVE_OPTION) {
                        try {
                            photoStream[0] = new java.io.FileInputStream(fileChooser.getSelectedFile());
                            photoLabel.setText(fileChooser.getSelectedFile().getName());

                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            });

            JButton saveButton = new JButton("Save");
            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {

                        String studentID = idField.getText();
                        String firstName = firstNameField.getText();
                        String lastName = lastNameField.getText();

                        int age = Integer.parseInt(ageField.getText());
                        String email = emailField.getText();
                        String password = new String(passwordField.getPassword());


                        connect.addStudentWithFields(studentID, firstName, lastName, age, email, password, photoStream[0]);

                        JOptionPane.showMessageDialog(dialog, "Student added!");
                        dialog.dispose();
                        StudentManagement.this.loadStudents();

                        new TeacherMain();

                    } catch (Exception ex) {

                        JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage());
                    }
                }
            });

            dialog.add(new JLabel("Student ID:"));
            dialog.add(idField);

            dialog.add(new JLabel("First Name:"));
            dialog.add(firstNameField);

            dialog.add(new JLabel("Last Name:"));
            dialog.add(lastNameField);

            dialog.add(new JLabel("Age:"));
            dialog.add(ageField);

            dialog.add(new JLabel("Email:"));
            dialog.add(emailField);

            dialog.add(new JLabel("Password:"));
            dialog.add(passwordField);

            dialog.add(photoLabel);
            dialog.add(choosePhotoButton);
            dialog.add(new JLabel());

            dialog.add(saveButton);

            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);

    }

    private void loadPhotos() {
        try {
            ArrayList<Object[]> studentPhotos = connect.getAllStudentPhotos();

            for (int i = 0; i < table1.getRowCount(); i++) {
                String studentID = (String) table1.getValueAt(i, 0);

                for (Object[] photoData : studentPhotos) {
                    String photoStudentID = (String) photoData[0];
                    Blob photoBlob = (Blob) photoData[1];

                    if (studentID.equals(photoStudentID) && photoBlob != null) {
                        try (InputStream inputStream = photoBlob.getBinaryStream()) {
                            BufferedImage originalImage = ImageIO.read(inputStream);
                            if (originalImage != null) {
                                int newWidth = 100;
                                int newHeight = 100;
                                Image scaledImage = originalImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);


                                ImageIcon icon = new ImageIcon(scaledImage);
                                table1.setValueAt(icon, i, 3);
                            }
                        }
                        break;
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading photos: " + e.getMessage());
        }
    }
}