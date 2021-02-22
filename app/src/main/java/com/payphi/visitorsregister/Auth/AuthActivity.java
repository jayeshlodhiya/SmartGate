package com.payphi.visitorsregister.Auth;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.iid.FirebaseInstanceId;
import com.payphi.visitorsregister.ProfieCompleteActivity;
import com.payphi.visitorsregister.R;
import com.payphi.visitorsregister.model.User;
import com.payphi.visitorsregister.utils.Constants;
import com.payphi.visitorsregister.utils.GMailSender;
import com.payphi.visitorsregister.utils.SharedPrefManager;
import com.payphi.visitorsregister.utils.Utils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by swapnil.g on 1/24/2018.
 */
public class AuthActivity extends BaseAuthActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    public static GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "MainActivity";
    private String idToken;
    public SharedPrefManager sharedPrefManager;
    public static Context mContext = null;

    private String name, email;
    private String photo;
    private Uri photoUri;
    private SignInButton mSignInButton;
    public static AuthActivity authActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acivity_signin);
        mContext = this;
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
        mSignInButton = (SignInButton) findViewById(R.id.login_with_google);
        mSignInButton.setSize(SignInButton.SIZE_WIDE);

        mSignInButton.setOnClickListener(this);
        FirebaseApp.initializeApp(getApplicationContext());
        configureSignIn();

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();

        //this is where we start the Auth state Listener to listen for whether the user is signed in or not
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                // Get signedIn user
                FirebaseUser user = firebaseAuth.getCurrentUser();

                //if user is signed in, we call a helper method to save the user details to Firebase
                if (user != null) {
                    // User is signed in
                    createUserInFirebaseHelper();
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
    }

    //This method creates a new user on our own Firebase database
    //after a successful Authentication on Firebase
    //It also saves the user info to SharedPreference
    private void createUserInFirebaseHelper() {

        //Since Firebase does not allow "." in the key name, we'll have to encode and change the "." to ","
        // using the encodeEmail method in class Utils
        final String encodedEmail = Utils.encodeEmail(email.toLowerCase());

        //create an object of Firebase database and pass the the Firebase URL
        final Firebase userLocation = new Firebase(Constants.FIREBASE_URL_USERS).child(encodedEmail);

        //Add a Listerner to that above location
        userLocation.addListenerForSingleValueEvent(new com.firebase.client.ValueEventListener() {
            @Override
            public void onDataChange(com.firebase.client.DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    /* Set raw version of date to the ServerValue.TIMESTAMP value and save into dateCreatedMap */
                    HashMap<String, Object> timestampJoined = new HashMap<>();
                    timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                    // Insert into Firebase database
                    User newUser = new User(name, photo, encodedEmail, timestampJoined);
                    userLocation.setValue(newUser);
                    //    SaveUsers();
                    //Toast.makeText(, "Account created!", Toast.LENGTH_SHORT).show();

                    // After saving data to Firebase, goto next activity
//                    Intent intent = new Intent(MainActivity.this, NavDrawerActivity.class);
//                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                    startActivity(intent);
//                    finish();
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

                Log.d(TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                //hideProgressDialog();
                if (firebaseError.getCode() == FirebaseError.EMAIL_TAKEN) {
                } else {
                    //Toast.makeText(g, firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public static void getGoogleSignOut() {
        /*if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {

        }
*/
        mGoogleApiClient.clearDefaultAccountAndReconnect();
        // Auth.GoogleSignInApi.signOut(mGoogleApiClient);
    }

    // This method configures Google SignIn
    public void configureSignIn() {
// Configure sign-in to request the user's basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(AuthActivity.this.getResources().getString(R.string.web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();

    }


    // This method is called when the signIn button is clicked on the layout
    // It prompts the user to select a Google account.
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    // This IS the method where the result of clicking the signIn button will be handled
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {

            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, save Token and a state then authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                idToken = account.getIdToken();

                name = account.getDisplayName();
                email = account.getEmail();
                photoUri = account.getPhotoUrl();
                photo = photoUri.toString();

                // Save Data to SharedPreference
                sharedPrefManager = new SharedPrefManager(mContext);
                sharedPrefManager.saveIsLoggedIn(mContext, true);

                sharedPrefManager.saveEmail(mContext, email);
                sharedPrefManager.saveName(mContext, name);
                sharedPrefManager.savePhoto(mContext, photo);

                sharedPrefManager.saveToken(mContext, idToken);
                //sharedPrefManager.saveIsLoggedIn(mContext, true);
                //SaveUsers();
                if (account != null) {
                    //Take all data You Want

                    //After You got your data add this to clear the priviously selected mail
                    mGoogleApiClient.clearDefaultAccountAndReconnect();
                }


                AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
                firebaseAuthWithGoogle(credential);
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful. ");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    //After a successful sign into Google, this method now authenticates the user with Firebase
    private void firebaseAuthWithGoogle(AuthCredential credential) {
        showProgressDialog();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential" + task.getException().getMessage());
                            task.getException().printStackTrace();
                            Toast.makeText(AuthActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            createUserInFirebaseHelper();
                            Toast.makeText(AuthActivity.this, "Login successful",
                                    Toast.LENGTH_SHORT).show();


                            Intent intent = new Intent(AuthActivity.this, ProfieCompleteActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        }
                        hideProgressDialog();
                    }
                });
    }

    private void SaveUsers() {
        Map<String, Object> visitorMap;

        DocumentReference bookingref = FirebaseFirestore.getInstance().collection("VisitorRegister").document("UserDoc").collection("Sousers").document();
        visitorMap = new HashMap<String, Object>();


        visitorMap.put("Name", name);
        visitorMap.put("Email", email);
        visitorMap.put("FlatNumber", "");
        visitorMap.put("Photo", photo);
        visitorMap.put("DeviceId", FirebaseInstanceId.getInstance().getToken());
        visitorMap.put("MobileNumber", "");
        visitorMap.put("Cretaed", new Date());
        visitorMap.put("DocId", bookingref.getId());

//        paydialog.show();
        bookingref.set(visitorMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {

                Toast.makeText(getApplicationContext(), "Registration done!!", Toast.LENGTH_LONG).show();
                //            paydialog.dismiss();
                //LocalNotification();
                // SendNotificationToShop();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Error Occured.!", Toast.LENGTH_LONG).show();
                //        paydialog.dismiss();

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuthListener != null) {
            FirebaseAuth.getInstance().signOut();
        }
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onClick(View view) {

        Utils utils = new Utils(this);
        int id = view.getId();

        if (id == R.id.login_with_google) {
            if (utils.isNetworkAvailable()) {
                signIn();
            } else {
                Toast.makeText(AuthActivity.this, "Oops! no internet connection!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
