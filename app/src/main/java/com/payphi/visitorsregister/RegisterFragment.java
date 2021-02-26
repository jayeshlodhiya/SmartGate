package com.payphi.visitorsregister;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.client.Firebase;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.gson.Gson;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.microsoft.projectoxford.face.contract.Accessory;
import com.microsoft.projectoxford.face.contract.Emotion;
import com.microsoft.projectoxford.face.contract.FacialHair;
import com.microsoft.projectoxford.face.contract.Hair;
import com.microsoft.projectoxford.face.contract.HeadPose;
import com.microsoft.projectoxford.face.contract.Makeup;
import com.payphi.visitorsregister.FaceRecognition.AwsFaceDetection;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.model.Visitor;
import com.payphi.visitorsregister.utils.FragDetectionTaskActivity;
import com.payphi.visitorsregister.utils.GMailSender;
import com.payphi.visitorsregister.utils.Utils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RegisterFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {

    /* public class FragDetectionTask extends AsyncTask<InputStream, String, Face[]> {
         private boolean mSucceed = true;

         @Override
         protected Face[] doInBackground(InputStream... params) {
             // Get an instance of face service client to detect faces in image.
             // FaceServiceClient faceServiceClient = SampleApp.getFaceServiceClient();
             FaceServiceClient faceServiceClient = new FaceServiceRestClient(getString(R.string.end_point), getString(R.string.subscription_key));
             try {
                 publishProgress("Detecting...");

                 // Start detection.
                 return faceServiceClient.detect(
                         params[0],  *//* Input stream of image to detect *//*
                        true,       *//* Whether to return face ID *//*
                        true,       *//* Whether to return face landmarks *//*
                        *//* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair *//*
                        new FaceServiceClient.FaceAttributeType[]{
                                FaceServiceClient.FaceAttributeType.Age,
                                FaceServiceClient.FaceAttributeType.Gender,
                                FaceServiceClient.FaceAttributeType.Smile,
                                FaceServiceClient.FaceAttributeType.Glasses,
                                FaceServiceClient.FaceAttributeType.FacialHair,
                                FaceServiceClient.FaceAttributeType.Emotion,
                                FaceServiceClient.FaceAttributeType.HeadPose,
                                FaceServiceClient.FaceAttributeType.Accessories,
                                FaceServiceClient.FaceAttributeType.Blur,
                                FaceServiceClient.FaceAttributeType.Exposure,
                                FaceServiceClient.FaceAttributeType.Hair,
                                FaceServiceClient.FaceAttributeType.Makeup,
                                FaceServiceClient.FaceAttributeType.Noise,
                                FaceServiceClient.FaceAttributeType.Occlusion
                        });
            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                //addLog(e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            //   mProgressDialog.show();
            //  addLog("Request: Detecting in image " + mImageUri);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            // mProgressDialog.setMessage(progress[0]);
            //setInfo(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            List<Face> faces = new ArrayList<>();
            int position = 0;
            faces = Arrays.asList(result);
            if (mSucceed) {
                //addLog("Response: Success. Detected " + (result == null ? 0 : result.length) + " face(s) in " + mImageUri);
                String face_description = String.format("Age: %s  Gender: %s\nHair: %s  FacialHair: %s\nMakeup: %s  %s\nForeheadOccluded: %s  Blur: %s\nEyeOccluded: %s  %s\n" + "MouthOccluded: %s  Noise: %s\nGlassesType: %s\nHeadPose: %s\nAccessories: %s",
                        faces.get(position).faceAttributes.age,
                        faces.get(position).faceAttributes.gender,
                        getHair(faces.get(position).faceAttributes.hair),
                        getFacialHair(faces.get(position).faceAttributes.facialHair),
                        getMakeup((faces.get(position)).faceAttributes.makeup),
                        getEmotion(faces.get(position).faceAttributes.emotion),
                        faces.get(position).faceAttributes.occlusion.foreheadOccluded,
                        faces.get(position).faceAttributes.blur.blurLevel,
                        faces.get(position).faceAttributes.occlusion.eyeOccluded,
                        faces.get(position).faceAttributes.exposure.exposureLevel,
                        faces.get(position).faceAttributes.occlusion.mouthOccluded,
                        faces.get(position).faceAttributes.noise.noiseLevel,
                        faces.get(position).faceAttributes.glasses,
                        getHeadPose(faces.get(position).faceAttributes.headPose),
                        getAccessories(faces.get(position).faceAttributes.accessories)

                );
                System.out.println("face_description in fragment="+face_description);

                // Show the result on screen when detection is done.
                //   setUiAfterDetection(result, mSucceed);


            }
        }
    }*/
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public final static String AUTH_KEY_FCM = "AIzaSyB28LSaN0BW-iUbQw27ek0fHb4g2yLlaAs";
    public final static String API_URL_FCM = "https://fcm.googleapis.com/fcm/send";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String deviceId;
    private String vName;
    String visitorNumber = "";
    private String vmobileNo;
    private String flatNo;
    private String vinTime;
    private String verifiedBy = " ";
    private String insertedBy;
    private String whomTomeet;
    private String addr = "";
    ImageView openMembers;
    private String vehi = "";
    private String purpose;
    ImageView vimg;
    ListenerRegistration registration;
    TextView verifyNumber;
    ListView listView;
     AlertDialog waitingvisitorDialog,approveAlertDialg,rejectAlertDailog;
    Dialog dialog;
    String docId = "";
    String imageEncoded;
    private String outTime;
    Random rnd = new Random();
    int PRIVATE_MODE = 0;
    String securitycode = "";
    public ProgressDialog paydialog; // this = YourActivity
    EditText evname, evmobileNo, eflatNo, evinTime, eoutTime, epurpose, ewhom, address, vehicle;
    Button registerbutton, signButton;
    ImageButton camerabutton;
    ImageView verfyIcon;
    String selectedVisitorCatagory="";
    String vmStatus = "";
    public static RegisterFragment registerFragment;
    private static final String PREF_NAME = "sosessionPref";
    private OnFragmentInteractionListener mListener;
    String otp = "";
    SharedPreferences sharedPreferences;
    User user;
    TextView insT;
    Bitmap imageBitmap;
    String sound;
    MaterialSpinner spinner;
    ArrayList visitorTypeList = new ArrayList();
    public static boolean approveFlag;
    public static boolean rejectFlag;
    ArrayList<String> flatMemberList = new ArrayList<>();
    FragDetectionTaskActivity fragDetectionTaskActivity = new FragDetectionTaskActivity();
    AwsFaceDetection awsFaceDetection = new AwsFaceDetection();
    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegisterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
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
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread paramThread, Throwable paramThrowable) {
                //Catch your exception
                // Without System.exit() this will not work.
                // System.exit(2);

                //     Utils.SendEmail(getApplicationContext(),"jglodhiya@gmail.com","Exception",paramThrowable.getMessage());
                GMailSender gMailSender =  new GMailSender();
                gMailSender.sendMail("Exception",this.getClass().getName()+":"+paramThrowable.getMessage(),"jglodhiya@gmail.com","jglodhiya@gmail.com");
            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        waitingvisitorDialog = new AlertDialog.Builder(getContext()).create();
        approveAlertDialg = new AlertDialog.Builder(getContext()).create();
        rejectAlertDailog = new AlertDialog.Builder(getContext()).create();
        registerFragment = this;
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        evname = (EditText) view.findViewById(R.id.vNameId);
      /*  verifyNumber = (TextView) view.findViewById(R.id.verifyId);*/
        evmobileNo = (EditText) view.findViewById(R.id.mobileId);
        eflatNo = (EditText) view.findViewById(R.id.flatId);
        evinTime = (EditText) view.findViewById(R.id.inTimeId);
        epurpose = (EditText) view.findViewById(R.id.purposeId);
        ewhom = (EditText) view.findViewById(R.id.whomId);
        address = (EditText) view.findViewById(R.id.addressId);
        vehicle = (EditText) view.findViewById(R.id.vehicalId);
        openMembers = (ImageView) view.findViewById(R.id.openMemberId);
        spinner = (MaterialSpinner)  view.findViewById(R.id.spinner);
        insT = (TextView) view.findViewById(R.id.instId);
        spinner.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //Snackbar.make(view, "Clicked " + item, Snackbar.LENGTH_LONG).show();
                //Toast.makeText(getApplicationContext(),"Clicked " + item,Toast.LENGTH_LONG).show();
                selectedVisitorCatagory = item;
            }
        });

      /*  verfyIcon = (ImageView) view.findViewById(R.id.verifyiconId);
        verfyIcon.setVisibility(View.INVISIBLE);*/

        registerFragment = this;
        //  eoutTime= (EditText) view.findViewById(R.id.outTimeId);

        registerbutton = (Button) view.findViewById(R.id.button_register);
        camerabutton = (ImageButton) view.findViewById(R.id.takePic);
        signButton = (Button) view.findViewById(R.id.button_sign);
        vimg = (ImageView) view.findViewById(R.id.imgId);
        visitorTypeList = new ArrayList();
        visitorTypeList.add("Select Visitor Catagory");
        visitorTypeList.add("Family Visit");
        visitorTypeList.add("Friend Visit");
        visitorTypeList.add("Delivery Visit");
        visitorTypeList.add("Business Visit");
        visitorTypeList.add("Other");
        selectedVisitorCatagory = "Select Visitor Catagory";
        spinner.setItems(visitorTypeList);
        spinner.setSelectedIndex(0);
        vmStatus = "N";
        GetUserData();

        eflatNo.setFocusable(true);

        openMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OpenMemberIntent();
            }
        });

        eflatNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    // Toast.makeText(getContext(), "on focus", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getContext(), "lost focus", Toast.LENGTH_LONG).show();
                    //GetAllMemberList();
                }
            }
        });

        registerbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ValidateAndRegister();
            }
        });

        camerabutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //       count++;
                vName = evname.getText().toString();
                vmobileNo = evmobileNo.getText().toString();
                flatNo = eflatNo.getText().toString();
                String file = vName + flatNo + ".jpg";
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
                //   cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                //  startActivityForResult(cameraIntent, 20);
            }
        });

     /*   vimg.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //       count++;
                vName= evname.getText().toString();
                vmobileNo= evmobileNo.getText().toString();
                flatNo= eflatNo.getText().toString();
                String file = vName+flatNo+".jpg";
                File newfile = new File(file);
                try {
                    newfile.createNewFile();
                }
                catch (IOException e)
                {
                }

                Uri outputFileUri = Uri.fromFile(newfile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (cameraIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                    startActivityForResult(cameraIntent, 20);
                }
                //   cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);

                //  startActivityForResult(cameraIntent, 20);
            }
        });*/
       /* verifyNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                otp = String.valueOf(100000 + rnd.nextInt(900000));

                if (evmobileNo.getText().toString().equals("")) {
                    Toast.makeText(getContext(), "Enter Mobile Number", Toast.LENGTH_LONG).show();
                } else if (evmobileNo.getText().toString().length() < 10) {
                    Toast.makeText(getContext(), "Enter valid Mobile Number", Toast.LENGTH_LONG).show();
                } else {
                    OpenVerficationOptions();

                }

            }
        });*/

        return view;
    }

    private void OpenMemberIntent() {
        Intent intent = new Intent(getContext(), MemberListActivity.class);
        startActivityForResult(intent,30);
    }


    private void GetAllMemberList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.list_item_chat, R.id.tv_user_name, flatMemberList);
        dialog = new Dialog(getContext());
        // dialog.requestWindowFeature(set);
        // dialog.setTitle("Select Time");
        dialog.setContentView(R.layout.memberlist);
        dialog.setCanceledOnTouchOutside(false);
        listView = (ListView) dialog.findViewById(R.id.flatmemberListid);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // ListView Clicked item index
                int itemPosition = position;
                // ListView Clicked item value
                String itemValue = (String) listView.getItemAtPosition(position);
                // Show Alert
             /*   Toast.makeText(getContext(),
                        "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                        .show();*/
                ewhom.setText(itemValue);

                //txtTime.setText(itemValue);
                dialog.dismiss();
            }
        });


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        flatNo = eflatNo.getText().toString();
        if (flatNo != null && !flatNo.equals("")) {
            CollectionReference docRef = db.collection(securitycode).document("UserDoc").collection("Soflats").document(flatNo).collection("Members");

            docRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
                //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                    int size = documentSnapshots.getDocuments().size();

                    System.out.println("Documents notification size=" + size);
                    if (size > 0) {
                        flatMemberList.removeAll(flatMemberList);
                        dialog.show();
                    }

                    for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                        //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                        CreateMemberList(documentSnapshot);
            }
                }
            });

        }
    }

    private void CreateMemberList(DocumentSnapshot documentSnapshot) {
        flatMemberList.add(String.valueOf(documentSnapshot.get("Name")));

    }

    private void OpenVerficationOptions() {
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View deleteDialogView = factory.inflate(R.layout.otp_verification_layout, null);
        final AlertDialog deleteDialog = new AlertDialog.Builder(getContext()).create();
        deleteDialog.setView(deleteDialogView);
        final RelativeLayout optionlayout = (RelativeLayout) deleteDialogView.findViewById(R.id.relativeLayout2);
        final Button vbutton = (Button) deleteDialogView.findViewById(R.id.vButtonId);
        final EditText vEditText = (EditText) deleteDialogView.findViewById(R.id.otpId);
        optionlayout.setVisibility(View.GONE);
        RadioGroup radioGroup = (RadioGroup) deleteDialogView.findViewById(R.id.radioGrpid);
        radioGroup.clearCheck();
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton rb = (RadioButton) group.findViewById(checkedId);

                if (null != rb && checkedId > -1) {
                    //Toast.makeText(getContext(), rb.getTag().toString(), Toast.LENGTH_SHORT).show();
                    String tag = rb.getTag().toString();

                    if (tag.equals("sms")) {
                        optionlayout.setVisibility(View.VISIBLE);

                        String number = evmobileNo.getText().toString();
                        String msg = otp + " is your verification code";
                        String apikey = "jglodzBxA0TvXjH42Eo9F36sLcP";
                        String fromMobile = "7350348860";
                        String password = "way2sms";
                        //       Utils.sendSMS(getContext(), number, msg);
                        Utils.SendByWay2SMS(fromMobile, password, msg, number, apikey);
                    } else if (tag.equals("phone")) {
                        String number = evmobileNo.getText().toString();
                        CallVisitor(number);
                    }
                }


            }

            ;
        });

        vbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vEditText.getText().toString() != null && !vEditText.getText().toString().equals("")) {
                    String enterotp = vEditText.getText().toString();
                    if (!enterotp.equals(otp)) {
                        Toast.makeText(getContext(), "Otp not matched!!", Toast.LENGTH_LONG).show();
                        return;
                    } else {
                        Toast.makeText(getContext(), "Otp  matched!!", Toast.LENGTH_LONG).show();
                       // verifyNumber.setVisibility(View.INVISIBLE);
                      //  verfyIcon.setVisibility(View.VISIBLE);
                        vmStatus = "V";
                        deleteDialog.dismiss();
                    }
                }
            }
        });
       /* deleteDialogView.findViewById(R.id.shareId).setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //your business logic
//                Share();
            }
        });*/


        deleteDialog.show();

    }

    public void setFacialData(String face_description) {
        System.out.println("face_description in fragment=" + face_description);
        // String features = getFeatures(face_description);
        UpdateVisitorFeatures(face_description);
    }
    public void setFacialData(HashMap map) {
        System.out.println("face_description in fragment map=" + map.toString());
        // String features = getFeatures(face_description);
        UpdateVisitorFeatures(map);
    }

    private String getFeatures(String face_description) {
        String newFeatures = "";
        //   0             1            2             3                         4                                5                                   6                              7                                  8                                9                                10
        //"Age: 7.0 - Gender: male - Hair: Brown - FacialHair: No - Makeup: No  Happiness: 0.979000-ForeheadOccluded: false  Blur: High-EyeOccluded: false  GoodExposure-MouthOccluded: false  Noise: High-GlassesType: NoGlasses-HeadPose: Pitch: 0.0, Roll: 2.5, Yaw: 0.7 - Accessories: NoAccessories"
        try {
            if (face_description != null) {
                String rawFeatures[] = face_description.split("-");
                System.out.println("rawFeatures size==" + rawFeatures.length);
                String genderarry[] = rawFeatures[1].split(":");
                String stringgender = "";

                if (genderarry[1] != null) {

                    if (genderarry[1].trim().length() > 4) {
                        stringgender = "Female|ladki|aurat|girl|Women";
                    } else {
                        stringgender = "Male|ladka|aadmi|boy|Man";
                    }
                }

                String hairColor = rawFeatures[2];
                String facialHair = rawFeatures[3];
                String glasses = rawFeatures[8];
                String accesories = rawFeatures[10];
                if (hairColor != null) {
                    String hairColoraar[] = hairColor.split(":");
                    if (hairColoraar[1].trim().equalsIgnoreCase("Brown")) {
                        hairColor = "Brown|bhura";
                    } else if (hairColoraar[1].trim().equalsIgnoreCase("White")) {
                        hairColor = "White|safed";
                    } else if (hairColoraar[1].trim().equalsIgnoreCase("black")) {
                        hairColor = "Black|kala";
                    } else if (hairColoraar[1].trim().equalsIgnoreCase("Bald")) {
                        hairColor = "Bald|takla";
                    } else {
                        hairColor = "sir dhaka hua hai|head is cover";
                    }


                    if (facialHair != null) {
                        String facialHairarry[] = facialHair.split(":");
                        if (facialHairarry[1].trim().equalsIgnoreCase("Yes")) {
                            facialHair = "Dhadhi|beared";
                        } else {
                            facialHair = "";
                        }

                    }


                    if (glasses != null) {
                        String glassesarr[] = glasses.split(":");
                        if (glassesarr[1].trim().equalsIgnoreCase("NoGlasses")) {
                            glasses = "";
                        } else {
                            glasses = "chashma|glasses";
                        }
                    }


             /*      if (accesories != null) {
                       String accesoriesarr[] = accesories.split(":");
                       if (accesoriesarr[1].trim().equalsIgnoreCase("NoAccessories")) {
                           accesories = "";
                       } else {
                           accesories = "cap|topi|gamcha|pagdi";
                       }

                   }*/

                }


                newFeatures = stringgender + "," + hairColor + "," + facialHair + "," + glasses; //+ "," + accesories;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newFeatures;
    }

    private void UpdateVisitorFeatures(String face_description) {
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Visitors").collection("SVisitors").document(docId);
        bookingref.update("Features", face_description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("Feautures Updated");
            }
        });
    }
    private void UpdateVisitorFeatures(HashMap face_description) {
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Visitors").collection("SVisitors").document(docId);
        bookingref.update(face_description).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println("Feautures Updated");
            }
        });


    }
    private class PhoneCallListener extends PhoneStateListener {

        private boolean isPhoneCalling = false;
        public boolean wasRinging;
        String LOG_TAG = "LOGGING 123";

        @Override
        public void onCallStateChanged(int state, String incomingNumber) {

            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.i(LOG_TAG, "RINGING");
                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.i(LOG_TAG, "OFFHOOK");

                    if (!wasRinging) {
                        // Start your new activity
                    } else {
                        // Cancel your old activity
                    }

                    // this should be the last piece of code before the break
                    wasRinging = true;
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.i(LOG_TAG, "IDLE");
                    // this should be the last piece of code before the break
                    wasRinging = false;
                    break;
            }
            System.out.println("State=" + wasRinging);
        }


    }

    private void CallVisitor(String vistorMobile) {
        PhoneCallListener phoneListener = new PhoneCallListener();
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(phoneListener,
                PhoneStateListener.LISTEN_CALL_STATE);

        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + vistorMobile));
        if (ActivityCompat.checkSelfPermission(getContext(), android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivityForResult(intent, 3);
    }


    public static RegisterFragment getInstance() {
        return registerFragment;
    }

    public void getMemberData(String flat,String Name){
        try {
           // Toast.makeText(getContext(),flat,Toast.LENGTH_LONG).show();
            eflatNo.setText(flat);
             ewhom.setText(Name);
        }catch (Exception e){

        }

    }
    public void setData(String data) {
        System.out.println("Data fragment.." + data);
        String arr[] = data.split(",");
        evname.setText(arr[0]);
        evmobileNo.setText(arr[1]);
        eflatNo.setText(arr[2]);
        epurpose.setText(arr[3]);
        ewhom.setText(arr[4]);


    }

    private void GetUserData() {
        SharedPreferences.Editor prefsEditor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        //  System.out.println("Inside Fragment"+requestCode);
        //     System.out.println("Inside Fragment resultCode"+resultCode);
        if (requestCode == 20) {
            try {


                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");

                if (imageBitmap != null) {
                    camerabutton.setVisibility(View.GONE);
                }
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // yourMethod();
                    }
                }, 2000);   //5 seconds
                vimg.setImageBitmap(imageBitmap);
                insT.setText("");
                encodeBitmapAndSaveToFirebase(imageBitmap);

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
        imageEncoded = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT);
        System.out.println("Image string==" + imageEncoded);
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public void ValidateAndRegister() {

        if (evname.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Visitor name", Toast.LENGTH_LONG).show();
            return;
        } else if (evmobileNo.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Mobile Number", Toast.LENGTH_LONG).show();
            return;
        } else if (evmobileNo.getText().toString().length() != 10) {
            Toast.makeText(getContext(), "Enter valid Mobile Number", Toast.LENGTH_LONG).show();
            return;
        } else if (eflatNo.getText().toString().equals("")) {
            flatNo = eflatNo.getText().toString();
            Toast.makeText(getContext(), "Enter Flat Number", Toast.LENGTH_LONG).show();
            return;
        }/*else if(evinTime.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter In Time ", Toast.LENGTH_LONG).show();
        }*/ else if (ewhom.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter to whom you want to meet ", Toast.LENGTH_LONG).show();
            return;
        } else if (epurpose.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter purpose ", Toast.LENGTH_LONG).show();
            return;
        } else if (address.getText().toString().equals("")) {
            Toast.makeText(getContext(), "Enter Address", Toast.LENGTH_LONG).show();

            return;
        } else if (imageEncoded == null || imageEncoded.equals("")) {
            Toast.makeText(getContext(), "Please take Pic", Toast.LENGTH_LONG).show();
            return;
        }  else if( selectedVisitorCatagory.equalsIgnoreCase("Select Visitor Catagory")){
            Toast.makeText(getContext(), "Please Select Visitor's Catagory", Toast.LENGTH_LONG).show();
            return;
        }else {
            vName = evname.getText().toString();
            vmobileNo = evmobileNo.getText().toString();
            flatNo = eflatNo.getText().toString();
            vinTime = evinTime.getText().toString();
            whomTomeet = ewhom.getText().toString();
            purpose = epurpose.getText().toString();
            addr = address.getText().toString();
            if (vehicle.getText().toString() != null && !vehicle.getText().toString().equals("")) {
                vehi = vehicle.getText().toString();
            }

            Register();

        }


    }

    public void Register() {
        Map<String, Object> visitorMap;
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);

        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Visitors").collection("SVisitors").document();
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        DateFormat onlydate = new SimpleDateFormat("dd-MM-yyyy");
        Date date = new Date();
        visitorMap = new HashMap<String, Object>();
        visitorNumber = String.valueOf(100000 + rnd.nextInt(900000));
        visitorNumber = visitorNumber + "-" + flatNo;

        GPSTracker gps;
        gps = new GPSTracker(getContext());
        String currentLoction = "";
        if (gps.canGetLocation()) {

            double latitude = gps.getLatitude();
            double longitude = gps.getLongitude();
            currentLoction = String.valueOf(latitude + "," + longitude);
        }

        visitorMap.put("VistorName", vName);
        visitorMap.put("VistorMobile", vmobileNo);
        visitorMap.put("FlatNumber", flatNo);
        visitorMap.put("VistorInTime", "");
        visitorMap.put("Date", String.valueOf(onlydate.format(date)));
        visitorMap.put("VistorOutTime", "");
        visitorMap.put("VistorSign", "");
        visitorMap.put("VistorAdrr", addr);
        visitorMap.put("VistorVehicleNo", vehi);
        visitorMap.put("VistorPic", imageEncoded);
        visitorMap.put("VistorVerfiedBy", verifiedBy);
        visitorMap.put("MobileVerify", vmStatus);
        visitorMap.put("VistorInsertedBy", sharedPreferences.getString("NAME", null));
        visitorMap.put("WhomTomeet", whomTomeet);
        visitorMap.put("Purpose", purpose);
        visitorMap.put("VisitNumber", visitorNumber);
        visitorMap.put("VisitorLocation", currentLoction);
        visitorMap.put("Features", "");
        visitorMap.put("VisitorExpected", "");
        visitorMap.put("VisitorApprove","waiting");
        visitorMap.put("SecurityDeviceId", FirebaseInstanceId.getInstance().getToken());
        visitorMap.put("VisitorEntryMethod", "Reg");
        visitorMap.put("timestamp", FieldValue.serverTimestamp());
        visitorMap.put("Gender","");
        visitorMap.put("Mustache","");
        visitorMap.put("Beard","");
        visitorMap.put("Eyeglass","");
        visitorMap.put("AgeL","");
        visitorMap.put("AgeU","");
        visitorMap.put("UniqueNumber",String.valueOf(100000 + rnd.nextInt(900000)));
        visitorMap.put("VisitorCatagory",selectedVisitorCatagory);
        visitorMap.put("Staytime","");
        docId = bookingref.getId();
        visitorMap.put("DocId", docId);

        paydialog.show();

        bookingref.set(visitorMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(getContext(), "Registration done!!", Toast.LENGTH_LONG).show();
                ClearData();
                TakeDeviceIdByFlatNoOrName();
                ShowWaitingPopup();
                //GetVisitorFeatures();
                visitorTypeList.removeAll(visitorTypeList);
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

    private void UpdateTheUsersNotificationDoc(DocumentSnapshot documentSnapshot) {

        DocumentReference docRef = FirebaseFirestore.getInstance().collection(securitycode).document("UserDoc").collection("Sousers").document(documentSnapshot.getString("DocId")).collection("NotificationUpdateId").document(documentSnapshot.getString("Email"));
        docRef.update("DocId",docId);
    }

    private void ShowWaitingPopup() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference docRef = db.collection(securitycode).document("Visitors").collection("SVisitors").document(docId);



         registration =   docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
                Visitor visitor = new Visitor();
                visitor.setVistorName(documentSnapshot.getString("VistorName"));
                visitor.setVisitorPic(documentSnapshot.getString("VistorPic"));
                visitor.setFlatNo(String.valueOf(documentSnapshot.get("FlatNumber")));
                visitor.setWhomTomeet(documentSnapshot.getString("WhomTomeet"));
                visitor.setVistorMobile(String.valueOf(documentSnapshot.get("VistorMobile")));
                visitor.setVisitNumber(documentSnapshot.getString("VisitNumber"));
                visitor.setDocId(documentSnapshot.getString("DocId"));
                visitor.setVistorInTime(String.valueOf(documentSnapshot.get("VistorInTime")));
                visitor.setVistorOutTime(String.valueOf(documentSnapshot.get("VistorOutTime")));
                visitor.setMobileVeriy(documentSnapshot.getString("MobileVerify"));
                visitor.setVisitorApprove(documentSnapshot.getString("VisitorApprove"));

                /*if(visitor.getVisitorApprove().equalsIgnoreCase("Approved")){

                    if(  waitingvisitorDialog!=null || waitingvisitorDialog.isShowing()  ){
                        waitingvisitorDialog.dismiss();

                    }
                    if(approveAlertDialg.isShowing()){
                        approveAlertDialg.dismiss();
                    }else{
                        CreateApprovePupup(visitor);
                    }
                    if(rejectAlertDailog.isShowing()){
                        rejectAlertDailog.dismiss();
                    }

                }else if(visitor.getVisitorApprove().equalsIgnoreCase("Rejected")){
                    if(waitingvisitorDialog!=null ||  waitingvisitorDialog.isShowing()){
                        waitingvisitorDialog.dismiss();
                    }
                    if(approveAlertDialg.isShowing()){
                        approveAlertDialg.dismiss();
                    }
                    if(rejectAlertDailog.isShowing()){
                        rejectAlertDailog.dismiss();
                    }else{
                        CreateRejectedPopUp(visitor);
                    }

                }else*/ if(visitor.getVisitorApprove().equalsIgnoreCase("waiting")){
                    if(waitingvisitorDialog.isShowing()){
                        waitingvisitorDialog.dismiss();
                    }else{
                        CreteateWaitingPopUp(visitor);
                    }
                    if(approveAlertDialg.isShowing()){
                        approveAlertDialg.dismiss();
                    }
                    if(rejectAlertDailog.isShowing()){
                        rejectAlertDailog.dismiss();
                    }
                    CreteateWaitingPopUp(visitor);

                }else{
                    CreteateWaitingPopUp(visitor);
                }



            }
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {


    });
    }



    private void CreateApprovePupup(Visitor visitor) {
        TextView status ;
        approveAlertDialg = new AlertDialog.Builder(getContext()).create();

        // Toast.makeText(this,visitor.getVistorName()+" is Waiting at gate",Toast.LENGTH_LONG).show();
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View visitorDialogView = factory.inflate(R.layout.visitor_status_layout, null);
        status = (TextView) visitorDialogView.findViewById(R.id.statusId);
        ImageView imageView = (ImageView) visitorDialogView.findViewById(R.id.visitorstatImageId);
        status.setText(visitor.getVisitorApprove());
        imageView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.approvedicon));
        status.setTextColor(getResources().getColor(R.color.green_new));
        approveAlertDialg.setView(visitorDialogView);
            //registration.remove();
        approveAlertDialg.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    //Toast.makeText(getContext(),"pressed a",Toast.LENGTH_LONG).show();
                    if(waitingvisitorDialog.isShowing()){
                        waitingvisitorDialog.dismiss();
                    }
                    if(approveAlertDialg.isShowing()){
                        approveAlertDialg.dismiss();
                    }

                    return false;
                }
                return false;
            }
        });



