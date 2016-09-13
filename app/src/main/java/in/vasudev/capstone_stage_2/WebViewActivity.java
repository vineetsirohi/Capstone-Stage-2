package in.vasudev.capstone_stage_2;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WebViewActivity extends AppCompatActivity {

    public static final String URL_TO_OPEN = "urtoop";

    @BindView(R.id.webView)
    WebView mWebView;

    @BindView(R.id.progressBar)
    ProgressBar mProgress;

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    private String mUrl;

    private Intent mShareIntent;

    public static void startActivity(Context context, String url) {
        Intent intent = getIntent(context, url);
        context.startActivity(intent);
    }

    @NonNull
    private static Intent getIntent(Context context, String url) {
        Intent intent = new Intent(context, WebViewActivity.class);
        intent.putExtra(URL_TO_OPEN, url);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
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
        prepareShareIntent();

        mUrl = getIntent().getStringExtra(URL_TO_OPEN);
        if (mUrl != null) {
            mWebView.loadUrl(mUrl);
        }
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
        if (android.os.Build.VERSION.SDK_INT >= 11) {
            webSettings.setDisplayZoomControls(false);
        }
        mWebView.setWebViewClient(new WebViewClient() {
            // Override page so it's load on my view only
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                mUrl = url;

                putUrlInShareIntent(url);

                WebViewActivity.this.mProgress.setVisibility(WebView.VISIBLE);
                WebViewActivity.this.mProgress.setProgress(0);

                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                WebViewActivity.this.mProgress.setVisibility(WebView.GONE);
                super.onPageFinished(view, url);
            }
        });

        // Setup callback support for mTitle and progress bar
        mWebView.setWebChromeClient(new WebChrome());
    }

    private void setUpProgressBar() {
        mProgress.setIndeterminate(false);
        mProgress.setMax(100);
    }

    private void putUrlInShareIntent(String url) {
        mShareIntent.removeExtra(Intent.EXTRA_TEXT);
        mShareIntent
                .putExtra(Intent.EXTRA_TEXT, getString(R.string.hey_checkout_reddit_post) + url);
        mShareIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.reddit_post));
    }

    private void prepareShareIntent() {
        mShareIntent = new Intent();
        mShareIntent.setAction(Intent.ACTION_SEND);
        mShareIntent.setType("text/plain");
    }

    class WebChrome extends WebChromeClient {

        @Override
        public void onReceivedTitle(WebView view, String title) {
            WebViewActivity.this.setTitle(title);
            putUrlInShareIntent(mUrl);
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            WebViewActivity.this.mProgress.setProgress(newProgress);
            if (newProgress == 100) {
                CookieSyncManager.getInstance().sync();
            }
        }

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
