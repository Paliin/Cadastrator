/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package Tela1;

import java.awt.CardLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author pauli
 */
public class Telalogin_1 extends javax.swing.JFrame {
    /**
     * Creates new form Telalogin_1
     */
    public Telalogin_1() {
        initComponents();
        this.setLocationRelativeTo(null);
    }

public String obterUltimoUsuarioLogado() {
    String nomeUsuario = "Desconhecido";  

    // Caminho do arquivo
    File arquivo = new File("ultimo_usuario_logado.txt");

    // Verifica se o arquivo existe
    if (arquivo.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            nomeUsuario = reader.readLine();  
        } catch (IOException e) {
            e.printStackTrace();
        }
    } else {
        // Se o arquivo não existir, inicializa com valor padrão
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(arquivo))) {
            writer.write(nomeUsuario);  
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    return nomeUsuario;
}

public void salvarUltimoUsuarioLogado(String nomeUsuario) {
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("ultimo_usuario_logado.txt"))) {
        writer.write(nomeUsuario);  // Salva o nome do último usuário logado
    } catch (IOException e) {
        e.printStackTrace();
    }
}



    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        Tela_Login = new javax.swing.JPanel();
        Tela_loginUsuario = new javax.swing.JPanel();
        Login_user_nome = new javax.swing.JTextField();
        Button_Confirmar = new javax.swing.JButton();
        Button_Cancelar = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        Login_user_senha = new javax.swing.JPasswordField();
        jButton3 = new javax.swing.JButton();
        Tela_LoginCadastro = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        Cad_user_nome = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        Cad_user_senha = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        Cad_user_confirmasenha = new javax.swing.JTextField();
        Cad_user_confirma = new javax.swing.JButton();
        Cad_user_cancela = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        Tela_Login.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        Tela_Login.setPreferredSize(new java.awt.Dimension(600, 480));
        Tela_Login.setLayout(new java.awt.CardLayout());

        Tela_loginUsuario.setBackground(java.awt.SystemColor.control);
        Tela_loginUsuario.setPreferredSize(new java.awt.Dimension(600, 480));

        Login_user_nome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Login_user_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Login_user_nomeActionPerformed(evt);
            }
        });

        Button_Confirmar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button_Confirmar.setText("CONFIRMAR");
        Button_Confirmar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Button_ConfirmarActionPerformed(evt);
            }
        });

        Button_Cancelar.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Button_Cancelar.setText("CANCELAR");

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel1.setText("Usuário:");

        jLabel2.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel2.setText("Senha:");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel6.setText("Cadastrator São Vicente");

        Login_user_senha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        jButton3.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jButton3.setForeground(new java.awt.Color(51, 51, 51));
        jButton3.setText("Criar Cadastro");
        jButton3.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jButton3.setBorderPainted(false);
        jButton3.setContentAreaFilled(false);
        jButton3.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout Tela_loginUsuarioLayout = new javax.swing.GroupLayout(Tela_loginUsuario);
        Tela_loginUsuario.setLayout(Tela_loginUsuarioLayout);
        Tela_loginUsuarioLayout.setHorizontalGroup(
            Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_loginUsuarioLayout.createSequentialGroup()
                .addGap(148, 148, 148)
                .addGroup(Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(Login_user_senha)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Tela_loginUsuarioLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Tela_loginUsuarioLayout.createSequentialGroup()
                        .addComponent(Button_Confirmar)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 101, Short.MAX_VALUE)
                        .addComponent(Button_Cancelar))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, Tela_loginUsuarioLayout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addGroup(Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(Login_user_nome, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(Tela_loginUsuarioLayout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addGap(0, 0, Short.MAX_VALUE)))))
                .addGap(144, 144, 144))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_loginUsuarioLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_loginUsuarioLayout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(248, 248, 248))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_loginUsuarioLayout.createSequentialGroup()
                        .addComponent(jLabel6)
                        .addGap(161, 161, 161))))
        );
        Tela_loginUsuarioLayout.setVerticalGroup(
            Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_loginUsuarioLayout.createSequentialGroup()
                .addContainerGap(85, Short.MAX_VALUE)
                .addComponent(jLabel6)
                .addGap(46, 46, 46)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Login_user_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Login_user_senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton3)
                .addGap(10, 10, 10)
                .addGroup(Tela_loginUsuarioLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(Button_Confirmar)
                    .addComponent(Button_Cancelar))
                .addContainerGap(126, Short.MAX_VALUE))
        );

        Tela_Login.add(Tela_loginUsuario, "Tela_login");

        Tela_LoginCadastro.setOpaque(false);
        Tela_LoginCadastro.setPreferredSize(new java.awt.Dimension(600, 480));

        jLabel3.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel3.setText("Usuário:");

        Cad_user_nome.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Cad_user_nome.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cad_user_nomeActionPerformed(evt);
            }
        });

        jLabel4.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel4.setText("Senha:");

        Cad_user_senha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        Cad_user_senha.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cad_user_senhaActionPerformed(evt);
            }
        });

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        jLabel5.setText("Repetir Senha:");

        Cad_user_confirmasenha.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N

        Cad_user_confirma.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cad_user_confirma.setText("CONFIRMAR");
        Cad_user_confirma.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cad_user_confirmaActionPerformed(evt);
            }
        });

        Cad_user_cancela.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N
        Cad_user_cancela.setText("CANCELAR");
        Cad_user_cancela.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Cad_user_cancelaActionPerformed(evt);
            }
        });

        jLabel7.setFont(new java.awt.Font("Segoe UI", 1, 22)); // NOI18N
        jLabel7.setText("NOVO USUARIO");

        javax.swing.GroupLayout Tela_LoginCadastroLayout = new javax.swing.GroupLayout(Tela_LoginCadastro);
        Tela_LoginCadastro.setLayout(Tela_LoginCadastroLayout);
        Tela_LoginCadastroLayout.setHorizontalGroup(
            Tela_LoginCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_LoginCadastroLayout.createSequentialGroup()
                .addGap(113, 113, 113)
                .addGroup(Tela_LoginCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel5)
                    .addComponent(jLabel4)
                    .addComponent(jLabel3)
                    .addGroup(Tela_LoginCadastroLayout.createSequentialGroup()
                        .addComponent(Cad_user_confirma)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 126, Short.MAX_VALUE)
                        .addComponent(Cad_user_cancela))
                    .addComponent(Cad_user_nome)
                    .addComponent(Cad_user_senha)
                    .addComponent(Cad_user_confirmasenha))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, Tela_LoginCadastroLayout.createSequentialGroup()
                .addContainerGap(219, Short.MAX_VALUE)
                .addComponent(jLabel7)
                .addGap(212, 212, 212))
        );
        Tela_LoginCadastroLayout.setVerticalGroup(
            Tela_LoginCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(Tela_LoginCadastroLayout.createSequentialGroup()
                .addGap(88, 88, 88)
                .addComponent(jLabel7)
                .addGap(18, 18, 18)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cad_user_nome, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cad_user_senha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(Cad_user_confirmasenha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addGroup(Tela_LoginCadastroLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(Cad_user_confirma)
                    .addComponent(Cad_user_cancela))
                .addContainerGap(86, Short.MAX_VALUE))
        );

        Tela_Login.add(Tela_LoginCadastro, "Tela_Cadastro_1");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tela_Login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(Tela_Login, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void Login_user_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Login_user_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Login_user_nomeActionPerformed

    private void Button_ConfirmarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Button_ConfirmarActionPerformed
// Obtendo os dados inseridos pelo usuário
Telalogin_1 telaLogin = new Telalogin_1();  
String nomeUltimoUsuario = telaLogin.obterUltimoUsuarioLogado();      

// Obtendo os dados inseridos pelo usuário
String nome = Login_user_nome.getText(); 
String senha = String.valueOf(Login_user_senha.getPassword());  

// Verifica se o usuário existe
boolean usuarioValido = false;

try {
    
    BufferedReader reader = new BufferedReader(new FileReader("cadastros.txt"));
    String linha;

    while ((linha = reader.readLine()) != null) {
        
        if (linha.startsWith("Nome: " + nome)) {
            String senhaSalva = reader.readLine();
            
            if (senhaSalva != null && senhaSalva.equals("Senha: " + senha)) {
                usuarioValido = true;
                break;
            }
        }
    }
    // Fechar o arquivo
    reader.close();  

    // Se o usuário for válido e a senha estiver correta
if (usuarioValido) {
    // Salvar o nome do usuário logado no arquivo "ultimo_usuario_logado.txt"
    try (BufferedWriter writer = new BufferedWriter(new FileWriter("ultimo_usuario_logado.txt"))) {
        writer.write(nome); 
    } catch (IOException e) {
        e.printStackTrace();
    }

    // Oculta a tela  
    this.setVisible(false);

    Menu_principal menuprincipal = new Menu_principal();
    
    // nome do usuário logado para o Menu_principal
    menuprincipal.setUsuarioLogado(nome);  // Passa o nome para o menu principal
    
    // Exibe o menu principal
    menuprincipal.setVisible(true);
} else {
    // Se o usuário ou senha estiverem incorretos
    JOptionPane.showMessageDialog(this, "Nome de usuário ou senha incorretos. Tente novamente.", "Erro de Login", JOptionPane.ERROR_MESSAGE);
}
}       catch (FileNotFoundException ex) {
            Logger.getLogger(Telalogin_1.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Telalogin_1.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_Button_ConfirmarActionPerformed

    private void Cad_user_nomeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cad_user_nomeActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cad_user_nomeActionPerformed

    private void Cad_user_senhaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cad_user_senhaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_Cad_user_senhaActionPerformed

    private void Cad_user_cancelaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cad_user_cancelaActionPerformed
        CardLayout card = (CardLayout) Tela_Login.getLayout(); 
        card.show(Tela_Login, "Tela_login");
    }//GEN-LAST:event_Cad_user_cancelaActionPerformed

    private void Cad_user_confirmaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_Cad_user_confirmaActionPerformed
        //salva os dados do menor
        String nome = Cad_user_nome.getText();
        String senha = Cad_user_senha.getText(); 
        String confirmasenha = Cad_user_confirmasenha.getText();

        //nao pode deixar em branco os dados bro
        if (nome.trim().isEmpty() || senha.trim().isEmpty() || confirmasenha.trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Por favor, preencha todos os campos.", "Erro de Cadastro", JOptionPane.ERROR_MESSAGE);
        return;
        }
        
        // senha iguais?
        if (senha.equals(confirmasenha)) {
            // Se sim
            try {
                // arquivo pra salvar
                BufferedWriter writer = new BufferedWriter(new FileWriter("cadastros.txt", true));  
                
                writer.write("Nome: " + nome);
                writer.newLine();
                writer.write("Senha: " + senha);  
                writer.newLine();
                writer.write("-----");
                writer.newLine();

                writer.close(); 

                // Mensagem de salve
                System.out.println("Dados salvos com sucesso!");
                JOptionPane.showMessageDialog(this, "Cadastro realizado!");
              
                CardLayout card = (CardLayout) Tela_Login.getLayout(); 
                card.show(Tela_Login, "Tela_login");
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            // caso bote senhas diferentes
            System.out.println("As senhas não coincidem. Tente novamente.");
            JOptionPane.showMessageDialog(this, "As senhas não coincidem. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
        }   
    }//GEN-LAST:event_Cad_user_confirmaActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
         CardLayout card = (CardLayout) Tela_Login.getLayout();
         card.show(Tela_Login, "Tela_Cadastro_1");
    }//GEN-LAST:event_jButton3ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Telalogin_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Telalogin_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Telalogin_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Telalogin_1.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new Telalogin_1().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton Button_Cancelar;
    private javax.swing.JButton Button_Confirmar;
    private javax.swing.JButton Cad_user_cancela;
    private javax.swing.JButton Cad_user_confirma;
    private javax.swing.JTextField Cad_user_confirmasenha;
    private javax.swing.JTextField Cad_user_nome;
    private javax.swing.JTextField Cad_user_senha;
    private javax.swing.JTextField Login_user_nome;
    private javax.swing.JPasswordField Login_user_senha;
    private javax.swing.JPanel Tela_Login;
    private javax.swing.JPanel Tela_LoginCadastro;
    private javax.swing.JPanel Tela_loginUsuario;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    // End of variables declaration//GEN-END:variables
}