//        waitingvisitorDialog.setCancelable(false);
        approveAlertDialg.setCanceledOnTouchOutside(false);

















        //inTime.setText(visitor.getVistorInTime());
        //outTime.setText(visitor.getVistorOutTime());

        approveAlertDialg.show();
        approveAlertDialg.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                RefreshFragment();
            }
        });


        registration.remove();
    }
    private void CreateRejectedPopUp(Visitor visitor) {
        TextView status ;
        rejectAlertDailog = new AlertDialog.Builder(getContext()).create();
        // Toast.makeText(this,visitor.getVistorName()+" is Waiting at gate",Toast.LENGTH_LONG).show();
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View visitorDialogView = factory.inflate(R.layout.visitor_status_layout, null);
        status = (TextView) visitorDialogView.findViewById(R.id.statusId);
        ImageView imageView = (ImageView) visitorDialogView.findViewById(R.id.visitorstatImageId);
        status.setText(visitor.getVisitorApprove());
        imageView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.rejecticon));
        status.setTextColor(getResources().getColor(R.color.red_error));
        rejectAlertDailog.setView(visitorDialogView);
        //registration.remove();
        rejectAlertDailog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    if(waitingvisitorDialog.isShowing()){
                        waitingvisitorDialog.dismiss();
                    }
                    if(approveAlertDialg.isShowing()){
                        approveAlertDialg.dismiss();
                    }
                    return false;
                }
                return false;
            }
        });


