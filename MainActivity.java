package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private EditText txtTitle,txtDesc;
    private Button btnSave,btnShow;
    private FirebaseFirestore db;
    private String uTitle,uDesc,uId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        txtTitle=findViewById(R.id.editTitle);
        txtDesc=findViewById(R.id.editDescription);
        btnSave=findViewById(R.id.btnSave);
        btnShow=findViewById(R.id.btnShow);
        db=FirebaseFirestore.getInstance();
        Bundle bundle = getIntent().getExtras();
        if(bundle != null){
            btnSave.setText("Update");
            uTitle=bundle.getString("uTitle");
            uId=bundle.getString("uId");
            uDesc=bundle.getString("uDesc");
            txtTitle.setText(uTitle);
            txtDesc.setText(uDesc);
        }else{
            btnSave.setText("Save");
        }
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,ShowActivity.class));
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = txtTitle.getText().toString();
                String desc =txtDesc.getText().toString();
                Bundle bundle1= getIntent().getExtras();
                if(bundle1 !=null){
                    String id= uId;
                    updatetoFirestore(id,title,desc);
                }else{
                    String id = UUID.randomUUID().toString();
                    savetoFireStore(id,title,desc);
                }

            }
        });
    }
    private void updatetoFirestore(String id,String title,String desc){
        db.collection("Documents").document(id).update("title",title,"desc",desc).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Toast.makeText(MainActivity.this,"data updated",Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(MainActivity.this,"Error: "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void savetoFireStore(String id,String title,String desc){
        if(!title.isEmpty() && !desc.isEmpty()){
            HashMap<String,Object>map= new HashMap<>();
            map.put("id",id);
            map.put("title",title);
            map.put("desc",desc);
            db.collection("Documents").document(id).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(MainActivity.this,"Data Saved",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this,"Failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(this, "Empty Fields not allowed", Toast.LENGTH_SHORT).show();
        }
    }
}