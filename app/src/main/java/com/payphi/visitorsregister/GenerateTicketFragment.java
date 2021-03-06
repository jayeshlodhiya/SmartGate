package com.payphi.visitorsregister;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.utils.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GenerateTicketFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GenerateTicketFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GenerateTicketFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String PREF_NAME = "sosessionPref";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Bitmap bmp;
    int PRIVATE_MODE = 0;
    Random rnd = new Random();
    public ProgressDialog paydialog; // this = YourActivity
    SharedPreferences sharedPreferences;
    User user;
    EditText name, mobile, pupose;
    Button button, fromdate, todate;
    String ticketString = "";
    ImageView imageView, share, contactPicker;
    LinearLayout sucheader;
    TextView fromText, toText;
    String socCode;
    String visitorNumber = "";
    String vname = "";
    String vmoileNo = "";
    String vpurpose = "";
    String vaddress = "";
    String vehicleNumber = "";
    String docId = "";
    String uniqueNumber="";
    public static GenerateTicketFragment generateTicketFragment;
    public GenerateTicketFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment GenerateTicketFragment.
     */
    // TODO: Rename and change types and number of parameters
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_generate_ticket, container, false);
        generateTicketFragment =  this;
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        socCode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);

        name = (EditText) view.findViewById(R.id.tNameId);
        mobile = (EditText) view.findViewById(R.id.tmobileId);
        pupose = (EditText) view.findViewById(R.id.tpurposeId);
        button = (Button) view.findViewById(R.id.tbutton_register);
        //imageView =  (ImageView) view.findViewById(R.id.timgId);
        share = (ImageView) view.findViewById(R.id.shareId);
        contactPicker = (ImageView) view.findViewById(R.id.pickcontactId);
        //sucheader = (LinearLayout) view.findViewById(R.id.headermsgId);
        fromdate = (Button) view.findViewById(R.id.frombuttonId);
        todate = (Button) view.findViewById(R.id.tobuttonId);
        fromText = (TextView) view.findViewById(R.id.fromDateId);
        toText = (TextView) view.findViewById(R.id.toDateId);
        //imageView.setVisibility(View.INVISIBLE);
        //share.setVisibility(View.INVISIBLE);

        GetUserData();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateAndShareTicket();
            }
        });
      /*  share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Share();
            }
        });*/
        contactPicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PickContact();
            }
        });
        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createDialogWithoutDateField().show();
            }
        });
        return view;
    }
    public static GenerateTicketFragment getInstance(){
        return generateTicketFragment;
    }

    private void PickContact() {
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, 2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == -1) {
            switch (requestCode) {
                case 2:
                    Cursor cursor = null;
                    try {
                        String conphoneNo = null;
                        String conname = null;

                        Uri uri = data.getData();
                        cursor = getActivity().getContentResolver().query(data.getData(), null, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
                            cursor.moveToFirst();

                            int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                            int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
                            conphoneNo = cursor.getString(phoneIndex);
                            conphoneNo = conphoneNo.replaceAll("\\s", "");
                            conname = cursor.getString(nameIndex);
                            name.setText(conname);
                            mobile.setText(conphoneNo);
                            cursor.close();
                        }
                        // System.out.println("Name and Contact number is"+name+","+phoneNo);
                        //  Log.e("Name and Contact number is",name+","+phoneNo);


                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        } else {
            Log.e("Failed", "Not able to pick contact");
        }
    }

    public void generateQRCode(String url) {

        QRCodeWriter writer = new QRCodeWriter();
        try {
            BitMatrix bitMatrix = writer.encode(url, BarcodeFormat.QR_CODE, 512, 512);
            int width = bitMatrix.getWidth();
            int height = bitMatrix.getHeight();
            bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    bmp.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }
            //((ImageView) findViewById(R.id.qrImage)).setImageBitmap(bmp);
            paydialog.dismiss();
            CreateDailog();
            //    sucheader.setVisibility(View.VISIBLE);

            paydialog.dismiss();

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    public void CreateDailog() {
     final   String msgBody= uniqueNumber + " is your verification code from Smart Gate";
        try {
            TextView uniqnumber;
            ImageView msg;
            LayoutInflater factory = LayoutInflater.from(getContext());
            final View deleteDialogView = factory.inflate(R.layout.visitorpass, null);
            final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
            deleteDialog.setView(deleteDialogView);
            imageView = (ImageView) deleteDialogView.findViewById(R.id.timgId);
            imageView.setImageBitmap(bmp);
            msg = (ImageView) deleteDialogView.findViewById(R.id.msgCodeId);
            msg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Utils.sendSMS(getContext(), visitorNumber, msgBody);
                }
            });
            uniqnumber = (TextView) deleteDialogView.findViewById(R.id.uniqueId);
            uniqnumber.setText(uniqueNumber);


            deleteDialogView.findViewById(R.id.shareId).setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //your business logic
                    Share();
                }
            });


            deleteDialog.show();
        }catch (Exception e){

        }
    }

    public void openMonthSelector(View view) {
        createDialogWithoutDateField().show();
    }


    private DatePickerDialog createDialogWithoutDateField() {
        DatePickerDialog dpd = new DatePickerDialog(getContext(), null, 2014, 1, 24);
        try {
            java.lang.reflect.Field[] datePickerDialogFields = dpd.getClass().getDeclaredFields();
            for (java.lang.reflect.Field datePickerDialogField : datePickerDialogFields) {
                if (datePickerDialogField.getName().equals("mDatePicker")) {
                    datePickerDialogField.setAccessible(true);
                    DatePicker datePicker = (DatePicker) datePickerDialogField.get(dpd);
                    java.lang.reflect.Field[] datePickerFields = datePickerDialogField.getType().getDeclaredFields();
                    for (java.lang.reflect.Field datePickerField : datePickerFields) {
                        Log.i("test", datePickerField.getName());
                        fromText.setText(datePickerField.getName());
                        if ("mDaySpinner".equals(datePickerField.getName())) {
                            datePickerField.setAccessible(true);
                            Object dayPicker = datePickerField.get(datePicker);
                            ((View) dayPicker).setVisibility(View.GONE);
                        }
                    }
                }
            }
        } catch (Exception ex) {
        }
        return dpd;
    }

    private void Share() {

        String shareBody = "Please Show this QR code at Security Gate generated by " + user.getFullName() + " ,valid for 1 day only";
       /* Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("image*//*");
        sharingIntent.putExtra(Intent.EXTRA_STREAM,bmp);
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        getActivity().startActivity(Intent.createChooser(sharingIntent, "Share via"));*/


        try {
            File file = new File(getActivity().getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
            getActivity().startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void CreateAndShareTicket() {

        vname = name.getText().toString();
        vmoileNo = mobile.getText().toString();
        vpurpose = pupose.getText().toString();
        if (vname == null || vname.equals("")) {
            Toast.makeText(getContext(), "Enter or Select Visitor's Name!", Toast.LENGTH_LONG).show();
            return;
        } else if (vmoileNo == null || vmoileNo.equals("")) {
            Toast.makeText(getContext(), "Enter or Select Visitor's Mobile Number", Toast.LENGTH_LONG).show();
            return;
        } else if (vpurpose == null || vpurpose.equals("")) {
            Toast.makeText(getContext(), "Enter Visitor's Purpose", Toast.LENGTH_LONG).show();
            return;
        } else {
            // ticketString = vname + "," + vmoileNo + "," + user.getFlatNo() + "," + vpurpose + "," + user.getFullName();
            //   imageView.setVisibility(View.VISIBLE);
            //  share.setVisibility(View.VISIBLE);
            // paydialog.show();

            Register();

            //  System.out.println("ticketString==" + ticketString);
        }
    }


    public void Register() {
        Map<String, Object> visitorMap;
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(socCode).document("Visitors").collection("SVisitors").document();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        visitorMap = new HashMap<String, Object>();
        visitorNumber = String.valueOf(100000 + rnd.nextInt(900000));
        visitorNumber = visitorNumber + "-" + user.getFlatNo()+"-"+name.getText();

        GPSTracker gps;
        gps = new GPSTracker(getContext());
        String currentLoction = "";
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            currentLoction = String.valueOf(latitude + "," + longitude);
        }
        visitorMap.put("VistorName", vname);
        visitorMap.put("VistorMobile", vmoileNo);
        visitorMap.put("FlatNumber", user.getFlatNo());
        visitorMap.put("VistorInTime", "");
        visitorMap.put("Date", String.valueOf(onlydate.format(date)));
        visitorMap.put("VistorOutTime", "");
        visitorMap.put("VistorSign", "");
        visitorMap.put("VistorAdrr", vaddress);
        visitorMap.put("VistorVehicleNo", vehicleNumber);
        visitorMap.put("VistorPic", "");
        visitorMap.put("VistorVerfiedBy", "");
        visitorMap.put("MobileVerify", "");
        visitorMap.put("VistorInsertedBy", sharedPreferences.getString("NAME", null));
        visitorMap.put("WhomTomeet", user.getFullName());
        visitorMap.put("Purpose", vpurpose);
        visitorMap.put("VisitNumber", visitorNumber);
        visitorMap.put("VisitorLocation", currentLoction);
        visitorMap.put("Features", "");
        visitorMap.put("VisitorExpected", "Y");
        visitorMap.put("VisitorApprove", "Approved");
        visitorMap.put("VisitorEntryMethod", "QR");
        visitorMap.put("timestamp", FieldValue.serverTimestamp());
        uniqueNumber = String.valueOf(100000 + rnd.nextInt(900000));
        visitorMap.put("UniqueNumber",uniqueNumber);
        docId = bookingref.getId();
        visitorMap.put("DocId", docId);

        paydialog.show();

        bookingref.set(visitorMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Registration done!!", Toast.LENGTH_LONG).show();
                //    ClearData();
                //   TakeDeviceIdByFlatNoOrName();
                //  GetVisitorFeatures();
                //getImageStringFromVisitNumber(docId);

                paydialog.dismiss();
                visitorNumber = visitorNumber+"-"+docId;
                generateQRCode(visitorNumber);
                ClearFields();
                //LocalNotification();
                // SendNotificationToShop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Error Occured.!", Toast.LENGTH_LONG).show();
                paydialog.dismiss();

            }
        });

    }

    private void ClearFields() {
        name.setText("");
        mobile.setText("");
        pupose.setText("");

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        // mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public static GenerateTicketFragment newInstance(String param1, String param2) {
        GenerateTicketFragment fragment = new GenerateTicketFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }
}
