package br.com.joaovictor.meuritmolite;
import android.app.*;import android.content.*;import android.net.Uri;import android.os.*;import org.json.JSONObject;import java.net.*;import java.io.*;
/** Verifica a versão mínima exigida pelo servidor; não permite ignorar uma atualização obrigatória. */
public final class UpdateGate {
 private static final String POLICY="https://meuritmo.net/android-version.json";
 private static final long INTERVAL=15*60*1000L;
 private final Activity activity;private final Handler handler=new Handler(Looper.getMainLooper());
 private AlertDialog dialog;private boolean checking=false;private boolean required=false;private String downloadUrl="";
 public UpdateGate(Activity activity){this.activity=activity;}
 public void check(){if(checking||activity.isFinishing())return;checking=true;new Thread(()->{
  try{HttpURLConnection c=(HttpURLConnection)new URL(POLICY).openConnection();c.setConnectTimeout(9000);c.setReadTimeout(9000);c.setInstanceFollowRedirects(false);
   if(c.getResponseCode()!=200)throw new IOException("HTTP "+c.getResponseCode());
   ByteArrayOutputStream bytes=new ByteArrayOutputStream();try(InputStream in=c.getInputStream()){byte[] b=new byte[4096];int n;while((n=in.read(b))!=-1){bytes.write(b,0,n);if(bytes.size()>16384)throw new IOException("Policy too large");}}
   JSONObject v=new JSONObject(bytes.toString("UTF-8"));int min=v.getInt("minSupportedVersionCode");String url=v.getString("apkUrl");String message=v.optString("message","Atualize para continuar usando o aplicativo.");
   if(!url.startsWith("https://"))throw new IOException("Invalid download URL");
   handler.post(()->{checking=false;if(min>BuildConfig.VERSION_CODE){required=true;downloadUrl=url;show(message);}else if(!required){schedule();}});
  }catch(Exception e){handler.post(()->{checking=false;if(required)show("Atualização obrigatória. Conecte-se à internet para atualizar.");else schedule();});}
 }).start();}
 private void schedule(){handler.removeCallbacksAndMessages(null);handler.postDelayed(this::check,INTERVAL);}
 private void show(String message){if(activity.isFinishing()||(dialog!=null&&dialog.isShowing()))return;
  dialog=new AlertDialog.Builder(activity).setTitle("Atualização obrigatória").setMessage(message)
  .setCancelable(false).setPositiveButton("Atualizar agora",(d,w)->{try{activity.startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(downloadUrl)));}catch(Exception ignored){}handler.postDelayed(()->show(message),500);}).create();
  dialog.setCanceledOnTouchOutside(false);dialog.show();}
 public boolean isRequired(){return required;}
 public void stop(){handler.removeCallbacksAndMessages(null);if(dialog!=null)dialog.dismiss();}
}
