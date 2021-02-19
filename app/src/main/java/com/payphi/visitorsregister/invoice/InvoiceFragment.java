package com.payphi.visitorsregister.invoice;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.payphi.customersdk.Application;
import com.payphi.customersdk.PaymentOptions;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.Society;
import com.payphi.visitorsregister.model.User;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import static com.payphi.customersdk.Utility.generateHMAC;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link InvoiceFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link InvoiceFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InvoiceFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button paybutton;
  static   String orderId;
    Random rnd = new Random();
    Application application = new Application();
    private OnFragmentInteractionListener mListener;
   static String amnt;
    String merchantId;
    Society society;
    static User user;
    SharedPreferences sharedPreferences;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "sosessionPref";
    public static ProgressDialog paydialog; // this = YourActivity
    static String  securitycode;
    public InvoiceFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InvoiceFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static InvoiceFragment newInstance(String param1, String param2) {
        InvoiceFragment fragment = new InvoiceFragment();
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
        View view =inflater.inflate(R.layout.invoice, container, false);
        sharedPreferences = getContext().getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        securitycode = sharedPreferences.getString("SOC_CODE", null);
      //  amnt ="1500";
        paydialog = new ProgressDialog(getContext());
        paydialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        paydialog.setMessage("Please wait..");
        paydialog.setIndeterminate(true);
        paydialog.setCanceledOnTouchOutside(false);
        paybutton = (Button) view.findViewById(R.id.paybuttonid);
        paybutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProceedToPay();
            }
        });

        GetUserData();
        GetSocityInfo();

        return view;
    }
    private void GetUserData() {

        Gson gson = new Gson();
        String json = sharedPreferences.getString("UserObj", "");
        user = gson.fromJson(json, User.class);
    }

    private void GetSocityInfo() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("SocObj", "");
        society = gson.fromJson(json, Society.class);
        System.out.println("Maintance change=="+society.getMainAmnt());
        amnt =society.getMainAmnt();
        merchantId = society.getMid();
    }

    private void ProceedToPay() {


        paydialog.show();
        GetSocityInfo();
        //  merchantId="T_00046";//
        // String   appId="6d0730283013f24e";
        String appId="b3ce546b304c25a2";

        //Production
        //    merchantId = "P_00010";
        //   String appId="d10b2005d5bd09a5";


        //

        application.setEnv(Application.QA);
        application.setMerchantName("Atria Grande", getContext());
        application.setAppInfo(merchantId, appId, getContext(), new Application.IAppInitializationListener() {
            @Override
            public void onSuccess(String status) {
                //Toast.makeText(getContext(),status,Toast.LENGTH_LONG).show();
                paydialog.cancel();

                ChoosePayOption();



            }



            @Override
            public void onFailure(String errorCode) {
                paydialog.cancel();
                // application logic for initialization failure handling
                if (errorCode.equals("201")) {
                    Toast.makeText(getContext(), "Login failed!!" + errorCode, Toast.LENGTH_LONG).show();
                } else if (errorCode.equals("504")) {
                    Toast.makeText(getContext(), "Connection Error!==" + errorCode, Toast.LENGTH_LONG).show();
                    // finish();
                } else if (errorCode.equals("205")) {
                    Toast.makeText(getContext(), "Payments not enabled!", Toast.LENGTH_LONG).show();
                } else if (errorCode.equals("101")) {
                    Toast.makeText(getContext(), "internal Error!", Toast.LENGTH_LONG).show();
                }


            }


        });



    }
    public static void SaveTrans(Bundle bundle){
        System.out.println("Inside SAVEtrans=="+bundle.toString());
        bundle.putString("TransRefNo",orderId);
        bundle.putString("Amount",amnt);
        bundle.putString("FlatNo",user.getFlatNo());
        HashMap map = new HashMap();
        for (String key : bundle.keySet()) {
            map.put(key,bundle.get(key));
        }
        Random rnd = new Random();
        DocumentReference bookingref = FirebaseFirestore.getInstance().collection(securitycode).document("Tranlog").collection("STranlog").document();
        paydialog.show();

        bookingref.set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
              //  Toast.makeText(getContext(), "Registration done!!", Toast.LENGTH_LONG).show();

                // ClearData();
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
                //Toast.makeText(getContext(), "Error Occured.!", Toast.LENGTH_LONG).show();
                paydialog.dismiss();

            }
        });


    }
    private void ChoosePayOption() {
        try {
            orderId = String.valueOf(100000 + rnd.nextInt(900000));
            String scecureToken = getSecureToken();
            //   scecureToken=null;
            System.out.println("MerchantId=" + merchantId);
            System.out.println("SecureToken ==" + scecureToken);
            Intent intent = new Intent(getContext(), PaymentOptions.class);
            intent.putExtra("Amount", amnt);   //200
            intent.putExtra("MerchantID", merchantId);
            intent.putExtra("MerchantTxnNo", orderId);
            intent.putExtra("CurrencyCode", 356);
            intent.putExtra("CustomerEmailID", "jai@phicommerce.com");
            intent.putExtra("SecureToken", scecureToken);
            //intent.putExtra("CustomerID", "Vivek007");
            //intent.putExtra("PaymentType","nb");
            intent.putExtra("vpa", "ramtest@axis");
            startActivityForResult(intent, 1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private String generateHMAC(String message, String secretKey) {
        Mac sha256_HMAC;
        byte[] hashedBytes = null;
        try {
            sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
            sha256_HMAC.init(secret_key);

            hashedBytes = sha256_HMAC.doFinal(message.getBytes());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        //Check here
        return bytesToHex(hashedBytes);
    }
    public static String bytesToHex(byte[] message) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            for (int i = 0; i < message.length; i++) {
                stringBuffer.append(Integer.toString((message[i] & 0xff) + 0x100, 16).substring(1));
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }


    private String getSecureToken() {
        DecimalFormat df = new DecimalFormat();
        df.setMinimumFractionDigits(2);
        Float f = Float.parseFloat(amnt);
        df.format(f);
        amnt = String.format("%.2f", f);
        String currencyCode="356";

        String val = amnt + currencyCode + merchantId + orderId;
        System.out.println("TokeString=="+val);
        //  String secureToken = generateHMAC(val, "abc");//P_00035
        // String secureToken = generateHMAC(val,"9392c19853c24bceb948c4c5343a1e60");//wep

        String secureToken = generateHMAC(val,"abc");

        return secureToken;
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