//        waitingvisitorDialog.setCancelable(false);
        rejectAlertDailog.setCanceledOnTouchOutside(false);

















        //inTime.setText(visitor.getVistorInTime());
        //outTime.setText(visitor.getVistorOutTime());

        rejectAlertDailog.show();
        rejectAlertDailog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                registration.remove();
                RefreshFragment();
            }
        });
        registration.remove();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(registration!=null){
            registration.remove();
        }

        if(HomeDashboard.getInstance()!=null){
            HomeDashboard.getInstance().Refresh();
        }
        if(visitorTypeList.size()>0){
            visitorTypeList.removeAll(visitorTypeList);
        }



        registerFragment = null;
    }

    public void ClearPopUp(){
        if( waitingvisitorDialog.isShowing()){
            waitingvisitorDialog.dismiss();
        }
    }
    AlertDialog alertDialog;
    private void CreteateWaitingPopUp(final Visitor visitor) {
       final TextView status ;

                // Toast.makeText(this,visitor.getVistorName()+" is Waiting at gate",Toast.LENGTH_LONG).show();
        if(alertDialog!=null && alertDialog.isShowing()){

            alertDialog.dismiss();

        }
        alertDialog = getDialogInstance();
        LayoutInflater factory = LayoutInflater.from(getContext());
        final View visitorDialogView = factory.inflate(R.layout.visitor_status_layout, null);
        status = (TextView) visitorDialogView.findViewById(R.id.statusId);
        ImageView imageView = (ImageView) visitorDialogView.findViewById(R.id.visitorstatImageId);

        status.setText(visitor.getVisitorApprove());
        if(visitor.getVisitorApprove().equalsIgnoreCase("Approved")){
            imageView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.approvedicon));
        }else if(visitor.getVisitorApprove().equalsIgnoreCase("Rejected")){
            imageView.setBackgroundDrawable(this.getResources().getDrawable(R.drawable.rejecticon));
        }
       /* getActivity().runOnUiThread(
                new Runnable(){
                    @Override
                    public void run(){
                        // HERE UPDATE GUI
                        status.setText(visitor.getVisitorApprove());
                    }
                }
        );

*/
        alertDialog.setView(visitorDialogView);
