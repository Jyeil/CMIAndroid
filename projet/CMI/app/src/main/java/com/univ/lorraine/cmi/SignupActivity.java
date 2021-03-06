package com.univ.lorraine.cmi;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.univ.lorraine.cmi.database.CmidbaOpenDatabaseHelper;
import com.univ.lorraine.cmi.database.model.Utilisateur;
import com.univ.lorraine.cmi.retrofit.CallMeIshmaelServiceProvider;

import nl.siegmann.epublib.domain.Book;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    private static final int REQUEST_LOGIN = 0;

    // Helper permettant d'interagir avec la database
    private CmidbaOpenDatabaseHelper dbhelper = null;

    EditText pseudoText;
    EditText emailText;
    EditText passwordText;
    EditText passwordConfirmText;
    Button signupButton;
    TextView loginLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        pseudoText = (EditText) this.findViewById(R.id.input_pseudo);
        emailText = (EditText) this.findViewById(R.id.input_email);
        passwordText = (EditText) this.findViewById(R.id.input_password);
        passwordConfirmText = (EditText) this.findViewById(R.id.input_password_verif);
        signupButton = (Button) this.findViewById(R.id.btn_signup);
        loginLink = (TextView) this.findViewById(R.id.link_login);

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
                // on cache le clavier
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(signupButton.getWindowToken(), 0);
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivityForResult(intent, REQUEST_LOGIN);
                finish();
            }
        });
    }

    /**
     * Overriden in order to close the database
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbhelper != null){
            OpenHelperManager.releaseHelper();
            dbhelper = null;
        }
    }

    /**
     * Retourne le databaseHelper (crée si il n'existe pas)
     * @return dbhelper
     */
    private CmidbaOpenDatabaseHelper getHelper(){
        if (dbhelper == null){
            dbhelper = OpenHelperManager.getHelper(this, CmidbaOpenDatabaseHelper.class);
        }
        return dbhelper;
    }

    public void signup() {

        if (!validate()) {
            Toast.makeText(getApplicationContext(), "Champ(s) invalide(s)", Toast.LENGTH_LONG).show();
            return;
        }

        // Si on dispose d'une connexion internet
        if (Utilities.checkNetworkAvailable(this)) {

            final ProgressDialog progressDialog = new ProgressDialog(SignupActivity.this);
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.signup_progress_dialog));
            progressDialog.show();

            String pseudo = pseudoText.getText().toString();
            String email = emailText.getText().toString();
            String password = passwordText.getText().toString();
            final Utilisateur send = new Utilisateur();

            send.setPseudo(pseudo);
            send.setEmail(email);
            send.setPassword(password);

            CallMeIshmaelServiceProvider.getService()
                    .signup(send)
                    .enqueue(new Callback<Utilisateur>() {
                        @Override
                        public void onResponse(Call<Utilisateur> call, Response<Utilisateur> response) {
                            progressDialog.dismiss();
                            if (response.code() == 409){
                                emailAlreadyTaken();
                            } else {
                                if (response.body() == null) onSignupFailed();
                                else onSignupSuccess(response.body());
                            }
                        }

                        @Override
                        public void onFailure(Call<Utilisateur> call, Throwable t) {
                            Log.e("FAILURE", t.toString());
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Erreur connexion serveur", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


    public void onSignupSuccess(Utilisateur newUser) {
        // Sauvegarde du nouveau currentUser
        newUser.setPassword(passwordText.getText().toString());
        CredentialsUtilities.setCurrentUser(getApplicationContext(), newUser);
        CredentialsUtilities.initialiseUser(getApplicationContext());
        CallMeIshmaelServiceProvider.setHeaderAuth(CredentialsUtilities.getCurrentToken());
        Toast.makeText(SignupActivity.this, "Incription réussie !", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK, null);
        // On supprime les livres web
        BookUtilities.removeOnlineContent(this, getHelper());
        finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Impossible de finaliser l'inscription", Toast.LENGTH_LONG).show();
    }

    private void emailAlreadyTaken(){
        Toast.makeText(getApplicationContext(), "Email déjà utilisé", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String pseudo = pseudoText.getText().toString();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        String passwordVerif = passwordConfirmText.getText().toString();

        if (pseudo.isEmpty() || pseudo.length() < 3) {
            pseudoText.setError(getString(R.string.pseudo_court));
            valid = false;
        } else {
            pseudoText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError(getString(R.string.email_non_valide));
            valid = false;
        } else {
            emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            passwordText.setError(getString(R.string.password_court));
            valid = false;
        } else {
            passwordText.setError(null);
        }

        if (!passwordVerif.equals(password)) {
            passwordConfirmText.setError(getString(R.string.verif_password_incorrecte));
            valid = false;
        } else {
            passwordConfirmText.setError(null);
        }

        return valid;
    }
}