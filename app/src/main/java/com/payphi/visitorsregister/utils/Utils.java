package com.payphi.visitorsregister.utils;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.payphi.visitorsregister.BuildConfig;
import com.payphi.visitorsregister.model.Visitor;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gino Osahon on 04/03/2017.
 */
public class Utils extends AppCompatActivity {

    private Context mContext = null;
    static StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();


    /**
     * Public constructor that takes mContext for later use
     */
    public Utils(Context con) {
        mContext = con;
    }

    /**
     * Encode user email to use it as a Firebase key (Firebase does not allow "." in the key name)
     * Encoded email is also used as "userEmail", list and item "owner" value
     */
    public static String encodeEmail(String userEmail) {
        return userEmail.replace(".", ",");
    }

    //This is a method to Check if the device internet connection is currently on
    public  boolean isNetworkAvailable() {

        ConnectivityManager connectivityManager

                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();

    }

    public static void sendSMS(Context context, String phoneNo, String msg) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNo, null, msg, null, null);
            Toast.makeText(context, "Message Sent",
                    Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(context, ex.getMessage().toString(),
                    Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }


    public static void SendByWay2SMS(final String fromMobile, final String password, final String msg, final String tophoneNo, final String apikey) {
        //StrictMode.setThreadPolicy(policy);
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Do network action in this function
                try {
                    URL url = new URL("https://smsapi.engineeringtgr.com/send/?Mobile=" + fromMobile + "&Password=" + password + "&Message=" + msg + "&To=" + tophoneNo + "&Key=" + apikey);
                    URLConnection urlcon = url.openConnection();
                    InputStream stream = urlcon.getInputStream();
                    int i;
                    String response = "";
                    while ((i = stream.read()) != -1) {
                        response += (char) i;
                    }
                    if (response.contains("success")) {
                        System.out.println("Successfully send SMS");
                        //your code when message send success
                    } else {
                        System.out.println(response);
                        //your code when message not send
                    }
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }).start();
    }

    public static void SendEmail(Context context, String recipientEmail, String subject, String body) {
        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setType("text/plain");
           /* i.putExtra(Intent.EXTRA_EMAIL  , new String[]{recipientEmail});
            i.putExtra(Intent.EXTRA_SUBJECT, subject);
            i.putExtra(Intent.EXTRA_TEXT   ,body);*/

        i.setData(Uri.parse("mailto:" + recipientEmail));
        i.putExtra(Intent.EXTRA_SUBJECT, "My email's subject");
        i.putExtra(Intent.EXTRA_TEXT, "My email's body");
        try {
            context.startActivity(Intent.createChooser(i, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(context, "There are no email clients installed.", Toast.LENGTH_SHORT).show();
        }

    }
    public static void DownloadVisitorList(Context context, List<Visitor> visitorsLists) {
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet firstSheet = workbook.createSheet(String.valueOf(onlydate.format(date)));

        int rowNum = 1;
        int j = 0;
     /*   HashMap<Integer,QueueModel> map = new HashMap<>();

       for(QueueModel queueModel : queuesLists){
           map.put(j,queueModel);
       }

        HSSFRow rowB = firstSheet.createRow(1);
        HSSFRow rowC =  firstSheet.createRow(2);
        HSSFRow rowD = firstSheet.createRow(3);
        Set <Integer> keyset = map.keySet();
        for(int key : keyset){
            HSSFRow rowA =  firstSheet.createRow(rowNum);
            QueueModel[] queueModels =map.get(key);
        }*/

        HSSFRow headingRow = firstSheet.createRow(0);

        HSSFCell head1 = headingRow.createCell(0);
        head1.setCellValue("Name");

        HSSFCell head2 = headingRow.createCell(1);
        head2.setCellValue("Mobile No");

        HSSFCell head3 = headingRow.createCell(2);
        head3.setCellValue("In Time");

        HSSFCell head4 = headingRow.createCell(3);
        head4.setCellValue("Out Time");

        HSSFCell head5 = headingRow.createCell(4);
        head5.setCellValue("Flat No");

        HSSFCell head6 = headingRow.createCell(5);
        head6.setCellValue("Visitor Number");

        HSSFCell head7 = headingRow.createCell(6);
        head7.setCellValue("Meeting");

        HSSFCell head8 = headingRow.createCell(7);
        head8.setCellValue("Purpose");

        HSSFCell head9 = headingRow.createCell(8);
        head9.setCellValue("Status");

        HSSFCell head10 = headingRow.createCell(9);
        head10.setCellValue("Stay Time");

        for (Visitor visitorModel : visitorsLists) {
            HSSFRow rowA = firstSheet.createRow(rowNum);

            HSSFCell cellA = rowA.createCell(0);
            String name=visitorModel.getVistorName();
            cellA.setCellValue(name);

            HSSFCell cellB = rowA.createCell(1);
            String mobile=visitorModel.getVistorMobile();
            cellB.setCellValue(mobile);

            HSSFCell cellC = rowA.createCell(2);
            String date1=visitorModel.getVistorInTime();
            cellC.setCellValue(date1);

            HSSFCell cellD = rowA.createCell(3);
            String slot=visitorModel.getVistorOutTime();
            cellD.setCellValue(slot);

            HSSFCell cellE = rowA.createCell(4);
            String source=visitorModel.getFlatNo();
            cellE.setCellValue(source);

            HSSFCell cellF = rowA.createCell(5);
            String verify=visitorModel.getVisitNumber();
            cellF.setCellValue(verify);

            HSSFCell cellG = rowA.createCell(6);
            String stat=visitorModel.getWhomTomeet();
            cellG.setCellValue(stat);


            HSSFCell cellH = rowA.createCell(7);
            String checouttime=visitorModel.getVisitPurpose();
            cellH.setCellValue(checouttime);

            HSSFCell cellI = rowA.createCell(8);
            String token=visitorModel.getVisitorApprove();
            cellI.setCellValue(token);

            HSSFCell cellJ = rowA.createCell(9);
            String totalTime=visitorModel.getTotalVisitorStay();
            cellJ.setCellValue(totalTime);


            rowNum =rowNum+1;
        }








        FileOutputStream fos = null;
        String str_path="";
        File file=null ;
        try {
            str_path = context.getExternalFilesDir(null).getAbsolutePath();

            file = new File(str_path, String.valueOf(onlydate.format(date)) + ".xls");
            fos = new FileOutputStream(file);
            workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            Toast.makeText(context, "Excel Sheet Generated", Toast.LENGTH_SHORT).show();
            openXLS(file,context);
        }

    }
    private static void openXLS( File file, Context context) {

        Uri uri;
        try {
            uri = Uri.fromFile(file);
            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //  uri = FileProvider.getUriForFile(context, context.getPackageName() , file);
                uri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider", file);
                //uri = Uri.fromFile(file);
            } else {
                uri = Uri.fromFile(file);
            }
*/
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setDataAndType(uri, "application/vnd.ms-excel");
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(context, "Application not found", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}
