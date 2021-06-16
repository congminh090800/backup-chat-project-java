/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author Admin
 */
public class ServerListGUI extends javax.swing.JFrame {

    /**
     * Creates new form ServerListGUI
     */
    public ServerListGUI() {
        initComponents();
        readConfig();
        checkServers();
    }
    
    public void addServer(String host){
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        listModel.addElement(host);
    }
    public void removeServer(int index){
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        listModel.remove(index);
    }
    public void editServer(int index, String host){
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        listModel.setElementAt(host, index);
    }
    public String getServerUrl(String item){
        return item.split(" ")[0];
    }
    public void checkServers(){
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        for (int i=0; i<listModel.getSize();i++){
            String serverUrl = getServerUrl((String)listModel.getElementAt(i));
            String[] serverInfo= (serverUrl).split(":");
            boolean isOpen = checkServerStatus(serverInfo[0], Integer.parseInt(serverInfo[1]));
            if (isOpen) {
                String url = getServerUrl((String) listModel.getElementAt(i));
                listModel.setElementAt(url + " (open)", i);
            } else {
                String url = getServerUrl((String) listModel.getElementAt(i));
                listModel.setElementAt(url + " (closed)", i);               
            }
        }
    }
    public boolean checkServerStatus(String ip, int port){
        Socket checkSocket = null;
        try {     
            checkSocket = new Socket(ip, port);
            checkSocket.close();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
    
    public String[] getAddressList(JList serverList){
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        String[] result = new String[listModel.getSize()];
        for (int i=0; i<listModel.getSize(); i++){
            result[i] = getServerUrl((String) listModel.getElementAt(i));
        }
        return result;
    }
    
    public void saveConfig() {
        try {                                                                   
            BufferedWriter bw = new BufferedWriter(new FileWriter("address_list.txt")); 
            for (int i=0; i<serverList.getModel().getSize(); i++){                       
                bw.write(getServerUrl(serverList.getModel().getElementAt(i)));                                   
                bw.newLine();                                                       
            }                                                                  
            bw.close();                                                        
        } catch (IOException ex) {                                           
            JOptionPane.showConfirmDialog(null, "Failed to saved config", "Failure", JOptionPane.DEFAULT_OPTION);
        }    
    }
    
    private void readConfig(){
        File f = new File("address_list.txt");
        if (f.exists()){
            FileReader fr = null; 
            try {
                fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
                try {
                    String line;
                    while ((line = br.readLine()) != null) {
                        addServer(line);
                    }
                    br.close();            
                    fr.close();
                } catch (IOException ex) {
                    JOptionPane.showConfirmDialog(null, "Failed to load config", "Failure", JOptionPane.DEFAULT_OPTION);
                }
            } catch (FileNotFoundException ex) {
                Logger.getLogger(ServerListGUI.class.getName()).log(Level.SEVERE, null, ex);            
            } finally {
                try {
                    fr.close();
                } catch (IOException ex) {
                    Logger.getLogger(ServerListGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
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

        serverListScroll = new javax.swing.JScrollPane();
        serverList = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        addBtn = new javax.swing.JButton();
        removeBtn = new javax.swing.JButton();
        editBtn = new javax.swing.JButton();
        connectBtn = new javax.swing.JButton();
        refreshBtn = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setResizable(false);

        serverList.setModel(new DefaultListModel<>());
        serverListScroll.setViewportView(serverList);

        jLabel1.setText("Server list:");

        addBtn.setText("Add");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        removeBtn.setText("Remove");
        removeBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBtnActionPerformed(evt);
            }
        });

        editBtn.setText("Edit");
        editBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editBtnActionPerformed(evt);
            }
        });

        connectBtn.setText("Connect");
        connectBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectBtnActionPerformed(evt);
            }
        });

        refreshBtn.setText("Refresh");
        refreshBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 79, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(refreshBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(serverListScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(addBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(editBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(connectBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(refreshBtn))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(serverListScroll, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(addBtn)
                        .addGap(35, 35, 35)
                        .addComponent(removeBtn)
                        .addGap(37, 37, 37)
                        .addComponent(editBtn)
                        .addGap(42, 42, 42)
                        .addComponent(connectBtn)))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        // TODO add your handling code here:
        new ConfigServerGUI(this).setVisible(true);
    }//GEN-LAST:event_addBtnActionPerformed

    private void removeBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBtnActionPerformed
        // TODO add your handling code here:
        int selectedUrl = serverList.getSelectedIndex();
        removeServer(selectedUrl);
        saveConfig();
    }//GEN-LAST:event_removeBtnActionPerformed
 
    private void editBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editBtnActionPerformed
        // TODO add your handling code here:
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        int index = serverList.getSelectedIndex();
        String item = (String) listModel.getElementAt(index);
        String url = getServerUrl(item);
        String[] info = url.split(":");
        new ConfigServerGUI(this, info[0], Integer.parseInt(info[1]), index).setVisible(true);
    }//GEN-LAST:event_editBtnActionPerformed

    private void refreshBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshBtnActionPerformed
        // TODO add your handling code here:
        checkServers();
    }//GEN-LAST:event_refreshBtnActionPerformed

    private void connectBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectBtnActionPerformed
        // TODO add your handling code here:
        DefaultListModel listModel = (DefaultListModel) serverList.getModel();
        int index = serverList.getSelectedIndex();
        String host = (String)listModel.getElementAt(index);
        new LoginGUI(getServerUrl(host)).setVisible(true);
    }//GEN-LAST:event_connectBtnActionPerformed

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
            java.util.logging.Logger.getLogger(ServerListGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ServerListGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ServerListGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ServerListGUI.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new ServerListGUI().setVisible(true);
            }
        });
    }

    public JList<String> getServerList() {
        return serverList;
    }

    public void setServerList(JList<String> serverList) {
        this.serverList = serverList;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton connectBtn;
    private javax.swing.JButton editBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JButton refreshBtn;
    private javax.swing.JButton removeBtn;
    private javax.swing.JList<String> serverList;
    private javax.swing.JScrollPane serverListScroll;
    // End of variables declaration//GEN-END:variables
}
