package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.models.ContactoChat;
import com.aek.whatsapp.models.Message;
import com.aek.whatsapp.models.MessageSended;
import com.aek.whatsapp.utils.BitmapUtils;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.PixelsDPUtils;
import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.aek.whatsapp.utils.TimestampConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.UUID;

public class ChatController {

    public static void addUserToContacts(String uidReceiver, Message message,
                                         WeakReference<MessageSended> messageSendedWeakReference) {
        /////////////   SENDER
        DocumentReference senderContacts = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId()).collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .document(uidReceiver);
        /////////////   RECEIVER
        DocumentReference receiverContacts = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(uidReceiver).collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .document(FbUser.getCurrentUserId());
        /////////////   ELIMINAR CHAT DE ARCHIVADOS
        DocumentReference senderContactsArchivado = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId()).collection(FirebaseConstants.CONTACTOS_ARCHIVADOS)
                .document(uidReceiver);

        DocumentReference reveiverContactsArchivado = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(uidReceiver).collection(FirebaseConstants.CONTACTOS_ARCHIVADOS)
                .document(FbUser.getCurrentUserId());

        /////////////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        /////////////
        long timestamp = System.currentTimeMillis();
        ContactoChat contactoChatSender = new ContactoChat(true, timestamp);
        ContactoChat contactoChatReceiver = new ContactoChat(false, timestamp);
        /////////////
        batch.set(senderContacts, contactoChatSender, SetOptions.merge());
        batch.set(receiverContacts, contactoChatReceiver, SetOptions.merge());
        batch.delete(senderContactsArchivado);
        batch.delete(reveiverContactsArchivado);
        /////////////
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    sendMessage(uidReceiver, message, messageSendedWeakReference);
                } else {
                    MessageSended messageSended = messageSendedWeakReference.get();
                    if (messageSended != null) {
                        messageSended.onMessageSended(false);
                    }
                }
            }
        });

    }

    private static void sendMessage(String uidReceiver, Message message,
                                    WeakReference<MessageSended> messageSendedWeakReference) {

        String messageId = FirebaseFirestore.getInstance().collection(FirebaseConstants.CHATS)
                .document().getId();
        //////////////////////
        DocumentReference refMessageSender = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CANALES_CHATS)
                .document(FbUser.getCurrentUserId()).collection(FirebaseConstants.CHATS)
                .document(uidReceiver).collection(FirebaseConstants.MESSAGES).document(messageId);
        //////////////////////
        DocumentReference refMessageReceiver = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CANALES_CHATS)
                .document(uidReceiver).collection(FirebaseConstants.CHATS)
                .document(FbUser.getCurrentUserId()).collection(FirebaseConstants.MESSAGES).document(messageId);
        ////////////////////// NOTIFICATION
        DocumentReference refNotificationReceiver = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.NOTIFICATIONS)
                .document(uidReceiver)
                .collection(FirebaseConstants.MESSAGES)
                .document(messageId);
        ///////////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();
        ///////////
        batch.set(refMessageSender, message, SetOptions.merge());
        batch.set(refMessageReceiver, message, SetOptions.merge());
        batch.set(refNotificationReceiver, message, SetOptions.merge());
        ///////////
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                MessageSended messageSended = messageSendedWeakReference.get();
                if (messageSended != null) {
                    if (task.isSuccessful()) {
                        messageSended.onMessageSended(true);
                    } else {
                        messageSended.onMessageSended(false);
                    }
                }
            }
        });

    }

    public static void showMessageTimestamp(String message, long timestamp, TextView txtMessage) {
        try {
            String txtFinal;

            if (message != null && message.length() > 0) {
                txtFinal = message + "  " + TimestampConverter.getTimestamp(timestamp);
            } else {
                txtFinal = TimestampConverter.getTimestamp(timestamp);
            }
            Spannable spannable = new SpannableString(txtFinal);
            spannable.setSpan(new RelativeSizeSpan(0.7f), message.length(), txtFinal.length(), 0);
            spannable.setSpan(new ForegroundColorSpan(Color.GRAY), message.length(), txtFinal.length(), 0);

            txtMessage.setText(spannable);

        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    public static void cambiarMessageDesign(String authorMessageId, CardView cardView,
                                            TextView textView, Context context) {

        if (authorMessageId.equals(FbUser.getCurrentUserId())) {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

            RelativeLayout.LayoutParams paramsEnd = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsEnd.addRule(RelativeLayout.ALIGN_PARENT_END);
            paramsEnd.setMargins(PixelsDPUtils.convertPixelsToDp(56, context), 0, 0, 0);

            cardView.setLayoutParams(paramsEnd);
            cardView.setBackground(context.getResources().getDrawable(R.drawable.shape_mensaje_mio, null));
        } else {
            textView.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

            RelativeLayout.LayoutParams paramsStart = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            paramsStart.addRule(RelativeLayout.ALIGN_PARENT_START);
            paramsStart.setMargins(0, 0, PixelsDPUtils.convertPixelsToDp(56, context), 0);

            cardView.setLayoutParams(paramsStart);
            cardView.setBackground(context.getResources().getDrawable(R.drawable.shape_mensaje, null));
        }

    }

    public static void sendFileMessage(Uri uriData, String uidReceiver, String mensaje, String mediaFile,
                                       String extensionFile, int typeMessage, String mensajeProgressDialog,
                                       WeakReference<Activity> activityWeakReference,
                                       WeakReference<MessageSended> messageSendedWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(activityWeakReference.get(), null, null);
        progressDialogWeakReference.get().show();

        StorageReference reference = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.CANALES_CHATS + "Media")
                .child(FbUser.getCurrentUserId())
                .child(FirebaseConstants.CHATS)
                .child(uidReceiver)
                .child(mediaFile)
                .child(UUID.randomUUID().toString() + extensionFile);

        reference.putFile(uriData).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProgressDialog progressDialog = progressDialogWeakReference.get();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        String url = uri.toString();
                        Message message = new Message(uidReceiver, mensaje, url, null,
                                null, typeMessage);
                        ChatController.addUserToContacts(uidReceiver, message, messageSendedWeakReference);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                Activity activity = activityWeakReference.get();
                if (progressDialog != null && activity != null) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error al compartir el archivo...", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (progressDialog != null) {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                    progressDialog.setMessage(mensajeProgressDialog + " " + (int) progress + "%");
                }
            }
        });

    }

    public static void getThumbnailFromVideo(String uidReceiver, Uri videoUri,
                                             WeakReference<Activity> activityWeakReference,
                                             WeakReference<MessageSended> messageSendedWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(activityWeakReference.get(), null, "Compartiendo video...");
        progressDialogWeakReference.get().show();

        Glide.with(activityWeakReference.get())
                .asBitmap()
                .load(videoUri)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Activity activity = activityWeakReference.get();
                        if (activity != null) {
                            Uri thumbnailUri = BitmapUtils.getUri(resource, activity);
                            if (thumbnailUri != null) {
                                sendThumbnailToMedia(uidReceiver, thumbnailUri, videoUri,
                                        activityWeakReference, progressDialogWeakReference, messageSendedWeakReference);
                            }
                        }
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }

                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        super.onLoadFailed(errorDrawable);
                        Activity activity = activityWeakReference.get();
                        ProgressDialog progressDialog = progressDialogWeakReference.get();
                        if (activity != null && progressDialog != null) {
                            progressDialog.dismiss();
                            Toast.makeText(activity, "Error al extraer el thumbnail del video", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    private static void sendThumbnailToMedia(String uidReceiver, Uri thumbnailUri, Uri videoUri,
                                             WeakReference<Activity> activityWeakReference,
                                             WeakReference<ProgressDialog> progressDialogWeakReference,
                                             WeakReference<MessageSended> messageSendedWeakReference) {

        String folderName = UUID.randomUUID().toString();
        String fileName = UUID.randomUUID().toString();

        StorageReference referenceThumbnail = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.CANALES_CHATS + "Media")
                .child(FbUser.getCurrentUserId())
                .child(FirebaseConstants.CHATS)
                .child(uidReceiver)
                .child("video")
                .child(folderName)
                .child(fileName + ".jpg");

        referenceThumbnail.putFile(thumbnailUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                referenceThumbnail.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String thumbnailUri = uri.toString();
                        sendVideoToMedia(videoUri, thumbnailUri, uidReceiver, folderName, fileName,
                                activityWeakReference, progressDialogWeakReference, messageSendedWeakReference);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Activity activity = activityWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (activity != null && progressDialog != null) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error al publicar el thumbnail del video", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private static void sendVideoToMedia(Uri videoUri, String thumbnailUri, String uidReceiver,
                                         String folderName, String fileName,
                                         WeakReference<Activity> activityWeakReference,
                                         WeakReference<ProgressDialog> progressDialogWeakReference,
                                         WeakReference<MessageSended> messageSendedWeakReference) {

        StorageReference referenceVideo = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.CANALES_CHATS + "Media")
                .child(FbUser.getCurrentUserId())
                .child(FirebaseConstants.CHATS)
                .child(uidReceiver)
                .child("video")
                .child(folderName)
                .child(fileName + ".mp4");

        referenceVideo.putFile(videoUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                referenceVideo.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        ProgressDialog progressDialog = progressDialogWeakReference.get();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                        }
                        String videoUrl = uri.toString();
                        Message message = new Message(uidReceiver, "Video", videoUrl, null, thumbnailUri, Message.TYPE_VIDEO);
                        ChatController.addUserToContacts(uidReceiver, message, messageSendedWeakReference);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Activity activity = activityWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (activity != null && progressDialog != null) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error al publicar el video", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public static void getLastMessage(String contactId,
                                      WeakReference<TextView> txtLastMessageWeakReference,
                                      WeakReference<TextView> txtTimestampLastMessageWeakReference,
                                      WeakReference<ImageView> imgTypeMessageWeakReference,
                                      WeakReference<Context> contextWeakReference) {

        Query queryLastMessage = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CANALES_CHATS)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CHATS)
                .document(contactId)
                .collection(FirebaseConstants.MESSAGES)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limitToLast(1);

        queryLastMessage.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {

                try {

                    if (queryDocumentSnapshots != null) {
                        Message message = queryDocumentSnapshots.getDocuments().get(0).toObject(Message.class);

                        TextView txtLastMessage = txtLastMessageWeakReference.get();
                        TextView txtTimestampLastMessage = txtTimestampLastMessageWeakReference.get();
                        ImageView imgTypeMessage = imgTypeMessageWeakReference.get();
                        Context context = contextWeakReference.get();

                        if (message != null &&
                                txtLastMessage != null &&
                                txtTimestampLastMessage != null &&
                                imgTypeMessage != null &&
                                context != null) {

                            txtLastMessage.setText(message.getMessage());
                            txtTimestampLastMessage.setText(TimestampConverter.getTimestamp(message.getTimestamp()));

                            switch (message.getType()) {
                                case Message.TYPE_TEXT:
                                    imgTypeMessage.setVisibility(View.GONE);
                                    break;
                                case Message.TYPE_GIF:
                                    imgTypeMessage.setVisibility(View.VISIBLE);
                                    imgTypeMessage.setBackground(context.getDrawable(R.drawable.ic_gif));
                                    break;
                                case Message.TYPE_FOTO:

                                    if (message.getMessage().length() > 0) {
                                        txtLastMessage.setText(message.getMessage());
                                    } else {
                                        txtLastMessage.setText("FOTO");
                                    }
                                    imgTypeMessage.setVisibility(View.VISIBLE);
                                    imgTypeMessage.setBackground(context.getDrawable(R.drawable.ic_galeria_gray));
                                    break;
                                case Message.TYPE_VIDEO:
                                    imgTypeMessage.setVisibility(View.VISIBLE);
                                    imgTypeMessage.setBackground(context.getDrawable(R.drawable.ic_video_gray));

                                    break;
                                case Message.TYPE_AUDIO:
                                    imgTypeMessage.setVisibility(View.VISIBLE);
                                    imgTypeMessage.setBackground(context.getDrawable(R.drawable.ic_audio_gray));

                                    break;
                                case Message.TYPE_DOC_PDF:
                                    imgTypeMessage.setVisibility(View.VISIBLE);
                                    imgTypeMessage.setBackground(context.getDrawable(R.drawable.ic_file_gray));

                                    break;
                            }

                        }

                    }

                } catch (NullPointerException | IndexOutOfBoundsException e) {
                    e.getCause();
                }

            }
        });

    }

    public static void checkChatAsVisto(String uidReceiver) {
        HashMap<String, Object> mapMensajeVisto = new HashMap<>();
        mapMensajeVisto.put("isChatVisto", true);
        /////////
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .document(uidReceiver)
                .update(mapMensajeVisto);
    }

    public static void showDoubleCheckMessageSeen(String uidReceiver,
                                                  WeakReference<ImageView> imgMessageSeenWeakReference,
                                                  WeakReference<Context> contextWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(uidReceiver)
                .collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .document(FbUser.getCurrentUserId())
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        try {
                            ImageView imgMessageSeen = imgMessageSeenWeakReference.get();
                            Context context = contextWeakReference.get();

                            if (documentSnapshot != null && imgMessageSeen != null && context != null) {
                                boolean chatVisto = documentSnapshot.getBoolean("isChatVisto");
                                if (chatVisto) {
                                    imgMessageSeen.setBackground(context.getDrawable(R.drawable.ic_message_seen));
                                } else {
                                    imgMessageSeen.setBackground(context.getDrawable(R.drawable.ic_message_not_seen));
                                }
                            }

                        } catch (NullPointerException e) {
                            e.getMessage();
                        }
                    }
                });

    }

    public static void archivarChat(String uidReceiver, WeakReference<Context> contextWeakReference) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch batch = db.batch();

        DocumentReference documentBase = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId());

        DocumentReference chatActivo = documentBase.collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .document(uidReceiver);

        DocumentReference chatArchivado = documentBase.collection(FirebaseConstants.CONTACTOS_ARCHIVADOS)
                .document(uidReceiver);

        ContactoChat contactoChatSender = new ContactoChat(true, System.currentTimeMillis());

        batch.set(chatArchivado, contactoChatSender, SetOptions.merge());
        batch.delete(chatActivo);
        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Context context = contextWeakReference.get();
                if (context != null) {
                    if (task.isSuccessful()) {
                        Toast.makeText(context, "Chat archivado", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Error al intentar archivar el chat", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
