package com.aek.whatsapp.vista.mainfragments.chat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityOptions;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.ChatController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityChatBinding;
import com.aek.whatsapp.models.Message;
import com.aek.whatsapp.models.MessageSended;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TimestampConverter;
import com.aek.whatsapp.viewholders.chat.MessageTypeAudioViewHolder;
import com.aek.whatsapp.viewholders.chat.MessageTypeDocPDFViewHolder;
import com.aek.whatsapp.viewholders.chat.MessageTypeGIFViewHolder;
import com.aek.whatsapp.viewholders.chat.MessageTypeImagenViewHolder;
import com.aek.whatsapp.viewholders.chat.MessageTypeTextViewHolder;
import com.aek.whatsapp.viewholders.chat.MessageTypeVideoViewHolder;
import com.aek.whatsapp.vista.mainfragments.CameraFragment;
import com.aek.whatsapp.vista.mainfragments.camera.CameraActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ver.VerDocPDFActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ver.VerFotoActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ver.VerGifActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ver.VerVideoPIPModeActivity;
import com.aek.whatsapp.vista.perfil.PerfilUsuarioActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.giphy.sdk.core.models.Media;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.GPHContentType;
import com.giphy.sdk.ui.GPHSettings;
import com.giphy.sdk.ui.Giphy;
import com.giphy.sdk.ui.themes.GPHTheme;
import com.giphy.sdk.ui.themes.GridType;
import com.giphy.sdk.ui.views.GPHMediaView;
import com.giphy.sdk.ui.views.GiphyDialogFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements MessageSended, GiphyDialogFragment.GifSelectionListener, Handler.Callback {

    public static final int CODE_RECOGNIZE_SPEECH = 44;
    public static final int CODE_DOC_PDF = 22;
    public static final int CODE_VIDEO_GALLERY = 33;
    public static final int CODE_FOTO_GALLERY = 55;
    public static final int CODE_AUDIO = 66;
    ///////////////////
    public static final String UID = "KeyUID";
    private String uidReceiver;
    ///////////////////
    private ActivityChatBinding binding;
    private Toolbar toolbar;
    private CircleImageView imgUser;
    private TextView txtNombreUser, txtWSChatVacio, txtMensaje;
    private ImageButton btnFinish, btnGif, btnAdjuntarArchivos, btnCamera;
    private RelativeLayout btnEnviarMensaje;
    private ImageView imgBtnEnviarMensaje;
    private boolean isTypeMessageChanged = false, isViewAdjuntarArchivosInvisible = true;
    private FloatingActionButton fabAdjuntarDocumento, fabAdjuntarFotoGaleria, fabAdjuntarVideo, fabAdjuntarAudio;
    private View viewAdjuntarArchivos;
    private RecyclerView recyclerViewMensajes;
    private FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder> adapter;
    ///////////////
    private MediaPlayer mediaPlayer;
    private int playingPosition = -1;
    private Handler handlerActualizarUI;
    private MessageTypeAudioViewHolder viewHolderPlaying;
    public static final int MSG_ACTUALIZAR_SEEK_BAR = 12345;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();

        init();
        Giphy.INSTANCE.configure(getBaseContext(), getString(R.string.giphy_api_key), true);
        setListeners();
        FbUser.getUserBasicData(uidReceiver,
                new WeakReference<>(imgUser),
                new WeakReference<>(txtNombreUser),
                new WeakReference<>(this));

        ChatController.checkChatAsVisto(uidReceiver);

        getMensajes();
    }

    private void getMensajes() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CANALES_CHATS)
                .document(FbUser.getCurrentUserId()).collection(FirebaseConstants.CHATS)
                .document(uidReceiver).collection(FirebaseConstants.MESSAGES)
                .orderBy("timestamp", Query.Direction.ASCENDING);
        ////////////////
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class).build();

        adapter = new FirestoreRecyclerAdapter<Message, RecyclerView.ViewHolder>(options) {

            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                switch (viewType) {
                    case Message.TYPE_TEXT:
                        View viewText = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_text, parent, false);
                        return new MessageTypeTextViewHolder(viewText);
                    case Message.TYPE_GIF:
                        View viewGIF = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_gif, parent, false);
                        return new MessageTypeGIFViewHolder(viewGIF);
                    case Message.TYPE_DOC_PDF:
                        View viewDocPDF = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_doc_pdf, parent, false);
                        return new MessageTypeDocPDFViewHolder(viewDocPDF);
                    case Message.TYPE_VIDEO:
                        View viewVideo = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_video, parent, false);
                        return new MessageTypeVideoViewHolder(viewVideo);
                    case Message.TYPE_FOTO:
                        View viewImagen = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_imagen, parent, false);
                        return new MessageTypeImagenViewHolder(viewImagen);
                    case Message.TYPE_AUDIO:
                        View viewAudio = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_message_type_audio, parent, false);
                        return new MessageTypeAudioViewHolder(viewAudio);
                    default:
                        throw new IllegalArgumentException("Invalid view type message chat....");
                }
            }

            @Override
            protected void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position, @NonNull Message message) {
                switch (holder.getItemViewType()) {
                    case Message.TYPE_TEXT:
                        TextView txtMessageTimestampText = ((MessageTypeTextViewHolder) holder).txtMensaje;
                        CardView cardViewText = ((MessageTypeTextViewHolder) holder).cardView;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampText);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewText, txtMessageTimestampText, getBaseContext());

                        break;
                    case Message.TYPE_GIF:
                        TextView txtMessageTimestampGIF = ((MessageTypeGIFViewHolder) holder).txtMensaje;
                        CardView cardViewGIF = ((MessageTypeGIFViewHolder) holder).cardView;
                        GPHMediaView gphMediaView = ((MessageTypeGIFViewHolder) holder).gphMediaView;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampGIF);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewGIF, txtMessageTimestampGIF, getBaseContext());

                        gphMediaView.setMediaWithId(message.getGiphyMediaId(), RenditionType.original, null, null);

                        gphMediaView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(), VerGifActivity.class);
                                intent.putExtra(VerGifActivity.GIF_ID, message.getGiphyMediaId());
                                startActivity(intent);
                            }
                        });

                        break;
                    case Message.TYPE_DOC_PDF:
                        TextView txtMessageTimestampPDF = ((MessageTypeDocPDFViewHolder) holder).txtMensaje;
                        CardView cardViewPDF = ((MessageTypeDocPDFViewHolder) holder).cardView;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampPDF);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewPDF, txtMessageTimestampPDF, getBaseContext());

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(), VerDocPDFActivity.class);
                                intent.putExtra(VerDocPDFActivity.PDF_URL, message.getDataUrl());
                                startActivity(intent);
                            }
                        });

                        break;
                    case Message.TYPE_VIDEO:
                        TextView txtMessageTimestampVideo = ((MessageTypeVideoViewHolder) holder).txtMensaje;
                        CardView cardViewVideo = ((MessageTypeVideoViewHolder) holder).cardView;
                        ImageView imgThumbnailVideo = ((MessageTypeVideoViewHolder) holder).imgThumbnail;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampVideo);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewVideo, txtMessageTimestampVideo, getBaseContext());

                        Glide.with(getBaseContext()).load(message.getVideoThumbnailUrl()).into(imgThumbnailVideo);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(getBaseContext(), VerVideoPIPModeActivity.class);
                                intent.putExtra(VerVideoPIPModeActivity.VIDEO_URL, message.getDataUrl());
                                startActivity(intent);
                            }
                        });

                        break;
                    case Message.TYPE_FOTO:
                        TextView txtMessageTimestampImagen = ((MessageTypeImagenViewHolder) holder).txtMensaje;
                        CardView cardViewImagen = ((MessageTypeImagenViewHolder) holder).cardView;
                        ImageView imagen = ((MessageTypeImagenViewHolder) holder).imagen;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampImagen);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewImagen, txtMessageTimestampImagen, getBaseContext());

                        Glide.with(getBaseContext()).load(message.getDataUrl()).into(imagen);

                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Pair<View, String> pairImage = Pair.create(imagen, imagen.getTransitionName());
                                ActivityOptions activityOptions = ActivityOptions.makeSceneTransitionAnimation(ChatActivity.this, pairImage);

                                Intent intent = new Intent(getBaseContext(), VerFotoActivity.class);
                                intent.putExtra(VerFotoActivity.FOTO_URL, message.getDataUrl());
                                startActivity(intent, activityOptions.toBundle());
                            }
                        });

                        break;
                    case Message.TYPE_AUDIO:
                        TextView txtMessageTimestampAudio = ((MessageTypeAudioViewHolder) holder).txtMensaje;
                        CardView cardViewAudio = ((MessageTypeAudioViewHolder) holder).cardView;
                        ImageButton btnPlayAudio = ((MessageTypeAudioViewHolder) holder).btnPlay;
                        SeekBar seekBarAudio = ((MessageTypeAudioViewHolder) holder).seekBar;

                        ChatController.showMessageTimestamp(message.getMessage(), message.getTimestamp(), txtMessageTimestampAudio);
                        ChatController.cambiarMessageDesign(message.getUidAuthor(), cardViewAudio, txtMessageTimestampAudio, getBaseContext());

                        if (position == playingPosition) {
                            viewHolderPlaying = ((MessageTypeAudioViewHolder) holder);
                            updatePlayingView();
                        } else {
                            updateNonPlayingView((MessageTypeAudioViewHolder) holder);
                        }

                        btnPlayAudio.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (handlerActualizarUI == null) {
                                    handlerActualizarUI = new Handler(ChatActivity.this);
                                }

                                if (holder.getAdapterPosition() == playingPosition) {

                                    if (mediaPlayer.isPlaying()) {
                                        mediaPlayer.pause();
                                    } else {
                                        mediaPlayer.start();
                                    }

                                } else {
                                    playingPosition = holder.getAdapterPosition();
                                    if (mediaPlayer != null) {
                                        if (null != viewHolderPlaying) {
                                            updateNonPlayingView(viewHolderPlaying);
                                        }
                                        mediaPlayer.release();
                                    }
                                    viewHolderPlaying = ((MessageTypeAudioViewHolder) holder);
                                    startMediaPlayer(message.getDataUrl());
                                }
                                updatePlayingView();
                            }
                        });

                        seekBarAudio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                            @Override
                            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                                if (fromUser) {
                                    mediaPlayer.seekTo(progress);
                                }
                            }

                            @Override
                            public void onStartTrackingTouch(SeekBar seekBar) {

                            }

                            @Override
                            public void onStopTrackingTouch(SeekBar seekBar) {

                            }
                        });

                        break;
                }
            }

            @Override
            public int getItemViewType(int position) {
                return getItem(position).getType();
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();
                int nrMensajes = getItemCount();

                if (nrMensajes == 0) {
                    txtWSChatVacio.setVisibility(View.VISIBLE);
                } else {
                    txtWSChatVacio.setVisibility(View.GONE);
                    recyclerViewMensajes.smoothScrollToPosition(adapter.getItemCount());
                }
            }
        };

        recyclerViewMensajes.setAdapter(adapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    private void setListeners() {
        txtMensaje.addTextChangedListener(textWatcher);

        btnEnviarMensaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = getMensaje().trim();

                if (mensaje.length() > 0) {
                    Message message = new Message(uidReceiver, getMensaje(), null,
                            null, null, Message.TYPE_TEXT);
                    ChatController.addUserToContacts(uidReceiver, message, new WeakReference<>(ChatActivity.this));

                } else {
                    startActivityVoiceToText();
                }
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAdjuntarArchivos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViewAdjuntarArchivos();
            }
        });

        btnGif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GPHSettings settings = new GPHSettings();
                settings.setGridType(GridType.waterfall);
                settings.setTheme(GPHTheme.Dark);
                settings.setStickerColumnCount(3);
                settings.setMediaTypeConfig(new GPHContentType[]{GPHContentType.gif, GPHContentType.sticker,
                        GPHContentType.text, GPHContentType.emoji});
                GiphyDialogFragment.Companion.newInstance(settings)
                        .show(getSupportFragmentManager(), ChatActivity.class.getName());
            }
        });

        fabAdjuntarDocumento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViewAdjuntarArchivos();
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("application/pdf"), ChatActivity.CODE_DOC_PDF);
            }
        });

        fabAdjuntarVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViewAdjuntarArchivos();
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("video/mp4"), ChatActivity.CODE_VIDEO_GALLERY);
            }
        });

        fabAdjuntarFotoGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViewAdjuntarArchivos();
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("imagen/*"), ChatActivity.CODE_FOTO_GALLERY);
            }
        });

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ChatActivity.this, CameraActivity.class);
                intent.putExtra(CameraFragment.TYPE_ACTION_CAMERA, CameraFragment.ACTION_MESSAGE);
                intent.putExtra(ChatActivity.UID, uidReceiver);
                startActivity(intent);
            }
        });

        fabAdjuntarAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleViewAdjuntarArchivos();
                startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("audio/*"), ChatActivity.CODE_AUDIO);
            }
        });

        txtNombreUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ChatActivity.this, PerfilUsuarioActivity.class)
                .putExtra(ChatActivity.UID, uidReceiver));
            }
        });
    }

    private void startActivityVoiceToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Di algo...");
        try {
            startActivityForResult(intent, CODE_RECOGNIZE_SPEECH);
        } catch (ActivityNotFoundException e) {
            e.getCause();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case ChatActivity.CODE_RECOGNIZE_SPEECH:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> resultado = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (resultado != null) {
                        txtMensaje.setText(resultado.get(0));
                    }
                }
                break;

            case ChatActivity.CODE_DOC_PDF:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri pdfUri = data.getData();
                        if (pdfUri != null) {
                            ChatController.sendFileMessage(pdfUri, uidReceiver, "Documento PDF",
                                    "docPDF", ".pdf", Message.TYPE_DOC_PDF,
                                    "Compartiendo pdf...", new WeakReference<>(this),
                                    new WeakReference<>(this));
                        }
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                }
                break;

            case ChatActivity.CODE_VIDEO_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri videoUri = data.getData();
                        if (videoUri != null) {
                            ChatController.getThumbnailFromVideo(uidReceiver, videoUri,
                                    new WeakReference<>(this),
                                    new WeakReference<>(this));
                        }
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                }
                break;

            case ChatActivity.CODE_FOTO_GALLERY:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imageUri = data.getData();
                        if (imageUri != null) {
                            ChatController.sendFileMessage(imageUri, uidReceiver, "Foto",
                                    "fotos", ".jpeg", Message.TYPE_FOTO,
                                    "Compartiendo foto...",
                                    new WeakReference<>(this),
                                    new WeakReference<>(this));
                        }
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                }
                break;

            case ChatActivity.CODE_AUDIO:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri audioUri = data.getData();
                        if (audioUri != null) {
                            ChatController.sendFileMessage(audioUri, uidReceiver, "AUDIO",
                                    "audio", ".mp3", Message.TYPE_FOTO,
                                    "Compartiendo audio...",
                                    new WeakReference<>(this),
                                    new WeakReference<>(this));
                        }
                    } catch (NullPointerException e) {
                        e.getMessage();
                    }
                }
                break;
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String mensaje = getMensaje().trim();

            if (mensaje.length() > 0) {

                if (!isTypeMessageChanged) {
                    AnimUtils.changeIconButtonSendMessage(R.drawable.ic_send, imgBtnEnviarMensaje);
                    AnimUtils.animateIconsChat(btnAdjuntarArchivos, btnCamera, 38, getBaseContext());
                    isTypeMessageChanged = true;
                }

            } else {
                AnimUtils.changeIconButtonSendMessage(R.drawable.ic_mic, imgBtnEnviarMensaje);
                AnimUtils.animateIconsChat(btnAdjuntarArchivos, btnCamera, 0, getBaseContext());
                isTypeMessageChanged = false;
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private String getMensaje() {
        return txtMensaje.getText().toString();
    }

    private void init() {
        this.toolbar = binding.toolbarChat;
        setSupportActionBar(toolbar);
        this.imgUser = binding.imgUserChat;
        this.txtNombreUser = binding.txtNombreUserChat;
        this.txtWSChatVacio = binding.txtWSChatVacio;
        this.txtMensaje = binding.txtMensaje;
        this.btnFinish = binding.btnFinish;
        this.btnAdjuntarArchivos = binding.btnChatAdjuntarArchivo;
        this.btnCamera = binding.btnChatEnviarFoto;
        this.btnGif = binding.btnChatEnviarGif;
        this.btnEnviarMensaje = binding.btnChatEnviarMensaje;
        this.imgBtnEnviarMensaje = binding.imgBtnEnviarMensaje;
        this.viewAdjuntarArchivos = findViewById(R.id.layoutAdjuntarArchivos);
        this.fabAdjuntarDocumento = binding.layoutAdjuntarArchivos.fabAdjuntarDocumento;
        this.fabAdjuntarFotoGaleria = binding.layoutAdjuntarArchivos.fabAdjuntarFotoGaleria;
        this.fabAdjuntarVideo = binding.layoutAdjuntarArchivos.fabAdjuntarVideo;
        this.fabAdjuntarAudio = binding.layoutAdjuntarArchivos.fabAdjuntarAudio;
        this.recyclerViewMensajes = binding.recyclerViewMensajes;
    }

    private void getExtras() {
        try {
            this.uidReceiver = getIntent().getExtras().getString(ChatActivity.UID, null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_chat, menu);

        try {
            menu.getItem(2).getSubMenu().getItem(6).getSubMenu().clearHeader();
        } catch (IndexOutOfBoundsException e) {
            e.getCause();
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuArchivarChat:
                ChatController.archivarChat(uidReceiver, new WeakReference<>(getBaseContext()));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (!isViewAdjuntarArchivosInvisible) {
            toggleViewAdjuntarArchivos();
        } else {
            super.onBackPressed();
        }
    }

    private void toggleViewAdjuntarArchivos() {
        AnimUtils.showViewAdjuntarArchivos(viewAdjuntarArchivos, isViewAdjuntarArchivosInvisible, getBaseContext());
        isViewAdjuntarArchivosInvisible = !isViewAdjuntarArchivosInvisible;
    }

    @Override
    public void onMessageSended(boolean messageIsSended) {
        if (messageIsSended) {
            txtMensaje.setText("");
        } else {
            Toast.makeText(this, "Error al intentar enviar el mensaje", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void didSearchTerm(@NonNull String s) {

    }

    @Override
    public void onDismissed(@NonNull GPHContentType gphContentType) {

    }

    @Override
    public void onGifSelected(@NonNull Media media, @Nullable String s, @NonNull GPHContentType gphContentType) {
        String title = media.getTitle() != null ? media.getTitle() : "GIF";
        Message message = new Message(uidReceiver, title, null, media.getId(),
                null, Message.TYPE_GIF);
        ChatController.addUserToContacts(uidReceiver, message, new WeakReference<>(this));
    }

    @Override
    public boolean handleMessage(@NonNull android.os.Message msg) {
        switch (msg.what) {
            case ChatActivity.MSG_ACTUALIZAR_SEEK_BAR:
                viewHolderPlaying.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                handlerActualizarUI.sendEmptyMessageDelayed(ChatActivity.MSG_ACTUALIZAR_SEEK_BAR, 100);
                return true;
        }
        return false;
    }

    private void startMediaPlayer(String urlAudio) {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), Uri.parse(urlAudio));
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMediaPlayer();
            }
        });
        mediaPlayer.start();
    }

    private void releaseMediaPlayer() {
        if (null != viewHolderPlaying) {
            updateNonPlayingView(viewHolderPlaying);
        }
        mediaPlayer.release();
        mediaPlayer = null;
        playingPosition = -1;
    }

    private void updatePlayingView() {
        viewHolderPlaying.seekBar.setMax(mediaPlayer.getDuration());
        viewHolderPlaying.seekBar.setProgress(mediaPlayer.getCurrentPosition());
        viewHolderPlaying.seekBar.setEnabled(true);
        if (mediaPlayer.isPlaying()) {
            handlerActualizarUI.sendEmptyMessageDelayed(ChatActivity.MSG_ACTUALIZAR_SEEK_BAR, 100);
            viewHolderPlaying.btnPlay.setImageResource(R.drawable.ic_pause);
        } else {
            handlerActualizarUI.removeMessages(ChatActivity.MSG_ACTUALIZAR_SEEK_BAR);
            viewHolderPlaying.btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void updateNonPlayingView(MessageTypeAudioViewHolder holder) {
        if (holder != null) {
            if (holder == viewHolderPlaying) {
                if (handlerActualizarUI != null) {
                    handlerActualizarUI.removeMessages(ChatActivity.MSG_ACTUALIZAR_SEEK_BAR);
                }
            }
            holder.seekBar.setEnabled(false);
            holder.seekBar.setProgress(0);
            holder.btnPlay.setImageResource(R.drawable.ic_play);
        }
    }

    private void stopPlayer() {
        if (mediaPlayer != null) {
            releaseMediaPlayer();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayer();
    }
}