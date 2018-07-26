package com.dsapps.wallpaperapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import org.w3c.dom.Text;

import static android.support.constraint.Constraints.TAG;

/**
 * Created by Shridhar on 22-Jun-18.
 */

public class SettingsFragment extends Fragment
{

    private static final String TAG = "SettingsFragment";

    public static final int SIGN_IN_CODE=110;
    GoogleSignInClient mGoogleSignInClient;
    FirebaseAuth mAuth;
    SignInButton signInButton;
    ImageView userImage;
    TextView userName;
    TextView userEmail;
    Button logoutBtn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if(FirebaseAuth.getInstance().getCurrentUser()==null)
            return inflater.inflate(R.layout.fragment_settings_default, container, false);
        else
            return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient=GoogleSignIn.getClient(getContext(), gso);

        mAuth=FirebaseAuth.getInstance();

        if(FirebaseAuth.getInstance().getCurrentUser()==null) {
            signInButton = (SignInButton) view.findViewById(R.id.sign_in_btn);

            signInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                    startActivityForResult(signInIntent, SIGN_IN_CODE);
                }
            });
        }

        else
        {
            userImage=(ImageView)view.findViewById(R.id.user_image);
            userName=(TextView)view.findViewById(R.id.username_display);
            userEmail=(TextView)view.findViewById(R.id.email_display);
            logoutBtn=(Button)view.findViewById(R.id.logout_btn);

            Glide.with(getContext()).load(mAuth.getCurrentUser().getPhotoUrl()).into(userImage);
            userName.setText(mAuth.getCurrentUser().getDisplayName());
            userEmail.setText(mAuth.getCurrentUser().getEmail());

            logoutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mAuth.signOut();
                    mGoogleSignInClient.signOut();


                    RelativeLayout relativeLayoutLoggedIn=(RelativeLayout)getView().findViewById(R.id.relative_layout_logged_in);
                    relativeLayoutLoggedIn.removeAllViewsInLayout();
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .replace(R.id.relative_layout_logged_in, new SettingsFragment())
                            .commit();

                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SIGN_IN_CODE) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            Toast.makeText(getActivity(), "Login Successful!", Toast.LENGTH_SHORT).show();

                            signInButton.setVisibility(View.GONE);

                            RelativeLayout relativeLayout=(RelativeLayout)getView().findViewById(R.id.relative_layout);
                            relativeLayout.removeAllViewsInLayout();
                            getActivity().getSupportFragmentManager().beginTransaction()
                                    .replace(R.id.relative_layout, new SettingsFragment())
                                    .commit();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            Toast.makeText(getActivity(), "Sign In Failed."+ task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }

                });
    }

}
