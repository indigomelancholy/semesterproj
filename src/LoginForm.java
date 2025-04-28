import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.InputStream;

public class LoginForm extends JFrame {
    private JTextField textField2;
    private JPasswordField textField3;
    private JButton loginButton;
    private JButton resetButton;
    private JLabel login;
    private JComboBox comboBox1;
    private JPanel panel;
    private JLabel loginStatus;
    private JLabel loginStatusDisp;
    private JLabel logolabel;
    private JLabel email;
    private JLabel position;
    private JLabel pass;
    private JLabel cred;


    public LoginForm() {
        setSize(800, 600);
        setContentPane(panel);

        panel.setBackground(new Color(241, 182, 238));

        Font font = new Font("Roboto", Font.PLAIN, 20);

        comboBox1.setFont(font);
        comboBox1.setBackground(new Color(230, 230, 250));

        textField2.setFont(font);
        textField3.setFont(font);
        textField2.setForeground(Color.MAGENTA);
        textField3.setForeground(Color.MAGENTA);

        loginStatus.setFont(new Font("Cambria", Font.BOLD, 24));
        loginStatus.setForeground(new Color(100, 100, 150));

        loginButton.setBackground(new Color(115, 76, 187));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFont(new Font("Cambria", Font.BOLD, 24));
        loginButton.setPreferredSize(new Dimension(200, 40));

        textField2.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 255), 2));
        textField3.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 255), 2));

        resetButton.setBackground(new Color(200, 100, 133));
        resetButton.setForeground(Color.WHITE);
        resetButton.setFont(new Font("Cambria", Font.BOLD, 24));
        resetButton.setPreferredSize(new Dimension(200, 40));

        Font labelFont = new Font("Roboto", Font.BOLD, 18);
        Color labelColor = new Color(115, 76, 187);
        loginStatusDisp.setFont(labelFont);
        loginStatusDisp.setForeground(labelColor);

        email.setFont(labelFont);
        email.setForeground(labelColor);

        pass.setFont(labelFont);
        pass.setForeground(labelColor);

        cred.setFont(labelFont);
        cred.setForeground(labelColor);

        position.setFont(labelFont);
        position.setForeground(labelColor);

        login.setFont(labelFont);
        login.setForeground(labelColor);

        ImageIcon logoimg = new ImageIcon(getClass().getResource("/logo.png"));
        Image img = logoimg.getImage();
        Image scaledImg = img.getScaledInstance(200, 200, Image.SCALE_SMOOTH);
        logolabel.setIcon(new ImageIcon(scaledImg));

        logolabel.setBorder(BorderFactory.createLineBorder(new Color(100, 100, 255), 3));
        logolabel.setBackground(new Color(241, 182, 238));


        comboBox1.setModel(new DefaultComboBoxModel<>(new String[]{"student", "teacher"}));

        setVisible(true);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String role = (String) comboBox1.getSelectedItem();
                String email = textField2.getText().trim();
                String password = textField3.getText().trim();

                    loginStatus.setText("Logging in...");


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


                    if (role.equalsIgnoreCase("student")) {
                        User.setStudentID(Integer.parseInt(id));
                        new StudentMain();

                    }else if (role.equalsIgnoreCase("teacher")) {
                        User.setTeacherID(Integer.parseInt(id));
                        new TeacherMain();
                    }

                    dispose();
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

    private JPanel createFieldPanel(JLabel label, JComponent field) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        return panel;
    }
}

