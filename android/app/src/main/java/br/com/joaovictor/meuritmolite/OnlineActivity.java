package br.com.joaovictor.meuritmolite;
import android.Manifest;import android.content.pm.PackageManager;import android.app.*;import android.content.*;import android.graphics.Color;import android.net.Uri;import android.os.Bundle;import android.webkit.*;import android.view.*;import android.widget.*;
/** Online account, plans, community and weather. Native offline alarms remain in MainActivity. */
public final class OnlineActivity extends Activity {
 private WebView web;private UpdateGate gate;private LinearLayout root;private GeolocationPermissions.Callback geoCallback;private String geoOrigin;private static final int LOCATION_REQUEST=204;
 @Override public void onCreate(Bundle state){super.onCreate(state);
  gate=new UpdateGate(this);
  web=new WebView(this);web.setBackgroundColor(Color.rgb(250,249,245));
  WebSettings settings=web.getSettings();settings.setJavaScriptEnabled(true);settings.setDomStorageEnabled(true);
  settings.setAllowFileAccess(false);settings.setAllowContentAccess(false);settings.setMixedContentMode(WebSettings.MIXED_CONTENT_NEVER_ALLOW);
  settings.setSafeBrowsingEnabled(true);settings.setSupportMultipleWindows(false);settings.setJavaScriptCanOpenWindowsAutomatically(false);
  CookieManager.getInstance().setAcceptCookie(true);CookieManager.getInstance().setAcceptThirdPartyCookies(web,false);
  web.setWebChromeClient(new WebChromeClient(){
   @Override public void onGeolocationPermissionsShowPrompt(String origin,GeolocationPermissions.Callback callback){
    Uri uri=Uri.parse(origin);
    if(!"https".equals(uri.getScheme())||!("meuritmo.net".equals(uri.getHost())||"www.meuritmo.net".equals(uri.getHost()))){callback.invoke(origin,false,false);return;}
    if(android.os.Build.VERSION.SDK_INT>=23&&checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
     if(geoCallback!=null)geoCallback.invoke(geoOrigin,false,false);
     geoCallback=callback;geoOrigin=origin;
     requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},LOCATION_REQUEST);
    }else callback.invoke(origin,true,false);
   }
   @Override public void onGeolocationPermissionsHidePrompt(){ }
  });
  web.setWebViewClient(new WebViewClient(){
   @Override public void onReceivedError(WebView view,WebResourceRequest req,WebResourceError error){if(req.isForMainFrame())Toast.makeText(OnlineActivity.this,"Sem conexão com o Meu Ritmo. Verifique sua internet e tente novamente.",Toast.LENGTH_LONG).show();}

   @Override public boolean shouldOverrideUrlLoading(WebView view,WebResourceRequest request){
    Uri uri=request.getUrl();String host=uri.getHost();
    if("https".equals(uri.getScheme())&&("meuritmo.net".equals(host)||"www.meuritmo.net".equals(host)))return false;
    if(request.isForMainFrame()){try{startActivity(new Intent(Intent.ACTION_VIEW,uri));}catch(Exception ignored){}return true;}
    return false;
   }
  });
  root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);
  root.addView(web,new LinearLayout.LayoutParams(-1,0,1));
  setContentView(root);gate.protect();gate.check();web.loadUrl("https://meuritmo.net/app?source=android-app");
 }
 @Override public void onRequestPermissionsResult(int request,String[] permissions,int[] results){
  super.onRequestPermissionsResult(request,permissions,results);
  if(request==LOCATION_REQUEST&&geoCallback!=null){
   geoCallback.invoke(geoOrigin,results.length>0&&results[0]==PackageManager.PERMISSION_GRANTED,false);
   geoCallback=null;geoOrigin=null;
  }
 }
 @Override protected void onResume(){super.onResume();if(gate!=null)gate.check();}
 @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
 @Override protected void onDestroy(){if(gate!=null)gate.stop();if(geoCallback!=null){geoCallback.invoke(geoOrigin,false,false);geoCallback=null;}if(web!=null){web.destroy();web=null;}super.onDestroy();}
}
