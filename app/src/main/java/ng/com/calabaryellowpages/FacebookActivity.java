package ng.com.calabaryellowpages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.json.JSONObject;

import java.util.Arrays;

import ng.com.calabaryellowpages.util.volleySingleton;

public class FacebookActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    LoginButton loginButton;
    CallbackManager callbackManager;
    volleySingleton volley;
    Context c;
    RequestQueue requestQueue;
    SharedPreferences preferences;
    ProgressBar bar;
    SharedPreferences.Editor editor;
    int RC_SIGN_IN = 300;
    Button gmail;
    private GoogleApiClient mGoogleApiClient;
    TextView errorMsg;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facebook);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        c = this;
        bar = (ProgressBar) findViewById(R.id.progressGmail);

        volley = volleySingleton.getsInstance();
        requestQueue = volley.getmRequestQueue();
        errorMsg = (TextView) findViewById(R.id.errorMsg);
       // Log.d("Facebook", AccessToken.getCurrentAccessToken().getUserId());
        gmail = (Button) findViewById(R.id.gmail);
        gmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                errorMsg.setVisibility(View.GONE);
                bar.setVisibility(View.VISIBLE);
                signIn();
            }
        });
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("email"));

        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("facebookActivity", loginResult.toString());
                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                try{
                                    object.put("ID", object.get("id"));
                                    object.put("Type", "facebook");
                                    object.remove("id");
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                SendToServer(object);
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "name,link, email, gender, birthday, location");
                request.setParameters(parameters);
                request.executeAsync();

            }

            @Override
            public void onCancel() {
                errorMsg.setVisibility(View.VISIBLE);
            }

            @Override
            public void onError(FacebookException error) {
                error.printStackTrace();
                //editor.putBoolean("notlogged", false);
                //editor.commit();
                errorMsg.setVisibility(View.VISIBLE);


            }
        });


        //loginButton.setFragment(this);
        preferences = getSharedPreferences("app", MODE_PRIVATE);
        editor = preferences.edit();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("110934577974-9cdrom3gs21bk9164qndps0p6hhebe39.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    errorMsg.setVisibility(View.GONE);
                    // User is signed in
                    Log.d("FacebookActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    try{
                        JSONObject json = new JSONObject();
                        json.put("ID", user.getUid());
                        json.put("Name", user.getDisplayName());
                        json.put("Link", user.getProviderId());
                        json.put("Email", user.getEmail());
                        json.put("Type", "google");

                        SendToServer(json);

                    }catch (Exception e){
                        e.printStackTrace();
                    }
                } else {
                    // User is signed out
                    Log.d("FacebookActivity", "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };

        //google sign in





    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("Facebook", "result");
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            }

    }else{
            bar.setVisibility(View.VISIBLE);
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("FacebookActivity", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d("FacebookActivity", "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w("FAcebookActivity", "signInWithCredential", task.getException());
                            errorMsg.setVisibility(View.VISIBLE);
                            Toast.makeText(FacebookActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }



    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.d("FaceBookActivity", "onConnectionFailed:" + connectionResult);
        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();
        errorMsg.setVisibility(View.VISIBLE);

    }

    public void SendToServer(JSONObject object){
       JsonObjectRequest objectRequest = null;
        //Log.d("Facebook", object.toString());
        try{
            objectRequest = new JsonObjectRequest(Request.Method.POST, volleySingleton.URL + "api/social_login", object, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject jsonObject) {
                    //preferences.getBoolean("notlogged", true)
                    editor.putBoolean("isnotlogged", false);
                    editor.commit();
                    Intent i = new Intent(c, tabbed.class);
                    startActivity(i);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    editor.putBoolean("isnotlogged", false);
                    editor.commit();
                    Intent i = new Intent(c, tabbed.class);
                    startActivity(i);
                    finish();
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        requestQueue.add(objectRequest);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
