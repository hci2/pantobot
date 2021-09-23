package at.ac.univie.pantobot.view;

/*
Useful links:

http://blog.plotprojects.com/developer/2015/09/30/dangerous-permissions-on-android-marshmallow/
https://forums.xamarin.com/discussion/59965/record-audio-project-problems-open-failed-eacces-permission-denied
http://stackoverflow.com/questions/7569937/unable-to-add-window-android-view-viewrootw44da9bc0-permission-denied-for-t#answer-34061521
http://stackoverflow.com/questions/32224452/android-unable-to-add-window-permission-denied-for-this-window-type
 */

import ai.api.android.AIConfiguration;
import ai.api.model.AIRequest;
import android.Manifest;
import android.app.Service;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import at.ac.univie.pantobot.R;
import at.ac.univie.pantobot.controller.APITextConnector;
import at.ac.univie.pantobot.controller.MessageAdapter;
import at.ac.univie.pantobot.controller.NewTopicController;
import at.ac.univie.pantobot.controller.TextToSpeechManager;
import at.ac.univie.pantobot.model.DBAccessor;
import at.ac.univie.pantobot.model.Message;
import at.ac.univie.pantobot.model.Topic;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Our only Activity to display the chat messages
 */
public class HomeActivity extends AppCompatActivity {

    //for new chart activity
    public final static String EXTRA_TOPICS = "univie.ac.at.meineinkaufswagerl.TOPICS";
    private ArrayList<Topic> topicArrayList = new ArrayList<>();

    private final AIConfiguration config = new AIConfiguration("7fa10e7fccb145b9a71bc81510840654",
            AIConfiguration.SupportedLanguages.English,
            AIConfiguration.RecognitionEngine.System);

    private Toolbar toolbar;
    private ListView msgListView;
    private EditText userInputText;
    private ImageButton sendButton;
    private ImageButton voiceInputButton;
    private ArrayAdapter arrayAdapter;
    private ArrayList<Message> messages = new ArrayList<>();
    private APITextConnector apiTextConnector = null;
    private TextToSpeechManager ttsManager = null;
    protected HomeActivity homeActivity = null;
    private DBAccessor dbAccessor;
    private ActionMenuItemView actionMenuItemView;
    private NewTopicController newTopicController;


    /**
     * create method
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initializeVariables();

        initializeMsgView();

        initializeListener();

    }


    /**
     * Initialize Variables
     */
    private void initializeVariables() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ttsManager = new TextToSpeechManager();
        ttsManager.init(this);

        this.homeActivity = this;
        msgListView= (ListView) findViewById(R.id.msg_listview);
        userInputText = (EditText) findViewById(R.id.userInputText);
        sendButton= (ImageButton) findViewById(R.id.sendButton);
        voiceInputButton = (ImageButton) findViewById(R.id.voiceInputButton);
        dbAccessor = new DBAccessor(getApplicationContext());
        newTopicController = new NewTopicController(this);

