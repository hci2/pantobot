package at.ac.univie.pantobot.controller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import at.ac.univie.pantobot.R;
import at.ac.univie.pantobot.model.Message;

import java.util.List;

/**
 * Class to manage the displayed TextMessages in the view
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    private Context context;
    private int resource;
    private List<Message> objects;

    /**
     * Constructor
     * @param context Context of the activity
     * @param resource int The List
     * @param objects List<Message> The Messages
     */
    public MessageAdapter(Context context, int resource, List<Message> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    /**
     * Get a specific item in list view
     * @param position item id
     * @param convertView item view
     * @param parent list view
     * @return Item View
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        TextView msg_content;
        ImageView msg_chart;

        if (convertView == null) {
            LayoutInflater inflater = ((Activity) getContext()).getLayoutInflater();
            convertView = inflater.inflate(this.resource, parent, false);
        }

        LinearLayout bot_frame = (LinearLayout) convertView.findViewById(R.id.bot_frame);
        LinearLayout client_frame = (LinearLayout) convertView.findViewById(R.id.client_frame);

        Message item = objects.get(position);

        if(item != null) {
            if (item.getMsg_type() == 0) { //client
                client_frame.setVisibility(View.VISIBLE);
                bot_frame.setVisibility(View.GONE);

                msg_content = (TextView) convertView.findViewById(R.id.msg_client_textview);
                msg_content.setTextColor(Color.WHITE);
                msg_content.setText(item.getText());
            }
            else if (item.getMsg_type() == 2) { //chart
                bot_frame.setVisibility(View.VISIBLE);
                client_frame.setVisibility(View.GONE);

                msg_chart = (ImageView) convertView.findViewById(R.id.msg_bot_imageview);
                msg_chart.setVisibility(View.VISIBLE);
                msg_chart.setImageResource(R.drawable.chart_lupe);

                msg_content = (TextView) convertView.findViewById(R.id.msg_bot_textview);
                msg_content.setText(item.getText());
            }
            else{ //bot
                bot_frame.setVisibility(View.VISIBLE);
                client_frame.setVisibility(View.GONE);

                msg_chart = (ImageView) convertView.findViewById(R.id.msg_bot_imageview);
                msg_chart.setVisibility(View.GONE);

                msg_content = (TextView) convertView.findViewById(R.id.msg_bot_textview);
                msg_content.setTextColor(Color.BLACK);

                msg_content.setMovementMethod(LinkMovementMethod.getInstance());
                msg_content.setText(Html.fromHtml(item.getText()));
            }

        }

        return convertView;
    }
}
