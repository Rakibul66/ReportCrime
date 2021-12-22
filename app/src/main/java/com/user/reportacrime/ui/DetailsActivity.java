package com.user.reportacrime.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.user.reportacrime.R;
import com.user.reportacrime.model.FeedAdapter;

import java.util.HashMap;
import java.util.Map;

public class DetailsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private Spinner category, sub_category;
    private ImageView back;
    private EditText date, location, description;
    private RadioGroup radioGroup;
    private RadioButton radioButton, pistol, knife, others;
    private CardView save;
    String[] Category = {"Ground","Air"};
    String[] Sub_category = {"Bus", "Car", "Bike"};
    String CategoryText1, method;
    private FirebaseFirestore db;
    private DocumentReference document_reference;
    private CollectionReference feed;
    private FeedAdapter adapter;
    private CardView update, delete;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        category = findViewById(R.id.category);
        sub_category = findViewById(R.id.sub_category);

        back = findViewById(R.id.back);
        date = findViewById(R.id.et_time);
        location = findViewById(R.id.et_location);
        description = findViewById(R.id.et_description);
        update = findViewById(R.id.update);
        delete = findViewById(R.id.delete);

        //Spinner for Category
        category.setOnItemSelectedListener(this);
        sub_category.setOnItemSelectedListener(this);

        ArrayAdapter ad = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Category);
        ArrayAdapter ad2 = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Sub_category);

        ad.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(ad);

        ad2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sub_category.setAdapter(ad2);
//.......................................................



        //Database
        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();


        final Intent intent = getIntent();

        final String id = intent.getStringExtra(MainActivity.EXTRA_ID);

        document_reference = db.collection("Report").document(id);

//...............................

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        loadData();
        update();

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                document_reference.delete();
                finish();
                Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void update() {

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String Date = date.getText().toString();
                String Location = location.getText().toString();
                String Description = description.getText().toString();
                String Category = CategoryText1;


                if (!Date.isEmpty()  && !Location.isEmpty()) {
                    Map<String, Object> userMap = new HashMap<>();

                    userMap.put("date", Date);
                    userMap.put("location",Location);
                    userMap.put("category",Category);
                    userMap.put("description",Description);


                    document_reference.update(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(), "Updating..", Toast.LENGTH_SHORT).show();
                            Intent saved = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(saved);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                } else {
                    Toast.makeText(getApplicationContext(), "You must fill all the fields!", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void loadData() {
        document_reference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.exists()) {


                    String Location = documentSnapshot.getString("Location");
                    String Date = documentSnapshot.getString("date");
                    String Description = documentSnapshot.getString("description");
                    String Category = documentSnapshot.getString("category");


                    location.setText(Location);
                    date.setText(Date);
                    description.setText(Description);



                    //Spinner
                    if (Category.equals("Bus")) {
                        sub_category.setSelection(0);
                    } else if (Category.equals("Car")) {
                        sub_category.setSelection(1);
                    } else if (Category.equals("Bike")) {
                        sub_category.setSelection(2);
                    }
//.............................


//.....................................

                } else {
                    Toast.makeText(DetailsActivity.this, "Something wrong!", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        CategoryText1 = Sub_category[position];

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}