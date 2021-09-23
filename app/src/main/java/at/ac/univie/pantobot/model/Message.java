package at.ac.univie.pantobot.model;

public class Message {

    private String text; //is set to src when msg_type == 2(chart)
    private int msg_type; //0 = user, 1 = bot, 2 = chart

    public Message (String text, int msg_type) {
        this.text = text;
        this.msg_type = msg_type;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

     public int getMsg_type() {
        return msg_type;
    }

    public void setMsg_type(int msg_type) {
        this.msg_type = msg_type;
    }
}
