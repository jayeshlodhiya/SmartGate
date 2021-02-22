
package com.payphi.visitorsregister.utils;

import android.os.AsyncTask;

import com.payphi.visitorsregister.RegisterFragment;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import java.security.Security;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;


import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sql.DataSource;





public class GMailSender extends javax.mail.Authenticator {
    private String mailhost = "smtp.gmail.com";
    private   final String user="jglodhiya@gmail.com";
    private String password="mobile21";
    private Session session;

    static {
        Security.addProvider(new JSSEProvider());
    }

    public GMailSender() {
       // user = user;


        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        session =  Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(user, password);
                    }
                });
    }

    protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients)  {
       /* try{
            MimeMessage message = new MimeMessage(session);
            //DataHandler handler = new DataHandler((javax.activation.DataSource) new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
          //  message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }*/
        SendMailAsync myAsyncTask = (SendMailAsync) new SendMailAsync().execute(body);
    }

    public class ByteArrayDataSource implements DataSource {
        private byte[] data;
        private String type;

        public ByteArrayDataSource(byte[] data, String type) {
            super();
            this.data = data;
            this.type = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            this.data = data;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getContentType() {
            if (type == null)
                return "application/octet-stream";
            else
                return type;
        }

        public InputStream getInputStream() throws IOException {
            return new ByteArrayInputStream(data);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }

        @Override
        public Connection getConnection() throws SQLException {
            return null;
        }

        @Override
        public Connection getConnection(String s, String s1) throws SQLException {
            return null;
        }

        @Override
        public PrintWriter getLogWriter() throws SQLException {
            return null;
        }

        @Override
        public void setLogWriter(PrintWriter printWriter) throws SQLException {

        }

        @Override
        public void setLoginTimeout(int i) throws SQLException {

        }

        @Override
        public int getLoginTimeout() throws SQLException {
            return 0;
        }

        @Override
        public Logger getParentLogger() throws SQLFeatureNotSupportedException {
            return null;
        }

        @Override
        public <T> T unwrap(Class<T> aClass) throws SQLException {
            return null;
        }

        @Override
        public boolean isWrapperFor(Class<?> aClass) throws SQLException {
            return false;
        }
    }
    class SendMailAsync extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String body = strings[0];
            //sendMail("Exception",body,,"jglodhiya@gmail.com");
            try{
                MimeMessage message = new MimeMessage(session);
                //DataHandler handler = new DataHandler()
                message.setSender(new InternetAddress("jglodhiya@gmail.com"));
                message.setSubject("Exception");
                message.setText(body);
                  //message.setDataHandler(handler);

                message.setRecipient(Message.RecipientType.TO, new InternetAddress("jglodhiya@gmail.com"));
                Transport.send(message);
            }catch(Exception e){
                e.printStackTrace();
            }
            return null;
        }
        public synchronized void sendMail(String subject, String body, String sender, String recipients)  {

        }
    }

}

