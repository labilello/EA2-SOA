package ar.com.lbilello.soa.models;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Calendar;
import java.util.Date;

import ar.com.lbilello.soa.Main2Activity;


public class TokenLive implements Runnable {

    private final int DURATION_TOKEN = 2;
    private Calendar finishTime;


    public TokenLive(Calendar tokenTime) {
        tokenTime.set(Calendar.MINUTE, tokenTime.get(Calendar.MINUTE) + DURATION_TOKEN);
        this.finishTime = tokenTime;

    }

    @Override
    public void run() {


        Message msg;
        Bundle bundle = new Bundle();
        Handler handler = Main2Activity.getHandler();

        Calendar now = Calendar.getInstance();
        long diff = finishTime.getTimeInMillis() - now.getTimeInMillis();

        long minutes = diff / 60000 ;
        long seconds = (diff % 60000) / 1000;

        while ( diff > 0 ) {

            bundle.putLong("H", seconds);
            bundle.putLong("M", minutes);

            msg = new Message();
            msg.setData( bundle );
            synchronized ( handler ) {
                handler.sendMessage( msg );
            }

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            bundle.clear();
            now = Calendar.getInstance();
            diff = finishTime.getTimeInMillis() - now.getTimeInMillis();

            minutes = diff / 60000 ;
            seconds = (diff % 60000) / 1000;
        }
    }
}
