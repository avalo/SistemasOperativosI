/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chat_sockets;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Kerlyn
 */
public class Cliente {

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        MarcoCliente mimarco = new MarcoCliente();
        mimarco.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}

class MarcoCliente extends JFrame {

    public MarcoCliente() {
        setBounds(600, 300, 280, 350);
        LaminaMarcoCliente milamina = new LaminaMarcoCliente();
        add(milamina);
        setVisible(true);
        addWindowListener(new EnvioOnline());
    }
}

class EnvioOnline extends WindowAdapter {

    public void windowOpened(WindowEvent e) {
        try {
            //ip del server
            Socket misocket = new Socket("192.168.56.1", 9999);
            PaqueteEnvio datos = new PaqueteEnvio();
            datos.setMensaje(" online");
            ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
            paquete_datos.writeObject(datos);
            misocket.close();
        } catch (Exception e2) {

        }
    }
}

class LaminaMarcoCliente extends JPanel implements Runnable {

    public LaminaMarcoCliente() {
        String nick_usuario = JOptionPane.showInputDialog("Nick: ");
        JLabel n_nick = new JLabel("Nick: ");
        add(n_nick);
        nick = new JLabel();
        nick.setText(nick_usuario);
        add(nick);
        JLabel texto = new JLabel("Online: ");
        add(texto);
        ip = new JComboBox();
        add(ip);
        campochat = new JTextArea(12, 20);
        add(campochat);
        campo1 = new JTextField(20);
        add(campo1);
        miboton = new JButton("Enviar");
        EnviaTexto mievento = new EnviaTexto();
        miboton.addActionListener(mievento);
        add(miboton);
        Thread mihilo = new Thread(this);
        mihilo.start();
    }

    @Override
    public void run() {
        try {
            ServerSocket servidor_cliente = new ServerSocket(9090);
            Socket cliente;
            PaqueteEnvio paqueteRecibido;
            while (true) {
                cliente = servidor_cliente.accept();
                ObjectInputStream flujoentrada = new ObjectInputStream(cliente.getInputStream());
                paqueteRecibido = (PaqueteEnvio) flujoentrada.readObject();
                if (paqueteRecibido.getMensaje().equals(" online")) {
                    campochat.append("\n" + paqueteRecibido.getNick() + ": " + paqueteRecibido.getMensaje());
                } else {
                    ArrayList<String> IpsMenu = new ArrayList<String>();
                    IpsMenu = paqueteRecibido.getIps();
                    ip.removeAllItems();
                    for (String z : IpsMenu) {
                        ip.addItem(z);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private class EnviaTexto implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            campochat.append("\n" + campo1.getText());
            try {
                //ip del server
                Socket misocket = new Socket("192.168.56.1", 9999);
                PaqueteEnvio datos = new PaqueteEnvio();
                datos.setNick(nick.getText());
                datos.setIp(ip.getSelectedItem().toString());
                datos.setMensaje(campo1.getText());
                ObjectOutputStream paquete_datos = new ObjectOutputStream(misocket.getOutputStream());
                paquete_datos.writeObject(datos);
                misocket.close();
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                System.out.println(e1.getMessage());
            }
        }
    }
    private JTextField campo1;
    private JComboBox ip;
    private JLabel nick;
    private JTextArea campochat;
    private JButton miboton;
}

class PaqueteEnvio implements Serializable {

    private String nick, ip, mensaje;

    public ArrayList<String> getIps() {
        return Ips;
    }

    public void setIps(ArrayList<String> Ips) {
        this.Ips = Ips;
    }
    private ArrayList<String> Ips;

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

}
