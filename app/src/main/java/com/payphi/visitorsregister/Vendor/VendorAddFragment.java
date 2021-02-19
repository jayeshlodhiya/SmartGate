package com.payphi.visitorsregister.Vendor;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.payphi.visitorsregister.FaceRecognition.FileUtils;
import com.payphi.visitorsregister.FaceRecognition.RecognizeFace;
import com.payphi.visitorsregister.GPSTracker;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.User;
import com.tzutalin.dlib.Constants;
import com.tzutalin.dlib.FaceRec;
import com.tzutalin.dlib.VisionDetRet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link VendorAddFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link VendorAddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class VendorAddFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    EditText name,addr,aadhaarNumber,mobileno,flatno,work;
    String pic,aadharpic;
    Button register;
    int BITMAP_QUALITY = 100;
    int MAX_IMAGE_SIZE = 500;
    private OnFragmentInteractionListener mListener;
    private String venMobile,venFlatNo,venAadhaarNo;
    private String  venName;
    private String venAddr;
    private String venWork;
    String imageEncoded="";
    private static final String PREF_NAME = "sosessionPref";
    int PRIVATE_MODE = 0;
    SharedPreferences sharedPreferences;
    public ProgressDialog paydialog; // this = YourActivity
    User user;
    String securitycode = "";
    private String vmStatus="";
   ImageButton  picButton;
    Bitmap imageBitmap;
    private ImageView vimg;
    Bundle extrasPics,extrasAadhar;
    private Bitmap bitmap;
    private File destination;
    private FaceRec mFaceRec;
    private String imgPath=null;

    public VendorAddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment VendorAddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static VendorAddFragment newInstance(String param1, String param2) {
        VendorAddFragment fragment = new VendorAddFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_vendor_add, container, false);
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        name = (EditText) view.findViewById(R.id.venNameId);
        mobileno = (EditText) view.findViewById(R.id.venmobileId);
        flatno = (EditText) view.findViewById(R.id.venflatId);
        work = (EditText) view.findViewById(R.id.venpurposeId);
        addr =  (EditText) view.findViewById(R.id.venaddressId);
      aadhaarNumber =  (EditText) view.findViewById(R.id.venaddressId);
        vimg = (ImageView) view.findViewById(R.id.vendorImagId);
        picButton =(ImageButton) view.findViewById(R.id.ventakePic);
        register = (Button) view.findViewById(R.id.button_registerId);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RegisterVendor();
            }
        });
        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakePic();
            }
        });
        destination = new File(Constants.getDLibDirectoryPath() + "/temp.jpg");
        GetUserData();
        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

          System.out.println("Inside Fragment"+requestCode);
        //     System.out.println("Inside Fragment resultCode"+resultCode);
        if (requestCode == 20) {
            try {


                extrasPics = data.getExtras();
                imageBitmap = (Bitmap) extrasPics.get("data");
                if (imageBitmap != null) {
                   // camerabutton.setVisibility(View.GONE);
                }
                vimg.setImageBitmap(imageBitmap);
                encodeBitmapAndSaveToFirebase(imageBitmap);
                DetectFace();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (requestCode == 3) {

            // System.out.println(data.getExtras().toString());
            // Toast.makeText(getContext(),String.valueOf(resultCode),Toast.LENGTH_LONG).show();
        }


        // In fragment class callback
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        pic = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        System.out.println("Image string==" + imageEncoded);

    }

    private void TakePic() {




        String file = venName + ".jpg";
        File newfile = new File(file);
        try {
            newfile.createNewFile();
        } catch (IOException e) {
        }

        Uri outputFileUri = Uri.fromFile(newfile);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(cameraIntent, 20);
        }

    }



    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void RegisterVendor() {
        if (name.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter  name", Toast.LENGTH_LONG).show();
            return;
        } else if (mobileno.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Mobile Number", Toast.LENGTH_LONG).show();
            return;
        } else if (mobileno.getText().toString().length() != 10) {
            Toast.makeText(getContext(), "Enter valid Mobile Number", Toast.LENGTH_LONG).show();
            return;
        } else if (flatno.getText().toString().equals("")) {

            Toast.makeText(getContext(), "Enter Flat Number", Toast.LENGTH_LONG).show();
            return;
        } else if (work.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter work ", Toast.LENGTH_LONG).show();
            return;
        } else if (addr.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_LONG).show();

            return;
        }else if (aadhaarNumber.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Aadhhar Number", Toast.LENGTH_LONG).show();
            return;
        } /*else if (pic == null || pic.equals("")) {
            Toast.makeText(getContext(), "Please take Pic", Toast.LENGTH_LONG).show();
            return;
        }else if (aadharpic == null || aadharpic.equals("")) {
            Toast.makeText(getContext(), "Please take Aadhaar card pic", Toast.LENGTH_LONG).show();
            return;
        }*/ else {
            venName = name.getText().toString();
            venMobile = mobileno.getText().toString();
            venAddr = addr.getText().toString();
            venFlatNo = flatno.getText().toString();
            venWork = work.getText().toString();
            venAadhaarNo = aadhaarNumber.getText().toString();


           /* vName = evname.getText().toString();
            vmobileNo = evmobileNo.getText().toString();
            flatNo = eflatNo.getText().toString();
            vinTime = evinTime.getText().toString();
            whomTomeet = ewhom.getText().toString();
            purpose = epurpose.getText().toString();
            addr = address.getText().toString();
            if (vehicle.getText().toString() != null && !vehicle.getText().toString().equals("")) {
                vehi = vehicle.getText().toString();
            }*/

            Register();
        }
    }

    // TODO: Rename method, update argument and hook method into UI event

    private void Register() {
        Map<String, Object> visitorMap;
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        Random rnd = new Random();
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Vendors").collection("SVendors").document();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        visitorMap = new HashMap<String, Object>();
        String vendorNumber = String.valueOf(100000 + rnd.nextInt(900000));


        GPSTracker gps;
        gps = new GPSTracker(getContext());
        String currentLoction = "";
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            currentLoction = String.valueOf(latitude + "," + longitude);
        }
        visitorMap.put("Name", venName);
        visitorMap.put("Mobile", venMobile);
        visitorMap.put("FlatNo", venFlatNo);
        visitorMap.put("Date", String.valueOf(onlydate.format(date)));
        visitorMap.put("Address", venAddr);
        visitorMap.put("Active", "true");
        visitorMap.put("AadhaarPic", aadharpic);
        visitorMap.put("AadhaarNumber", venAadhaarNo);
        visitorMap.put("Pic", pic);
        visitorMap.put("VistorVerfiedBy", user.getFullName());
        visitorMap.put("MobileVerify", vmStatus);
        visitorMap.put("VistorInsertedBy",user.getFullName());
        visitorMap.put("work", venWork);

        String docId = bookingref.getId();
        visitorMap.put("DocId", docId);

        paydialog.show();

        bookingref.set(visitorMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Registration done!!", Toast.LENGTH_LONG).show();

                ClearData();
               // TakeDeviceIdByFlatNoOrName();
                //GetVisitorFeatures();
                //getImageStringFromVisitNumber(docId);
                paydialog.dismiss();
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

    private void DetectFace() {
        System.out.println("Inside DetectFace....");
        bitmap = (Bitmap) extrasPics.get("data");
        Bitmap scaledBitmap = scaleDown(bitmap, MAX_IMAGE_SIZE, true);
     //   et_image.setText(destination.getAbsolutePath());
        new detectAsync().execute(scaledBitmap);

    }



    public static Bitmap scaleDown(Bitmap realImage, float maxImageSize, boolean filter) {
        float ratio = Math.min(
                (float) maxImageSize / realImage.getWidth(),
                (float) maxImageSize / realImage.getHeight());
        int width = Math.round((float) ratio * realImage.getWidth());
        int height = Math.round((float) ratio * realImage.getHeight());

        Bitmap newBitmap = Bitmap.createScaledBitmap(realImage, width,
                height, filter);
        return newBitmap;
    }

    private class detectAsync extends AsyncTask<Bitmap, Void, String> {
        ProgressDialog dialog = new ProgressDialog(getContext());

        @Override
        protected void onPreExecute() {
            dialog.setMessage("Detecting face...");
            dialog.setCancelable(false);
            dialog.show();
            super.onPreExecute();
        }

        protected String doInBackground(Bitmap... bp) {
            mFaceRec = new FaceRec(Constants.getDLibDirectoryPath());
            List<VisionDetRet> results;
            results = mFaceRec.detect(bp[0]);
            String msg = null;
            if (results.size()==0) {
                msg = "No face was detected or face was too small. Please select a different image";
            } else if (results.size() > 1) {
                msg = "More than one face was detected. Please select a different image";
            } else {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bp[0].compress(Bitmap.CompressFormat.JPEG, BITMAP_QUALITY, bytes);
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
               imgPath = destination.getAbsolutePath();
                String targetPath = Constants.getDLibImageDirectoryPath() + "/" + venName + ".jpg";
                FileUtils.copyFile(imgPath,targetPath);
                Intent i = new Intent(getContext(),RecognizeFace.class);
                startActivity(i);
            }
            return msg;
        }

        protected void onPostExecute(String result) {
            if(dialog != null && dialog.isShowing()){
                dialog.dismiss();
                if (result!=null) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(getContext());
                    builder1.setMessage(result);
                    builder1.setCancelable(true);
                    AlertDialog alert11 = builder1.create();
                    alert11.show();
                    //imgPath = null;
                    //et_image.setText("");
                }
                //enableSubmitIfReady();
            }

        }
    }


    private void ClearData() {
    name.setText("");

    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
}
