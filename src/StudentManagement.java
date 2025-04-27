import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.InputStream;
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

    private DefaultTableModel model;
    private ArrayList<String[]> students;

    public StudentManagement() {
        setSize(800, 600);
        setContentPane(panel);
        setVisible(true);

        model = new DefaultTableModel(new String[]{"ID", "FirstName", "LastName", "Photo", "Age", "Email", "GPA"}, 0);
        table1.setModel(model);

        loadStudents();

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
dispose();

new TeacherMain();

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
                        (selected.equals("High GPA (>3.5)") && gpa > 3.5) ||
                        (selected.equals("Low GPA (<2.0)") && gpa < 2.0)) {

                    model.addRow(student);
                }
            }
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
}