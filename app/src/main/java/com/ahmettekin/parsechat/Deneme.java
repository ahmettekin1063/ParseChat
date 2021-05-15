package com.ahmettekin.parsechat;

import android.app.Activity;
import android.content.Context;
import android.widget.Toast;

import com.ahmettekin.parsechat.view.activity.ChatActivity;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.livequery.ParseLiveQueryClient;
import com.parse.livequery.SubscriptionHandling;

import java.util.List;

public class Deneme {

    public static void changeChatLive(Context context) {
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<ParseObject> parseQuery = ParseQuery.getQuery("Message");
        parseQuery.setLimit(50);
        parseQuery.orderByAscending("createdAt");
        SubscriptionHandling<ParseObject> subscriptionHandling=
        parseLiveQueryClient.subscribe(parseQuery);
        subscriptionHandling.handleEvents( new SubscriptionHandling.HandleEventsCallback<ParseObject>() {
            @Override
            public void onEvents(ParseQuery<ParseObject> query, SubscriptionHandling.Event event, ParseObject object) {
                ((Activity)context). runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        query.findInBackground(new FindCallback<ParseObject>() {
                            @Override
                            public void done(List<ParseObject> objects, ParseException e) {
                                if (e == null) {
                                    ((ChatActivity)context).getMessagesInCurrentRoom(objects);
                                } else {
                                    Toast.makeText(context.getApplicationContext(),e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

}
