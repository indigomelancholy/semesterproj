import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

public class LoginForm extends JFrame {
    private JTextField textField2;
    private JTextField textField3;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel login;
    private JComboBox comboBox1;
    private JPanel panel;
    private JLabel loginStatus;
    private JLabel loginStatusDisp;
    private JLabel logolabel;




    public LoginForm() {
        setSize(400, 400);
        setContentPane(panel);

        ImageIcon logoimg = new ImageIcon(getClass().getResource("/logo.png"));
        Image img = logoimg.getImage();
        Image scaledImg = img.getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logolabel.setIcon(new ImageIcon(scaledImg));

        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{"student", "teacher"}));

        setVisible(true);



        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = (String) comboBox1.getSelectedItem();
                String email = textField2.getText().trim();
                String password = textField3.getText().trim();

                Object[] userDetails = connect.loginMethod(email, password, role);
                if (userDetails != null) {

                    loginStatus.setText("Login Successful");
                    String id = (String) userDetails[0];
                    String firstName = (String) userDetails[1];
                    String lastName = (String) userDetails[2];
                    InputStream imgStream = (InputStream) userDetails[3];

                    User.setFirstName(firstName);
                    User.setLastName(lastName);
                    User.setPhotoStream(imgStream);

                    dispose();

                    if (role.equalsIgnoreCase("student")) {
                        User.setStudentID(Integer.parseInt(id));

                    }else if (role.equalsIgnoreCase("teacher")){
                        User.setTeacherID(Integer.parseInt(id));

                }
                    User.setFirstName(firstName);
                    User.setLastName(lastName);
                    User.setPhotoStream(imgStream);

                    dispose();

                    if (role.equalsIgnoreCase("student")) {
                        new StudentMain();
                    } else if (role.equalsIgnoreCase("teacher")) {
                        new TeacherMain();
                    }

                }else{
                    loginStatus.setText("Login failed!");
                }
            }
        });


        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                textField2.setText("");
                textField3.setText("");
                comboBox1.setSelectedIndex(0);
                loginStatus.setText("");
            }
        });
    }
}

