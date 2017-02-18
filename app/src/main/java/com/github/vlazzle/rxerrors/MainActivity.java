package com.github.vlazzle.rxerrors;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.io.IOException;

import rx.Subscriber;
import rx.exceptions.Exceptions;
import rx.subjects.PublishSubject;
import timber.log.Timber;

/**
 * See also:
 * - http://blog.danlew.net/2015/12/08/error-handling-in-rxjava/
 * - https://github.com/ReactiveX/RxJava/wiki/Plugins#rxjavahooks
 */
public class MainActivity extends AppCompatActivity {

    private final Subscriber<String> subscriber = new Subscriber<String>() {
        @Override
        public void onCompleted() {
            Timber.d("onCompleted");
        }

        /**
         * Handle expected errors, propagate the rest to be thrown
         */
        @Override
        public void onError(Throwable e) {
            if (e instanceof IOException) {
                showNetworkErrorMessage((IOException) e);
            } else {
                throw Exceptions.propagate(e);
            }
        }

        @Override
        public void onNext(String s) {
            Timber.d("onNext %s", s);
            if (shouldError) {
                /** This will be handled in {@link #onError(Throwable)}. */
                throw new RuntimeException();
            }
        }
    };

    private boolean shouldError;

    private PublishSubject<String> subject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        subject = PublishSubject.create();
        subject.subscribe(subscriber);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                subject.onNext("hi");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_error_next) {
            shouldError = true;
            return true;
        }

        if (id == R.id.action_error_now) {
            subject.onError(new Throwable("oh noes!"));
            return true;
        }

        if (id == R.id.action_error_now) {
            subject.onError(new Throwable("oh noes!"));
            return true;
        }

        if (id == R.id.action_finish) {
            subject.onCompleted();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }



    /**
     * Let the user know that there was a network error.
     */
    private void showNetworkErrorMessage(IOException e) {
        Timber.e(e);
    }
}
