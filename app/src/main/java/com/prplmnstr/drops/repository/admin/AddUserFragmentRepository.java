package com.prplmnstr.drops.repository.admin;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.prplmnstr.drops.models.Attendance;
import com.prplmnstr.drops.models.Date;
import com.prplmnstr.drops.models.Plant;
import com.prplmnstr.drops.models.User;
import com.prplmnstr.drops.utils.Constants;
import com.prplmnstr.drops.utils.Helper;

import java.util.List;

public class AddUserFragmentRepository {
    private OnFirebaseRespond onFirebaseRespond;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private FirebaseAuth  firebaseAuth = FirebaseAuth.getInstance();
    private String ADMIN_PASSWORD ;
    public AddUserFragmentRepository(OnFirebaseRespond onFirebaseRespond) {
        this.onFirebaseRespond = onFirebaseRespond;
    }

    public void getInvestors(String plantName){
        firebaseFirestore.collection(Constants.INVESTOR)
                .whereEqualTo(Constants.PLANT_NAME,plantName)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                onFirebaseRespond.onInvetorLoaded(
                                        task.getResult().toObjects(User.class)
                                );
                            }else{
                                onFirebaseRespond.onInvetorLoaded(
                                        null
                                );
                            }

                        }

                    }
                });
    }
    public void getWorkers(String plantName){
        firebaseFirestore.collection(Constants.WORKER)
                .whereEqualTo(Constants.PLANT_NAME,plantName)

                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            QuerySnapshot document = task.getResult();
                            if (!document.isEmpty()) {
                                onFirebaseRespond.onWorkersLoaded(
                                        task.getResult().toObjects(User.class)
                                );
                            }else{
                                onFirebaseRespond.onWorkersLoaded(
                                        null
                                );
                            }

                        }

                    }
                });
    }

    public void getAdminPassword(Context context){
        firebaseFirestore.collection("admin")
                .document("password")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            ADMIN_PASSWORD = task.getResult().get("password").toString();
                            signInAdmin(Constants.ADMIN_MAIL,ADMIN_PASSWORD,context);
                        }
                        else{
                            Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                            onFirebaseRespond.onUserAdded(false);
                        }
                    }
                });
    }

    private void signInAdmin(String email, String admin_password,Context context) {
        firebaseAuth.signInWithEmailAndPassword(email,admin_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(context, "User account created", Toast.LENGTH_SHORT).show();
                    onFirebaseRespond.onUserAdded(true);
                }else{
                    Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                    onFirebaseRespond.onUserAdded(false);
                }
            }
        });

    }

    public void addNewUser(User user,boolean newUser,Context context){
        Date today = Helper.getTodayDateObject();
        if(newUser){
            if(user.getUserType().equals(Constants.WORKER)){

                Attendance attendance = new Attendance(user.getPlantName(), user.getUserName(),
                      today.getDay(), today.getMonth(), today.getYear(), Constants.NO_ATTENDANCE);
                firebaseFirestore.collection(Constants.ATTENDANCE)
                        .document(user.getPlantName()+"_"+user.getUserName()+"_"+today.getDateInStringFormat())
                        .set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()){

                                }
                            }
                        });
            }
        }





        firebaseFirestore.collection(user.getUserType())
                .document(user.getPlantName()+"_"+user.getEmail())
                .set(user)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            if(newUser){
                                Toast.makeText(context, "Creating user account...", Toast.LENGTH_SHORT).show();
                                signUpUser(user.getEmail(), user.getPassword(),context);
                            }else{
                                onFirebaseRespond.onUserAdded(false);
                                Toast.makeText(context, "User account created", Toast.LENGTH_SHORT).show();
                            }

                        }else{
                           
                        }
                    }
                });


    }

    public void deleteUser(User user, Context context){


        firebaseFirestore.collection(user.getUserType())
                .document(user.getPlantName()+"_"+user.getEmail())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(context, "User Deleted", Toast.LENGTH_SHORT).show();
                            onFirebaseRespond.onUserDeleted(true);
                        }else{
                            Toast.makeText(context, "Something went wrong", Toast.LENGTH_SHORT).show();
                            onFirebaseRespond.onUserDeleted(false);
                        }
                    }
                });


    }

    public void signUpUser(String email, String password,Context context){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()){
                  getAdminPassword(context);
              }else{
                  Toast.makeText(context, "Something went wrong...", Toast.LENGTH_SHORT).show();
                  onFirebaseRespond.onUserAdded(false);
              }
            }
        });

    }

    public interface OnFirebaseRespond{

        void onUserAdded(Boolean result);
        void onInvetorLoaded(List<User> investors);
        void onWorkersLoaded(List<User> worker);

        void onUserDeleted(Boolean result);



    }
}