        userInputText.addTextChangedListener(textWatcher);
    }


    /**
     * Initializes the message view and creates first welcome messages
     */
    private void initializeMsgView() {
        msgListView.setTranscriptMode(ListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        msgListView.setStackFromBottom(true);
        arrayAdapter = new MessageAdapter(this, R.layout.message, messages);
        msgListView.setAdapter(arrayAdapter);
        addMsg("Hi there! I'm PantoBot.", 1);
        addMsg("How can I help you today?", 1);
    }


    /**
     * initializes all the button listeners
     */
    private void initializeListener() {

        initializeTextInputListener();

        initializeSpeechInputListener();

    }


    /**
     * initializes the text input button listener
     */
    private void initializeTextInputListener() {
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String userInput= userInputText.getText().toString();
                addMsg(userInput, 0);
                if(!(userInput.isEmpty())){
                    apiCall(userInput);
                } else{

                }
                userInputText.setText("");
            }
        });
    }


    /**
     * initializes the listener for speech input button
     */
    private void initializeSpeechInputListener() {
        voiceInputButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    final String[] permissions = new String[]{Manifest.permission.RECORD_AUDIO};
                    ActivityCompat.requestPermissions(HomeActivity.this, permissions, 0);
                }
                if(Build.VERSION.SDK_INT >= 23) {
                    if (!Settings.canDrawOverlays(HomeActivity.this)) {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + getPackageName()));
                        startActivityForResult(intent, 1234);
                    }
                }
                else
                {
                    Intent intent = new Intent(HomeActivity.this, Service.class);
                    startService(intent);
                }
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now");
                try {
                    startActivityForResult(intent, 100);
                } catch (ActivityNotFoundException a) {
                    Toast.makeText(getApplicationContext(),
                            "Not supported",
                            Toast.LENGTH_SHORT).show();
                }
            }

        });
    }


    /**
     * Creates a new Request to be sent to api.ai
     * @param userInput String to send
     */
    private void apiCall(String userInput) {
        apiTextConnector = new APITextConnector(homeActivity);//
        AIRequest aiRequest = new AIRequest();
        aiRequest.setQuery(userInput);
        apiTextConnector.execute(aiRequest);
    }


    /**
     * Listener for TextInput
     * @param requestCode int requestCode
     * @param resultCode int resultCode
     * @param data Intent TextData
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 100: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userInput = result.get(0);
                    addMsg(userInput, 0);
                    if(!(userInput.isEmpty())){
                        apiCall(userInput);
                    } else{
                        Toast.makeText(getApplicationContext(), "Please don´t send an empty text!", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            }

        }
    }


    //TextWatcher to watch EditText, weather or not it's empty
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {}

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            String userInput= userInputText.getText().toString();
            if(!(userInput.trim().isEmpty())) {
                voiceInputButton.setVisibility(View.INVISIBLE);
                sendButton.setVisibility(View.VISIBLE);
            } else {
                voiceInputButton.setVisibility(View.VISIBLE);
                sendButton.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {}
    };


    /**
     * initializes menus on action bar
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar, menu);

        return true;
    }


    /**
     * Listener for action bar menu on click
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_notification:
                Topic topic = newTopicController.getNewTopic();
                if(topic!= null){
                    addMsg(newTopicController.toString(topic), 1);
                    deactivateNotificationIcon(R.id.action_notification);
                }
                return true;
            case R.id.action_voiceoutput:
                readLastMsg();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }


    /**
     * turns notification bell icon to inactive
     */
    public void deactivateNotificationIcon(int icon_id) {
        actionMenuItemView = (ActionMenuItemView) findViewById(icon_id);
        actionMenuItemView.getItemData().setIcon(R.drawable.ic_notifications_none);
    }


    /**
     * Adds a Message to the chat
     * @param text String Text to display
     * @param msg_type int from Chatbot(1) or User(0)
     */
    public void addMsg(String text, int msg_type) {
        Message msg = new Message(text, msg_type);
        messages.add(msg);
        arrayAdapter.notifyDataSetChanged();
    }


    /**
     * Reads the last message to the User
     */
    public void readLastMsg() {
        ttsManager.addQueue(messages.get(messages.size() - 1).getText());
    }


    /**
     * Method to access the TextToSpeechManager instance
     * @return TextToSpeechManager instance of activity
     */
    public TextToSpeechManager getTtsManager(){ return this.ttsManager;}


    /**
     * This method set the arraylist of topics in the homeactivity
     * @param topicArrayList a arraylist containing ascending ordered topics
     */
    public void setTopicArrayList(ArrayList<Topic> topicArrayList){
        this.topicArrayList =topicArrayList;
    }


    /**
     * This method is used to return an subArrayList of the last amount of elements selected per splitsize
     * @param arrayListToSplit the arraylist to split
     * @param splitSize the size of the new array and the number of the last elements to add
     * @return the subArraylist which is shorten
     */
    private ArrayList<Topic> splitArrayListTopics(ArrayList<Topic> arrayListToSplit, int splitSize){
        ArrayList<Topic> smallArrayListTopics = new ArrayList<>();
        for(int i=arrayListToSplit.size()-splitSize; i<arrayListToSplit.size();i++){
            smallArrayListTopics.add(arrayListToSplit.get(i));
        }
        return smallArrayListTopics;
    }


    /**
     * The method of the imageview to get to the chart activity
     * @param v the current view, in our case it will be the home actitivy xml
     */
    public void goToChartActivity(View v) {
        Intent intent= new Intent(this, ChartActivity.class);

        if(topicArrayList.size()>=3){
            ArrayList<Topic> topics = splitArrayListTopics(topicArrayList, 3);
            intent.putExtra(EXTRA_TOPICS,topics);
        }
        startActivity(intent);
    }


}
