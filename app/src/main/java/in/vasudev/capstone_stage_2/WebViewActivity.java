package in.vasudev.capstone_stage_2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.vasudev.capstone_stage_2.utils.MyIntentUtils;
import in.vasudev.capstone_stage_2.utils.MyStringUtils;


public class WebViewActivity extends AppCompatActivity {

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    private String mUrl;

    private Intent mShareIntent;

    public static void startActivity(Context context, String url, String title) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(MyIntentUtils.EXTRA_URL, url);
        intent.putExtra(MyIntentUtils.EXTRA_TITLE, title);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setUpProgressBar();
        setUpWebView();

        mUrl = getIntent().getStringExtra(MyIntentUtils.EXTRA_URL);
        if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        }

        String title = getIntent().getStringExtra(MyIntentUtils.EXTRA_TITLE);
        setTitle(title);
        prepareShareIntent(title);
    }

    private void setUpWebView() {
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setSaveFormData(false);
        webSettings.setJavaScriptEnabled(true);
        webSettings.setSupportZoom(true);       //Zoom Control on web (You don't need this
        //if ROM supports Multi-Touch
        webSettings.setBuiltInZoomControls(true); //Enable Multitouch if supported by ROM
//        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        mWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                mProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });

        mWebView.setWebChromeClient(new WebChromeClient() {

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                mProgress.setProgress(newProgress);
            }
        });
    }

    private void setUpProgressBar() {
        mProgress.setIndeterminate(false);
        mProgress.setMax(100);
    }

    private void prepareShareIntent(String title) {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
        mShareIntent.removeExtra(Intent.EXTRA_TEXT);
        String textToShare = new StringBuilder()
                .append(getString(R.string.hey_checkout_reddit_post))
                .append(MyStringUtils.NEW_LINE)
                .append(title)
                .append(MyStringUtils.NEW_LINE)
                .append(mUrl).toString();
        mShareIntent
                .putExtra(Intent.EXTRA_TEXT,
                        textToShare);
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.reddit_post));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.web_view, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_share) {
            startActivity(mShareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            return;
        }
        super.onBackPressed();
    }

}
