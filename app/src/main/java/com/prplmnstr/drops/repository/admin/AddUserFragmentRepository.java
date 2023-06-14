package com.prplmnstr.drops.repository.admin;

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

    public void getInvestors(){
        firebaseFirestore.collection(Constants.INVESTOR)
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
    public void getWorkers(){
        firebaseFirestore.collection(Constants.WORKER)
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

    public void getAdminPassword(){
        firebaseFirestore.collection("admin")
                .document("password")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            ADMIN_PASSWORD = task.getResult().get("password").toString();
                            signInAdmin(Constants.ADMIN_MAIL,ADMIN_PASSWORD);
                        }
                        else{
                            onFirebaseRespond.onUserAdded(false);
                        }
                    }
                });
    }

    private void signInAdmin(String email, String admin_password) {
        firebaseAuth.signInWithEmailAndPassword(email,admin_password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    onFirebaseRespond.onUserAdded(true);
                }else{
                    onFirebaseRespond.onUserAdded(false);
                }
            }
        });

    }

    public void addNewUser(User user,boolean newUser){

        if(newUser){
            if(user.getUserType().equals(Constants.WORKER)){
                Date today = Helper.getTodayDateObject();
                Attendance attendance = new Attendance(user.getPlantName(), user.getUserName(),
                      today.getDay(), today.getMonth(), today.getYear(), Constants.NO_ATTENDANCE);
                firebaseFirestore.collection(Constants.ATTENDANCE)
                        .document(user.getPlantName()+"_"+user.getUserName()+"_"+today.getDateInStringFormat())
                        .set(attendance).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

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

                                signUpUser(user.getEmail(), user.getPassword());
                            }else{
                                onFirebaseRespond.onUserAdded(true);
                            }
                        }else{
                            onFirebaseRespond.onUserAdded(false);
                        }
                    }
                });


    }

    public void signUpUser(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
              if(task.isSuccessful()){
                  getAdminPassword();
              }else{
                  onFirebaseRespond.onUserAdded(false);
              }
            }
        });

    }

    public interface OnFirebaseRespond{

        void onUserAdded(Boolean result);
        void onInvetorLoaded(List<User> investors);
        void onWorkersLoaded(List<User> worker);



    }
}
