package studio.brunocasamassa.ajudaquioficial;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;
import studio.brunocasamassa.ajudaquioficial.helper.Base64Decoder;
import studio.brunocasamassa.ajudaquioficial.helper.FirebaseConfig;
import studio.brunocasamassa.ajudaquioficial.helper.Preferences;
import studio.brunocasamassa.ajudaquioficial.helper.User;

/**
 * Created by bruno on 24/04/2017.
 */

public class ConfiguracoesActivity extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST = 1;
    private CircleImageView circleImageView;
    private ImageView cam_config;
    private EditText name;
    private Button salvar;
    private TextView seekValue;
    private String auth = null;
    private int distance;
    private SeekBar maxDistance;
    private StorageReference storage = FirebaseConfig.getFirebaseStorage();
    private DatabaseReference dbUser = FirebaseConfig.getFireBase();
    private DatabaseReference dbUser2 = FirebaseConfig.getFireBase();
    private String userKey = Base64Decoder.encoderBase64(FirebaseConfig.getFirebaseAuthentication().getCurrentUser().getEmail());
    private boolean photoWasChanged = false;
    private Button passButton;
    private String email;
    private String senha;
    private Toolbar toolbar;
    private boolean isFromFacebook = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes);
        maxDistance = (SeekBar) findViewById(R.id.max_distance_seekbar);
        seekValue = (TextView) findViewById(R.id.seekbar_distance);
        circleImageView = (CircleImageView) findViewById(R.id.photo_config);
        cam_config = (ImageView) findViewById(R.id.cam_config);
        name = (EditText) findViewById(R.id.name_config);
        salvar = (Button) findViewById(R.id.save_config);
        passButton = (Button) findViewById(R.id.pass_button);
        toolbar = (Toolbar) findViewById(R.id.toolbar_principal_configuracoes);

        toolbar.setTitle("Configurações");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_left);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Preferences preferences = new Preferences(ConfiguracoesActivity.this);

        for (UserInfo user : FirebaseAuth.getInstance().getCurrentUser().getProviderData()) {
            if (user.getProviderId().equals("facebook.com")) {
                System.out.println("User is signed in with Facebook");
                isFromFacebook = true;
            }
        }

        passButton.setVisibility(View.INVISIBLE);
        if (!isFromFacebook) {
            passButton.setVisibility(View.VISIBLE);
            email = preferences.getMail();
            senha = preferences.getSenha();

            System.out.println("senha " + senha);
            passButton.setText("ALTERAR MINHA SENHA");
            passButton.setBackgroundColor(Color.argb(255, 20, 118, 122));
            passButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfiguracoesActivity.this);
                    alertDialog.setTitle("Alterar sua senha");
                    alertDialog.setMessage("digite sua nova senha");
                    final EditText edit = new EditText(ConfiguracoesActivity.this);
                    alertDialog.setView(edit);
                    alertDialog.setPositiveButton("ALTERAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(email, senha);

// Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(edit.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Log.d("CHANGEPASS", "Password updated");
                                                            Toast.makeText(getApplicationContext(), "Senha alterada", Toast.LENGTH_SHORT).show();
                                                            dbUser.child("usuarios").child(userKey).child("senha").setValue(edit.getText().toString());
                                                            finish();
                                                        } else {
                                                            Log.d("CHANGEPASS", "Error password not updated");
                                                        }
                                                    }
                                                });
                                            } else {
                                                Log.d("CHANGEPASS", "Error auth failed");
                                            }
                                        }
                                    });


                        }
                    });

                    alertDialog.setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    }).create().show();
                }
            });
        }


        maxDistance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distance = progress;
                seekValue.setText(String.valueOf(progress) + "km");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

        });

        dbUser.child("usuarios").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);
                name.setText(user.getName());
                maxDistance.setProgress(user.getMaxDistance());

                try {
                    storage.child("userImages").child(userKey + ".png").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Glide.with(ConfiguracoesActivity.this).load(uri).into(circleImageView);
                            circleImageView.setBackgroundColor(Color.TRANSPARENT);
                            Picasso.with(ConfiguracoesActivity.this).load(uri).resize(680, 680).onlyScaleDown().into(circleImageView);
                            System.out.println("my groups lets seee2 " + uri);
                        /*try {
                            InputStream streamUri = getApplicationContext().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(streamUri);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);

                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                circleImageView.setImageBitmap(resized);
                            } catch (OutOfMemoryError e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("error in get image ", e.toString());
                        }*/


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            storage.child("userImages").child(userKey + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    //Glide.with(ConfiguracoesActivity.this).load(uri).into(circleImageView);
                                    circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                    Picasso.with(ConfiguracoesActivity.this).load(uri).resize(1000, 1000).onlyScaleDown().into(circleImageView);
                                    System.out.println("my groups lets seee2 " + uri);
                        /*try {
                            InputStream streamUri = getApplicationContext().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(streamUri);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);

                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                circleImageView.setImageBitmap(resized);
                            } catch (OutOfMemoryError e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("error in get image ", e.toString());
                        }*/


                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    if (dataSnapshot.child("profileImg").exists()) { //todo bug manual register or facebook register
                                        //Glide.with(ConfiguracoesActivity.this).load(user.getProfileImg()).override(68, 68).into(circleImageView);
                                        circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                        Picasso.with(ConfiguracoesActivity.this).load(user.getProfileImg()).resize(1000, 1000).onlyScaleDown().into(circleImageView);
                                    }
                                }
                            });
                        }

                    });
                    ;
                } catch (Exception e) {
                    storage.child("userImages").child(userKey + ".jpg").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            //Glide.with(ConfiguracoesActivity.this).load(uri).into(circleImageView);
                            circleImageView.setBackgroundColor(Color.TRANSPARENT);
                            Picasso.with(ConfiguracoesActivity.this).load(uri).resize(1000, 1000).onlyScaleDown().into(circleImageView);
                            System.out.println("my groups lets seee2 " + uri);
                        /*try {
                            InputStream streamUri = getApplicationContext().getContentResolver().openInputStream(uri);
                            Bitmap bitmap = BitmapFactory.decodeStream(streamUri);
                            Log.d("image", String.valueOf(bitmap));
                            //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);

                            try {
                                Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                                circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                circleImageView.setImageBitmap(resized);
                            } catch (OutOfMemoryError e) {
                                System.out.println("memory error " + e);
                                //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("error in get image ", e.toString());
                        }*/


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            if (dataSnapshot.child("profileImg").exists()) { //todo bug manual register or facebook register
                                //Glide.with(ConfiguracoesActivity.this).load(user.getProfileImg()).override(68, 68).into(circleImageView);
                                circleImageView.setBackgroundColor(Color.TRANSPARENT);
                                Picasso.with(ConfiguracoesActivity.this).load(user.getProfileImg()).resize(1000, 1000).onlyScaleDown().into(circleImageView);
                            }
                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        cam_config.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                // Show only images, no videos or anything else
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                // Always show the chooser (if there are multiple options available)
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                photoWasChanged = true;
            }
        });

        salvar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfiguracoesActivity.this);

                alertDialog.setTitle("Alterar Dados Usuario");
                alertDialog.setMessage("Deseja realmente alterar seus dados?");
                alertDialog.setCancelable(false);


                alertDialog.setNegativeButton("Voltar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }

                });
                alertDialog.setPositiveButton("Prosseguir", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbUser.child("usuarios").child(userKey).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                User user = dataSnapshot.getValue(User.class);
                                if (distance != 0) {
                                    user.setMaxDistance(distance);
                                }
                                if (user.getProfileImg() != null) {
                                    // user.setProfileImg(null);
                                }
                                user.setId(userKey);
                                if (!name.getText().equals(user.getName())) {
                                    user.setName(name.getText().toString());
                                    user.save();
                                } else System.out.println("username nao mudad0");

                                if (photoWasChanged) {
                                    uploadImages();
                                    System.out.println("photo mudada");
                                } else System.out.println("photo nao mudada");
                                Toast.makeText(getApplicationContext(), "Alteraçoes Salvas", Toast.LENGTH_SHORT).show();
                                refresh();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                    }

                }).create().show();

            }


        });
    }

    ;

    private void uploadImages() {
        StorageReference imgRef = storage.child("userImages").child(userKey + ".png");
        System.out.println("lei lei " + userKey);
        //download img source
        circleImageView.setDrawingCacheEnabled(true);
        circleImageView.buildDrawingCache();

        Bitmap bitmap = circleImageView.getDrawingCache();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imgRef.putBytes(data);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                Uri downloadUrl = taskSnapshot.getMetadata().getDownloadUrl();
                System.out.println("huehuebrjava " + downloadUrl);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            photoWasChanged = true;
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Log.d("image", String.valueOf(bitmap));
                //Bitmap resized = Bitmap.createScaledBitmap(bitmap, circleImageView.getWidth(), circleImageView.getHeight(), true);

                try {
                    Bitmap resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                    circleImageView.setBackgroundColor(Color.TRANSPARENT);
                    circleImageView.setImageBitmap(resized);
                } catch (OutOfMemoryError e) {
                    System.out.println("memory error " + e);
                    //resized = Bitmap.createScaledBitmap(bitmap, (int) (bitmap.getWidth() * 1.6), (int) (bitmap.getHeight() * 1.6), true);
                }

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("error in get image ", e.toString());
            }
        }
    }

    private void refresh() {
        Intent intent = new Intent(ConfiguracoesActivity.this, PedidosActivity.class);
        finish();
        startActivity(intent);
    }


}