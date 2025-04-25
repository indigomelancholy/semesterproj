import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

    private String id;
    private String firstName;
    private String lastName;


    public LoginForm() {
        setSize(400, 400);
        setContentPane(panel);
        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = (String) comboBox1.getSelectedItem();
                String email = textField2.getText().trim();
                String password = textField3.getText().trim();

                String[] userDetails = connect.loginMethod(email, password, role);
                if (userDetails != null) {

                    loginStatus.setText("Login Successful");

                    dispose();

                    if (role.equalsIgnoreCase("student")) {
                        User.setStudentID(Integer.parseInt(userDetails[0]));
                        User.setFirstName(userDetails[1]);
                        User.setLastName(userDetails[2]);
                        dispose();
                        new StudentMain();
                    }else if (role.equalsIgnoreCase("teacher")){
                        User.setTeacherID(Integer.parseInt(userDetails[0]));
                        User.setFirstName(userDetails[1]);
                        User.setLastName(userDetails[2]);
                        dispose();
                        new TeacherMain();                     }
                }else{
                    loginStatus.setText("Login Failed");
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