//        waitingvisitorDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
















        //inTime.setText(visitor.getVistorInTime());
        //outTime.setText(visitor.getVistorOutTime());

        alertDialog.show();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {

               // registration.remove();
                RefreshFragment();
            }
        });
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK &&
                        event.getAction() == KeyEvent.ACTION_UP &&
                        !event.isCanceled()) {
                    //Toast.makeText(getContext(),"pressed a",Toast.LENGTH_LONG).show();
                   registration.remove();

                    return false;
                }
                return false;
            }
        });



    }

    public void RefreshFragment() {
        getFragmentManager().beginTransaction().detach(this).attach(this).commit();
    }


    public void GetVisitorFeatures() {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
       // fragDetectionTaskActivity.getFaceData(inputStream);
        awsFaceDetection.detectFace(inputStream);



    }

    private void ClearData() {
        if (!evname.getText().toString().equals("")) {
            evname.setText("");
        }
        if (!evmobileNo.getText().toString().equals("")) {
            evmobileNo.setText("");
        }
        if (!eflatNo.getText().toString().equals("")) {
            eflatNo.setText("");
        }
        if (!evinTime.getText().toString().equals("")) {
            evinTime.setText("");
        }

        if (!epurpose.getText().toString().equals("")) {
            epurpose.setText("");
        }
        if (!ewhom.getText().toString().equals("")) {
            ewhom.setText("");
        }
        if (!address.getText().toString().equals("")) {
            address.setText("");
        }
        if (!vehicle.getText().toString().equals("")) {
            vehicle.setText("");
        }
        camerabutton.setVisibility(View.VISIBLE);
        vimg.setVisibility(View.INVISIBLE);
       // verifyNumber.setVisibility(View.VISIBLE);
       // verfyIcon.setVisibility(View.INVISIBLE);

        imageEncoded = "";

    }

    private void TakeDeviceIdByFlatNoOrName() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference docRef = db.collection(securitycode).document("UserDoc").collection("Sousers");

        docRef.whereEqualTo("FlatNumber", flatNo).addSnapshotListener(new EventListener<QuerySnapshot>() {
            //   docRef.addOnCompleteListener(getActivity(),new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
                int size = documentSnapshots.getDocuments().size();

                System.out.println("Documents notification size=" + size);


                for (DocumentSnapshot documentSnapshot : documentSnapshots.getDocuments()) {
                    //System.out.println("Documents data==="+documentSnapshot.getData().toString());
                    SendNotification(documentSnapshot);
                 //   UpdateTheUsersNotificationDoc(documentSnapshot);
                }
            }
        });
        paydialog.dismiss();

    }

    private void SendNotification(DocumentSnapshot snapshot) {
        deviceId = snapshot.getString("DeviceId");
        sound = "android.resource://" + getActivity().getPackageName() + "/raw/bell_ring";
        myAsyncTask myAsyncTask = (RegisterFragment.myAsyncTask) new myAsyncTask().execute(deviceId);
    }

    private class myAsyncTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... pParams) {
            String deviceId = pParams[0];
            //      System.out.println("DeviceId="+deviceId);
            //
            String authKey = AUTH_KEY_FCM; // You FCM AUTH key
            String FMCurl = API_URL_FCM;
            try {
                URL url = new URL(FMCurl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setUseCaches(false);
                conn.setDoInput(true);
                conn.setDoOutput(true);

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Authorization", "key=" + authKey);
                conn.setRequestProperty("Content-Type", "application/json");

                JSONObject json = new JSONObject();
                json.put("to", deviceId.trim());
                JSONObject payload = new JSONObject();
                JSONObject info = new JSONObject();
                info.put("title", "Booking "); // Notification title
                info.put("sound", "default");
                info.put("body", " " + whomTomeet + ", " + vName + " is at security gate for " + purpose); // Notification body
                info.put("docId", docId);
                info.put("sound", sound);
                info.put("Scode", securitycode);
                //payload.put("payloaddata",info);
                json.put("data", info);
                // json.put("notification", info);


                OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(json.toString());
                wr.flush();
                conn.getInputStream();

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
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

    @Override
    public void onResume() {
        super.onResume();
        System.out.println("In Resume");
    }

    private void getFaceData() {

    }

    private String getHair(Hair hair) {
        if (hair.hairColor.length == 0) {
            if (hair.invisible)
                return "Invisible";
            else
                return "Bald";
        } else {
            int maxConfidenceIndex = 0;
            double maxConfidence = 0.0;

            for (int i = 0; i < hair.hairColor.length; ++i) {
                if (hair.hairColor[i].confidence > maxConfidence) {
                    maxConfidence = hair.hairColor[i].confidence;
                    maxConfidenceIndex = i;
                }
            }

            return hair.hairColor[maxConfidenceIndex].color.toString();
        }
    }

    private String getMakeup(Makeup makeup) {
        return (makeup.eyeMakeup || makeup.lipMakeup) ? "Yes" : "No";
    }

    private String getAccessories(Accessory[] accessories) {
        if (accessories.length == 0) {
            return "NoAccessories";
        } else {
            String[] accessoriesList = new String[accessories.length];
            for (int i = 0; i < accessories.length; ++i) {
                accessoriesList[i] = accessories[i].type.toString();
            }

            return TextUtils.join(",", accessoriesList);
        }
    }

    private String getFacialHair(FacialHair facialHair) {
        return (facialHair.moustache + facialHair.beard + facialHair.sideburns > 0) ? "Yes" : "No";
    }

    private String getEmotion(Emotion emotion) {
        String emotionType = "";
        double emotionValue = 0.0;
        if (emotion.anger > emotionValue) {
            emotionValue = emotion.anger;
            emotionType = "Anger";
        }
        if (emotion.contempt > emotionValue) {
            emotionValue = emotion.contempt;
            emotionType = "Contempt";
        }
        if (emotion.disgust > emotionValue) {
            emotionValue = emotion.disgust;
            emotionType = "Disgust";
        }
        if (emotion.fear > emotionValue) {
            emotionValue = emotion.fear;
            emotionType = "Fear";
        }
        if (emotion.happiness > emotionValue) {
            emotionValue = emotion.happiness;
            emotionType = "Happiness";
        }
        if (emotion.neutral > emotionValue) {
            emotionValue = emotion.neutral;
            emotionType = "Neutral";
        }
        if (emotion.sadness > emotionValue) {
            emotionValue = emotion.sadness;
            emotionType = "Sadness";
        }
        if (emotion.surprise > emotionValue) {
            emotionValue = emotion.surprise;
            emotionType = "Surprise";
        }
        return String.format("%s: %f", emotionType, emotionValue);
    }

    private String getHeadPose(HeadPose headPose) {
        return String.format("Pitch: %s, Roll: %s, Yaw: %s", headPose.pitch, headPose.roll, headPose.yaw);
    }
public AlertDialog getDialogInstance(){
        if(waitingvisitorDialog!=null){
            return waitingvisitorDialog;
        }else{
            waitingvisitorDialog = new AlertDialog.Builder(getContext()).create();
        }
        return waitingvisitorDialog;
}

}
