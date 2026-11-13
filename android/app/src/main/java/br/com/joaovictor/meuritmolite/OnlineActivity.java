package br.com.joaovictor.meuritmolite;
import android.app.*;import android.content.*;import android.graphics.Color;import android.net.Uri;import android.os.Bundle;import android.webkit.*;import android.view.*;import android.widget.*;
/** Online account, plans, community and weather. Native offline alarms remain in MainActivity. */
public final class OnlineActivity extends Activity {
 private WebView web;private UpdateGate gate;
 @Override public void onCreate(Bundle state){super.onCreate(state);
  gate=new UpdateGate(this);gate.check();
  web=new WebView(this);web.setBackgroundColor(Color.rgb(250,249,245));
  WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);
  settings.setAllowFileAccess(false);settings.setAllowContentAccess(false);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  settings.setSafeBrowsingEnabled(true);settings.setSupportMultipleWindows(false);
  CookieManager.getInstance().setAcceptCookie(true);
  web.setWebViewClient(new WebViewClient(){
   @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
    Uri uri=request.getUrl();String host=uri.getHost();
    if("https".equals(uri.getScheme())&&("meuritmo.net".equals(host)||"www.meuritmo.net".equals(host)))return false;
    if(request.isForMainFrame()){try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception ignored){}return true;}
    return false;
   }
  });
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  LinearLayout bar=new LinearLayout(this);bar.setPadding(12,8,12,8);bar.setGravity(Gravity.CENTER_VERTICAL);
  TextView title=new TextView(this);title.setText("MR: Meu Ritmo App");title.setTextSize(16);title.setTextColor(Color.rgb(35,40,38));bar.addView(title,new LinearLayout.LayoutParams(0,-2,1));
  Button alarms=new Button(this);alarms.setText("Alarmes");alarms.setAllCaps(false);alarms.setOnClickListener(v->startActivity(new Intent(this,MainActivity.class)));bar.addView(alarms);
  root.addView(bar,new LinearLayout.LayoutParams(-1,-2));root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  setContentView(root);web.loadUrl("https://meuritmo.net/app");
 }
 @Override protected void onResume(){super.onResume();if(gate!=null)gate.check();}
 @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(gate!=null)gate.stop();if(web!=null){web.destroy();web=null;}super.onDestroy();}
}
