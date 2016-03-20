package exun.cli.in.brinjal.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import exun.cli.in.brinjal.R;
import exun.cli.in.brinjal.helper.AppConstants;
import exun.cli.in.brinjal.helper.SQLiteHandler;
import exun.cli.in.brinjal.helper.SessionManager;

/**
 * Created by n00b on 3/9/2016.
 */
public class UserDetails extends AppCompatActivity {

    private TextView userName,userEmail;
    private ImageView add,edit,save,cancel;
    EditText inputFullName,inputEmail;
    String name,email,editedName,editedEmail;
    ProgressBar pbDetails,pbAddress;
    private SQLiteHandler db;
    private Toolbar toolbar;
    private CardView logoutCard;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_details);

        // SQLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // Session manager
        session = new SessionManager(getApplicationContext());

        name = db.getUserDetails().get("name");
        email = db.getUserDetails().get("email");

        initialize();
        setToolbar();
        setTitleOnCollapse();

        setImageOnClickListeners();

        logoutCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(UserDetails.this)
                        .setTitle("Logout")
                        .setMessage("Are you sure you want to logout?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                logoutUser();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing

                            }
                        })
                        .show();
            }
        });

    }

    private void setTitleOnCollapse() {
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbarLayout.setTitle("User Profile");
        collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
    }

    void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void setImageOnClickListeners() {

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Coming soon!");
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.GONE);
                userEmail.setVisibility(View.GONE);
                userName.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);
                cancel.setVisibility(View.VISIBLE);
                inputFullName.setText(name);
                inputEmail.setText(email);
                inputEmail.setVisibility(View.VISIBLE);
                inputFullName.setVisibility(View.VISIBLE);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pbDetails.setVisibility(View.VISIBLE);
                editedEmail = inputEmail.getText().toString();
                editedName = inputFullName.getText().toString();

                if (validate(editedName, editedEmail)) {
                    if (!(editedEmail.equals(email) && editedName.equals(name))){
                        showToast("Updated!");
                        name = editedName;
                        email = editedEmail;
                    }
                    else
                        showToast("No change! Time pass :/");

                    cancel.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);
                    inputEmail.setVisibility(View.GONE);
                    inputFullName.setVisibility(View.GONE);
                    userEmail.setText(editedEmail);
                    userName.setText(editedName);
                    userName.setVisibility(View.VISIBLE);
                    userEmail.setVisibility(View.VISIBLE);
                    edit.setVisibility(View.VISIBLE);
                }

                pbDetails.setVisibility(View.GONE);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                inputEmail.setVisibility(View.GONE);
                inputFullName.setVisibility(View.GONE);
                userEmail.setText(email);
                userName.setText(name);
                userName.setVisibility(View.VISIBLE);
                userEmail.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
            }
        });
    }

    private boolean validate(String editedName, String editedEmail) {

        boolean valid = true;

        if (editedName.isEmpty() || editedName.length() < 3) {
            inputFullName.setError("at least 3 characters");
            valid = false;
        } else {
            inputFullName.setError(null);
        }

        if (editedEmail.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(editedEmail).matches()) {
            inputEmail.setError("enter a valid email address");
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        return valid;
    }

    private void initialize() {
        userName = (TextView) findViewById(R.id.userDetailsName);
        userEmail = (TextView) findViewById(R.id.userDetailsEmail);
        add = (ImageView) findViewById(R.id.userAddBtn);
        edit = (ImageView) findViewById(R.id.userDetailsEditBtn);
        save = (ImageView) findViewById(R.id.saveUserEdit);
        cancel = (ImageView) findViewById(R.id.cancelUserEdit);
        logoutCard = (CardView) findViewById(R.id.logoutCard);
        inputEmail = (EditText) findViewById(R.id.reg_email);
        inputFullName = (EditText) findViewById(R.id.reg_fullname);
        pbAddress = (ProgressBar) findViewById(R.id.progressBarAddress);
        pbDetails = (ProgressBar) findViewById(R.id.progressBarDetails);

        userName.setText(name);
        userEmail.setText(email);
    }

    private void setToolbar() {
        toolbar= (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        if (menuItem.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.delete(AppConstants.TABLE_LOGIN);

        // Launching the login activity
        Intent intent = new Intent(UserDetails.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
